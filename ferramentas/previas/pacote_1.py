"""Pacote 1 do Gabinete do Limiar: texturas 16x16 das 4 espadas, dos 12 artefatos e do giz (PRÉVIA).

Nomes e IDs: `docs/nomes-pacote-1.md` (reforma de estilo de 15/11/2026). O objeto de cada peça continua o de
`docs/reliquias-v1.md`; mudou o nome, o ID e o nome do arquivo. Cada peça guarda o nome antigo só como semente do
desgaste: assim quem não mudou de desenho sai igual, pixel a pixel, ao que já estava na prévia anterior.
Arte própria: cada peça é uma grade de 16 linhas de texto, pintada com a paleta abaixo. Nada vem de Ordem Paranormal
nem de textura da Mojang (pedra, slots e hotbar da folha também são desenhados aqui).

Uso:
  python3 ferramentas/previas/pacote_1.py              gera as 17 texturas em assets/odteste/textures/item/
                                                       e a folha docs/previas/pacote-1.png
  python3 ferramentas/previas/pacote_1.py --saida DIR  gera tudo dentro de DIR (para comparar rodadas)
  python3 ferramentas/previas/pacote_1.py --conferir   gera duas vezes em pastas temporárias e compara os md5

Precisa de Pillow e numpy, e do `sigilos_v1.py` e do `sigilos_v2.py` ao lado (a folha mostra os sigilos v2; os
arquivos deles saem do `sigilos_v2.py`). Determinístico: seed fixa por nome (crc32). O md5 das texturas não
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
import sigilos_v2 as s2  # noqa: E402

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
    "x": (172, 198, 206), "z": (100, 140, 170), "Z": (62, 88, 130),  # chama azul, fraca (era um azul de magia)
    "a": (178, 198, 192), "A": (124, 148, 148),  # vidro
    "n": (216, 122, 42),  # fio laranja
    "q": (140, 132, 214),  # roxo-azulado elétrico, apagado (era ciano vivo)
    "l": (198, 208, 218), "s": (150, 164, 182), "S": (106, 120, 142), "d": (66, 78, 98),  # aço azulado
    "m": (72, 28, 26),  # líquido escuro
    "i": (158, 152, 140), "f": (118, 112, 104), "F": (80, 76, 72),  # plástico cinza
    "p": (226, 221, 206, 110),  # pó de giz
}
SEM_DESGASTE = set("o.pqxzZ")  # contorno, pó e luz não sujam

# (ID novo = nome do arquivo, nome novo, elemento, o que é, nome antigo = semente do desgaste, grade)
PECAS = [
    ("facao_de_ritual_de_sangue", "Facão de Ritual de Sangue", "sangue", "facão de desossa", "sede_de_domingo", [
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
    # reforma de estilo: a guarda dourada virou latão escurecido (era a peça mais "espada de fantasia")
    ("sabre_de_ritual_de_morte", "Sabre de Ritual de Morte", "morte", "sabre de cerimônia", "sabre_do_ultimo_adeus", [
        "................",
        ".............oo.",
        "............oKKo",
        "...........oKko.",
        "..........o12o..",
        ".........o12o...",
        "........o12o....",
        ".......o13o.....",
        "..oo..o12o......",
        ".oGgoo12o.......",
        "..oBG23o........",
        "...oGgBo........",
        "..oboBGGo.......",
        ".obBo.oBo.......",
        "obBo...o........",
        ".oo.............",
    ]),
    # reforma de estilo: a lâmina azulada e brilhante virou aço comum, sujo
    ("espada_de_ritual_de_conhecimento", "Espada de Ritual de Conhecimento", "conhecimento", "espada de fórum",
     "sentenca_sem_recurso", [
        "................",
        ".............oo.",
        "...........oo12o",
        "..........o123o.",
        ".........o143o..",
        "........o123o...",
        ".......o143o....",
        "......o123o.....",
        "..oo.o143o......",
        ".ohbo123o.......",
        "..obBb3o........",
        "...oBbBo........",
        "..oboBbho.......",
        ".obBo.oo........",
        "obBo............",
        ".oo.............",
    ]),
    ("espeto_de_ritual_de_energia", "Espeto de Ritual de Energia", "energia", "espeto de churrasco",
     "espeto_da_casa_cheia", [
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
    ("seringa_de_coleta_ritual", "Seringa de Coleta Ritual", "sangue", "seringa de vidro", "seringa_velha", [
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
    ("luva_de_abate_ritual", "Luva de Abate Ritual", "sangue", "luva de borracha", "luva_de_acougueiro", [
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
    ("medalha_do_sangue_doado", "Medalha do Sangue Doado", "sangue", "medalha com gota", "medalha_de_doador", [
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
    ("oculos_de_visao_paranormal", "Óculos de Visão Paranormal", "morte", "óculos de grau", "oculos_de_velorio", [
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
    ("moeda_de_obito", "Moeda de Óbito", "morte", "moeda sobre o olho", "moeda_de_defunto", [
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
    ("vela_de_rito_funebre", "Vela de Rito Fúnebre", "morte", "toco de vela", "vela_de_setimo_dia", [
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
    ("lupa_de_investigacao_paranormal", "Lupa de Investigação Paranormal", "conhecimento", "lupa de latão",
     "lupa_do_arquivo_morto", [
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
    ("caneta_de_registro_apagado", "Caneta de Registro Apagado", "conhecimento", "caneta seca", "caneta_sem_tinta", [
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
    ("fita_de_gravacao_anomala", "Fita de Gravação Anômala", "conhecimento", "fita cassete", "fita_sem_etiqueta", [
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
    ("celular_de_sinal_anomalo", "Celular de Sinal Anômalo", "energia", "celular rachado", "uma_barra_de_sinal", [
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
    ("fusivel_sobrecarregado", "Fusível Sobrecarregado", "energia", "fusível de vidro", "fusivel_queimado", [
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
    ("radio_de_interferencia", "Rádio de Interferência", "energia", "rádio pequeno", "radinho_de_pilha", [
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
    ("giz", "Giz Ritualístico", None, "giz branco, gasto", "giz", [
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


def textura(nome, grade, semente=None):
    """Pinta a grade. O desgaste (seed fixa pelo nome ANTIGO da peça) mexe um pouco no tom e escurece alguns
    pixels: sujeira."""
    assert len(grade) == 16 and all(len(l) == 16 for l in grade), "grade de %s não é 16x16" % nome
    rng = random.Random(sig.semente("pacote1:" + (semente or nome)))
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


def no_chao(tex, escala, margem):
    """A textura 32x32 do sigilo num bloco de pedra, ampliada `escala` vezes sem borrar. O bloco de pedra tem 16
    pixels de lado e o sigilo 32: cada pixel de pedra vale dois de sigilo, como vai ser no jogo. `margem` (em pixels
    da folha) mostra um pedaço dos blocos vizinhos, com a divisa marcada de leve."""
    bloco = 32 * escala
    pedra = sig.amplia(s2.pedra_chao(), 2 * escala)
    lado = bloco + 2 * margem
    img = Image.new("RGBA", (lado, lado))
    for by in (-1, 0, 1):
        for bx in (-1, 0, 1):
            img.paste(pedra, (margem + bx * bloco, margem + by * bloco))
    img.alpha_composite(sig.amplia(tex, escala), (margem, margem))
    if margem:
        d = ImageDraw.Draw(img)
        for v in (margem - 1, margem + bloco):
            d.line([(v, 0), (v, lado - 1)], fill=(58, 58, 62, 255))
            d.line([(0, v), (lado - 1, v)], fill=(58, 58, 62, 255))
    return img


def folha(texturas, sigs):
    """`texturas`: {id: imagem 16x16}. `sigs`: {arquivo: imagem 32x32} do sigilos_v2."""
    M, larg = 38, 1684
    pedra = sig.pedra_16()
    img = sig.ladrilha(pedra, larg, 4600, 4, brilho=0.62).convert("RGBA")
    d = ImageDraw.Draw(img)
    T = sig.texto
    borda = (16, 16, 18)

    def cola(peca, x, y):
        img.paste(peca.convert("RGB"), (x, y))
        d.rectangle([x - 1, y - 1, x + peca.width, y + peca.height], outline=borda)

    def cabe(s, largura, tam, minimo=12):
        while tam > minimo and d.textlength(s, font=sig.fonte(tam)) > largura:
            tam -= 1
        return tam

    def divisa(y):
        d.rectangle([0, y, larg, y + 3], fill=borda)

    T(d, (M, 18), "PACOTE 1 DO GABINETE DO LIMIAR  ·  REFORMA DE ESTILO  ·  PRÉVIA (nada disto está no jogo)", 26)
    T(d, (M, 54), "Esta folha substitui a anterior. Nomes novos em tudo. Sigilos refeitos: riscados à mão, sujos, "
      "com o círculo que não fecha. Nada de runa nem de régua.", 15, sig.APAGADO)

    # 1. sigilos de elemento, grandes, no chão
    y = 100
    T(d, (M, y), "SIGILOS DE ELEMENTO  ·  a textura de verdade (32x32), ampliada sem borrar, riscada num bloco de "
      "pedra  ·  um de cada cor", 17)
    y += 34
    PW, lado = (larg - 2 * M) // 5, 32 * 8 + 2 * 24
    for i, el in enumerate(s2.ELEMENTOS):
        x = M + i * PW
        cola(no_chao(sigs["sigilo_de_" + el], 8, 24), x + (PW - lado) // 2, y)
        T(d, (x + PW // 2, y + lado + 22), "Sigilo de " + s2.ROTULO[el], 20, ancora="mm")
        rot = "sigilo_de_" + el
        tw = d.textlength(rot, font=sig.fonte(14))
        x0 = x + PW / 2 - (tw + 16) / 2
        d.rectangle([x0, y + lado + 40, x0 + 10, y + lado + 50], fill=s2.COR_ELEMENTO[el], outline=(200, 196, 184))
        T(d, (x0 + 16, y + lado + 45), rot, 14, sig.APAGADO, "lm")
        # embaixo: a mesma forma em giz, e os dois sem ampliar
        yy = y + lado + 70
        giz, cor1, giz1 = (no_chao(sigs[rot + "_giz"], 3, 8), no_chao(sigs[rot], 1, 8),
                           no_chao(sigs[rot + "_giz"], 1, 8))
        xx = x + (PW - (giz.width + 20 + cor1.width + 8 + giz1.width)) // 2
        cola(giz, xx, yy)
        cola(cor1, xx + giz.width + 20, yy + giz.height - cor1.height)
        cola(giz1, xx + giz.width + 20 + cor1.width + 8, yy + giz.height - giz1.height)
        T(d, (xx + giz.width // 2, yy + giz.height + 14), "o mesmo, em giz", 14, sig.APAGADO, "mm")
        T(d, (xx + giz.width + 20 + cor1.width + 4, yy + giz.height + 14), "sem ampliar", 14, sig.APAGADO, "mm")
    y += lado + 70 + 112 + 44

    # 2. itens em tamanho real
    divisa(y)
    y += 24
    nomes = [p[0] for p in PECAS]
    T(d, (M, y), "ITENS NA HOTBAR  ·  as 17 peças em tamanho real, na hotbar e no inventário "
      "(slots desenhados pelo script, não são a textura do Minecraft)", 17)
    y += 40
    legenda = {1: "escala 1 da interface: 1 pixel da peça = 1 pixel desta folha",
               2: "escala 2 da interface (cada pixel dobrado)",
               3: "escala 3 da interface (o mais comum em monitor Full HD)"}
    for n in (3, 2, 1):
        hot = sig.amplia(tira(texturas, nomes, slot_hotbar(), grupo=9), n)
        inv = sig.amplia(tira(texturas, nomes, slot_inventario()), n)
        img.alpha_composite(hot, (M, y))
        img.alpha_composite(inv, (M, y + hot.height + 4))
        T(d, (M + max(hot.width, inv.width) + 24, y + hot.height), legenda[n], 15, sig.APAGADO, "lm")
        y += hot.height + inv.height + 36

    # 3. itens ampliados, com o nome novo
    divisa(y)
    y += 24
    CW, CH, AMP = 268, 276, 10
    T(d, (M, y), "ITENS AMPLIADOS 10x  ·  nome novo, ID novo (é o nome do arquivo) e o objeto", 17)
    T(d, (M, y + 26), "Mesmo desenho de antes, com nome novo. Só mudaram: Sabre (guarda sem dourado), Espada (lâmina "
      "sem azul brilhante), chama da Vela e luz de Energia (mais apagadas).", 15, sig.APAGADO)
    y += 96

    def casa(x, y, peca):
        arq, nome, elem, oque = peca[:4]
        px, lado = x + (CW - 176) // 2, 176
        d.rectangle([px, y, px + lado - 1, y + lado - 1], fill=(139, 139, 139), outline=borda)
        d.line([(px + 1, y + 1), (px + lado - 2, y + 1)], fill=(84, 84, 84))
        d.line([(px + 1, y + 1), (px + 1, y + lado - 2)], fill=(84, 84, 84))
        img.alpha_composite(sig.amplia(texturas[arq], AMP), (px + 8, y + 8))
        T(d, (x + CW // 2, y + lado + 18), nome, cabe(nome, CW - 10, 18), ancora="mm")
        T(d, (x + CW // 2, y + lado + 38), arq, cabe(arq, CW - 10, 13, 10), sig.APAGADO, "mm")
        sub = (ROTULO_ELEMENTO[elem] + "  ·  " if elem else "") + oque
        if elem:
            tw = d.textlength(sub, font=sig.fonte(14))
            x0 = x + CW / 2 - (tw + 16) / 2
            d.rectangle([x0, y + lado + 51, x0 + 10, y + lado + 61], fill=s2.COR_ELEMENTO[elem],
                        outline=(200, 196, 184))
            T(d, (x0 + 16, y + lado + 56), sub, 14, sig.APAGADO, "lm")
        else:
            T(d, (x + CW // 2, y + lado + 56), sub, 14, sig.APAGADO, "mm")

    T(d, (M, y - 30), "ESPADAS (relíquias)", 17)
    T(d, (M + 4 * CW + 40, y - 30), "GIZ", 17)
    for i, p in enumerate(PECAS[:4]):
        casa(M + i * CW, y, p)
    casa(M + 4 * CW + 40, y, PECAS[16])
    y += CH + 40
    T(d, (M, y - 30), "ARTEFATOS  ·  três por elemento", 17)
    for i, p in enumerate(PECAS[4:16]):
        casa(M + (i % 6) * CW, y + (i // 6) * CH, p)
    y += 2 * CH + 16

    # 4. alfabeto e dígitos
    divisa(y)
    y += 24
    T(d, (M, y), "ALFABETO E DÍGITOS  ·  os mesmos três gestos (risco, farpa e ponto), agora riscados à mão  ·  "
      "giz, 32x32, ampliado 3x num bloco de pedra", 17)
    T(d, (M, y + 26), "A regra não mudou: as 5 vogais são os traços-base; cada consoante é a vogal anterior mais "
      "marcas (farpa vale 1, ponto vale 3). Dígitos: a mesma conta pendurada na Trave.", 15, sig.APAGADO)
    y += 62
    glifos = [chr(65 + i) for i in range(26)] + [str(n) for n in range(10)]
    CL, passo = (larg - 2 * M) // 13, 32 * 3 + 2 * 6 + 40
    for i, ch in enumerate(glifos):
        peca = no_chao(sigs["sigilo_" + ch.lower()], 3, 6)
        x, yy = M + (i % 13) * CL, y + (i // 13) * passo
        cola(peca, x + (CL - peca.width) // 2, yy)
        T(d, (x + CL // 2, yy + peca.height + 16), ch, 18, ancora="mm")
    for i, linha in enumerate(["Pedra desenhada pelo script (não é a textura",
                               "do Minecraft). Arte própria: nenhum símbolo",
                               "vem de Ordem Paranormal. Os 5 sigilos de tipo",
                               "de ritual ficam para a próxima etapa."]):
        T(d, (M + 10 * CL + 16, y + 2 * passo + 8 + i * 22), linha, 14, sig.APAGADO)
    y += 3 * passo + 10
    return img.crop((0, 0, larg, y)).convert("RGB")


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
    for arq, _nome, _elem, _oque, antigo, grade in PECAS:
        texturas[arq] = textura(arq, grade, antigo)
        salva(texturas[arq], saida_item, arq + ".png")
    sigs = {arquivo: im for arquivo, _rot, im in s2.sigilos()}
    salva(folha(texturas, sigs), saida_folha, "pacote-1.png")
    return md5


def main(argv):
    if "--conferir" in argv:
        with tempfile.TemporaryDirectory() as a, tempfile.TemporaryDirectory() as b:
            m1 = gera(a, a)
            s2._cache.clear()  # a segunda rodada refaz os sigilos do zero
            m2 = gera(b, b)
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
