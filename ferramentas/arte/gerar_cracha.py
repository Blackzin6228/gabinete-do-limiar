"""Gera as texturas do crachá do Gabinete do Limiar (aprovadas pelo Nikolas em 15/11/2026).
Uso: python3 gerar_cracha.py <pasta_saida>   (precisa de Pillow)
Saída: cracha.png (ícone 32x32), cracha_cartao.png (lotação borrada), cracha_cartao_limpo.png (o jogo escreve o
elemento), cracha_cartao_mestre.png (foto em branco). Cartões 160x100. Nome e lotação são desenhados pelo jogo."""
import random, sys
from PIL import Image, ImageDraw, ImageFilter

G = {
    "A": ["01110", "10001", "10001", "11111", "10001", "10001", "10001"],
    "B": ["11110", "10001", "10001", "11110", "10001", "10001", "11110"],
    "C": ["01110", "10001", "10000", "10000", "10000", "10001", "01110"],
    "D": ["11110", "10001", "10001", "10001", "10001", "10001", "11110"],
    "E": ["11111", "10000", "10000", "11110", "10000", "10000", "11111"],
    "G": ["01111", "10000", "10000", "10011", "10001", "10001", "01111"],
    "I": ["111", "010", "010", "010", "010", "010", "111"],
    "L": ["10000", "10000", "10000", "10000", "10000", "10000", "11111"],
    "M": ["10001", "11011", "10101", "10101", "10001", "10001", "10001"],
    "N": ["10001", "11001", "10101", "10011", "10001", "10001", "10001"],
    "O": ["01110", "10001", "10001", "10001", "10001", "10001", "01110"],
    "R": ["11110", "10001", "10001", "11110", "10100", "10010", "10001"],
    "T": ["11111", "00100", "00100", "00100", "00100", "00100", "00100"],
    "a": ["00000", "00000", "01110", "00001", "01111", "10001", "01111"],
    "c": ["00000", "00000", "01110", "10000", "10000", "10001", "01110"],
    "d": ["00001", "00001", "01101", "10011", "10001", "10001", "01111"],
    "e": ["00000", "00000", "01110", "10001", "11111", "10000", "01110"],
    "g": ["00000", "00000", "01111", "10001", "10001", "10011", "01101", "00001", "01110"],
    "m": ["00000", "00000", "11010", "10101", "10101", "10001", "10001"],
    "n": ["00000", "00000", "10110", "11001", "10001", "10001", "10001"],
    "o": ["00000", "00000", "01110", "10001", "10001", "10001", "01110"],
    "p": ["00000", "00000", "10110", "11001", "10001", "11110", "10000", "10000", "10000"],
    "t": ["0100", "0100", "1111", "0100", "0100", "0100", "0011"],
    ".": ["0", "0", "0", "0", "0", "0", "1"],
    " ": ["000", "000", "000", "000", "000", "000", "000"],
}
TIL = ["01010", "10100"]


def glifo(ch):
    if ch == "Ã":
        return G["A"], TIL
    if ch == "Ç":
        return G["C"] + ["00100", "01000"], None
    return G[ch], None


def largura(s):
    return sum(len(glifo(c)[0][0]) + 1 for c in s) - 1


def escrever(d, x, y, s, cor):
    for ch in s:
        g, ac = glifo(ch)
        if ac:
            for j, linha in enumerate(ac):
                for i, b in enumerate(linha):
                    if b == "1":
                        d.point((x + i, y + j - 3), fill=cor)
        for j, linha in enumerate(g):
            for i, b in enumerate(linha):
                if b == "1":
                    d.point((x + i, y + j), fill=cor)
        x += len(g[0]) + 1


W, H = 160, 100
PAPEL = (214, 205, 182)
PAPEL_ESCURO = (188, 178, 152)
FAIXA = (38, 42, 40)
TINTA = (40, 38, 36)
TINTA_CLARA = (110, 104, 92)
SANGUE = (112, 18, 20)


def ruido(img, base, amp):
    px = img.load()
    for y in range(H):
        for x in range(W):
            if random.random() < 1.0:
                d = random.randint(-amp, amp)
                px[x, y] = tuple(max(0, min(255, c + d)) for c in base) + (255,)


def emblema(d, x, y, cor):
    """Símbolo do Gabinete: o vão de uma porta (o limiar), a soleira e quem passa."""
    d.line([(x, y + 11), (x, y + 1), (x + 8, y + 1), (x + 8, y + 11)], fill=cor)
    d.line([(x - 2, y + 12), (x + 10, y + 12)], fill=cor)
    d.line([(x + 4, y + 4), (x + 4, y + 9)], fill=cor)
    d.point((x + 4, y + 3), fill=cor)


