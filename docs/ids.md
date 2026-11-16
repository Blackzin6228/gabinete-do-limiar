# IDs do mod

Mod id `odteste` (vem do mod antigo, Od Teste; nunca muda). O mundo do servidor já tem dados com estes IDs:
**um ID daqui nunca some nem muda** sem conversão (remap) aprovada pelo Nikolas. ID novo entra aqui no mesmo PR.
Os testes conferem esta lista.

## Em uso

| ID | Tipo | Observação |
|---|---|---|
| `odteste:exposicao` | dado do jogador (inteiro 0–100) | sobrevive à morte |
| `odteste:lucidez` | dado do jogador (inteiro 0–lucidez_max) | sobrevive à morte |
| `odteste:lucidez_max` | dado do jogador (inteiro 10–100, padrão 100) | sobrevive à morte |
| `odteste:exposicao_resto` | dado do jogador (inteiro 0–99) | centésimos até completar 1 ponto |
| `odteste:contato_elemento` | dado do jogador (mapa elemento → inteiro) | elemento desconhecido é ignorado ao ler |
| `odteste:exposicao_repeticao` | dado do jogador (mapa `fonte/elemento` → [início, vezes]) | freio de repetição; fontes: `criatura_vista`, `criatura_luta`, `criatura_morta`, `reliquia_pegar`, `reliquia_usar`, `ritual`, `dimensao`, `aparicao` |
| `odteste:elemento` | dado do jogador (`nenhum`, `sangue`, `conhecimento`, `morte`, `energia`) | definido pelo admin; **nunca vai para o cliente** |
| `odteste:elemento_revelado` | dado do jogador (sim/não) | trocar o elemento volta para "não"; nunca vai para o cliente |
| `odteste:recebeu_kit` | dado do jogador (sim/não) | já recebeu crachá e dossiê na primeira entrada; `/odteste kit esquecer` zera; sobrevive à morte |
| `odteste:cracha` | item (pilha de 1) | Crachá do Gabinete; abre o cartão na tela ao usar; sem aba criativa (precisa de dono) |
| `odteste:cracha_dados` | componente de dados do item (dono UUID, nome, mestre, elemento só se revelado) | sincroniza com o cliente; o elemento só entra depois de revelado |
| `odteste:lucidez_lentidao` | modificador de velocidade (temporário, não salvo) | Lucidez Quebrando com luz 7 ou menos: −8% |
| `odteste:paranormal` | tag de tipo de dano e tag de criatura (hoje vazias) | dano paranormal pesa mais na Lucidez |
| `odteste:comida_quente` | tag de item (`"replace": false`) | comer na luz com o grupo devolve Lucidez |
| `odteste:area_horror` | pacote servidor → jogador | área da história em que ele está (`""` = nenhuma; hoje nenhuma registrada) |
| `odteste:susto` | pacote servidor → jogador | tipos (também do `/odteste susto <tipo> <jogador>`): `passos`, `porta`, `luz`, `sussurro`, `silencio` |
| `odteste:noite_escura` | camada da HUD (cliente) | véu de reserva da camada de horror (outro mod troca o céu, ou shader pack do Iris) |
| `odteste:tela_paranormal` | camada da HUD (cliente) | vinheta, escurecer e tremor dos sustos |
| `cor_horror` | shader `odteste:shaders/core/odfx_cor_horror` | tom do mundo; força pela camada de horror |
| `minecraft:overworld` (efeitos de dimensão, só cliente) | troca do céu/luz do overworld | não é salvo; se outro mod trocar também, vale o véu de reserva |
| `odteste_mestre` | tag vanilla do jogador | quem tem nunca leva susto sozinho |
| `odteste_historia` | dado do mundo (`data/odteste_historia.dat`) | o mod usa só `grupos` (`nome`, `membros`, `marcos`); **o resto do arquivo é guardado e escrito de volta igual** (vilas, âncoras, colocações com backup da Vila Calada etc.) |
| `odteste_livros_lidos` | dado do mundo (`data/odteste_livros_lidos.dat`) | quem já abriu cada livro |
| `odteste:livro` | chave do custom_data do livro de história (`minecraft:written_book`) | o valor é o id do livro = nome do arquivo em `data/odteste/odteste_livros/` (hoje `dossie`, entregue no kit) |
| `efeitos_fortes` | config do cliente (`config/odteste-client.toml`: `LIGADO`, `SUAVE`, `DESLIGADO`) | diminui, não desliga (`DESLIGADO` vale como Mínimo) |
| `intensidade` | config do cliente (mesmo arquivo; 50–100, padrão 100) | intensidade da camada de horror |
| `mestre.mestres` | config do servidor (`serverconfig/odteste-mestre-server.toml`, seção `[mestre]`) | nomes separados por vírgula; vazio = todo op |
| `mestre.ouvir_padrao` | config do servidor (mesmo arquivo) | mestre em espectador vê os sustos de quem assiste |
| `mestre.pular_cenas` | config do servidor (mesmo arquivo) | sem uso hoje (as cenas ficaram no mod antigo); fica para não sumir do arquivo |

