"""Sigilos do Gabinete do Limiar, v1 (PRÉVIA: nada disto está no jogo).

Sistema próprio de sigilos de giz: alfabeto A-Z, dígitos 0-9, 5 símbolos de elemento e 5 de tipo de ritual.
Nada aqui é copiado de Ordem Paranormal: os glifos nascem da regra abaixo.

A REGRA DO ALFABETO
1. Tudo é riscado numa grade de 16x16 com só três gestos: risco, farpa (risco curto) e ponto.
2. As 5 vogais são os 5 traços-base puros: A Telhado, E Forquilha, I Haste, O Roda, U Cálice.
3. Consoante = o traço-base da vogal que vem antes dela no alfabeto + marcas que contam a distância até a vogal.
4. Farpa vale 1, ponto vale 3 (B = A + 1 farpa, C = A + 2 farpas, D = A + ponto, N = I + ponto + 2 farpas).
5. Dígitos usam a mesma conta pendurada numa Trave (risco no alto, pontas viradas); o zero é a trave com argola vazia.

Uso:
  python3 ferramentas/previas/sigilos_v1.py              gera arte/sigilos-v1/{16,128}/ e docs/previas/sigilos-v1.png
  python3 ferramentas/previas/sigilos_v1.py --saida DIR  gera tudo dentro de DIR (para comparar rodadas)
  python3 ferramentas/previas/sigilos_v1.py --conferir   gera duas vezes em pastas temporárias e compara os md5

Precisa de Pillow e numpy. Determinístico: seed fixa por nome de glifo (crc32). O md5 dos glifos depende só do numpy;
o da folha depende também da fonte embutida do Pillow (feito com Pillow 12.1.1). Essa fonte não tem acentos:
os acentos da folha são riscados pelo script (ver `texto`), para não depender de fonte instalada na máquina.
Formato: PNG RGBA 8 bits, 16x16 e 128x128 (potências de 2), giz branco sujo sobre transparente.
"""
import hashlib
import math
import os
import sys
import tempfile
import unicodedata
import zlib

import numpy as np
from PIL import Image, ImageDraw, ImageFont

