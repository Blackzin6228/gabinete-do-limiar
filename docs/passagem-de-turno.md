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

## Relíquias (lore, 15/11/2026)
Proposta das 8 relíquias, materiais, máquinas que falham e armadura em `docs/reliquias-v1.md`. **Não aprovada**: espera
as respostas do Nikolas; nenhum código nem ID novo ainda.

## Rituais com giz (plano, 15/11/2026, mesa Dev)
Plano técnico em `docs/rituais.md`. **Não aprovado**: espera as 5 perguntas do Nikolas e a arte dos sigilos
(Mesa Modelador). Nenhum código nem ID novo ainda. Máquinas marcadas "a confirmar".

## Próximo passo
Lore: o chamado na mata e a travessia. Primeiro conteúdo novo só com prévia aprovada.
