# Rituais com giz: plano técnico

**Proposta, sem código e sem ID registrado.** Pedido do Nikolas (15/11/2026): giz num bloco, tela, escolhe o sigilo,
ele aparece no chão. Respostas do Nikolas de 15/11/2026 (19h40 e 20h10) já estão neste plano. Sistema nosso
(`odteste`), sem nada do giz do Occultism. Os desenhos dos sigilos vêm da Mesa Modelador (prévia aprovada antes de
integrar).

**O que o ritual faz e o que não faz (decidido):**
- O ritual com giz serve **só para magias**. Não faz craft, não gera item nem material.
- Não mexe nas 4 espadas: elas são só do mestre/ator, no criativo, por comando.
- Não dá artefato: artefatos vêm de baús de dungeon.
- Não dá o Estilhaço do Limiar: a origem dele fica a decidir em outro documento.

## 1. Peças
- **Giz** (`odteste:giz`): um giz único, sem cor por elemento. Item com durabilidade (~64 traços), sem receita de
  craft na v1 (vem da história/kit).
- **Sigilo no chão** (`odteste:sigilo`): bloco fino como tapete de 1 px, sem colisão, quebra com 1 golpe, só sobre
  bloco firme. O desenho é uma propriedade do estado do bloco (`simbolo`, mais `virado` e `aceso`): o modelo vai
  pronto no chunk, custo zero por quadro e nada de block entity. Cada nome de `simbolo` também é para sempre.
- **Círculo**: risco de giz (`odteste:risco_de_giz`) em volta do sigilo central. Tamanhos: pequeno 3x3, médio 5x5,
  grande 7x7 (no máximo 49 posições). O risco só entra em **ar sobre bloco firme**: nunca troca bloco de jogador,
  nem grama alta. Faltou espaço, a tela avisa e não desenha.
- **Ingredientes**: itens jogados dentro do círculo, gastos para pagar a magia (nada sai no lugar). Ao ativar, uma
  busca só na caixa do círculo (teto de 16 itens). Os itens só somem no fim, de uma vez, checando se ainda existem.
- **Ativação**: clique direito no sigilo central com pederneira (vanilla, sem ID novo). Só quem aprendeu o sigilo
  ativa. O ritual leva alguns segundos: o bloco central agenda um tick por segundo (`scheduleTick`) só enquanto está
  aceso. Sair do círculo, quebrar o sigilo ou o servidor reiniciar cancela; os itens ficam no chão.
- **Custo**: Lucidez, vida e Exposição (fonte `ritual`, que já existe com freio de repetição). Nada de número na
  tela: o custo aparece como som, tremor e tela. Ritual errado falha: cobra metade e dá um susto (`odteste:susto`).

## 2. Sigilos aprendidos (dado do jogador)
O jogador **não** conhece todos os sigilos. Aprende aos poucos, achando coisas em eventos espalhados pelo mundo.
- **Quais eventos:** a definir (Lore e Nikolas). O código só precisa de uma porta: `Sigilos.aprender(jogador, nome)`,
  chamada pelo evento quando ele acontecer. Aprender dá som e uma frase curta, sem número.
- **Onde fica guardado (proposta):** um `AttachmentType` com a lista de nomes aprendidos (texto, ex.: `passagem`),
  salvo no jogador e mantido na morte (`copyOnDeath`). Nome proposto: `odteste:sigilos_conhecidos`. **Ainda não é
  ID**: entra em `docs/ids.md` só no PR que criar o dado. Nome que sair do código fica salvo e é só ignorado.
- **Cliente:** recebe só a lista de nomes aprendidos (pacote `lista_sigilos`), ao entrar no mundo e ao aprender um
  novo. Nunca recebe receitas.
- **Tela do giz:** mostra **só os sigilos aprendidos**. Sem nenhum aprendido, a tela diz que o agente ainda não
  conhece sigilo e não desenha.
- **Servidor confere sempre:** desenhar ou ativar um sigilo não aprendido é recusado, mesmo com pacote falso.
- **Mestre:** comando de admin para ensinar e tirar sigilo de um jogador (testes e cenas). Sem tela para o jogador.

## 3. Receita em JSON (magias novas sem código)
Pasta `data/<ns>/odteste_rituais/<nome>.json`, lida igual aos livros (`SimpleJsonResourceReloadListener`, aceita
`/reload`), com `Codec` (`RecordCodecBuilder`, `Ingredient.CODEC`). Arquivo com erro: aviso no log e o resto carrega.
A conferência das regras fica numa classe sem Minecraft, para ter teste. As receitas **não vão para o cliente**
(segredo da história). Não existe campo de item de saída: o resultado é sempre uma magia.
```json
{ "tipo": "poder", "sigilo": "passagem", "circulo": "medio", "elemento": "energia", "noite": true,
  "ingredientes": [ { "item": "minecraft:candle", "quantidade": 3 }, { "tag": "c:gems/diamond" } ],
  "duracao": 20, "custo": { "lucidez": 20, "vida": 6, "exposicao": 10 },
  "efeito": "odteste:protecao_paranormal", "segundos": 600, "alcance": "grupo",
  "limite": { "vezes": 1, "dias": 7, "por": "jogador" } }
```
- `poder` é o único tipo da v1. `efeito` é a magia (ex.: `protecao_paranormal`, `olhar_do_outro_lado`); `alcance`
  pode ser `si`, `grupo` (quem está no círculo) ou `area`.
