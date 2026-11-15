"""Pacote 1 do Gabinete do Limiar: texturas 16x16 das 4 espadas, dos 12 artefatos e do giz (PRÉVIA).

Aparência tirada de `docs/reliquias-v1.md` ("Aparência" das espadas e coluna "Objeto (16x16)" dos artefatos).
Arte própria: cada peça é uma grade de 16 linhas de texto, pintada com a paleta abaixo. Nada vem de Ordem Paranormal
nem de textura da Mojang (pedra, slots e hotbar da folha também são desenhados aqui).

Uso:
  python3 ferramentas/previas/pacote_1.py              gera as 17 texturas em assets/odteste/textures/item/
                                                       e a folha docs/previas/pacote-1.png
  python3 ferramentas/previas/pacote_1.py --saida DIR  gera tudo dentro de DIR (para comparar rodadas)
  python3 ferramentas/previas/pacote_1.py --conferir   gera duas vezes em pastas temporárias e compara os md5

Precisa de Pillow e numpy, e do `sigilos_v1.py` ao lado (a folha traz os sigilos v1 embaixo, para aprovar junto).
Determinístico: o desgaste de cada peça usa seed fixa por nome (crc32, igual aos sigilos). O md5 das texturas não
depende de fonte; o da folha depende da fonte embutida do Pillow (feito com Pillow 12.1.1).
Formato: PNG RGBA 8 bits, 16x16. Luz de cima à esquerda, contorno escuro, um acento de cor por peça.
"""
import hashlib
import os
import random
import sys
import tempfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from PIL import Image, ImageDraw  # noqa: E402

import sigilos_v1 as sig  # noqa: E402

RAIZ = sig.RAIZ
PASTA_ITEM = os.path.join("src", "main", "resources", "assets", "odteste", "textures", "item")

# ---------------------------------------------------------------------------------------------------------------
# Paleta: uma letra por tom. Tons sujos, pouco saturados. Os acentos são r/R (Sangue), h/g (Conhecimento),
# x/z/Z (chama da vela, Morte) e q (Energia).
# ---------------------------------------------------------------------------------------------------------------
PALETA = {
    "o": (26, 22, 24),  # contorno
    "1": (204, 206, 200), "2": (156, 160, 156), "3": (104, 108, 108), "4": (66, 70, 74),  # aço
    "w": (126, 88, 54), "W": (94, 62, 40), "v": (60, 40, 28),  # madeira
    "t": (196, 186, 154), "T": (150, 140, 110),  # fita adesiva
    "r": (134, 26, 26), "R": (92, 14, 18),  # sangue seco
    "h": (224, 196, 112), "g": (190, 150, 62), "G": (138, 102, 40),  # latão / dourado fosco
    "b": (150, 106, 60), "B": (102, 70, 42),  # bronze / couro
    "k": (44, 42, 46), "K": (24, 22, 26),  # preto
    "c": (228, 223, 208), "C": (190, 184, 166), "e": (140, 134, 120),  # branco sujo
    "u": (232, 206, 98), "y": (210, 172, 50), "Y": (160, 124, 34),  # borracha / plástico amarelo
    "x": (176, 228, 240), "z": (92, 170, 222), "Z": (48, 104, 186),  # chama azul
    "a": (178, 198, 192), "A": (124, 148, 148),  # vidro
    "n": (216, 122, 42),  # fio laranja
    "q": (96, 214, 226),  # ciano elétrico
    "l": (198, 208, 218), "s": (150, 164, 182), "S": (106, 120, 142), "d": (66, 78, 98),  # aço azulado
    "m": (72, 28, 26),  # líquido escuro
    "i": (158, 152, 140), "f": (118, 112, 104), "F": (80, 76, 72),  # plástico cinza
    "p": (226, 221, 206, 110),  # pó de giz
}
SEM_DESGASTE = set("o.pqxzZ")  # contorno, pó e luz não sujam

