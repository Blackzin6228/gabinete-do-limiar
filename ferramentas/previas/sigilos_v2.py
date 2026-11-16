"""Sigilos do Gabinete do Limiar, v2 (PRÉVIA: nada disto está no jogo).

Reforma de estilo (Nikolas, 15/11/2026): os sigilos v1 pareciam runa de fantasia, com geometria de régua.
Aqui tudo é riscado À MÃO, no chão, com giz ou com o que o elemento deixa: o círculo não fecha direito, o traço
engrossa, afina, treme e falha, e em volta fica sujeira, pó e respingo. Clima de terror investigativo e ritual.
Arte própria: nenhum símbolo vem de Ordem Paranormal. Os desenhos são nossos (ver ELEMENTOS, abaixo).

O QUE SAI DAQUI (tudo 32x32, PNG RGBA 8 bits, fundo transparente)
- 5 sigilos de ELEMENTO, cada um em duas versões com a MESMA forma:
    sigilo_de_<elemento>.png      na cor do elemento, dessaturada (Medo: preto e branco, com glitch)
    sigilo_de_<elemento>_giz.png  em giz branco sujo
- 26 letras (sigilo_a..sigilo_z) e 10 dígitos (sigilo_0..sigilo_9) em giz. O alfabeto continua o do v1
  (três gestos: risco, farpa e ponto; a regra está no topo do `sigilos_v1.py`), só que riscado à mão.
Nomes de arquivo = IDs de `docs/nomes-pacote-1.md`. Os 5 sigilos de tipo de ritual ficam para depois.

Uso:
  python3 ferramentas/previas/sigilos_v2.py              gera arte/sigilos-v2/32/
  python3 ferramentas/previas/sigilos_v2.py --saida DIR  gera dentro de DIR
  python3 ferramentas/previas/sigilos_v2.py --conferir   gera duas vezes e compara os md5

Precisa de Pillow e numpy, e do `sigilos_v1.py` ao lado (gestos do alfabeto). Determinístico: seed fixa por nome
(crc32). A forma e o acabamento têm seeds separadas: por isso a versão giz e a versão de cor têm o mesmo desenho.
Aviso para o Dev: os pixels têm alfa parcial (pó, falha); o bloco precisa de render "translucent".
"""
import hashlib
import math
import os
import sys
import tempfile

import numpy as np
from PIL import Image

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

import sigilos_v1 as v1  # noqa: E402

RAIZ = v1.RAIZ
TAM = 32  # lado da textura
SS = 4  # cada pixel é medido em 4x4 pontos antes de virar pixel
PASSO = 0.25  # distância entre dois toques da mão, em pixels

ELEMENTOS = ["sangue", "morte", "conhecimento", "energia", "medo"]
ROTULO = {"sangue": "Sangue", "morte": "Morte", "conhecimento": "Conhecimento", "energia": "Energia", "medo": "Medo"}
# cor de legenda de cada elemento (dessaturada)
COR_ELEMENTO = {
    "sangue": (104, 24, 22), "morte": (88, 90, 98), "conhecimento": (164, 130, 58), "energia": (104, 94, 174),
    "medo": (226, 226, 222),
}

# Acabamento de cada material. tons = (claro, base, escuro).
ESTILOS = {
    # giz branco sujo: muito pó em volta, poucas gotas
    "giz": dict(tons=((244, 241, 230), (228, 224, 210), (186, 182, 168)), borda=0.35, miolo=0.0, furos=0.08,
                sujeira=0.11, respingos=3, pingos=0, faiscas=0),
    # sangue seco: casca escura na borda, escorrido e muito respingo
    "sangue": dict(tons=((132, 36, 28), (102, 22, 20), (60, 12, 14)), borda=0.8, miolo=0.10, furos=0.04,
                   sujeira=0.05, respingos=12, pingos=7, faiscas=0),
    # carvão preto com cinza de chumbo por cima: esfarela (o preto manda: cinza some em pedra cinza)
    "morte": dict(tons=((158, 160, 166), (112, 114, 122), (14, 14, 18)), borda=1.0, miolo=0.50, furos=0.05,
                  sujeira=0.10, respingos=9, pingos=0, faiscas=0),
    # ocre, dourado sujo: borrado de dedo
    "conhecimento": dict(tons=((198, 166, 86), (162, 128, 58), (100, 76, 36)), borda=0.6, miolo=0.08, furos=0.07,
                         sujeira=0.10, respingos=4, pingos=0, faiscas=0),
    # roxo-azulado elétrico, apagado: fagulhas soltas
    "energia": dict(tons=((156, 152, 214), (102, 92, 172), (54, 48, 108)), borda=0.55, miolo=0.06, furos=0.06,
                    sujeira=0.05, respingos=3, pingos=0, faiscas=9),
    # branco: o preto e o glitch entram depois (ver `glitch`)
    "medo": dict(tons=((242, 242, 238), (226, 226, 222), (150, 150, 148)), borda=0.2, miolo=0.0, furos=0.05,
                 sujeira=0.04, respingos=2, pingos=0, faiscas=0),
}


