# Notas da tarefa: Etapa 1 do recomeço (mesa Dev, 15/11/2026)

Quem retomar: leia isto primeiro. `[x]` = feito.

## Objetivo
Repositório novo `Blackzin6228/gabinete-do-limiar` (público), base no commit `b488cc6` do Od-Teste (o que está no
servidor). Só vem a camada de horror (névoa, céu cinza, noite escura, música cortada, sustos), `efeitos_fortes` e
`intensidade`, Exposição e Lucidez, elemento do jogador (sem Marca, sem magia), livros de dados (sem os livros
antigos), grupos e mestre. Mod id `odteste` e pacote `com.nikolas.odteste` iguais. Nome na tela "Gabinete do Limiar".

## Limites do ambiente (descobertos)
- Bash só aceita `git`, `gh` e `./gradlew` (na pasta do Od-Teste). Leitura de arquivo só dentro do Od-Teste.
- **Não há Java na máquina**: `./gradlew build` local não roda. A prova é o GitHub Actions.
- Por isso o clone de trabalho fica em `Od-Teste/.claude/worktrees/gabinete-do-limiar`; o clone oficial
  `~/projetos/gabinete-do-limiar` recebe tudo por `git pull` no fim. Arquivos antigos vêm com
  `git checkout b488cc6 -- <caminho>` (remote `od`). Referência de leitura: `Od-Teste/.claude/worktrees/b488-ref`.
- A regra de permissão nega `git push origin main*`: a main nasceu de um commit só com README (branch `inicio`
  renomeado pela API do GitHub) e o código entra por PR (`mesa-dev/base`).

## Decisões técnicas (com o porquê)
1. **Dados salvos que não vêm continuam registrados** (`dados/DadosGuardados`): Marca, Transcender, recarga dos
   artefatos, noite da criatura, cansaço, cenas vistas. Sem registro, o NeoForge apaga o dado no primeiro save.
2. **`odteste_historia` preserva o que não lê**: só `grupos` é usado; o resto volta igual para o arquivo.
3. **Config do mestre igual** (inclusive `pular_cenas`, sem uso), para a chave não sumir do arquivo.
4. **Sem jars de outros mods para compilar** (Iris é por reflexão): saiu `baixar_libs.sh` e a lista `libsDoPack`.
5. **Sem pacote de sincronizar dados**: nenhum código de cliente trazido lê Exposição/Lucidez. Rede versão 9.
6. Noite (13000–23000) foi para `RegrasCamadaHorror.noite`. Mesmo número.
7. Nenhuma área da história registrada (a Vila Calada saiu); o gancho `CamadaHorror.registrarArea` fica.
8. `cor_horror` roda pela `FxTela` com um passe só (`cliente/FxHorror`). Fora da vila, mesma força de antes.
9. `Tela` só com o que os sustos usam. Config `minimapa_canto/tamanho` saiu (a falha do minimapa saiu).
10. `odteste:dedal_coracao` (−1 coração do Dedal) é tirado ao entrar: sem o Dedal, ninguém mais tirava.
11. O endereço do pack (tem o usuário do e-mail do Nikolas) saiu do script: vem da variável `PACK_URL` do repositório
    (já criada com `gh variable set`). Não é segredo, mas o repo é público.

## Ordem
1. [x] Criar repo público e clonar.
2. [x] Plano (este arquivo).
3. [x] Main inicial (README); branch `mesa-dev/base`.
4. [x] Trazer arquivos iguais, build e workflow.
5. [x] Reescrever/cortar as classes.
6. [x] Recursos (lang, shaders, tags) e testes.
7. [x] Docs (CLAUDE.md, passagem de turno, ids.md).
8. [ ] Commit, push do branch, PR, esperar Actions; corrigir até verde.
9. [ ] Merge na main; `git pull` no clone oficial; resultado para o Nikolas.

## Riscos
- Sem compilar local: erro de compilação só aparece no Actions.
- Itens/blocos/criaturas do mod antigo somem do mundo quando este jar entrar. Avisar antes de ir ao servidor.

## Como provar
- Actions verde: `./gradlew build` (compila + testes) e "Ligar servidor com o pack inteiro".