# (arquivo, nome, elemento, o que é, grade)
PECAS = [
    ("sede_de_domingo", "Sede de Domingo", "sangue", "facão de desossa", [
        "................",
        "...........oooo.",
        ".........oo1122o",
        "........o112223o",
        ".......o1122r3o.",
        "......o1122rr3o.",
        ".....o112233o...",
        "....o11223ooo...",
        "....o1223o......",
        "...oo123o.......",
        "..otTo3o........",
        ".oWtTWo.........",
        "oWWvtTo.........",
        "oWvWooo.........",
        ".ooo............",
        "................",
    ]),
    ("sabre_do_ultimo_adeus", "Sabre do Último Adeus", "morte", "sabre de cerimônia", [
        "................",
        ".............oo.",
        "............oKKo",
        "...........oKko.",
        "..........o12o..",
        ".........o12o...",
        "........o12o....",
        ".......o13o.....",
        "..oo..o12o......",
        ".oghoo12o.......",
        "..oGg23o........",
        "...oghGo........",
        "..oboGgho.......",
        ".obBo.oGo.......",
        "obBo...o........",
        ".oo.............",
    ]),
    ("sentenca_sem_recurso", "Sentença Sem Recurso", "conhecimento", "espada de fórum", [
        "................",
        ".............oo.",
        "...........oolso",
        "..........olsSo.",
        ".........oldSo..",
        "........olsSo...",
        ".......oldSo....",
        "......olsSo.....",
        "..oo.oldSo......",
        ".ohbolsSo.......",
        "..obBbSo........",
        "...oBbBo........",
        "..oboBbho.......",
        ".obBo.oo........",
        "obBo............",
        ".oo.............",
    ]),
    ("espeto_da_casa_cheia", "Espeto da Casa Cheia", "energia", "espeto de churrasco", [
        "..............o.",
        ".............oqo",
        "............o1o.",
        "...........o2o..",
        "..........o1o...",
        ".........o2o....",
        "........o1o.....",
        ".......o2o......",
        "......o1o.......",
        ".....o2o........",
        "...ooo3o........",
        "..onWto.........",
        ".onvno..........",
        "oWvno...........",
        "oKvo............",
        ".oo.............",
    ]),
    ("seringa_velha", "Seringa Velha", "sangue", "seringa de vidro", [
        "................",
        "..........ooo...",
        ".........oWWWo..",
        "..........oWWWo.",
        ".........ovooo..",
        ".......oovo.....",
        "......ocaAo.....",
        ".....ocWAo......",
        "....ocmAo.......",
        "...ocrmo........",
        "..oarmo.........",
        ".oo3o...........",
        ".o2o............",
        "o1o.............",
        "2...............",
        "................",
    ]),
    ("luva_de_acougueiro", "Luva de Açougueiro", "sangue", "luva de borracha", [
        "......oo.oo.....",
        "...oooRroRro....",
        "..oRroyRoyRooo..",
        "..oyRouyoyyoRro.",
        "..ouyouyouyoyRo.",
        "..ouyouyouyoyYo.",
        ".oouyyuyyuyyyYo.",
        "oRyouyyyyyyyyYo.",
        "oryyuyyyyyyyYYo.",
        ".oyyyyyyyyyyYo..",
        "..ooyyyyyyyYYo..",
        "...oyyyyyyyYo...",
        "...ouuuuuuuyo...",
        "...oyyyyyyyYo...",
        "...oYYYYYYYYo...",
        "....oooooooo....",
    ]),
    ("medalha_de_doador", "Medalha de Doador", "sangue", "medalha com gota", [
        "..oooo....oooo..",
        "..oCeCo..oCeCo..",
        "...oCeCooCeCo...",
        "....oCeCCeCo....",
        ".....oCeeCo.....",
        "......oeeo......",
        ".....oo33oo.....",
        "....o122223o....",
        "...o1222r223o...",
        "...o122rr233o...",
        "...o12rrRR34o...",
        "...o22rRRR34o...",
        "...o223RR344o...",
        "....o233344o....",
        ".....o3444o.....",
        "......oooo......",
    ]),
    ("oculos_de_velorio", "Óculos de Velório", "morte", "óculos de grau", [
        "................",
        "................",
        "................",
        "................",
        ".oooooo..oooooo.",
        "oKkkkkKooKkkkkKo",
        "okAaaAkKKkAacAko",
        "okaaAAkookAcAAko",
        "okaAAAkookcAcAko",
        "okAAAAkookAAcAko",
        "oKkkkkKooKkkkkKo",
        ".oooooo..oooooo.",
        "................",
        "................",
        "................",
        "................",
    ]),
    ("moeda_de_defunto", "Moeda de Defunto", "morte", "moeda sobre o olho", [
        "................",
        "......oooo......",
        "....oo2233oo....",
        "...o22333333o...",
        "...o23344443o...",
        "..o2334444443o..",
        ".oo234333344koo.",
        "oco2343kk344koco",
        ".oo3343kk344koo.",
        "..o334333344ko..",
        "...o3444444ko...",
        "...o344444kko...",
        "....oo4kkkoo....",
        "......oooo......",
        "................",
        "................",
    ]),
    ("vela_de_setimo_dia", "Vela de Sétimo Dia", "morte", "toco de vela", [
        "................",
        ".......o........",
        "......oxo.......",
        "......ozxo......",
        ".....ozxzo......",
        ".....oZzZo......",
        "......oZo.......",
        ".......k........",
        "....oookooo.....",
        "...occccCCeo....",
        "...ocCccCCeo....",
        "..oocccCCCeo....",
        "..occccCCeeo....",
        "..oocccCCCeo....",
        "..occcCCCeeeo...",
        "..ooooooooooo...",
    ]),
    ("lupa_do_arquivo_morto", "Lupa do Arquivo Morto", "conhecimento", "lupa de latão", [
        "....oooo........",
        "..oohgggoo......",
        ".ohgaaaagGo.....",
        ".ogacaaAAGo.....",
        "ohaccaaAAAGo....",
        "ogacaaAAAAGo....",
        "ogaaaAAAAAGo....",
        "ogaaAAAAAaGo....",
        ".ogAAAAAaGo.....",
        ".oGGAAAaGGo.....",
        "..ooGGGGoBo.....",
        "....oooooBbo....",
        ".........obBo...",
        "..........obBo..",
        "...........obBo.",
        "............ooo.",
    ]),
    ("caneta_sem_tinta", "Caneta Sem Tinta", "conhecimento", "caneta seca", [
        "................",
        "............o.o.",
        "...........oKoKo",
        "..........oKkKo.",
        ".........oKkKo..",
        "........oukKo...",
        ".......ouyYo....",
        "......ouyYo.....",
        ".....ouyYo......",
        "....ouyYo.......",
        "...ouyYo........",
        "..oeyYo.........",
        "..oCeo..........",
        ".oGo............",
        ".o..............",
        "................",
    ]),
    ("fita_sem_etiqueta", "Fita Sem Etiqueta", "conhecimento", "fita cassete", [
        "................",
        "................",
        "................",
        ".oooooooooooooo.",
        "oFfffffffffhgFFo",
        "ofCCCCCCCCChgCFo",
        "ofCcccccccchgCFo",
        "ofCokkkkkkohgCFo",
        "ofCokckkckohgCFo",
        "ofCokkkkkkhgcCFo",
        "ofCCCCCCCChgCCFo",
        "oFFFo3fff3hgFFFo",
        ".oooooooooooooo.",
        "................",
        "................",
        "................",
    ]),
    ("uma_barra_de_sinal", "Uma Barra de Sinal", "energia", "celular rachado", [
        "....oooooooo....",
        "...oyuuuuuuYo...",
        "...oukkkkkkYo...",
        "...oyKKKKqKYo...",
        "...oyKKeKKKYo...",
        "...oyKeKKKKYo...",
        "...oyKKeKKKYo...",
        "...oyKKKeeKYo...",
        "...oyKKKeKKYo...",
        "...oyKKeKKKYo...",
        "...oyKKKKKKYo...",
        "...oyKKKKKKYo...",
        "...oykkkkkkYo...",
        "...oyyYooYYYo...",
        "...oYYYYYYYYo...",
        "....oooooooo....",
    ]),
    ("fusivel_queimado", "Fusível Queimado", "energia", "fusível de vidro", [
        "................",
        "...........ooo..",
        "..........o122o.",
        ".........o1223o.",
        "........oa233o..",
        ".......oakAoo...",
        "......oaKAo.....",
        ".....oaKKo......",
        "....oaqAo.......",
        "...oakAo........",
        "..o12Ao.........",
        ".o1223o.........",
        ".o223o..........",
        "..ooo...........",
        "................",
        "................",
    ]),
    ("radinho_de_pilha", "Radinho de Pilha", "energia", "rádio pequeno", [
        ".........2......",
        "..........3.....",
        "...........3....",
        "...........3....",
        "............3...",
        ".ooooooooooo3oo.",
        "oiiiiiiiiiiiiffo",
        "oikFkFkFkifccfFo",
        "oiFkFkFkFifcqfFo",
        "oikFkFkFkifffFFo",
        "oiFkFkFkFifkkfFo",
        "oikFkFkFkifkkFFo",
        "offfffffffffFFFo",
        "oFFFFFFFFFFFFFFo",
        ".oooooooooooooo.",
        "................",
    ]),
    ("giz", "Giz", None, "giz branco, gasto", [
        "................",
        "................",
        "................",
        "................",
        "..........ooo...",
        ".........occCo..",
        "........occCeo..",
        ".......occCeo...",
        "......occCeo....",
        ".....occCeo.....",
        "....occCeo......",
        "...oCcCeo.......",
        "..poCCeo........",
        ".p..ooo.........",
        "...p.p..........",
        "................",
    ]),
]
ROTULO_ELEMENTO = {"sangue": "Sangue", "morte": "Morte", "conhecimento": "Conhecimento", "energia": "Energia"}