def semente(nome):
    return v1.semente("v2:" + nome)


# ---------------------------------------------------------------------------------------------------------------
# A mão: transforma um caminho num risco tremido, de espessura irregular, com falhas
# ---------------------------------------------------------------------------------------------------------------
def suave(rng, n, comprimento):
    """Ruído que muda devagar, entre -1 e 1: `comprimento` é de quantos em quantos pixels ele vira."""
    k = max(2, int(n * PASSO / comprimento) + 2)
    ctrl = rng.uniform(-1, 1, k)
    x = np.linspace(0, k - 1.001, n)
    i = x.astype(int)
    f = x - i
    f = f * f * (3 - 2 * f)
    return ctrl[i] * (1 - f) + ctrl[np.minimum(i + 1, k - 1)] * f


def reamostra(pts):
    pts = np.asarray(pts, dtype=float)
    seg = np.hypot(np.diff(pts[:, 0]), np.diff(pts[:, 1]))
    acum = np.concatenate([[0.0], np.cumsum(seg)])
    n = max(3, int(acum[-1] / PASSO) + 1)
    s = np.linspace(0, acum[-1], n)
    return np.stack([np.interp(s, acum, pts[:, 0]), np.interp(s, acum, pts[:, 1])], axis=1)


def alisa(p, janela):
    """Arredonda as quinas: ninguém faz canto vivo com giz no chão."""
    if janela < 3:
        return p
    m = janela // 2
    q = np.pad(p, ((m, m), (0, 0)), mode="edge")
    nucleo = np.ones(janela) / janela
    return np.stack([np.convolve(q[:, c], nucleo, mode="valid") for c in (0, 1)], axis=1)


def mao(pts, rng, grosso=0.8, tremor=0.4, falha=0.1, quina=1.6):
    """Devolve os toques do risco: linhas (x, y, raio, força)."""
    p = reamostra(pts)
    w = max(1, int(quina / PASSO)) | 1
    p = alisa(alisa(p, w), w)
    n = len(p)
    dx, dy = np.gradient(p[:, 0]), np.gradient(p[:, 1])
    comp = np.hypot(dx, dy) + 1e-9
    desvio = tremor * (1.2 * suave(rng, n, 7.0) + 0.55 * suave(rng, n, 1.5))  # a mão sai da linha
    x = p[:, 0] - dy / comp * desvio
    y = p[:, 1] + dx / comp * desvio
    raio = grosso * (1 + 0.40 * suave(rng, n, 4.5) + 0.20 * suave(rng, n, 1.2))  # aperta e solta
    t = np.arange(n) * PASSO
    raio = raio * (1 + 0.30 * np.exp(-t / 1.0))  # começa apertando
    raio = raio * np.clip(0.5 + (t[-1] - t) / 2.5, 0.5, 1.0)  # termina afinando
    raio = np.maximum(raio, 0.32)
    forca = 0.80 + 0.20 * suave(rng, n, 3.0)
    forca = np.where(suave(rng, n, 2.4) > 1 - 2.2 * falha, 0.12, forca)  # o giz pula
    return np.stack([x, y, raio, forca], axis=1)


def roda(cx, cy, r, rng, deriva=1.6, achata=0.07, aberta_em=None, abertura=0.0, voltas=1.0):
    """Caminho de um círculo feito à mão: meio oval, o raio vai mudando e o fim não encontra o começo.
    Sem `aberta_em`, a mão ou passa um pouco do começo ou para antes. Com ele (ângulo, 90 = cima), fica um vão ali."""
    sentido = 1 if rng.random_sample() < 0.5 else -1
    phi = rng.uniform(0, math.pi)
    if aberta_em is None:
        t0 = rng.uniform(0, 2 * math.pi)
        giro = 2 * math.pi * voltas + rng.uniform(0.2, 0.5) * (1 if rng.random_sample() < 0.6 else -1)
    else:
        t0 = math.radians(aberta_em) + sentido * abertura / 2
        giro = 2 * math.pi * voltas - abertura
    n = int(40 * voltas) + 8
    pts = []
    for i in range(n + 1):
        u = i / n
        t = t0 + sentido * giro * u
        rr = r + deriva * (u - 0.5)
        ex, ey = rr * (1 + achata) * math.cos(t - phi), rr * (1 - achata) * math.sin(t - phi)
        pts.append((cx + ex * math.cos(phi) - ey * math.sin(phi), cy - (ex * math.sin(phi) + ey * math.cos(phi))))
    return pts


