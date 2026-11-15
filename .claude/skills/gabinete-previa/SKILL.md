---
name: gabinete-previa
description: Use para fazer prévia visual no gabinete-do-limiar (texturas de item, sigilos, ícones, telas) antes de integrar no jogo. Script determinístico, folha de prévia para o Nikolas abrir no celular e checagem de md5.
---

# Prévia visual do Gabinete do Limiar

Regra do Nikolas: **nada visual entra no jogo sem ele aprovar a prévia** (prévia real, não cena falsa).
Leia junto `minecraft-resource-pack` (formatos de textura e modelo; 1.21.1).

## Como fazer
1. Fonte: o documento aprovado da tarefa (nome, ID, descrição visual de 1 linha).
2. **Script determinístico** em `ferramentas/previas/<assunto>.py` (Pillow; `random.Random(seed)` fixa).
   O script escreve os PNGs finais no lugar certo (`assets/odteste/textures/item/<id>.png` etc.) e a folha de prévia.
   Rode duas vezes e confira `md5sum` igual. Quem aprovar a prévia aprova **o script**, não um PNG solto.
3. Pixel art de verdade: 16x16, contorno escuro, luz de cima à esquerda (como o vanilla), 3+ tons por material,
   **um** acento de cor do elemento, sem ruído aleatório, sem degradê liso.
4. **Folha de prévia** em `docs/previas/<assunto>.png`:
   - cada peça ampliada 8x sem borrar (nearest-neighbor) com o nome embaixo;
   - a mesma peça em tamanho real dentro de um slot de inventário e na hotbar;
   - fundo escuro (pedra/deepslate). Para sigilos: no chão de bloco, como ficará no jogo.
5. No PR e no resultado: link direto da folha no GitHub
   (`https://github.com/Blackzin6228/gabinete-do-limiar/blob/<branch>/docs/previas/<assunto>.png`).

## Estilo do mod
- Horror brasileiro, decadência, objetos comuns e perturbadores (gastos, sujos, reais). Nada de fantasia épica.
- Cores por elemento: Sangue (vermelho escuro), Conhecimento (dourado/amarelo velho), Energia (roxo/ciano elétrico),
  Morte (cinza-chumbo/preto, acento pálido), Medo (branco sujo/ausência). Confirme com o Nikolas se mudar.
- Arte própria. Nada copiado do livro de Ordem Paranormal (inclusive a fonte "Sigilos"). Terceiros só com licença
  registrada em `arte/LICENCAS.md`.

## Não faça
- Não escreva Java nem tradução na tarefa de prévia (é do Dev).
- Não faça merge: PR sem merge até o "ok" do Nikolas.
