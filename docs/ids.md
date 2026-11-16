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
| `odteste:facao_de_ritual_de_sangue`, `odteste:sabre_de_ritual_de_morte`, `odteste:espada_de_ritual_de_conhecimento`, `odteste:espeto_de_ritual_de_energia` | itens (espadas, pilha de 1, inquebráveis) | sem receita: só aba do criativo e `/give`; só funcionam para quem é do elemento (Sangue, Morte, Conhecimento, Energia). Antes: `sede_de_domingo`, `sabre_do_ultimo_adeus`, `sentenca_sem_recurso`, `espeto_da_casa_cheia` |
| `odteste:seringa_de_coleta_ritual`, `odteste:luva_de_abate_ritual`, `odteste:medalha_do_sangue_doado`, `odteste:oculos_de_visao_paranormal`, `odteste:moeda_de_obito`, `odteste:vela_de_rito_funebre`, `odteste:lupa_de_investigacao_paranormal`, `odteste:caneta_de_registro_apagado`, `odteste:fita_de_gravacao_anomala`, `odteste:celular_de_sinal_anomalo`, `odteste:fusivel_sobrecarregado`, `odteste:radio_de_interferencia` | itens (artefatos) | qualquer jogador; slot `charm` do Curios (tag `curios:charm`, `data/odteste/curios/entities/artefatos.json`); Vela e Celular têm durabilidade. Antes: `seringa_velha`, `luva_de_acougueiro`, `medalha_de_doador`, `oculos_de_velorio`, `moeda_de_defunto`, `vela_de_setimo_dia`, `lupa_do_arquivo_morto`, `caneta_sem_tinta`, `fita_sem_etiqueta`, `uma_barra_de_sinal`, `fusivel_queimado`, `radinho_de_pilha` |
| `odteste:gabinete` | aba do criativo | espadas e artefatos |
| `odteste:sangramento` | efeito (volta do mod antigo) | dano leve por segundo; só em criaturas, nunca em jogador |
| `odteste:noite_usada` | componente de dados do item (número da noite) | poder "1x por noite" da Moeda de Óbito e da Espada de Ritual de Conhecimento |
| `odteste:artefato_no_bau` | tipo de loot modifier | arquivos `odteste:<artefato>` em `data/odteste/loot_modifiers/` (um por artefato, lista de baús vanilla), listados em `data/neoforge/loot_modifiers/global_loot_modifiers.json` |
| `odteste:contorno` | pacote servidor → jogador | ids de criaturas + tempo: contorno só na tela dele (Óculos, Espada de Conhecimento) |
| `odteste:reduz_falha_maquina` | tag de item (`"replace": false`) | hoje só o `odteste:fusivel_sobrecarregado`; quem leva um item dela tem menos chance de a máquina falhar (a regra da máquina é da `pacote-1/sistemas`) |
| `odteste-artefatos-server.toml` / `chance_bau` | config do servidor (`serverconfig/`, seção `[artefatos]`; 0–1, padrão 0.02) | chance de um artefato num baú que combina |
| `efeitos_fortes` | config do cliente (`config/odteste-client.toml`: `LIGADO`, `SUAVE`, `DESLIGADO`) | diminui, não desliga (`DESLIGADO` vale como Mínimo) |
| `intensidade` | config do cliente (mesmo arquivo; 50–100, padrão 100) | intensidade da camada de horror |
| `mestre.mestres` | config do servidor (`serverconfig/odteste-mestre-server.toml`, seção `[mestre]`) | nomes separados por vírgula; vazio = todo op |
| `mestre.ouvir_padrao` | config do servidor (mesmo arquivo) | mestre em espectador vê os sustos de quem assiste |
| `mestre.pular_cenas` | config do servidor (mesmo arquivo) | sem uso hoje (as cenas ficaram no mod antigo); fica para não sumir do arquivo |

Marcos de grupo que o mundo já tem (salvos em `odteste_historia`): `primeiro_sinal`, `enigma_sino`, `vila_calada`, `dobre`.

## Guardados do mod antigo (registrados só para não serem apagados)

Dado de jogador sem registro some no primeiro save. Estes ficam em `dados/DadosGuardados`, sem uso:
`odteste:marca_elemento`, `odteste:marca_estagio`, `odteste:transcender_nivel`, `odteste:artefato_recarga`,
`odteste:criatura_noite`, `odteste:cansaco_elemento` (`{dia, brutas}`), `odteste:cenas_vistas`.

`odteste:dedal_coracao` (−1 coração do Dedal Dentado, salvo no jogador): o mod novo **tira** ao entrar.

## Saíram com o mod antigo (somem do mundo quando este jar entrar no servidor)

Itens `rubra`, `agulha_de_cerzir_carne`, `dedal_dentado`, `sino_de_matadouro`, `materia_sangue_bruta`,
`martelo_do_ferreiro`; blocos `vela_da_capela`, `poca_de_sangue`, `analisador_sinal`; criaturas `retalhado`,
`sanguessuga_mae` (o efeito `sangramento` voltou no pacote 1). Arquivos do mundo `odteste_vila_calada`, `odteste_gatilhos`,
`odteste_sinais` e as configs `odteste-server.toml` e `odteste-gatilhos-server.toml` ficam no disco sem uso.
Lista completa: `docs/ids.md` do Od-Teste, commit `b488cc6`.

