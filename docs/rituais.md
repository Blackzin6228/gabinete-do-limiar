# Rituais com giz: plano técnico

**Proposta. Nada aprovado, nada registrado.** Pedido do Nikolas (15/11/2026): giz num bloco, tela, escolhe o sigilo,
ele aparece no chão. Primeira versão: criar, selar/despertar relíquias, poderes, invocar. Sistema nosso (`odteste`),
sem nada do giz do Occultism. Os desenhos dos sigilos vêm da Mesa Modelador (prévia aprovada antes de integrar).

## 1. Peças
- **Giz** (`odteste:giz`): item com durabilidade (~64 traços), sem receita de craft na v1 (vem da história/kit).
  Cor por elemento é pergunta (ver 7). Se houver, cada cor é um item próprio, porque o ID fica para sempre.
- **Sigilo no chão** (`odteste:sigilo`): bloco fino como tapete de 1 px, sem colisão, quebra com 1 golpe, só sobre
  bloco firme. O desenho é uma propriedade do estado do bloco (`simbolo`, mais `virado` e `aceso`): o modelo vai
  pronto no chunk, custo zero por quadro e nada de block entity. Cada nome de `simbolo` também é para sempre.
- **Círculo**: risco de giz (`odteste:risco_de_giz`) em volta do sigilo central. Tamanhos: pequeno 3x3, médio 5x5,
  grande 7x7 (no máximo 49 posições). O risco só entra em **ar sobre bloco firme**: nunca troca bloco de jogador,
  nem grama alta. Faltou espaço, a tela avisa e não desenha.
- **Ingredientes**: itens jogados dentro do círculo. Ao ativar, uma busca só na caixa do círculo (teto de 16 itens).
  Os itens só somem no fim, de uma vez, checando se ainda existem (evita duplicar).
- **Ativação**: clique direito no sigilo central com pederneira (vanilla, sem ID novo). O ritual leva alguns
  segundos: o bloco central agenda um tick por segundo (`scheduleTick`) só enquanto está aceso. Sair do círculo,
  quebrar o sigilo ou o servidor reiniciar cancela; os itens ficam no chão.
- **Custo**: Lucidez, vida e Exposição (fonte `ritual`, que já existe com freio de repetição). Nada de número na
  tela: o custo aparece como som, tremor e tela. Ritual errado falha: cobra metade e dá um susto (`odteste:susto`).

## 2. Receita em JSON (rituais novos sem código)
Pasta `data/<ns>/odteste_rituais/<nome>.json`, lida igual aos livros (`SimpleJsonResourceReloadListener`, aceita
`/reload`), com `Codec` (`RecordCodecBuilder`, `Ingredient.CODEC`, `ItemStack.CODEC`). Arquivo com erro: aviso no
log e o resto carrega. A conferência das regras fica numa classe sem Minecraft, para ter teste. As receitas **não vão
para o cliente** (segredo da história); o cliente só recebe a lista de sigilos.
```json
{ "tipo": "criar", "sigilo": "passagem", "circulo": "grande", "elemento": "energia", "noite": true,
  "ingredientes": [ { "item": "odteste:cinza_de_reliquia" }, { "tag": "c:gems/diamond", "quantidade": 4 } ],
  "duracao": 20, "custo": { "lucidez": 20, "vida": 6, "exposicao": 10 },
  "resultado": { "id": "odteste:estilhaco_do_limiar" }, "limite": { "vezes": 1, "dias": 7, "por": "servidor" } }
```
Os outros tipos mudam só o que vem depois do `tipo` (campos comuns iguais ao de cima):
- `selar`: `"reliquia": "odteste:sabre_do_ultimo_adeus", "ingredientes": [{ "item": "odteste:cera_negra" }]`
  muda o componente `estado_reliquia` para `selada` (corrupção para enquanto selada).
- `despertar`: mesma relíquia, `"exige": "selada"` → `desperta`. Custo alto de Lucidez e um susto garantido.
- `poder`: `"efeito": "odteste:protecao_paranormal", "segundos": 600, "alcance": "grupo"` (ou `olhar_do_outro_lado`).
- `invocar`: `"entidade": "<id da entidade>", "risco": 30, "falha": "hostil"` com chance de vir hostil ou de
  vir outra coisa. Hoje o mod não tem criaturas; o exemplo espera a Lore.
`elemento` é opcional: se bater com o elemento do jogador (só no servidor, nunca vai ao cliente), o custo cai.