- `elemento` é opcional: se bater com o elemento do jogador (só no servidor, nunca vai ao cliente), o custo cai.
- **Fora por enquanto:** `invocar` (chamar entidade). Não gera item, mas o mod ainda não tem criaturas e não ficou
  claro se conta como magia; volta só se o Nikolas pedir. Os antigos `criar`, `selar` e `despertar` saíram de vez
  (geravam item ou mexiam em relíquia).

## 4. Tela de escolha (`cliente/TelaSigilo`)
Abre quando o giz é usado num bloco (o servidor manda `abrir_tela_sigilo` com a posição). Mostra: a grade dos
sigilos que o jogador **aprendeu** (desenho e nome curto, sem número), o tamanho do círculo (três desenhos), o giro e
uma prévia do chão. Ao escolher, manda `desenhar_sigilo`; o servidor confere tudo de novo (distância até 6 blocos,
giz na mão, espaço livre, sigilo aprendido, 1 pedido por segundo) e gasta o giz. Brilho do sigilo aceso: luz do
próprio bloco e partícula vanilla (funciona com Iris); tremor e escurecer passam pela `FxTela` e diminuem com
`efeitos_fortes`.

## 5. Máquinas
- **Re:Avaritia e Mekanism: só falham**, como evento paranormal. Não fazem ritual, não geram item. Código só em
  `compat/reavaritia/` e `compat/mekanism/`, atrás de `Compat.Integracao.X.ligada()`.
- **Risco sem lag:** nada por tick. Checagem rara e barata perto de jogadores (busca só numa caixa pequena em volta,
  com teto de máquinas por checagem). Falha = susto leve, som e faísca; se a API do mod deixar com segurança, a
  máquina engasga alguns segundos. Nunca perde item, nunca quebra bloco. Tetos: 1 falha por jogador a cada 10 min;
  no máximo 4 máquinas arriscadas por chunk. Sal de Velório perto baixa o risco. Detalhe do gatilho: no PR de compat.
- **Máquinas nossas: fora por enquanto** (ideia guardada, sem ID): Pilão de Restos (mói restos de entidade em
  ingrediente) e Círculo Mecânico (repete uma magia simples ao lado de um sigilo). Voltam só se o Nikolas pedir.

## 6. Riscos e regras do CLAUDE.md
- **IDs para sempre**: item, blocos, nomes de `simbolo`, dados e pacotes entram em `docs/ids.md` no PR que os criar.
- **Sem lag**: nada por tick no mundo; buscas só na caixa do círculo, com teto; checagem de máquina rara e com teto.
- **Sem destruir construção**: risco e sigilo só em ar; nenhuma magia explode ou quebra bloco.
- **Trapaça**: ingrediente duplicado (consumo atômico), pacote falso (servidor confere tudo, inclusive se o sigilo foi
  aprendido), dois rituais sobre o mesmo item (o item é reservado pelo primeiro).
- **Conflitos com `reliquias-v1.md`** (a Lore ajusta lá): o Estilhaço "nasce de falha de máquina" e a Cinza de
  Relíquia "sobra do selamento de uma espada"; artefatos com origem "ritual de Sangue/Morte"; espada que "só se
  conserta com ritual". Pelas respostas, nada disso sai do ritual com giz.

**IDs propostos (ainda não registrados):** item `odteste:giz`; blocos `odteste:sigilo`, `odteste:risco_de_giz`;
efeitos `odteste:protecao_paranormal`, `odteste:olhar_do_outro_lado`; dado do jogador `odteste:sigilos_conhecidos`;
dado do mundo `odteste_rituais` (limites como "1 por semana"); pacotes `odteste:abrir_tela_sigilo`,
`odteste:desenhar_sigilo`, `odteste:lista_sigilos`; pasta `odteste_rituais/`.

## 7. Ordem (3 PRs pequenos)
1. **Desenhar**: giz, sigilo, risco, tela, pacotes, dado dos sigilos aprendidos e comando do mestre. Só desenha, não
   faz ritual. Precisa da arte aprovada.
2. **Magia**: leitura dos JSON, ativação, custo, falha, o tipo `poder`, uma receita de exemplo, testes.
3. **Falha das máquinas**: compat do Re:Avaritia e do Mekanism (evento paranormal, sem item).
