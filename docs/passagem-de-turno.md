# Passagem de turno

Atualize ao fim de cada etapa. Curto.

## Estado (15/11/2026, Etapa 1 do recomeço, mesa Dev)
Mod novo montado a partir do Od-Teste `b488cc6` (o jar que está no servidor). Ainda **não** foi para o servidor.

**Funciona igual ao servidor hoje (fora da Vila Calada):**
- Camada de horror no overworld, de dia e de noite: céu cinza, névoa, luz do dia mais baixa, noite escura com brilho
  travado (tocha clareia), música cortada, sons graves raros, véu de reserva com shader pack ou céu de outro mod,
  shader `cor_horror` (com e sem Iris). Sustos grandes de noite (1 ou 2 por noite) e `/odteste susto`.
- Config do cliente `efeitos_fortes` e `intensidade`.
- Exposição e Lucidez (dano, descanso, luz, grupo, comida quente, dormir, surto, lentidão) e `/exposicao`, `/lucidez`.
- Elemento do jogador (`/odteste elemento`, `revelar`), sem Marca e sem magia.
- Livros de dados (`data/odteste/odteste_livros/`, `/odteste livro`), com `{jogador}`, `{grupo}`, `{marcado}` e
  trechos escondidos. **Nenhum livro ainda** (os antigos ficaram no Od-Teste).
- Grupos (`/odteste grupo`) e mestre (`/odteste mestre ouvir|status`, config `odteste-mestre-server.toml`).

**Cuidados para quando for ao servidor (pedir ao Nikolas, com backup):**
- Itens, blocos e criaturas do mod antigo somem do mundo (lista em `docs/ids.md`).
- Dados dos jogadores ficam (inclusive Marca etc., guardados sem uso). O arquivo `odteste_historia` é preservado.
- Livros do Sineiro que já existem viram livro comum com o texto salvo.

## Etapa 2 (15/11/2026): crachá e dossiê
- Item `odteste:cracha` (dono no componente `cracha_dados`); botão direito abre o cartão na tela (`cliente/TelaCracha`).
  Cartão: mestre (lista da config) > elemento revelado (limpo) > lotação borrada. Atualiza no login e quando o
  elemento é definido ou revelado.
- Kit na primeira entrada (flag `recebeu_kit`): crachá + livro `dossie`. Quem já joga recebe no próximo login.
  `/odteste kit dar|esquecer <jogadores>`.
- Falta: print da tela do crachá aberto, feito pelo Nikolas no jogo.

## Relíquias (lore, respostas do Nikolas em 15/11/2026)
Em `docs/reliquias-v1.md`. Nenhum código nem ID novo ainda.
- **Espadas (4):** por enquanto não são dos jogadores; só quem está no criativo interpretando a entidade usa. Sem craft
  (comando ou aba do criativo). Só funcionam para quem é do elemento delas.
- **Máscaras** das entidades também são relíquias: aguardando o Nikolas mandar a modelagem. Nada definido.
- **Artefatos (12):** qualquer jogador usa, cada um com buff e debuff, no slot de acessório do Curios (Curios a
  confirmar no pack).
- **Máquinas** (Re:Avaritia, Mekanism): só falham (pausa e susto/visão), sem soltar material.
- Artefatos em baú; ritual só magia.
- **Em aberto:** de onde sai o Estilhaço do Limiar.

## Pacote 1, etapa 1 (mesa Dev, branch `pacote-1/itens`, sem PR: o Integrador junta)
- Código das 4 espadas e dos 12 artefatos, loot em baús vanilla (`chance_bau`, padrão 2%) e Curios opcional
  (`compat/curios/`). Texturas: placeholders com o nome final; vale a da mesa Modelador (PR #13) em conflito.
- Em breve (TODO): afinidade (magias), Caneta, Fita, Fusível, mapa da Sentença, visões na tela (precisam de prévia).
- Não testado no jogo: os efeitos, o contorno e o slot do Curios.

## Rituais e sigilos
- Plano técnico em `docs/rituais.md` (mesa Dev). O PR #8 está sendo atualizado pela mesa Dev. Giz único; sigilos
  aprendidos aos poucos em eventos pelo mundo.
- **Sigilos v1:** prévia com a mesa Modelador (PR #10), esperando o Nikolas.

## Pack Conguitos-ATM10
PR #18 **sem merge** (merge só depois do backup): Re:Avaritia (Catalisador do Infinito pede 1 ATM Star a mais), Lithium,
Subtle Effects e, só no PC do jogador, ETF, EMF e Fresh Animations.

## Próximo passo
1. Nikolas aprova sigilos e rituais.
2. Depois, prévias de textura (espadas, artefatos, materiais).
3. Depois, o código.
Lore, em paralelo: o chamado na mata e a travessia.