def ponto(x, y, rng, r=1.25):
    """Ponto: o giz apertado e girado no lugar."""
    return np.array([[x + rng.uniform(-0.3, 0.3), y + rng.uniform(-0.3, 0.3), r * rng.uniform(0.8, 1.1), 1.0]
                     for _ in range(4)])


def cobre(tracos):
    """Quanto de cada pixel 32x32 o risco cobre (0 a 1)."""
    g = TAM * SS
    hi = np.zeros((g, g), dtype=np.float32)
    for tr in tracos:
        for x, y, r, f in tr:
            px, py, pr = x * SS, y * SS, r * SS
            ia, ib = max(0, int(px - pr - 1)), min(g, int(px + pr + 2))
            ja, jb = max(0, int(py - pr - 1)), min(g, int(py + pr + 2))
            if ia >= ib or ja >= jb:
                continue
            ys, xs = np.mgrid[ja:jb, ia:ib]
            dentro = (xs + 0.5 - px) ** 2 + (ys + 0.5 - py) ** 2 <= pr * pr
            sl = hi[ja:jb, ia:ib]
            np.maximum(sl, np.where(dentro, np.float32(f), np.float32(0)), out=sl)
    return hi.reshape(TAM, SS, TAM, SS).mean(axis=(1, 3))


# ---------------------------------------------------------------------------------------------------------------
# Os 5 sigilos de elemento (desenho nosso). Coordenadas em pixels da textura 32x32, y para baixo.
# ---------------------------------------------------------------------------------------------------------------
def forma_sangue(rng):
    """Marcas de garra dentro do círculo: três riscos puxados de cima para baixo, e a mancha de onde a mão saiu."""
    return [
        mao(roda(16, 15.5, 11.6, rng, deriva=1.8), rng, grosso=0.95, tremor=0.5, falha=0.06),
        mao([(9.5, 8.5), (10.2, 14), (12.6, 22.5)], rng, grosso=1.0, tremor=0.45, falha=0.05),
        mao([(15.2, 7.0), (15.4, 15), (16.6, 25.5)], rng, grosso=1.05, tremor=0.45, falha=0.05),
        mao([(21.6, 8.5), (21.2, 14.5), (19.6, 22.0)], rng, grosso=1.0, tremor=0.45, falha=0.05),
        ponto(23.2, 22.6, rng, 1.3),
    ]


def forma_morte(rng):
    """O que murcha: uma haste com costelas caídas, tortas, cada lado de um jeito. O círculo fica aberto embaixo,
    esfarelando."""
    t = [
        mao(roda(16, 16, 11.8, rng, deriva=1.2, aberta_em=270, abertura=0.95), rng, grosso=0.9, tremor=0.5,
            falha=0.10),
        mao([(16.4, 6.5), (15.5, 15), (16.5, 25.5)], rng, grosso=0.95, tremor=0.45, falha=0.08),
    ]
    for y0, lado, larg, cai in ((9.5, -1, 5.5, 6.0), (11.5, 1, 6.0, 5.0), (14.5, -1, 5.0, 5.5), (17.0, 1, 4.5, 5.0),
                                (19.5, -1, 3.5, 4.0)):
        t.append(mao([(16, y0), (16 + lado * larg * 0.7, y0 + 0.8), (16 + lado * larg, y0 + cai)], rng,
                     grosso=0.75, tremor=0.35, falha=0.12))
    return t


def forma_conhecimento(rng):
    """Contagem de quem investiga: quatro riscos de pé e um quinto que atravessa, como marca de dias na parede."""
    t = [mao(roda(16, 16, 11.8, rng, deriva=1.5), rng, grosso=0.85, tremor=0.42, falha=0.09)]
    for x, y0, y1 in ((9.8, 10.5, 21.5), (13.6, 9.0, 22.5), (17.4, 9.5, 23.0), (21.2, 10.0, 21.0)):
        t.append(mao([(x + rng.uniform(-0.4, 0.4), y0), (x + rng.uniform(-0.4, 0.4), y1)], rng, grosso=0.62,
                     tremor=0.3, falha=0.07))
    t.append(mao([(7.0, 20.5), (16, 15.4), (25.0, 10.5)], rng, grosso=0.7, tremor=0.4, falha=0.06))
    return t