## 3. Tela de escolha (`cliente/TelaSigilo`)
Abre quando o giz é usado num bloco (o servidor manda `abrir_tela_sigilo` com a posição). Mostra: a grade de sigilos
que o jogador conhece (desenho e nome curto, sem número), o tamanho do círculo (três desenhos), o giro e uma prévia
do chão. Ao escolher, manda `desenhar_sigilo`; o servidor confere tudo de novo (distância até 6 blocos, giz na mão,
espaço livre, sigilo conhecido, 1 pedido por segundo) e gasta o giz. Brilho do sigilo aceso: luz do próprio bloco e
partícula vanilla (funciona com Iris); tremor e escurecer passam pela `FxTela` e diminuem com `efeitos_fortes`.

## 4. Máquinas (**a confirmar**: suposição do CEO, o Nikolas não respondeu)
- **Pilão de Restos** (`odteste:pilao_de_restos`): mói material que cai de entidade em ingrediente de ritual.
  Receitas em JSON (tipo `odteste:processar`). Block entity com inventário e energia FE do próprio NeoForge
  (cabos do Mekanism ligam sem código de compat). Só trabalha com item dentro; parada não faz nada.
- **Círculo Mecânico** (`odteste:circulo_mecanico`): ao lado de um sigilo, repete rituais marcados
  `"automatico": true` (só `criar` simples; nunca selar, despertar nem invocar).
- **Risco sem lag**: decidido só **ao fim de um ciclo** (evento, não tick), com um sorteio barato. Falha = pausa o
  ciclo e um susto leve em quem está a 12 blocos (busca única na caixa). Nunca perde item, nunca quebra bloco.
  Tetos: 1 falha por jogador a cada 10 min; no máximo 4 máquinas arriscadas por chunk (as outras não sorteiam).
  Sal de Velório perto baixa o risco. Máquinas do Mekanism e do Re:Avaritia ficam para `compat/` depois.

## 5. Riscos e regras do CLAUDE.md
- **IDs para sempre**: item, blocos, nomes de `simbolo`, dados e pacotes entram em `docs/ids.md` no PR que os criar.
- **Sem lag**: nada por tick no mundo; buscas só na caixa do círculo, com teto; máquina parada não roda nada.
- **Sem destruir construção**: risco e sigilo só em ar; invocação nunca explode nem quebra bloco.
- **Trapaça**: item duplicado (consumo atômico), pacote falso (servidor confere tudo), dois rituais sobre o mesmo
  item (o item é reservado pelo primeiro).
- **Conflitos com `reliquias-v1.md`**: lá o Estilhaço nasce só de falha de máquina e são 8 relíquias (2 armas);
  o pedido fala em ritual e "4 espadas". Ver perguntas.

**IDs propostos (ainda não registrados):** itens `odteste:giz`, `odteste:estilhaco_do_limiar`; blocos
`odteste:sigilo`, `odteste:risco_de_giz`; componente `odteste:estado_reliquia`; efeitos `odteste:protecao_paranormal`,
`odteste:olhar_do_outro_lado`; dado do jogador `odteste:sigilos_conhecidos`; dado do mundo `odteste_rituais`
(limites como "1 por semana"); pacotes `odteste:abrir_tela_sigilo`, `odteste:desenhar_sigilo`,
`odteste:lista_sigilos`; pasta `odteste_rituais/`. Se as máquinas forem aprovadas: blocos e block entities
`odteste:pilao_de_restos`, `odteste:circulo_mecanico`, tipo de receita `odteste:processar`.

## 6. Ordem (3 PRs pequenos)
1. **Desenhar**: giz, sigilo, risco, tela e pacotes. Só desenha, não faz ritual. Precisa da arte aprovada.
2. **Ritual**: leitura dos JSON, ativação, custo, falha, os 5 tipos, uma receita de exemplo de cada, testes.
3. **Máquinas**: só se o Nikolas confirmar a seção 4.

## 7. Perguntas para o Nikolas
1. Um giz só, ou um giz por elemento (Sangue, Morte, Conhecimento, Energia, Medo)?
2. O jogador já conhece todos os sigilos, ou aprende cada um (livro, mestre, história)?
3. O Estilhaço do Limiar sai do ritual, das máquinas, ou dos dois? E quantos por semana?
4. Quais são as "4 espadas únicas"? O plano de relíquias tem 8 relíquias e só 2 são armas.
5. As máquinas (seção 4) entram? Se sim, as duas ou só o Pilão?
