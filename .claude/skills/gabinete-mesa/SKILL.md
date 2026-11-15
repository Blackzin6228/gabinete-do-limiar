---
name: gabinete-mesa
description: Use em TODA tarefa de mesa do Escritório no gabinete-do-limiar (Dev, Lore, Modelador, Integrador, Revisor, Publicador). Diz como planejar, onde trabalhar, quais skills de Minecraft abrir, como abrir o PR e como escrever o resultado.
---

# Como uma mesa trabalha no Gabinete do Limiar

Mod de horror do CONGUITOS SMP. NeoForge **1.21.1** (21.1.x), Java 21, mod id `odteste` (nunca muda).
Dono: Nikolas. Ele não é programador: fale com ele em português curto, sem jargão.

## 1. Antes de tudo (sempre, nesta ordem)
1. Leia `CLAUDE.md` e `docs/passagem-de-turno.md`. Se a tarefa citar um documento (`docs/reliquias-v1.md`,
   `docs/rituais.md` etc.), leia da branch certa (`git fetch` e `git show origin/<branch>:<arquivo>`).
2. Abra as skills que servem para a tarefa (tabela abaixo) **antes** de escrever. Se a skill não aparecer no sistema,
   leia direto o arquivo `.claude/skills/<nome>/SKILL.md` e as `references/` que ela indicar.
3. **PARE E PLANEJE** (ultrathink). Escreva `NOTAS-DA-TAREFA.md` na raiz da worktree: objetivo, arquivos, ordem,
   riscos, como provar. Execute marcando os passos. Quem retomar lê esse arquivo primeiro.

| Tarefa | Skills |
|---|---|
| Código Java (item, bloco, sistema, comando, rede) | `minecraft-modding`, `gabinete-item`, `minecraft-testing` |
| Textura, modelo de item, prévia, sons, fontes | `minecraft-resource-pack`, `gabinete-previa` |
| Efeito de tela, partícula, shader, susto | `gabinete-fx`, `minecraft-resource-pack` |
| Teste automatizado | `minecraft-testing` |
| Estrutura / schematic (sem geração de mundo!) | `minecraft-worldedit-ops`, `minecraft-world-generation` (só leitura) |

**Atenção à versão:** as skills `minecraft-*` foram escritas para Minecraft 26.x com exemplos de 1.21.x. Este
projeto é **1.21.1**. Quando a skill e o código do projeto discordarem, vale o código do projeto e a API do
NeoForge 1.21.1 (ex.: `DeferredRegister.Items`, `DataComponentType`, `CustomPacketPayload`, modelos em
`assets/odteste/models/item/`, sem a pasta `items/` do 1.21.4+).

## 2. Onde trabalhar
- Só dentro de `/home/nikolas/projetos/gabinete-do-limiar`.
- Branch própria com worktree **dentro** do repo:
  `git worktree add .worktrees/<nome> -b mesa-<mesa>/<assunto> origin/main`.
- Outro repositório (ex.: o pack Conguitos-ATM10)? Clone em `.worktrees/<nome>` (pasta ignorada pelo git).
- Nunca `git add -A`. Nunca commitar `NOTAS-DA-TAREFA.md`. Nunca push no `main`, nunca force push.
- **Permissão bloqueou?** Não contorne (nada de python3/sed/cópia para escapar do bloqueio). Pare e diga ao Nikolas
  o comando exato que foi barrado. Se aparecer "workspace has not been trusted", diga isso.

## 3. Regras que derrubam PR
- Visual (textura, modelo, efeito, tela) só entra com **prévia aprovada pelo Nikolas**. Sem aprovação: PR sem merge.
- ID novo → `docs/ids.md` no mesmo PR. Texto → `pt_br.json` **e** `en_us.json`.
- Nada pesado por tick, nenhum laço sem teto. Lado cliente só em `cliente/`.
- `efeitos_fortes` diminui (não desliga) todo efeito de tela. Funciona com e sem shader do Iris.
- Nenhum número na tela do jogador.
- Ordem Paranormal: entidades, elementos e casos pelo nome podem. Texto, arte, símbolos e **nomes de personagens**
  não. Na dúvida sobre o canon, escreva `[conferir no livro]`, não invente.
- Segredo (endereço do pack, e-mail, token) nunca em arquivo nem log.

## 4. Provar que funciona
- `./gradlew build` (compila e testa). Se falhar ao preparar o Minecraft, valide pelo Actions (`gh run watch`).
- O PR só está pronto com Actions verde (inclusive o teste que liga o servidor com o pack).
- Diga sempre o que **não** foi testado no jogo.

## 5. Resultado (o que o Nikolas lê no celular)
Começa com `Nikolas,`. Poucas linhas: o que ficou pronto, links (PR, run, prévia), o que falta, perguntas reais.
Termina com `skills usadas: …` (ou qual foi bloqueada e por quê). Atualize `docs/passagem-de-turno.md` em até 5 linhas.