SEED = 20261115
GIZ = (233, 229, 215)  # branco sujo
RAIZ = os.path.normpath(os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", ".."))


def semente(nome):
    return (zlib.crc32(nome.encode("utf-8")) ^ SEED) & 0x7FFFFFFF


# ---------------------------------------------------------------------------------------------------------------
# Gestos. Coordenadas em pixels da grade 16x16 (0..15).
# ---------------------------------------------------------------------------------------------------------------
def L(x1, y1, x2, y2):
    return ("L", (x1, y1), (x2, y2))


def PL(*pts):
    return [("L", pts[i], pts[i + 1]) for i in range(len(pts) - 1)]


def C(cx, cy, r):
    return ("C", (cx, cy), r)


def A(cx, cy, r, a0, a1):  # arco, graus, 0 = direita, 90 = cima
    return ("A", (cx, cy), r, a0, a1)


def D(cx, cy):
    return ("D", (cx, cy))


# Traço-base de cada vogal: (riscos, [farpa 1, farpa 2], ponto)
BASES = {
    "A": (PL((2, 14), (8, 2), (14, 14)), [L(5, 8, 2, 8), L(11, 8, 14, 8)], D(8, 11)),  # Telhado
    "E": (PL((2, 2), (8, 8), (14, 2)) + [L(8, 8, 8, 14)], [L(8, 11, 5, 11), L(8, 11, 11, 11)], D(8, 3)),  # Forquilha
    "I": ([L(8, 2, 8, 14)], [L(8, 5, 14, 5), L(8, 11, 2, 11)], D(3, 5)),  # Haste
    "O": ([C(8, 8, 6)], [L(8, 2, 8, 4), L(2, 8, 4, 8)], D(8, 8)),  # Roda
    "U": (PL((2, 2), (2, 8), (8, 14), (14, 8), (14, 2)), [L(2, 2, 5, 2), L(14, 2, 11, 2)], D(8, 7)),  # Cálice
}
NOMES_BASE = {"A": "Telhado", "E": "Forquilha", "I": "Haste", "O": "Roda", "U": "Cálice"}
FAMILIAS = ["ABCD", "EFGH", "IJKLMN", "OPQRST", "UVWXYZ"]

TRAVE = PL((2, 4), (2, 2), (14, 2), (14, 4))
FARPAS_TRAVE = {0: [], 1: [5], 2: [5, 11]}  # a 1ª farpa fica de lado: no meio, o 1 virava um "T"
PONTOS_TRAVE = {0: [], 1: [8], 2: [5, 11], 3: [3, 8, 13]}

ELEMENTOS = {
    # triângulo que pinga
    "sangue": PL((4, 1), (12, 1), (8, 9), (4, 1)) + [D(8, 13)],
    # a forma dentro da forma
    "conhecimento": PL((2, 2), (14, 2), (14, 14), (2, 14), (2, 2)) + PL((8, 2), (14, 8), (8, 14), (2, 8), (8, 2)),
    # risco que não para quieto
    "energia": PL((1, 9), (4, 3), (7, 12), (10, 2), (13, 11), (15, 7)) + [D(3, 13)],
    # ampulheta com o grão caído
    "morte": [L(2, 2, 14, 2), L(2, 14, 14, 14), L(2, 2, 14, 14), L(14, 2, 2, 14), D(8, 12)],
    # o limiar: a porta aberta com algo dentro
    "medo": [L(4, 14, 4, 6), A(8, 6, 4, 0, 180), L(12, 6, 12, 14), L(1, 14, 4, 14), L(12, 14, 15, 14), D(8, 9)],
}
RITUAIS = {
    # três riscos que se juntam numa argola
    "criar": [C(8, 3, 2), L(8, 6, 8, 14), L(6, 6, 2, 14), L(10, 6, 14, 14)],
    # argola atravessada por uma tranca
    "selar": [C(8, 8, 4), L(1, 8, 15, 8), L(1, 6, 1, 10), L(15, 6, 15, 10)],
    # o que sobe do chão
    "despertar": [L(2, 12, 14, 12), A(8, 12, 4, 0, 180), L(8, 6, 8, 2), L(4, 8, 2, 6), L(12, 8, 14, 6)],
    # coroa com a pedra em cima
    "poder": PL((2, 5), (2, 12), (14, 12), (14, 5), (11, 8), (8, 5), (5, 8), (2, 5)) + [D(8, 2)],
    # quatro riscos que apontam para o que vem
    "invocar": [D(8, 8), L(2, 2, 5, 5), L(14, 2, 11, 5), L(2, 14, 5, 11), L(14, 14, 11, 11)],
}
COR_ELEMENTO = {
    "sangue": (160, 28, 28), "conhecimento": (214, 172, 62), "energia": (150, 84, 205),
    "morte": (58, 58, 64), "medo": (238, 238, 238),
}
ROTULO = {
    "sangue": "Sangue", "conhecimento": "Conhecimento", "energia": "Energia", "morte": "Morte", "medo": "Medo",
    "criar": "Criar", "selar": "Selar", "despertar": "Despertar", "poder": "Poder", "invocar": "Invocar",
}


def letra(ch):
    for fam in FAMILIAS:
        if ch in fam:
            base, farpas, ponto = BASES[fam[0]]
            n = fam.index(ch)
            return list(base) + farpas[: n % 3] + ([ponto] if n // 3 else [])
    raise KeyError(ch)


def digito(n):
    g = list(TRAVE)
    if n == 0:
        return g + [C(8, 9, 3)]
    g += [L(x, 2, x, 7) for x in FARPAS_TRAVE[n % 3]]
    g += [D(x, 11) for x in PONTOS_TRAVE[n // 3]]
    return g


def todos_os_glifos():
    """Lista ordenada de (nome_do_arquivo, rótulo, gestos)."""
    out = [("letra_" + ch.lower(), ch, letra(ch)) for fam in FAMILIAS for ch in fam]
    out.sort(key=lambda t: t[1])
    out += [("digito_%d" % n, str(n), digito(n)) for n in range(10)]
    out += [("elemento_" + k, ROTULO[k], v) for k, v in ELEMENTOS.items()]
    out += [("ritual_" + k, ROTULO[k], v) for k, v in RITUAIS.items()]
    return out


# ---------------------------------------------------------------------------------------------------------------
# 16x16: pixel exato (para o chão do jogo)
# ---------------------------------------------------------------------------------------------------------------
def px_linha(a, b):
    (x0, y0), (x1, y1) = sorted([a, b], key=lambda p: (p[1], p[0]))  # sempre de cima para baixo: fica espelhado
    dx, dy = abs(x1 - x0), abs(y1 - y0)
    sx, sy = (1 if x1 >= x0 else -1), 1
    err, out = dx - dy, []
    while True:
        out.append((x0, y0))
        if (x0, y0) == (x1, y1):
            return out
        e2 = 2 * err
        if e2 > -dy:
            err -= dy
            x0 += sx
        if e2 < dx:
            err += dx
            y0 += sy


def px_circulo(cx, cy, r, a0=0, a1=360):
    pts, x, y, d = set(), r, 0, 1 - r
    while x >= y:
        for qx, qy in ((x, y), (y, x), (-x, y), (-y, x), (x, -y), (y, -x), (-x, -y), (-y, -x)):
            pts.add((cx + qx, cy + qy))
        y += 1
        if d < 0:
            d += 2 * y + 1
        else:
            x -= 1
            d += 2 * (y - x) + 1
    if (a0, a1) != (0, 360):
        pts = {p for p in pts if a0 <= math.degrees(math.atan2(cy - p[1], p[0] - cx)) % 360 <= a1}
    return sorted(pts)


def px_gestos(gestos):
    """Devolve {(x, y): força}, força 1.0 no risco e menor no canto dos pontos."""
    pix = {}

    def poe(p, f=1.0):
        pix[p] = max(pix.get(p, 0.0), f)

    for g in gestos:
        if g[0] == "L":
            for p in px_linha(g[1], g[2]):
                poe(p)
        elif g[0] == "C":
            for p in px_circulo(g[1][0], g[1][1], g[2]):
                poe(p)
        elif g[0] == "A":
            for p in px_circulo(g[1][0], g[1][1], g[2], g[3], g[4]):
                poe(p)
        elif g[0] == "D":
            for dx in (-1, 0, 1):
                for dy in (-1, 0, 1):
                    poe((g[1][0] + dx, g[1][1] + dy), 0.5 if dx and dy else 1.0)
    return pix


def pinta_pixels(pix, larg, alt, nome):
    """Giz em pixel: branco sujo, cada pixel com um pouco mais ou menos de pó."""
    rng = np.random.RandomState(semente("16:" + nome))
    img = Image.new("RGBA", (larg, alt), (0, 0, 0, 0))
    for (x, y) in sorted(pix):
        f = pix[(x, y)]
        tom = int(rng.randint(-9, 10))
        a = int(f * (212 + int(rng.randint(-34, 35))))
        if 0 <= x < larg and 0 <= y < alt:
            img.putpixel((x, y), (GIZ[0] + tom, GIZ[1] + tom, GIZ[2] + tom, max(0, min(255, a))))
    return img


def glifo_16(nome, gestos):
    return pinta_pixels(px_gestos(gestos), 16, 16, nome)


# ---------------------------------------------------------------------------------------------------------------
# Grande: risco de giz com tremor de mão e grão
# ---------------------------------------------------------------------------------------------------------------
def tracos(gestos, rng, tremor=0.16):
    """Transforma os gestos em segmentos de reta e pontos, com o tremor da mão. Unidades da grade (centro do pixel)."""
    cache, segs, pontos = {}, [], []

    def pt(p):
        if p not in cache:
            cache[p] = (p[0] + 0.5 + rng.uniform(-tremor, tremor), p[1] + 0.5 + rng.uniform(-tremor, tremor))
        return cache[p]

    def curva(pts):
        segs.extend((pts[i][0], pts[i][1], pts[i + 1][0], pts[i + 1][1]) for i in range(len(pts) - 1))

    for g in gestos:
        if g[0] == "L":
            (ax, ay), (bx, by) = pt(g[1]), pt(g[2])
            comp = math.hypot(bx - ax, by - ay) or 1.0
            u = rng.uniform(-1, 1) * min(0.22, 0.03 * comp)  # barriga leve do risco
            mx, my = (ax + bx) / 2 - (by - ay) / comp * u * 2, (ay + by) / 2 + (bx - ax) / comp * u * 2
            n = 6
            curva([((1 - t) ** 2 * ax + 2 * t * (1 - t) * mx + t * t * bx,
                    (1 - t) ** 2 * ay + 2 * t * (1 - t) * my + t * t * by) for t in [i / n for i in range(n + 1)]])
        elif g[0] in ("C", "A"):
            cx, cy = g[1][0] + 0.5 + rng.uniform(-tremor, tremor), g[1][1] + 0.5 + rng.uniform(-tremor, tremor)
            r = g[2]
            amp = min(0.03 * r, 0.3)
            f1, f2 = rng.uniform(0, 6.283), rng.uniform(0, 6.283)
            if g[0] == "C":
                t0 = rng.uniform(0, 6.283)
                t1 = t0 + 6.283 + 0.12  # a mão passa um pouco do começo
            else:
                t0, t1 = math.radians(g[3]), math.radians(g[4])
            n = max(18, int(r * 7 * (t1 - t0) / 6.283))
            pts = []
            for i in range(n + 1):
                t = t0 + (t1 - t0) * i / n
                rr = r + amp * (math.sin(2 * t + f1) * 0.6 + math.sin(3 * t + f2) * 0.4)
                pts.append((cx + rr * math.cos(t), cy - rr * math.sin(t)))
            curva(pts)
        elif g[0] == "D":
            x, y = pt(g[1])
            pontos.append((x, y, 1.3))
    return segs, pontos


def ruido_suave(rng, tam, celula):
    n = max(2, tam // celula + 2)
    base = (rng.random_sample((n, n)) * 2 - 1).astype(np.float32)
    return np.asarray(Image.fromarray(base, mode="F").resize((tam, tam), Image.BILINEAR), dtype=np.float32)


def giz_grande(gestos, tam, nome, unidades=16.0, meia=0.43, tremor=0.16):
    """Desenha os gestos num quadrado de `tam` pixels. `meia` = meia largura do risco, em unidades da grade."""
    rng = np.random.RandomState(semente("g:%d:%s" % (tam, nome)))
    segs, pontos = tracos(gestos, rng, tremor)
    k = tam / unidades
    dist = np.full((tam, tam), 1e9, dtype=np.float32)  # distância ao risco, em pixels
    margem = int(meia * k * 3.2) + 4

    def janela(x0, y0, x1, y1):
        ia, ib = max(0, int(min(x0, x1) * k) - margem), min(tam, int(max(x0, x1) * k) + margem + 1)
        ja, jb = max(0, int(min(y0, y1) * k) - margem), min(tam, int(max(y0, y1) * k) + margem + 1)
        if ia >= ib or ja >= jb:
            return None
        ys, xs = np.mgrid[ja:jb, ia:ib].astype(np.float32)
        return (slice(ja, jb), slice(ia, ib)), xs + 0.5, ys + 0.5

    for (x0, y0, x1, y1) in segs:
        j = janela(x0, y0, x1, y1)
        if j is None:
            continue
        sl, xs, ys = j
        ax, ay, bx, by = x0 * k, y0 * k, x1 * k, y1 * k
        vx, vy = bx - ax, by - ay
        t = np.clip(((xs - ax) * vx + (ys - ay) * vy) / max(vx * vx + vy * vy, 1e-6), 0, 1)
        d = np.hypot(xs - (ax + t * vx), ys - (ay + t * vy))
        dist[sl] = np.minimum(dist[sl], d)
    for (x, y, r) in pontos:
        j = janela(x - r, y - r, x + r, y + r)
        if j is None:
            continue
        sl, xs, ys = j
        d = np.maximum(np.hypot(xs - x * k, ys - y * k) - (r - meia) * k, 0)
        dist[sl] = np.minimum(dist[sl], d)

    w = meia * k
    borda = ruido_suave(rng, tam, 9) * 0.22 * w  # borda irregular
    d = dist + borda
    alfa = np.clip((w - d) / 1.3 + 0.5, 0, 1)
    fino = rng.random_sample((tam, tam)).astype(np.float32)
    m = tam // 2 + 1
    bloco = np.kron(rng.random_sample((m, m)).astype(np.float32), np.ones((2, 2), dtype=np.float32))[:tam, :tam]
    grao = 0.55 * fino + 0.45 * bloco
    pressao = np.clip(1 - d / max(w, 1e-6), 0, 1)
    alfa = alfa * (0.50 + 0.50 * grao) * (0.72 + 0.28 * pressao)
    alfa = np.where(grao < 0.2, alfa * 0.35, alfa)  # falhas do giz na pedra
    po = np.clip(1 - dist / (2.7 * w), 0, 1) ** 2 * 0.13 * fino  # pó em volta do risco
    alfa = np.maximum(alfa, po)
    tom = ruido_suave(rng, tam, 14) * 7
    rgba = np.zeros((tam, tam, 4), dtype=np.uint8)
    for c in range(3):
        rgba[..., c] = np.clip(GIZ[c] + tom, 0, 255).astype(np.uint8)
    rgba[..., 3] = np.clip(alfa * 255 + 0.5, 0, 255).astype(np.uint8)
    return Image.fromarray(rgba, "RGBA")


# ---------------------------------------------------------------------------------------------------------------
# Pedra escura (feita aqui, não é a textura da Mojang) e o círculo de exemplo
# ---------------------------------------------------------------------------------------------------------------
def pedra_16():
    rng = np.random.RandomState(semente("pedra"))
    img = Image.new("RGB", (16, 16))
    faixa = [int(rng.randint(-4, 5)) for _ in range(16)]
    for y in range(16):
        for x in range(16):
            v = 44 + faixa[y] + int(rng.randint(-5, 6))
            if rng.random_sample() < 0.07:
                v -= 12
            elif rng.random_sample() < 0.06:
                v += 9
            img.putpixel((x, y), (v, v, v + 4))
    return img


def amplia(img, n):
    """Amplia n vezes sem borrar (cada pixel vira um quadrado). Feito à mão com numpy: o resize NEAREST do Pillow
    desta máquina falha, e assim o resultado não depende dele."""
    a = np.asarray(img)
    return Image.fromarray(np.repeat(np.repeat(a, n, axis=0), n, axis=1), img.mode)


def ladrilha(tile, larg, alt, escala, brilho=1.0):
    t = amplia(tile, escala)
    if brilho != 1.0:
        t = t.point(lambda v: int(v * brilho))
    out = Image.new("RGB", (larg, alt))
    for y in range(0, alt, t.height):
        for x in range(0, larg, t.width):
            out.paste(t, (x, y))
    return out


PALAVRA = "GABINETE"
ELEMENTO_EXEMPLO = "sangue"
LADO_JOGO = 9  # blocos de lado do círculo no chão
CASAS = [(0, -3), (2, -2), (3, 0), (2, 2), (0, 3), (-2, 2), (-3, 0), (-2, -2)]  # em blocos, a partir do centro


def circulo_jogo():
    """Círculo de ritual em escala de bloco: 9x9 blocos (144x144 pixels), um sigilo por bloco, sem girar.
    Os aros passam longe dos sigilos (raios 33 e 62; as letras ficam entre 37 e 55 do centro)."""
    pix = {}
    for p in px_circulo(72, 72, 62) + px_circulo(72, 72, 33):
        pix[p] = 1.0
    ang = math.radians(-22.5)  # ponto que marca onde a palavra começa
    sx, sy = round(72 + 48 * math.sin(ang)), round(72 - 48 * math.cos(ang))
    pix.update({(sx + dx, sy + dy): 1.0 for dx in (-1, 0, 1) for dy in (-1, 0, 1) if not (dx and dy)})

    def cola(gestos, bx, by):
        for (x, y), f in px_gestos(gestos).items():
            pix[((bx + LADO_JOGO // 2) * 16 + x, (by + LADO_JOGO // 2) * 16 + y)] = f

    cola(ELEMENTOS[ELEMENTO_EXEMPLO], 0, 0)
    for ch, (bx, by) in zip(PALAVRA, CASAS):
        cola(letra(ch), bx, by)
    return pinta_pixels(pix, LADO_JOGO * 16, LADO_JOGO * 16, "circulo_jogo")


def circulo_grande(tam=560):
    """O mesmo círculo riscado grande: dois aros, a palavra entre eles e o elemento no centro."""
    u = 70.0
    k = tam / u
    aros = [C(34.5, 34.5, 33), C(34.5, 34.5, 23), ("D", (round(34.5 + 28 * math.sin(math.radians(-22.5)) - 0.5),
                                                        round(34.5 - 28 * math.cos(math.radians(-22.5)) - 0.5)))]
    img = giz_grande(aros, tam, "circulo_aros", unidades=u, meia=0.36, tremor=0.25)
    centro = 224
    simb = giz_grande(ELEMENTOS[ELEMENTO_EXEMPLO], centro, "circulo_centro", meia=0.30)
    img.alpha_composite(simb, ((tam - centro) // 2, (tam - centro) // 2))
    for i, ch in enumerate(PALAVRA):
        g = giz_grande(letra(ch), 72, "circulo_letra_%d" % i, meia=0.62)
        graus = i * 360.0 / len(PALAVRA)
        g = g.rotate(-graus, resample=Image.BICUBIC, expand=True)
        cx = 35 * k + 28 * k * math.sin(math.radians(graus))
        cy = 35 * k - 28 * k * math.cos(math.radians(graus))
        img.alpha_composite(g, (int(round(cx - g.width / 2)), int(round(cy - g.height / 2))))
    return img


# ---------------------------------------------------------------------------------------------------------------
# Folha de prévia
# ---------------------------------------------------------------------------------------------------------------
TINTA = (226, 221, 206)
APAGADO = (150, 147, 138)


def fonte(tam):
    return ImageFont.load_default(size=tam)


ACENTOS = {"\u0301": "agudo", "\u0300": "grave", "\u0302": "circunflexo", "\u0303": "til", "\u0327": "cedilha"}


def texto(d, xy, s, tam=16, cor=TINTA, ancora="la"):
    """Escreve com a fonte embutida do Pillow (só tem letras sem acento) e risca os acentos por cima."""
    f = fonte(tam)
    letras, marcas = [], []  # marcas: (posição da letra, tipo de acento)
    for ch in s:
        dec = unicodedata.normalize("NFD", ch)
        if len(dec) > 1 and all(c in ACENTOS for c in dec[1:]):
            marcas.extend((len(letras), ACENTOS[c]) for c in dec[1:])
            letras.append(dec[0])
        else:
            letras.append(ch)
    base = "".join(letras)
    a = d.textbbox(xy, base, font=f, anchor=ancora)
    b = d.textbbox((0, 0), base, font=f, anchor="ls")
    ox, oy = a[0] - b[0], a[1] - b[1]  # começo da linha de base
    esp = max(1, int(round(tam / 13.0)))
    w, h, folga = tam * 0.17, tam * 0.20, tam * 0.10

    def acentos(dx, dy, tinta):
        for i, tipo in marcas:
            cx = ox + f.getlength(base[:i]) + f.getlength(base[i]) / 2.0 + dx
            topo = oy + f.getbbox(base[i], anchor="ls")[1] - folga + dy
            if tipo == "agudo":
                pts = [(cx - w * 0.6, topo), (cx + w * 0.8, topo - h)]
            elif tipo == "grave":
                pts = [(cx + w * 0.6, topo), (cx - w * 0.8, topo - h)]
            elif tipo == "circunflexo":
                pts = [(cx - w * 1.1, topo), (cx, topo - h), (cx + w * 1.1, topo)]
            elif tipo == "til":
                pts = [(cx - w * 1.3, topo - h * 0.2), (cx - w * 0.5, topo - h * 0.8), (cx + w * 0.5, topo - h * 0.2),
                       (cx + w * 1.3, topo - h * 0.8)]
            else:  # cedilha
                pts = [(cx + w * 0.3, oy + dy + 1), (cx - w * 0.5, oy + dy + 1 + h)]
            d.line([(round(x), round(y)) for x, y in pts], fill=tinta, width=esp)

    d.text((xy[0] + 1, xy[1] + 1), base, font=f, fill=(0, 0, 0), anchor=ancora)
    acentos(1, 1, (0, 0, 0))
    d.text(xy, base, font=f, fill=cor, anchor=ancora)
    acentos(0, 0, cor)


def folha(glifos16, glifos128):
    CW, CH, M = 144, 232, 30
    larg = M * 2 + 11 * CW + 40
    alt_grade = 5 * CH
    y_grade = 104
    y_ex = y_grade + alt_grade + 96
    lado = LADO_JOGO * 16
    alt = y_ex + lado * 4 + 50
    pedra = pedra_16()
    img = ladrilha(pedra, larg, alt, 4, brilho=0.62).convert("RGBA")
    d = ImageDraw.Draw(img)
    bloco4 = amplia(pedra, 4).convert("RGBA")

    texto(d, (M, 18), "SIGILOS DO GABINETE DO LIMIAR  ·  v1  ·  PRÉVIA (nada disto está no jogo)", 26)
    texto(d, (M, 54), "Em cada casa: o sigilo grande (128x128), o nome, e embaixo a versão do chão (16x16) "
          "ampliada 4x sem borrar e em tamanho real num bloco.", 15, APAGADO)

    def casa(x, y, nome, rot, cor=None):
        img.alpha_composite(glifos128[nome], (x + 8, y))
        if cor:
            tw = d.textlength(rot, font=fonte(17))
            x0 = x + CW / 2 - (tw + 16) / 2
            d.rectangle([x0, y + 134, x0 + 10, y + 144], fill=cor, outline=(200, 196, 184))
            texto(d, (x0 + 16, y + 139), rot, 17, ancora="lm")
        else:
            texto(d, (x + CW // 2, y + 139), rot, 18, ancora="mm")
        bx, by = x + 22, y + 154
        img.paste(bloco4, (bx, by))
        img.alpha_composite(amplia(glifos16[nome], 4), (bx, by))
        d.rectangle([bx - 1, by - 1, bx + 64, by + 64], outline=(16, 16, 18))
        rx, ry = x + 104, y + 154 + 48
        img.paste(pedra, (rx, ry))
        img.alpha_composite(glifos16[nome], (rx, ry))
        d.rectangle([rx - 1, ry - 1, rx + 16, ry + 16], outline=(16, 16, 18))

    # alfabeto: uma família por linha
    texto(d, (M, y_grade - 24), "ALFABETO  ·  cada linha é uma família: a vogal e as consoantes que vêm depois dela",
          17)
    for li, fam in enumerate(FAMILIAS):
        for ci, ch in enumerate(fam):
            rot = ch if ci else "%s  (%s)" % (ch, NOMES_BASE[ch])
            casa(M + ci * CW, y_grade + li * CH, "letra_" + ch.lower(), rot)

    # lado direito: dígitos, elementos, tipos de ritual, regra
    xd = M + 6 * CW + 40
    texto(d, (xd, y_grade - 24), "DÍGITOS  ·  a mesma conta, pendurada na Trave", 17)
    for n in range(10):
        casa(xd + (n % 5) * CW, y_grade + (n // 5) * CH, "digito_%d" % n, str(n))
    y = y_grade + 2 * CH + 26
    texto(d, (xd, y - 6), "ELEMENTOS", 17, ancora="lb")
    for i, k in enumerate(ELEMENTOS):
        casa(xd + i * CW, y, "elemento_" + k, ROTULO[k], COR_ELEMENTO[k])
    y = y_grade + 3 * CH + 52
    texto(d, (xd, y - 6), "TIPOS DE RITUAL", 17, ancora="lb")
    for i, k in enumerate(RITUAIS):
        casa(xd + i * CW, y, "ritual_" + k, ROTULO[k])
    y = y_grade + 4 * CH + 66
    texto(d, (xd, y), "A REGRA", 17)
    regra = [
        "1. Três gestos só: risco, farpa (risco curto) e ponto.",
        "2. As 5 vogais são os 5 traços-base puros (Telhado, Forquilha, Haste, Roda, Cálice).",
        "3. Consoante = traço-base da vogal anterior + marcas que contam a distância até ela.",
        "4. Farpa vale 1, ponto vale 3.  B = A + farpa.  D = A + ponto.  N = I + ponto + 2 farpas.",
        "5. Dígitos: a mesma conta pendurada na Trave. Zero = trave com argola vazia.",
        "Elementos e tipos de ritual: desenho livre, com os mesmos três gestos.",
    ]
    for i, s in enumerate(regra):
        texto(d, (xd, y + 30 + i * 24), s, 15, APAGADO if i == 5 else TINTA)

    # exemplo de círculo
    texto(d, (M, y_ex - 34), "EXEMPLO  ·  círculo de %s com a palavra %s (lê-se a partir do ponto, no sentido do "
          "relógio)" % (ROTULO[ELEMENTO_EXEMPLO].upper(), PALAVRA), 17)
    img.alpha_composite(circulo_grande(560), (M, y_ex + 8))
    texto(d, (M + 280, y_ex + lado * 4 + 8), "riscado grande (livro, tela de enigma)", 15, APAGADO, "ma")

    jogo = circulo_jogo()
    xj = M + 560 + 60
    chao = ladrilha(pedra, lado, lado, 1).convert("RGBA")
    chao.alpha_composite(jogo)
    amp = amplia(chao, 4)
    img.paste(amp, (xj, y_ex))
    fim = lado * 4 - 1
    for i in range(LADO_JOGO + 1):  # divisa dos blocos, bem fraca
        d.line([xj + i * 64, y_ex, xj + i * 64, y_ex + fim], fill=(20, 20, 22, 255))
        d.line([xj, y_ex + i * 64, xj + fim, y_ex + i * 64], fill=(20, 20, 22, 255))
    texto(d, (xj + lado * 2, y_ex + lado * 4 + 8), "no chão do jogo: %dx%d blocos, um sigilo por bloco (ampliado 4x)"
          % (LADO_JOGO, LADO_JOGO), 15, APAGADO, "ma")
    xr = xj + lado * 4 + 50
    img.paste(chao, (xr, y_ex))
    d.rectangle([xr - 1, y_ex - 1, xr + lado, y_ex + lado], outline=(16, 16, 18))
    texto(d, (xr + lado // 2, y_ex + lado + 8), "tamanho real", 15, APAGADO, "ma")
    texto(d, (xr, y_ex + 230), "Pedra de fundo feita pelo script", 14, APAGADO)
    texto(d, (xr, y_ex + 250), "(não é a textura do Minecraft).", 14, APAGADO)
    texto(d, (xr, y_ex + 280), "Sistema próprio do mod: nenhum", 14, APAGADO)
    texto(d, (xr, y_ex + 300), "sigilo vem de Ordem Paranormal.", 14, APAGADO)
    return img.convert("RGB")


# ---------------------------------------------------------------------------------------------------------------
def gera(saida_arte, saida_folha):
    """Gera tudo. Devolve {caminho relativo: md5}."""
    md5 = {}

    def salva(img, pasta, rel):
        caminho = os.path.join(pasta, rel)
        os.makedirs(os.path.dirname(caminho), exist_ok=True)
        img.save(caminho, "PNG", optimize=False)
        with open(caminho, "rb") as f:
            md5[rel] = hashlib.md5(f.read()).hexdigest()

    g16, g128 = {}, {}
    for nome, _rot, gestos in todos_os_glifos():
        g16[nome] = glifo_16(nome, gestos)
        g128[nome] = giz_grande(gestos, 128, nome)
        salva(g16[nome], saida_arte, os.path.join("16", nome + ".png"))
        salva(g128[nome], saida_arte, os.path.join("128", nome + ".png"))
    salva(folha(g16, g128), saida_folha, "sigilos-v1.png")
    return md5


def main(argv):
    if "--conferir" in argv:
        with tempfile.TemporaryDirectory() as a, tempfile.TemporaryDirectory() as b:
            m1, m2 = gera(a, a), gera(b, b)
        dif = sorted(k for k in m1 if m1[k] != m2.get(k))
        print("%d arquivos, %d diferentes entre as duas rodadas" % (len(m1), len(dif)))
        print("md5 do conjunto: " + hashlib.md5("".join(k + m1[k] for k in sorted(m1)).encode()).hexdigest())
        print("md5 da folha:    " + m1["sigilos-v1.png"])
        for k in dif:
            print("DIFERENTE: " + k)
        return 1 if dif else 0
    if "--saida" in argv:
        pasta = argv[argv.index("--saida") + 1]
        md5 = gera(pasta, pasta)
    else:
        md5 = gera(os.path.join(RAIZ, "arte", "sigilos-v1"), os.path.join(RAIZ, "docs", "previas"))
    print("%d arquivos gerados" % len(md5))
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