def cartao(mestre=False, limpo=False):
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle([0, 0, W - 1, H - 1], radius=6, fill=PAPEL + (255,))
    mascara = img.split()[3]
    ruido(img, PAPEL, 6)
    img.putalpha(mascara)
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, W - 1, 22], fill=FAIXA + (255,))
    img.putalpha(mascara)
    d = ImageDraw.Draw(img)
    emblema(d, 8, 4, (196, 186, 160))
    escrever(d, 26, 4, "GABINETE DO LIMIAR", (220, 210, 186, 255))
    escrever(d, 26, 13, "agente de campo", (140, 134, 118, 255))
    d.rounded_rectangle([72, 25, 87, 28], radius=1, fill=(70, 66, 58, 255))
    d.rectangle([8, 32, 47, 85], fill=(120, 122, 118, 255), outline=(60, 58, 54, 255))
    if mestre:
        d.rectangle([9, 33, 46, 84], fill=(232, 228, 214, 255))
        for _ in range(50):
            d.point((random.randint(10, 45), random.randint(34, 83)), fill=(210, 204, 186, 255))
    else:
        foto = Image.new("RGBA", (38, 52), (128, 130, 124, 255))
        fd = ImageDraw.Draw(foto)
        fd.ellipse([12, 8, 25, 23], fill=(64, 62, 60, 255))
        fd.rounded_rectangle([3, 25, 34, 56], radius=10, fill=(58, 56, 54, 255))
        foto = foto.filter(ImageFilter.GaussianBlur(1.0))
        img.alpha_composite(foto, (9, 33))
        d = ImageDraw.Draw(img)
    escrever(d, 55, 34, "NOME", TINTA_CLARA + (255,))
    d.line([(55, 55), (151, 55)], fill=TINTA_CLARA + (255,))
    escrever(d, 55, 63, "LOTAÇÃO", TINTA_CLARA + (255,))
    d.line([(55, 82), (151, 82)], fill=TINTA_CLARA + (255,))
    if not limpo:
        for x in range(57, 110):
            if random.random() < 0.7:
                d.point((x, 75 + random.randint(0, 3)), fill=(150, 142, 124, 255))
    lema = "ATENDE. ANOTA. NÃO COMENTA."
    escrever(d, (W - largura(lema)) // 2, 89, lema, TINTA + (255,))
    brilho = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    ImageDraw.Draw(brilho).polygon([(92, 0), (110, 0), (52, H), (34, H)], fill=(255, 255, 255, 20))
    brilho.putalpha(Image.composite(brilho.split()[3], Image.new("L", (W, H), 0), mascara))
    img.alpha_composite(brilho)
    d = ImageDraw.Draw(img)
    for (x, y) in [(W - 3, H - 4), (W - 4, H - 3), (W - 5, H - 3), (W - 3, H - 5), (W - 6, H - 3)]:
        d.point((x, y), fill=PAPEL_ESCURO + (255,))
    for (x, y, c) in [(44, 31, SANGUE), (45, 31, SANGUE), (45, 32, (90, 14, 16)), (44, 30, (130, 30, 30)), (45, 34, (100, 20, 22))]:
        d.point((x, y), fill=c + (255,))
    return img


def icone():
    img = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.line([(9, 0), (14, 9)], fill=(70, 30, 28, 255), width=2)
    d.line([(22, 0), (17, 9)], fill=(70, 30, 28, 255), width=2)
    d.rectangle([14, 8, 17, 11], fill=(120, 118, 112, 255))
    d.rounded_rectangle([5, 11, 26, 30], radius=2, fill=PAPEL + (255,), outline=(60, 56, 50, 255))
    d.rectangle([6, 12, 25, 15], fill=FAIXA + (255,))
    d.point((8, 13), fill=(196, 186, 160, 255))
    d.point((10, 13), fill=(196, 186, 160, 255))
    d.line([(12, 13), (23, 13)], fill=(150, 144, 128, 255))
    d.rectangle([7, 17, 13, 25], fill=(96, 96, 92, 255))
    d.ellipse([9, 18, 11, 20], fill=(58, 56, 54, 255))
    d.rectangle([8, 21, 12, 25], fill=(58, 56, 54, 255))
    for yy in (18, 21, 24):
        d.line([(15, yy), (24, yy)], fill=TINTA_CLARA + (255,))
    d.line([(7, 28), (24, 28)], fill=TINTA + (255,))
    d.point((24, 26), fill=SANGUE + (255,))
    d.line([(18, 12), (13, 29)], fill=(255, 255, 255, 40))
    return img


if __name__ == "__main__":
    out = sys.argv[1] if len(sys.argv) > 1 else "."
    random.seed(7); cartao().save(f"{out}/cracha_cartao.png")
    random.seed(7); cartao(limpo=True).save(f"{out}/cracha_cartao_limpo.png")
    random.seed(7); cartao(mestre=True).save(f"{out}/cracha_cartao_mestre.png")
    icone().save(f"{out}/cracha.png")
    print("ok")