### Giz e sigilos (pacote 1)

| ID | Tipo | Observação |
|---|---|---|
| `odteste:giz` | item (durabilidade 64) | giz único; sem aba criativa; botão direito no chão abre a tela dos sigilos aprendidos |
| `odteste:sigilo` | bloco (sem item, sem drop) | desenho riscado no chão, fino, sem colisão, quebra num golpe, luz 3; sem block entity |
| `simbolo` | propriedade do bloco `odteste:sigilo` | **42 nomes, para sempre**: `letra_a` a `letra_z`, `digito_0` a `digito_9`, `elemento_sangue`, `elemento_morte`, `elemento_conhecimento`, `elemento_energia`, `elemento_medo`, `ritual_poder` (arte v1 da Modelador) |
| `giro` | propriedade do bloco `odteste:sigilo` | 0 a 3 (quartos de volta) |
| `odteste:sigilos_conhecidos` | dado do jogador (lista de nomes de `simbolo`) | sigilos aprendidos; sobrevive à morte; nome desconhecido ou repetido some ao ler; **nunca vai para o cliente** (a tela recebe só a lista ao abrir) |
| `odteste:abrir_tela_sigilo` | pacote servidor → jogador | posição do bloco clicado e os sigilos aprendidos |
| `odteste:desenhar_sigilo` | pacote jogador → servidor | posição, sigilo e giro; o servidor confere giz, sigilo aprendido, distância (6 blocos), espaço livre e 1 pedido por segundo |
| `/odteste sigilo ensinar\|esquecer <jogador> <sigilo\|todos>` · `lista <jogador>` | comando de admin (permissão 2) | ensina/tira sigilo; método para eventos do mundo: `Sigilos.aprender(jogador, nome)` |

### Máquinas que falham (pacote 1)

| ID | Tipo | Observação |
|---|---|---|
| `odteste:reduz_falha_maquina` | tag de item (`"replace": false`, hoje vazia) | quem leva um item dela no inventário tem a chance de falha multiplicada por `fator_protecao_percentual`; a Dev põe o `fusivel_queimado` aqui. Outra forma de proteger (acessório): `FalhaMaquinas.registrarReducao(jogador -> fator)` |
| `odteste-maquinas-server.toml` | config do servidor (seção `[maquinas]`) | `ligado`, `chance_percentual` (4), `intervalo_maquina_s` (60), `espera_jogador_s` (600), `raio` (12), `chance_susto_percentual` (35), `fator_protecao_percentual` (50) |
| `mekanism`, `avaritia` | integrações opcionais (`Compat.Integracao`) | só por reflexão, em `compat/mekanism/` e `compat/reavaritia/`; o mod abre sem eles |

Marcos de grupo que o mundo já tem (salvos em `odteste_historia`): `primeiro_sinal`, `enigma_sino`, `vila_calada`, `dobre`.

## Guardados do mod antigo (registrados só para não serem apagados)

Dado de jogador sem registro some no primeiro save. Estes ficam em `dados/DadosGuardados`, sem uso:
`odteste:marca_elemento`, `odteste:marca_estagio`, `odteste:transcender_nivel`, `odteste:artefato_recarga`,
`odteste:criatura_noite`, `odteste:cansaco_elemento` (`{dia, brutas}`), `odteste:cenas_vistas`.

`odteste:dedal_coracao` (−1 coração do Dedal Dentado, salvo no jogador): o mod novo **tira** ao entrar.

## Saíram com o mod antigo (somem do mundo quando este jar entrar no servidor)

Itens `rubra`, `agulha_de_cerzir_carne`, `dedal_dentado`, `sino_de_matadouro`, `materia_sangue_bruta`,
`martelo_do_ferreiro`; blocos `vela_da_capela`, `poca_de_sangue`, `analisador_sinal`; criaturas `retalhado`,
`sanguessuga_mae`; efeito `sangramento`. Arquivos do mundo `odteste_vila_calada`, `odteste_gatilhos`,
`odteste_sinais` e as configs `odteste-server.toml` e `odteste-gatilhos-server.toml` ficam no disco sem uso.
Lista completa: `docs/ids.md` do Od-Teste, commit `b488cc6`.