def forma_energia(rng):
    """Não para quieto: o círculo riscado duas vezes, sem acertar, e um rabisco que volta sobre si mesmo."""
    return [
        mao(roda(16, 16, 11.4, rng, deriva=2.6, achata=0.10, voltas=1.8), rng, grosso=0.7, tremor=0.75, falha=0.12),
        mao([(7.5, 18.5), (11.5, 10), (15, 20.5), (13, 14), (19, 8.5), (21.5, 19.5), (17.5, 16), (25, 12.5)], rng,
            grosso=0.85, tremor=0.5, falha=0.08, quina=1.2),
    ]


def forma_medo(rng):
    """Alguém parado no limiar: um chão que atravessa o círculo, um risco de pé e um ponto em cima. Mais nada."""
    return [
        mao(roda(16, 15.5, 11.6, rng, deriva=1.4), rng, grosso=0.7, tremor=0.45, falha=0.10),
        mao([(2.5, 22.6), (16, 22.2), (29.5, 22.0)], rng, grosso=0.75, tremor=0.35, falha=0.08),
        mao([(16.0, 22.2), (16.3, 12.6)], rng, grosso=0.9, tremor=0.3, falha=0.04),
        ponto(16.2, 9.6, rng, 1.35),
    ]


FORMAS = {"sangue": forma_sangue, "morte": forma_morte, "conhecimento": forma_conhecimento,
          "energia": forma_energia, "medo": forma_medo}


def forma_v1(gestos, rng):
    """Risca à mão um glifo do alfabeto v1 (grade 16x16 -> 32x32). Riscos emendados viram um gesto só."""
    tracos, caminho = [], []

    giro = math.radians(rng.uniform(-4.5, 4.5))  # ninguém risca reto: o glifo sai meio caído
    torto = {}

    def c(p):
        if p not in torto:  # cada ponta escapa um pouco do lugar (a mesma ponta escapa sempre igual)
            x, y = p[0] * 2 + 1.0 - 16, p[1] * 2 + 1.0 - 16
            torto[p] = (16 + x * math.cos(giro) - y * math.sin(giro) + rng.uniform(-0.7, 0.7),
                        16 + x * math.sin(giro) + y * math.cos(giro) + rng.uniform(-0.7, 0.7))
        return torto[p]

    def fecha():
        if caminho:
            tracos.append(mao(list(caminho), rng, grosso=0.8, tremor=0.6, falha=0.08))
        del caminho[:]

    for g in gestos:
        if g[0] == "L":
            a, b = c(g[1]), c(g[2])
            if caminho and caminho[-1] == a:
                caminho.append(b)
            else:
                fecha()
                caminho.extend([a, b])
            continue
        fecha()
        cx, cy = c(g[1])
        if g[0] == "C":
            r = g[2] * 2
            tracos.append(mao(roda(cx, cy, r, rng, deriva=min(1.5, r * 0.14)), rng, grosso=0.8, tremor=0.38,
                              falha=0.08))
        elif g[0] == "A":
            r, a0, a1 = g[2] * 2, math.radians(g[3]), math.radians(g[4])
            arco = [(cx + r * math.cos(a0 + (a1 - a0) * i / 12), cy - r * math.sin(a0 + (a1 - a0) * i / 12))
                    for i in range(13)]
            tracos.append(mao(arco, rng, grosso=0.8, tremor=0.38, falha=0.08))
        elif g[0] == "D":
            tracos.append(ponto(cx, cy, rng, 1.6))
    fecha()
    return tracos


# ---------------------------------------------------------------------------------------------------------------
# Acabamento: borda roída, falha, pó, respingo, escorrido, fagulha
# ---------------------------------------------------------------------------------------------------------------
def dilata(m, n):
    for _ in range(n):
        o = m.copy()
        o[1:, :] |= m[:-1, :]
        o[:-1, :] |= m[1:, :]
        o[:, 1:] |= m[:, :-1]
        o[:, :-1] |= m[:, 1:]
        m = o
    return m