## Nomes oficiais do pacote 1 (reforma de estilo, 15/11/2026)

Nomes técnicos no lugar dos poéticos; tooltips completos em `docs/nomes-pacote-1.md`. Os IDs de giz e sigilos abaixo são **planejados** (ainda sem código); entram no jogo pelo PR do ritual. Na tela, Lucidez se chama Sanidade (só o texto).

| ID novo | pt_br | en_us |
|---|---|---|
| `facao_de_ritual_de_sangue` | Facão de Ritual de Sangue | Ritual Blood Cleaver |
| `sabre_de_ritual_de_morte` | Sabre de Ritual de Morte | Ritual Death Saber |
| `espada_de_ritual_de_conhecimento` | Espada de Ritual de Conhecimento | Ritual Sword of Knowledge |
| `espeto_de_ritual_de_energia` | Espeto de Ritual de Energia | Ritual Spit of Energy |
| `seringa_de_coleta_ritual` | Seringa de Coleta Ritual | Ritual Collection Syringe |
| `luva_de_abate_ritual` | Luva de Abate Ritual | Ritual Slaughter Glove |
| `medalha_do_sangue_doado` | Medalha do Sangue Doado | Medal of Given Blood |
| `oculos_de_visao_paranormal` | Óculos de Visão Paranormal | Paranormal Vision Glasses |
| `moeda_de_obito` | Moeda de Óbito | Death Certificate Coin |
| `vela_de_rito_funebre` | Vela de Rito Fúnebre | Funeral Rite Candle |
| `lupa_de_investigacao_paranormal` | Lupa de Investigação Paranormal | Paranormal Investigation Magnifier |
| `caneta_de_registro_apagado` | Caneta de Registro Apagado | Erased Record Pen |
| `fita_de_gravacao_anomala` | Fita de Gravação Anômala | Anomalous Recording Tape |
| `celular_de_sinal_anomalo` | Celular de Sinal Anômalo | Anomalous Signal Phone |
| `fusivel_sobrecarregado` | Fusível Sobrecarregado | Overloaded Fuse |
| `radio_de_interferencia` | Rádio de Interferência | Interference Radio |
| `giz` | Giz Ritualístico | Ritual Chalk |
| `sigilo_de_sangue` | Sigilo de Sangue | Blood Sigil |
| `sigilo_de_morte` | Sigilo de Morte | Death Sigil |
| `sigilo_de_conhecimento` | Sigilo de Conhecimento | Knowledge Sigil |
| `sigilo_de_energia` | Sigilo de Energia | Energy Sigil |
| `sigilo_de_medo` | Sigilo de Medo | Fear Sigil |
| `sigilo_de_invocacao` | Sigilo de Invocação | Summoning Sigil |
| `sigilo_de_protecao` | Sigilo de Proteção | Protection Sigil |
| `sigilo_de_banimento` | Sigilo de Banimento | Banishment Sigil |
| `sigilo_de_passagem` | Sigilo de Passagem | Passage Sigil |
| `sigilo_de_revelacao` | Sigilo de Revelação | Revelation Sigil |
| `sigilo_a` | Sigilo A | Sigil A |
| `sigilo_b` | Sigilo B | Sigil B |
| `sigilo_c` | Sigilo C | Sigil C |
| `sigilo_d` | Sigilo D | Sigil D |
| `sigilo_e` | Sigilo E | Sigil E |
| `sigilo_f` | Sigilo F | Sigil F |
| `sigilo_g` | Sigilo G | Sigil G |
| `sigilo_h` | Sigilo H | Sigil H |
| `sigilo_i` | Sigilo I | Sigil I |
| `sigilo_j` | Sigilo J | Sigil J |
| `sigilo_k` | Sigilo K | Sigil K |
| `sigilo_l` | Sigilo L | Sigil L |
| `sigilo_m` | Sigilo M | Sigil M |
| `sigilo_n` | Sigilo N | Sigil N |
| `sigilo_o` | Sigilo O | Sigil O |
| `sigilo_p` | Sigilo P | Sigil P |
| `sigilo_q` | Sigilo Q | Sigil Q |
| `sigilo_r` | Sigilo R | Sigil R |
| `sigilo_s` | Sigilo S | Sigil S |
| `sigilo_t` | Sigilo T | Sigil T |
| `sigilo_u` | Sigilo U | Sigil U |
| `sigilo_v` | Sigilo V | Sigil V |
| `sigilo_w` | Sigilo W | Sigil W |
| `sigilo_x` | Sigilo X | Sigil X |
| `sigilo_y` | Sigilo Y | Sigil Y |
| `sigilo_z` | Sigilo Z | Sigil Z |
| `sigilo_0` | Sigilo 0 | Sigil 0 |
| `sigilo_1` | Sigilo 1 | Sigil 1 |
| `sigilo_2` | Sigilo 2 | Sigil 2 |
| `sigilo_3` | Sigilo 3 | Sigil 3 |
| `sigilo_4` | Sigilo 4 | Sigil 4 |
| `sigilo_5` | Sigilo 5 | Sigil 5 |
| `sigilo_6` | Sigilo 6 | Sigil 6 |
| `sigilo_7` | Sigilo 7 | Sigil 7 |
| `sigilo_8` | Sigilo 8 | Sigil 8 |
| `sigilo_9` | Sigilo 9 | Sigil 9 |