def textura(nome, grade):
    """Pinta a grade. O desgaste (seed fixa por nome) mexe um pouco no tom e escurece alguns pixels: sujeira."""
    assert len(grade) == 16 and all(len(l) == 16 for l in grade), "grade de %s não é 16x16" % nome
    rng = random.Random(sig.semente("pacote1:" + nome))
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y, linha in enumerate(grade):
        for x, ch in enumerate(linha):
            if ch == ".":
                continue
            cor = PALETA[ch]
            alfa = cor[3] if len(cor) == 4 else 255
            r, g, b = cor[:3]
            if ch not in SEM_DESGASTE:
                tom = rng.randint(-5, 5)
                if rng.random() < 0.07:
                    tom -= 14
                r, g, b = (max(0, min(255, v + tom)) for v in (r, g, b))
            img.putpixel((x, y), (r, g, b, alfa))
    return img


# ---------------------------------------------------------------------------------------------------------------
# Folha de prévia
# ---------------------------------------------------------------------------------------------------------------
def slot_inventario(n=1):
    """Slot claro de inventário (18x18), desenhado aqui."""
    s = Image.new("RGBA", (18, 18), (139, 139, 139, 255))
    d = ImageDraw.Draw(s)
    d.line([(0, 0), (16, 0)], fill=(55, 55, 55, 255))
    d.line([(0, 0), (0, 16)], fill=(55, 55, 55, 255))
    d.line([(1, 17), (17, 17)], fill=(255, 255, 255, 255))
    d.line([(17, 1), (17, 17)], fill=(255, 255, 255, 255))
    return s