def acabamento(c, rng, estilo):
    """Pinta a cobertura `c` com o material `estilo`. Devolve RGBA uint8 32x32."""
    e = ESTILOS[estilo]
    r1, r2, r3, r4, r5 = (rng.random_sample((TAM, TAM)) for _ in range(5))
    forte = c >= 0.5
    medio = (c >= 0.2) & ~forte
    fraco = (c >= 0.06) & (c < 0.2) & (r2 < 0.5)  # borda roída: metade dos pixels de beira some
    marca = c >= 0.06
    alfa = np.zeros((TAM, TAM))
    tom = np.ones((TAM, TAM), dtype=int)
    alfa[forte] = (205 + 50 * r1)[forte]
    alfa[medio] = (110 + 70 * r1)[medio]
    alfa[fraco] = (45 + 55 * r1)[fraco]
    tom[forte & (r2 < 0.14)] = 0
    tom[forte & (r3 < e["miolo"])] = 2
    tom[(medio | fraco) & (r3 < e["borda"])] = 2
    alfa[forte & (r4 > 1 - e["furos"])] *= 0.3  # falhas no meio do risco

    sujo = dilata(marca, 2) & ~marca & (r5 < e["sujeira"])  # pó e sujeira em volta
    alfa[sujo] = (38 + 60 * r1)[sujo]
    tom[sujo] = 2

    ys, xs = np.nonzero(forte)

    def pinga(x, y, a, t):
        if 0 <= x < TAM and 0 <= y < TAM and a > alfa[y, x]:
            alfa[y, x] = a
            tom[y, x] = t

    for _ in range(e["respingos"]):
        k = rng.randint(len(ys))
        ang, dist = rng.uniform(0, 2 * math.pi), rng.uniform(2.0, 5.0)
        x, y = int(round(xs[k] + math.cos(ang) * dist)), int(round(ys[k] + math.sin(ang) * dist))
        a, par = 130 + 90 * rng.random_sample(), rng.random_sample() < 0.35
        if 0 <= x < TAM and 0 <= y < TAM and not marca[y, x]:
            pinga(x, y, a, 1)
            if par:
                pinga(x + 1, y, a * 0.6, 2)

    if e["pingos"]:  # escorrido: desce de onde o risco acaba embaixo
        beira = [(x, y) for y, x in zip(ys, xs) if 9 <= y < TAM - 3 and not marca[y + 1, x]]
        for _ in range(e["pingos"]):
            x, y = beira[rng.randint(len(beira))]
            comp = rng.randint(2, 7)
            for i in range(1, comp + 1):
                pinga(x, y + i, 215 - i * 20 + rng.randint(-10, 11), 2 if i == comp else 1)

    for _ in range(e["faiscas"]):  # fagulha: pixel claro solto, às vezes um tracinho
        k = rng.randint(len(ys))
        ang, dist = rng.uniform(0, 2 * math.pi), rng.uniform(3.0, 6.5)
        x, y = int(round(xs[k] + math.cos(ang) * dist)), int(round(ys[k] + math.sin(ang) * dist))
        dupla, dx, dy = rng.random_sample() < 0.35, rng.randint(-1, 2), rng.randint(-1, 2)
        pinga(x, y, 215 + 40 * rng.random_sample(), 0)
        if dupla:
            pinga(x + dx, y + dy, 150, 1)

    tons = np.array(e["tons"], dtype=int)
    rgba = np.zeros((TAM, TAM, 4), dtype=np.uint8)
    cor = np.clip(tons[tom] + rng.randint(-6, 7, (TAM, TAM))[..., None], 0, 255)
    a8 = np.clip(alfa + 0.5, 0, 255).astype(np.uint8)
    rgba[..., :3] = np.where(a8[..., None] > 0, cor, 0)
    rgba[..., 3] = a8
    return rgba


def desloca(a, dx, dy):
    o = np.zeros_like(a)
    h, w = a.shape[:2]
    o[max(0, dy):min(h, h + dy), max(0, dx):min(w, w + dx)] = a[max(0, -dy):min(h, h - dy), max(0, -dx):min(w, w - dx)]
    return o


