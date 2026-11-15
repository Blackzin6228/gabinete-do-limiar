#!/usr/bin/env bash
# Baixa do modpack (Blackzin6228/Conguitos-smp) os jars de outros mods que este mod usa para compilar.
# O Actions do Conguitos-ATM10 chama este script (sem argumentos, na raiz do repo) antes do ./gradlew build.
#
# Hoje o mod não compila contra nenhum outro mod, então não há nada a baixar e o script só avisa e sai com 0.
# Quando uma integração precisar da API de outro mod, ponha no build.gradle a lista
#   def libsDoPack = [
#       'nome-do-mod-1.0.jar',
#   ]
# e use compileOnly com esses jars (pasta libs/). Este script baixa o que estiver na lista.
set -euo pipefail

RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
PACK="https://raw.githubusercontent.com/Blackzin6228/Conguitos-smp/main/jars"
DESTINO="$RAIZ/libs"

JARS=()
if [[ -f "$RAIZ/build.gradle" ]]; then
  mapfile -t JARS < <(sed -n "/def libsDoPack = \[/,/^\]/p" "$RAIZ/build.gradle" | grep -oP "'\K[^']+\.jar" || true)
fi

if [[ ${#JARS[@]} -eq 0 ]]; then
  echo "baixar_libs: o mod não usa jars de outros mods para compilar; nada a baixar."
  exit 0
fi

mkdir -p "$DESTINO"
for nome in "${JARS[@]}"; do
  if [[ -s "$DESTINO/$nome" ]]; then
    echo "ok (já tinha): $nome"
    continue
  fi
  url="$PACK/$(python3 -c 'import sys,urllib.parse;print(urllib.parse.quote(sys.argv[1]))' "$nome")"
  echo "baixando: $nome"
  curl -fsSL --retry 3 -o "$DESTINO/$nome.tmp" "$url"
  # Confere se é um jar de verdade (zip), e não uma página de erro.
  if ! unzip -tq "$DESTINO/$nome.tmp" >/dev/null 2>&1; then
    echo "ERRO: $nome não é um jar válido" >&2
    rm -f "$DESTINO/$nome.tmp"
    exit 1
  fi
  mv "$DESTINO/$nome.tmp" "$DESTINO/$nome"
done

echo "Pronto: ${#JARS[@]} jars em $DESTINO"
