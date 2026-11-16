#!/usr/bin/env python3
"""Gera os assets do bloco odteste:sigilo e do giz a partir da arte v1 dos sigilos (Modelador, PR #10), em 32x32.

Uso (na raiz do repo, com a pasta arte/sigilos-v1/16 da branch mesa-modelador/sigilos-v1):
    python3 -I ferramentas/gerar_sigilos.py

Escreve em src/main/resources/assets/odteste/: texturas em textures/block/sigilo/, um modelo por sigilo, o
blockstate (simbolo x giro) e o modelo do giz. Cria um giz placeholder se textures/item/giz.png não existir
(a Modelador troca depois). A lista de sigilos vem de IdsSigilos.java: a ordem e os nomes nunca mudam.
"""
import json
import re
import struct
import sys
import zlib
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
ASSETS = RAIZ / "src/main/resources/assets/odteste"
ARTE = RAIZ / "arte/sigilos-v1/16"


def sigilos():
    texto = (RAIZ / "src/main/java/com/nikolas/odteste/sigilos/IdsSigilos.java").read_text(encoding="utf-8")
    bloco = texto.split("SIMBOLOS = List.of(")[1].split(");")[0]
    return re.findall(r'"([a-z_0-9]+)"', bloco)


# Placeholder até a Modelador entregar a arte 32x32 (branch mesa-modelador/pacote-1-texturas): usa o desenho v1 mais
# parecido. Os 4 últimos tipos de ritual não têm arte própria ainda.
RITUAIS_V1 = {"invocacao": "ritual_invocar", "protecao": "ritual_selar", "banimento": "ritual_despertar",
              "passagem": "ritual_criar", "revelacao": "ritual_poder"}


def arte_v1(i):
    """Nome do arquivo da arte v1 (16 px) que serve de placeholder para o ID novo."""
    resto = i.removeprefix("sigilo_")
    if resto.startswith("de_"):
        resto = resto[3:]
        return RITUAIS_V1.get(resto, f"elemento_{resto}")
    return f"digito_{resto}" if resto.isdigit() else f"letra_{resto}"


def ampliar_32(origem, destino):
    """16x16 -> 32x32 sem suavizar (cada pixel vira 2x2). A textura do sigilo no chão passa a ser 32x32."""
    from PIL import Image
    destino.parent.mkdir(parents=True, exist_ok=True)
    img = Image.open(origem).convert("RGBA")
    px = img.load()
    grande = Image.new("RGBA", (32, 32))
    gp = grande.load()
    for y in range(32):
        for x in range(32):
            gp[x, y] = px[x // 2, y // 2]
    grande.save(destino)


def png(largura, altura, pixels):
    """PNG RGBA simples, sem biblioteca. pixels: lista de linhas de tuplas (r, g, b, a)."""
    cru = b"".join(b"\x00" + b"".join(bytes(p) for p in linha) for linha in pixels)

    def bloco(tipo, dados):
        c = struct.pack(">I", len(dados)) + tipo + dados
        return c + struct.pack(">I", zlib.crc32(tipo + dados) & 0xFFFFFFFF)

    return (b"\x89PNG\r\n\x1a\n" + bloco(b"IHDR", struct.pack(">IIBBBBB", largura, altura, 8, 6, 0, 0, 0))
            + bloco(b"IDAT", zlib.compress(cru, 9)) + bloco(b"IEND", b""))


def giz_placeholder():
    linhas = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    for i in range(10):  # um bastão branco na diagonal
        for dx in (0, 1):
            linhas[12 - i][3 + i + dx] = (235, 232, 222, 255)
    for i in range(10):
        linhas[13 - i][3 + i] = (170, 168, 160, 255)
    return png(16, 16, linhas)


def escrever(caminho, conteudo):
    caminho.parent.mkdir(parents=True, exist_ok=True)
    if isinstance(conteudo, bytes):
        caminho.write_bytes(conteudo)
    else:
        caminho.write_text(json.dumps(conteudo, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def main():
    ids = sigilos()
    if len(ids) != 46:
        sys.exit(f"esperava 46 sigilos em IdsSigilos.java, achei {len(ids)}")
    # modelo base: um quadro fino deitado no chão, com brilho leve
    escrever(ASSETS / "models/block/sigilo_base.json", {
        "render_type": "minecraft:cutout",
        "textures": {"particle": "#sigilo"},
        "elements": [{
            "from": [0, 0, 0], "to": [16, 0.1, 16],
            "faces": {"up": {"uv": [0, 0, 16, 16], "texture": "#sigilo", "light_emission": 8}},
        }],
    })
    variantes = {}
    for i in ids:
        origem = ARTE / f"{arte_v1(i)}.png"
        destino = ASSETS / f"textures/block/sigilo/{i}.png"
        if origem.exists():
            ampliar_32(origem, destino)
        elif not destino.exists():
            sys.exit(f"falta a arte de {i} (esperava {origem})")
        escrever(ASSETS / f"models/block/sigilo_{i}.json", {
            "parent": "odteste:block/sigilo_base",
            "textures": {"sigilo": f"odteste:block/sigilo/{i}"},
        })
        for giro in range(4):
            v = {"model": f"odteste:block/sigilo_{i}"}
            if giro:
                v["y"] = 90 * giro
            variantes[f"simbolo={i},giro={giro}"] = v
    escrever(ASSETS / "blockstates/sigilo.json", {"variants": variantes})
    escrever(ASSETS / "models/item/giz.json", {
        "parent": "minecraft:item/generated", "textures": {"layer0": "odteste:item/giz"}})
    giz = ASSETS / "textures/item/giz.png"
    if not giz.exists():
        escrever(giz, giz_placeholder())
        print("giz.png: placeholder criado (a Modelador entrega o definitivo)")
    print(f"ok: {len(ids)} sigilos, {len(variantes)} estados")


if __name__ == "__main__":
    main()