def glitch(rgba, rng):
    """Medo: preto e branco com defeito. Uma cópia preta fora do lugar, um eco fraco, e faixas que escorregam."""
    preto = desloca(rgba, 2, 1)
    preto[..., :3] = np.where(preto[..., 3:] > 0, np.array([6, 6, 8], dtype=np.uint8), 0)
    eco = desloca(rgba, -2, 0)
    eco[..., 3] = (eco[..., 3] * 0.22).astype(np.uint8)
    img = Image.fromarray(preto, "RGBA")
    img.alpha_composite(Image.fromarray(eco, "RGBA"))
    img.alpha_composite(Image.fromarray(rgba, "RGBA"))
    a = np.array(img)
    for _ in range(4):  # faixas deslocadas
        y, h, dx = rng.randint(4, TAM - 5), rng.randint(1, 3), int(rng.choice([-3, -2, 2, 3]))
        a[y:y + h] = desloca(a[y:y + h], dx, 0)
    y = rng.randint(6, TAM - 7)  # uma linha repetida
    a[y + 1] = a[y]
    a[..., :3] = np.where(a[..., 3:] > 0, a[..., :3], 0)
    return a


# ---------------------------------------------------------------------------------------------------------------
_cache = {}


def sigilos():
    """Lista ordenada de (arquivo, rótulo, imagem RGBA 32x32). Calcula uma vez só."""
    if "lista" in _cache:
        return _cache["lista"]
    out = []

    def pinta(arquivo, cobertura, estilo):
        rgba = acabamento(cobertura, np.random.RandomState(semente("acabamento:" + arquivo)), estilo)
        if estilo == "medo":
            rgba = glitch(rgba, np.random.RandomState(semente("glitch:" + arquivo)))
        return Image.fromarray(rgba, "RGBA")

    for el in ELEMENTOS:
        cobertura = cobre(FORMAS[el](np.random.RandomState(semente("forma:" + el))))
        out.append(("sigilo_de_" + el, "Sigilo de " + ROTULO[el], pinta("sigilo_de_" + el, cobertura, el)))
        out.append(("sigilo_de_%s_giz" % el, "Sigilo de %s (giz)" % ROTULO[el],
                    pinta("sigilo_de_%s_giz" % el, cobertura, "giz")))
    letras = sorted(ch for fam in v1.FAMILIAS for ch in fam)
    glifos = [("sigilo_" + ch.lower(), ch, v1.letra(ch)) for ch in letras]
    glifos += [("sigilo_%d" % n, str(n), v1.digito(n)) for n in range(10)]
    for arquivo, rot, gestos in glifos:
        cobertura = cobre(forma_v1(gestos, np.random.RandomState(semente("forma:" + arquivo))))
        out.append((arquivo, rot, pinta(arquivo, cobertura, "giz")))
    _cache["lista"] = out
    return out


def pedra_chao():
    """Bloco de pedra 16x16 de tom médio, para ver o sigilo no chão. Feita aqui: não é a textura da Mojang."""
    rng = np.random.RandomState(semente("pedra_chao"))
    v = 88 + (v1.ruido_suave(rng, 16, 5) * 9).astype(int) + rng.randint(-5, 6, (16, 16))
    v = np.where(rng.random_sample((16, 16)) < 0.08, v - 16, v)
    v = np.where(rng.random_sample((16, 16)) < 0.06, v + 10, v)
    v = np.clip(v, 0, 255)
    return Image.fromarray(np.stack([v, v, v + 3], axis=-1).astype(np.uint8), "RGB")


def gera(saida):
    """Grava os PNGs em `saida`/32/. Devolve {caminho relativo: md5}."""
    md5 = {}
    for arquivo, _rot, img in sigilos():
        rel = os.path.join("32", arquivo + ".png")
        caminho = os.path.join(saida, rel)
        os.makedirs(os.path.dirname(caminho), exist_ok=True)
        img.save(caminho, "PNG", optimize=False)
        with open(caminho, "rb") as f:
            md5[rel] = hashlib.md5(f.read()).hexdigest()
    return md5


def main(argv):
    if "--conferir" in argv:
        with tempfile.TemporaryDirectory() as a, tempfile.TemporaryDirectory() as b:
            m1 = gera(a)
            _cache.clear()
            m2 = gera(b)
        dif = sorted(k for k in m1 if m1[k] != m2.get(k))
        print("%d arquivos, %d diferentes entre as duas rodadas" % (len(m1), len(dif)))
        print("md5 do conjunto: " + hashlib.md5("".join(k + m1[k] for k in sorted(m1)).encode()).hexdigest())
        for k in dif:
            print("DIFERENTE: " + k)
        return 1 if dif else 0
    if "--saida" in argv:
        md5 = gera(argv[argv.index("--saida") + 1])
    else:
        md5 = gera(os.path.join(RAIZ, "arte", "sigilos-v2"))
    print("%d arquivos gerados" % len(md5))
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
