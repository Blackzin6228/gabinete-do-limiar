# Gabinete do Limiar: regras para todo agente

Mod de horror do CONGUITOS SMP. NeoForge 1.21.1 (21.1.262), Java 21. Mod id **`odteste`** e pacote
`com.nikolas.odteste` vêm do mod antigo (Od Teste) e **nunca mudam**: o mundo do servidor (ATM10) já tem dados com eles.
Dono: Nikolas (GitHub `Blackzin6228`).

## A direção
Os jogadores são agentes do Gabinete do Limiar. Atenderam um chamado na mata e atravessaram para outro lugar.
Horror forte, contexto claro para o jogador, pouca papelada. Do mod antigo só veio o que o Nikolas gostou: a neblina e
a camada de horror (mais Exposição, Lucidez, elemento, livros, grupos e mestre como base). Vila Calada, sino, enigmas,
criaturas, magias, artefatos, sinais, cenas e estruturas ficaram **arquivados** no repositório `Od-Teste`
(commit `b488cc6`): servem de referência, não se trazem sem o Nikolas pedir.

## Regras que continuam
1. Português do Brasil, frases curtas, sem jargão com o Nikolas.
2. **Prévia visual aprovada pelo Nikolas antes de integrar** (imagem, print, render). Visual, jogo e lore: perguntar antes.
3. **`efeitos_fortes` sempre respeitado**: todo efeito de tela diminui com ele (diminui, não desliga).
4. **Tem de funcionar com shader do Iris**: efeito de tela passa pela `cliente/FxTela`; testar com e sem shader pack.
5. **Nenhum número na tela do jogador.** Números só para admin/mestre.
6. Elementos de Ordem Paranormal (Sangue, Morte, Conhecimento, Energia, Medo) podem; arte, símbolos, personagens e
   textos da obra não. Arte própria.
7. Integração sem dependência dura: código de outro mod só em `compat/<mod>/`, atrás de `Compat.Integracao.X.ligada()`.
8. **IDs são para sempre**: todo ID novo entra em `docs/ids.md` no mesmo PR. Dado salvo que sai do código continua
   registrado (ver `dados/DadosGuardados`), senão o NeoForge apaga do jogador.
9. Nada de lag: nada pesado por tick, nenhum laço sem teto em jogadores ou entidades.
10. Lado cliente só em `cliente/` (nunca carregado no servidor). Traduções em `pt_br.json` **e** `en_us.json`.
11. Venda só de cosmético. Se uma ideia depende de venda, marcar no PR.

## Segurança
- **Nunca** pôr senha, token, e-mail ou endereço do servidor em arquivo do repositório. Segredos só em *Secrets*,
  e quem cola é o Nikolas. O endereço do pack fica na variável `PACK_URL` do repositório, não em arquivo.
- **Nada vai para o servidor sem backup e sem o Nikolas pedir.**
- Nunca remover mods do pack. WATERMeDIA e o plugin do YouTube nunca vão para o servidor.

## Documentos (regra nova)
Documento só quando ajudar a próxima mesa. Nada de documento gigante. O estado atual fica em
`docs/passagem-de-turno.md` (curto, atualizado ao fim de cada etapa); os IDs em `docs/ids.md`.

## Como trabalhar
- Branch `mesa-<mesa>/<assunto>`, PR para o `main`. O PR só entra com o Actions verde (compila, testa e liga um
  servidor com o pack inteiro).
- `./gradlew build` compila e testa. `bash ferramentas/teste_servidor.sh build/libs/<jar>` liga o servidor com o pack
  (precisa de ~12 GB de RAM e da variável `PACK_URL`).
- Commits em português, curtos, dizendo o que mudou para quem joga.
- Grafo do código (se existir `graphify-out/`): `graphify query` antes de buscar; `graphify update .` depois de mexer.
- As skills de Minecraft em .claude/skills foram escritas para Minecraft 26.x; este mod é NeoForge 1.21.1. Quando a skill e o código do projeto discordarem, vale o código do projeto e a API do NeoForge 1.21.1.

## Onde está cada coisa (`src/main/java/com/nikolas/odteste/`)
- `horror/` camada de horror (servidor: áreas e sustos) · `cliente/` névoa, céu, luz, música, sustos, shader.
- `sistemas/` Exposição, Lucidez, elemento · `dados/` dados do jogador · `historia/` grupos, livros, mestre.
- `comandos/` comandos de admin · `rede/` pacotes · `compat/` integrações.