def slot_hotbar():
    """Slot escuro de hotbar (20x20), desenhado aqui."""
    s = Image.new("RGBA", (20, 20), (46, 46, 50, 255))
    d = ImageDraw.Draw(s)
    d.rectangle([0, 0, 19, 19], outline=(18, 18, 20, 255))
    d.rectangle([1, 1, 18, 18], outline=(96, 96, 102, 255))
    return s


def tira(texturas, nomes, slot, grupo=None):
    """Uma fileira de slots com as peças em tamanho real (1 pixel = 1 pixel)."""
    lado = slot.width
    folga = (lado - 16) // 2
    vaos = (len(nomes) - 1) // grupo if grupo else 0
    img = Image.new("RGBA", (lado * len(nomes) + vaos * 6, lado), (0, 0, 0, 0))
    for i, nome in enumerate(nomes):
        x = i * lado + (i // grupo * 6 if grupo else 0)
        img.alpha_composite(slot, (x, 0))
        img.alpha_composite(texturas[nome], (x + folga, folga))
    return img


def folha(texturas, folha_sigilos):
    M, CW, CH, AMP = 38, 268, 262, 10
    larg = folha_sigilos.width
    y_esp = 128
    y_art = y_esp + CH + 44
    y_real = y_art + 2 * CH + 60
    alt_real = 40 + (18 + 20) * (1 + 2 + 3) + 3 * 44
    y_sig = y_real + alt_real + 40
    alt = y_sig + 60 + folha_sigilos.height
    pedra = sig.pedra_16()
    img = sig.ladrilha(pedra, larg, alt, 4, brilho=0.62).convert("RGBA")
    d = ImageDraw.Draw(img)
    T = sig.texto

    T(d, (M, 18), "PACOTE 1 DO GABINETE DO LIMIAR  ·  PRÉVIA (nada disto está no jogo)", 26)
    T(d, (M, 54), "4 espadas, 12 artefatos e o giz, em 16x16. Cada peça ampliada 10x sem borrar, com o nome. "
      "Mais abaixo: todas em tamanho real, e os sigilos v1.", 15, sig.APAGADO)

    def casa(x, y, peca):
        arq, nome, elem, oque, _ = peca
        px, lado = x + (CW - 176) // 2, 176
        d.rectangle([px, y, px + lado - 1, y + lado - 1], fill=(139, 139, 139), outline=(16, 16, 18))
        d.line([(px + 1, y + 1), (px + lado - 2, y + 1)], fill=(84, 84, 84))
        d.line([(px + 1, y + 1), (px + 1, y + lado - 2)], fill=(84, 84, 84))
        img.alpha_composite(sig.amplia(texturas[arq], AMP), (px + 8, y + 8))
        T(d, (x + CW // 2, y + lado + 18), nome, 18, ancora="mm")
        sub = (ROTULO_ELEMENTO[elem] + "  ·  " if elem else "") + oque
        if elem:
            tw = d.textlength(sub, font=sig.fonte(14))
            x0 = x + CW / 2 - (tw + 16) / 2
            d.rectangle([x0, y + lado + 35, x0 + 10, y + lado + 45], fill=sig.COR_ELEMENTO[elem],
                        outline=(200, 196, 184))
            T(d, (x0 + 16, y + lado + 40), sub, 14, sig.APAGADO, "lm")
        else:
            T(d, (x + CW // 2, y + lado + 40), sub, 14, sig.APAGADO, "mm")

    T(d, (M, y_esp - 30), "ESPADAS (relíquias)", 17)
    T(d, (M + 4 * CW + 40, y_esp - 30), "GIZ", 17)
    for i, p in enumerate(PECAS[:4]):
        casa(M + i * CW, y_esp, p)
    casa(M + 4 * CW + 40, y_esp, PECAS[16])
    T(d, (M, y_art - 30), "ARTEFATOS  ·  três por elemento", 17)
    for i, p in enumerate(PECAS[4:16]):
        casa(M + (i % 6) * CW, y_art + (i // 6) * CH, p)

    # tamanho real
    nomes = [p[0] for p in PECAS]
    T(d, (M, y_real), "TAMANHO REAL  ·  as 17 peças na ordem de cima, na hotbar e no inventário "
      "(slots desenhados pelo script, não são a textura do Minecraft)", 17)
    y = y_real + 40
    legenda = {1: "escala 1 da interface: 1 pixel da peça = 1 pixel desta folha",
               2: "escala 2 da interface (cada pixel dobrado)",
               3: "escala 3 da interface (o mais comum em monitor Full HD)"}
    for n in (1, 2, 3):
        hot = sig.amplia(tira(texturas, nomes, slot_hotbar(), grupo=9), n)
        inv = sig.amplia(tira(texturas, nomes, slot_inventario()), n)
        img.alpha_composite(hot, (M, y))
        img.alpha_composite(inv, (M, y + hot.height + 4))
        T(d, (M + max(hot.width, inv.width) + 24, y + hot.height), legenda[n], 15, sig.APAGADO, "lm")
        y += hot.height + inv.height + 44

    # sigilos v1 (a folha do PR #10, igual)
    d.rectangle([0, y_sig, larg, y_sig + 3], fill=(16, 16, 18))
    T(d, (M, y_sig + 18), "SIGILOS v1  ·  a mesma folha do PR #10, colada aqui para aprovar tudo junto", 20)
    img.paste(folha_sigilos, (0, y_sig + 60))
    return img.convert("RGB")


# ---------------------------------------------------------------------------------------------------------------
def gera(saida_item, saida_folha):
    """Gera tudo. Devolve {caminho relativo: md5}."""
    md5 = {}

    def salva(img, pasta, rel):
        caminho = os.path.join(pasta, rel)
        os.makedirs(os.path.dirname(caminho), exist_ok=True)
        img.save(caminho, "PNG", optimize=False)
        with open(caminho, "rb") as f:
            md5[rel] = hashlib.md5(f.read()).hexdigest()

    texturas = {}
    for arq, _nome, _elem, _oque, grade in PECAS:
        texturas[arq] = textura(arq, grade)
        salva(texturas[arq], saida_item, arq + ".png")
    g16, g128 = {}, {}
    for nome, _rot, gestos in sig.todos_os_glifos():
        g16[nome] = sig.glifo_16(nome, gestos)
        g128[nome] = sig.giz_grande(gestos, 128, nome)
    salva(folha(texturas, sig.folha(g16, g128)), saida_folha, "pacote-1.png")
    return md5


def main(argv):
    if "--conferir" in argv:
        with tempfile.TemporaryDirectory() as a, tempfile.TemporaryDirectory() as b:
            m1, m2 = gera(a, a), gera(b, b)
        dif = sorted(k for k in m1 if m1[k] != m2.get(k))
        print("%d arquivos, %d diferentes entre as duas rodadas" % (len(m1), len(dif)))
        print("md5 das 17 texturas: " + hashlib.md5(
            "".join(k + m1[k] for k in sorted(m1) if k != "pacote-1.png").encode()).hexdigest())
        print("md5 da folha:        " + m1["pacote-1.png"])
        for k in dif:
            print("DIFERENTE: " + k)
        return 1 if dif else 0
    if "--saida" in argv:
        pasta = argv[argv.index("--saida") + 1]
        md5 = gera(pasta, pasta)
    else:
        md5 = gera(os.path.join(RAIZ, PASTA_ITEM), os.path.join(RAIZ, "docs", "previas"))
    print("%d arquivos gerados" % len(md5))
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
