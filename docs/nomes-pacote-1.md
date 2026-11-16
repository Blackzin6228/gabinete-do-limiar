# Nomes oficiais do pacote 1

Reforma de estilo, decisão do Nikolas de 15/11/2026 (22h18): nomes técnicos, de investigação, no padrão de Ordem
Paranormal ("Sigilo de Sangue", "Faca de Ritual"), no lugar dos nomes poéticos. Esta tabela **vale no lugar** de
qualquer nome em `docs/reliquias-v1.md` e `docs/rituais.md`. O objeto de cada item continua o mesmo; só nome, ID e texto mudam.

- **Na tela, "Lucidez" passa a se chamar "Sanidade"** (só o texto: o ID `odteste:lucidez`, o dado e o comando não mudam).
  No en_us, "Lucidity" vira "Sanity". "Exposição" / "Exposure" ficam; "NEX" só aparece como explicação em documento.
- IDs novos são `odteste:<id>`. Os 16 itens do pacote 1 trocam de ID (nada foi para o servidor, então dá para trocar).
  Tradução: `item.odteste.<id>` é o nome; o tooltip é `item.odteste.<id>.desc` (linha 1) e `.preco` (linha 2).
  `<br>` na tabela = quebra de linha.
- **Giz** mantém o ID `giz` (ainda não existe no código). Os sigilos abaixo são os valores de `simbolo` do bloco
  `odteste:sigilo` (ver `docs/rituais.md`); a chave de tradução fica a cargo da mesa Dev.
- Os 5 sigilos de **tipo de ritual** são proposta minha: `docs/rituais.md` só tem o tipo `poder`, e `invocar` está fora da
  v1. Nikolas confirma a lista antes de virar código.
- O efeito dos sigilos de **elemento** é o que a mesa Dev precisa implementar (alvo: criaturas num raio pequeno com teto;
  o custo aparece só por som, tela e corpo, nunca em número). Medo não tem relíquia, mas tem sigilo.
- Nenhum nome de personagem do canon. Nenhum número em tooltip.

### Espadas (4)
| ID novo | pt_br | en_us | Tooltip pt_br | Tooltip en_us |
|---|---|---|---|---|
| `facao_de_ritual_de_sangue` | Facão de Ritual de Sangue | Ritual Blood Cleaver | Só responde a quem é de Sangue. Dano alto; cada golpe faz sangrar.<br>Cobra Sanidade e deixa a Exposição subir. | Answers only to those of Blood. Heavy damage; every hit makes the target bleed.<br>It costs Sanity and raises your Exposure. |
| `sabre_de_ritual_de_morte` | Sabre de Ritual de Morte | Ritual Death Saber | Só responde a quem é de Morte. Encerra de vez a criatura que já está por um fio.<br>Nunca funciona em jogador. | Answers only to those of Death. Finishes off a creature already hanging by a thread.<br>Never works on players. |
| `espada_de_ritual_de_conhecimento` | Espada de Ritual de Conhecimento | Ritual Sword of Knowledge | Só responde a quem é de Conhecimento. Quem é atingido fica marcado e sofre mais dano.<br>Julga uma criatura por vez. | Answers only to those of Knowledge. Whoever is struck is marked and takes more damage.<br>It judges one creature at a time. |
| `espeto_de_ritual_de_energia` | Espeto de Ritual de Energia | Ritual Spit of Energy | Só responde a quem é de Energia. Os golpes enchem uma carga que se solta em cadeia e atordoa.<br>Cheira a ozônio. | Answers only to those of Energy. Hits build a charge that discharges in a chain and stuns.<br>It smells of ozone. |

### Artefatos (12)
| ID novo | pt_br | en_us | Tooltip pt_br | Tooltip en_us |
|---|---|---|---|---|
| `seringa_de_coleta_ritual` | Seringa de Coleta Ritual | Ritual Collection Syringe | Usar: troca um pouco do seu sangue por Força.<br>Custa vida. O Outro Lado percebe. | Use: trades some of your blood for Strength.<br>It costs health. The Other Side notices. |
| `luva_de_abate_ritual` | Luva de Abate Ritual | Ritual Slaughter Glove | Seus golpes comuns fazem sangrar.<br>Cada golpe dá fome. | Your ordinary hits make targets bleed.<br>Every hit makes you hungry. |
| `medalha_do_sangue_doado` | Medalha do Sangue Doado | Medal of Given Blood | Com a vida por um fio, ela volta aos poucos.<br>Cada retorno cobra Sanidade. | With your health hanging by a thread, it returns little by little.<br>Every return costs Sanity. |
| `oculos_de_visao_paranormal` | Óculos de Visão Paranormal | Paranormal Vision Glasses | Mostra o que não quer ser visto, até o que está invisível.<br>Quem olha também é olhado: a Exposição sobe. | Shows what does not want to be seen, even the invisible.<br>Whoever looks is looked at: Exposure rises. |
| `moeda_de_obito` | Moeda de Óbito | Death Certificate Coin | Uma vez por noite, a morte passa reto e os monstros esquecem você.<br>Cobra Sanidade e esfria até a noite seguinte. | Once per night, death passes you by and monsters forget you.<br>It costs Sanity and stays cold until the next night. |
| `vela_de_rito_funebre` | Vela de Rito Fúnebre | Funeral Rite Candle | Acesa, enfraquece os mortos que andam por perto.<br>A vela se gasta. Cova pesa na Sanidade. | Lit, it weakens the walking dead nearby.<br>The candle burns down. Graves wear on your Sanity. |
| `lupa_de_investigacao_paranormal` | Lupa de Investigação Paranormal | Paranormal Investigation Magnifier | Nos livros, revela trechos que estavam escondidos.<br>Cada trecho revelado cobra Sanidade. | In books, it reveals passages that were hidden.<br>Every revealed passage costs Sanity. |
| `caneta_de_registro_apagado` | Caneta de Registro Apagado | Erased Record Pen | Magias de Conhecimento recarregam mais rápido.<br>Faz esquecer: apaga uma marcação do mapa ao usar. | Knowledge spells recharge faster.<br>It makes you forget: it erases a map marker when used. |
| `fita_de_gravacao_anomala` | Fita de Gravação Anômala | Anomalous Recording Tape | Uma vez por noite, revela uma verdade a quem a toca com um livro na mão.<br>A resposta sempre traz uma frase a mais. | Once per night, reveals a truth to whoever plays it holding a book.<br>The answer always carries one sentence too many. |
| `celular_de_sinal_anomalo` | Celular de Sinal Anômalo | Anomalous Signal Phone | Recarrega seus aparelhos. Usar: um pulso atordoa quem está perto.<br>Se gasta. De noite, toca sozinho. | Recharges your devices. Use: a pulse stuns whoever is close.<br>It wears out. At night, it rings by itself. |
| `fusivel_sobrecarregado` | Fusível Sobrecarregado | Overloaded Fuse | Máquinas por perto falham menos.<br>Quando uma máquina falha, o paranormal percebe. | Nearby machines fail less often.<br>When a machine fails, the paranormal takes notice. |
| `radio_de_interferencia` | Rádio de Interferência | Interference Radio | O chiado aumenta quando o perigo chega perto.<br>O silêncio pesa na Sanidade. Há vozes no chiado. | The static grows louder when danger draws near.<br>Silence weighs on your Sanity. There are voices in the static. |

### Giz (1)
| ID novo | pt_br | en_us | Tooltip pt_br | Tooltip en_us |
|---|---|---|---|---|
| `giz` | Giz Ritualístico | Ritual Chalk | Desenha sigilos e círculos no chão. Só serve para rituais.<br>Se gasta a cada traço. | Draws sigils and circles on the floor. Good only for rituals.<br>It wears down with every stroke. |

### Sigilos de elemento (5)
| ID novo | pt_br | en_us | Tooltip pt_br | Tooltip en_us |
|---|---|---|---|---|
| `sigilo_de_sangue` | Sigilo de Sangue | Blood Sigil | Causa dano às criaturas ao redor e dá Força a quem ativa.<br>Cobra vida e Sanidade. | Damages creatures around it and grants Strength to whoever activates it.<br>It costs health and Sanity. |
| `sigilo_de_morte` | Sigilo de Morte | Death Sigil | Faz as criaturas ao redor definharem.<br>Cobra Sanidade e sobe a Exposição. | Makes creatures around it wither.<br>It costs Sanity and raises Exposure. |
| `sigilo_de_conhecimento` | Sigilo de Conhecimento | Knowledge Sigil | Dá visão noturna a quem ativa e revela as entidades por perto com um brilho.<br>Cobra Sanidade. | Grants night vision to whoever activates it and reveals nearby entities with a glow.<br>It costs Sanity. |
| `sigilo_de_energia` | Sigilo de Energia | Energy Sigil | Dá velocidade a quem ativa e atordoa as criaturas ao redor.<br>Cobra Sanidade e sobe a Exposição. | Grants speed to whoever activates it and stuns creatures around it.<br>It costs Sanity and raises Exposure. |
| `sigilo_de_medo` | Sigilo de Medo | Fear Sigil | Cega e deixa lentas as criaturas ao redor.<br>Cobra Sanidade e sobe a Exposição. | Blinds and slows creatures around it.<br>It costs Sanity and raises Exposure. |

### Sigilos de tipo de ritual (5)
| ID novo | pt_br | en_us | Tooltip pt_br | Tooltip en_us |
|---|---|---|---|---|
| `sigilo_de_invocacao` | Sigilo de Invocação | Summoning Sigil | Chama algo do Outro Lado.<br>Quem chama paga a conta. | Calls something from the Other Side.<br>Whoever calls pays the price. |
| `sigilo_de_protecao` | Sigilo de Proteção | Protection Sigil | Protege quem está no círculo contra o paranormal.<br>Não protege de dano comum. | Shields those inside the circle from the paranormal.<br>It does not stop ordinary damage. |
| `sigilo_de_banimento` | Sigilo de Banimento | Banishment Sigil | Empurra de volta o que veio do Outro Lado.<br>Cobra Sanidade. | Pushes back what came from the Other Side.<br>It costs Sanity. |
| `sigilo_de_passagem` | Sigilo de Passagem | Passage Sigil | Abre uma passagem frágil na Membrana.<br>Quem passa volta diferente. | Opens a fragile passage in the Membrane.<br>Whoever crosses comes back changed. |
| `sigilo_de_revelacao` | Sigilo de Revelação | Revelation Sigil | Mostra o que a Membrana esconde.<br>Cobra Sanidade. | Shows what the Membrane hides.<br>It costs Sanity. |

### Sigilos de letra (26)
| ID novo | pt_br | en_us | Tooltip pt_br | Tooltip en_us |
|---|---|---|---|---|
| `sigilo_a` | Sigilo A | Sigil A | Letra A do alfabeto ritual. Sozinha não faz nada. | Letter A of the ritual alphabet. On its own it does nothing. |
| `sigilo_b` | Sigilo B | Sigil B | Letra B do alfabeto ritual. Sozinha não faz nada. | Letter B of the ritual alphabet. On its own it does nothing. |
| `sigilo_c` | Sigilo C | Sigil C | Letra C do alfabeto ritual. Sozinha não faz nada. | Letter C of the ritual alphabet. On its own it does nothing. |
| `sigilo_d` | Sigilo D | Sigil D | Letra D do alfabeto ritual. Sozinha não faz nada. | Letter D of the ritual alphabet. On its own it does nothing. |
| `sigilo_e` | Sigilo E | Sigil E | Letra E do alfabeto ritual. Sozinha não faz nada. | Letter E of the ritual alphabet. On its own it does nothing. |
| `sigilo_f` | Sigilo F | Sigil F | Letra F do alfabeto ritual. Sozinha não faz nada. | Letter F of the ritual alphabet. On its own it does nothing. |
| `sigilo_g` | Sigilo G | Sigil G | Letra G do alfabeto ritual. Sozinha não faz nada. | Letter G of the ritual alphabet. On its own it does nothing. |
| `sigilo_h` | Sigilo H | Sigil H | Letra H do alfabeto ritual. Sozinha não faz nada. | Letter H of the ritual alphabet. On its own it does nothing. |
| `sigilo_i` | Sigilo I | Sigil I | Letra I do alfabeto ritual. Sozinha não faz nada. | Letter I of the ritual alphabet. On its own it does nothing. |
| `sigilo_j` | Sigilo J | Sigil J | Letra J do alfabeto ritual. Sozinha não faz nada. | Letter J of the ritual alphabet. On its own it does nothing. |
| `sigilo_k` | Sigilo K | Sigil K | Letra K do alfabeto ritual. Sozinha não faz nada. | Letter K of the ritual alphabet. On its own it does nothing. |
| `sigilo_l` | Sigilo L | Sigil L | Letra L do alfabeto ritual. Sozinha não faz nada. | Letter L of the ritual alphabet. On its own it does nothing. |
| `sigilo_m` | Sigilo M | Sigil M | Letra M do alfabeto ritual. Sozinha não faz nada. | Letter M of the ritual alphabet. On its own it does nothing. |
| `sigilo_n` | Sigilo N | Sigil N | Letra N do alfabeto ritual. Sozinha não faz nada. | Letter N of the ritual alphabet. On its own it does nothing. |
| `sigilo_o` | Sigilo O | Sigil O | Letra O do alfabeto ritual. Sozinha não faz nada. | Letter O of the ritual alphabet. On its own it does nothing. |
| `sigilo_p` | Sigilo P | Sigil P | Letra P do alfabeto ritual. Sozinha não faz nada. | Letter P of the ritual alphabet. On its own it does nothing. |
| `sigilo_q` | Sigilo Q | Sigil Q | Letra Q do alfabeto ritual. Sozinha não faz nada. | Letter Q of the ritual alphabet. On its own it does nothing. |
| `sigilo_r` | Sigilo R | Sigil R | Letra R do alfabeto ritual. Sozinha não faz nada. | Letter R of the ritual alphabet. On its own it does nothing. |
| `sigilo_s` | Sigilo S | Sigil S | Letra S do alfabeto ritual. Sozinha não faz nada. | Letter S of the ritual alphabet. On its own it does nothing. |
| `sigilo_t` | Sigilo T | Sigil T | Letra T do alfabeto ritual. Sozinha não faz nada. | Letter T of the ritual alphabet. On its own it does nothing. |
| `sigilo_u` | Sigilo U | Sigil U | Letra U do alfabeto ritual. Sozinha não faz nada. | Letter U of the ritual alphabet. On its own it does nothing. |
| `sigilo_v` | Sigilo V | Sigil V | Letra V do alfabeto ritual. Sozinha não faz nada. | Letter V of the ritual alphabet. On its own it does nothing. |
| `sigilo_w` | Sigilo W | Sigil W | Letra W do alfabeto ritual. Sozinha não faz nada. | Letter W of the ritual alphabet. On its own it does nothing. |
| `sigilo_x` | Sigilo X | Sigil X | Letra X do alfabeto ritual. Sozinha não faz nada. | Letter X of the ritual alphabet. On its own it does nothing. |
| `sigilo_y` | Sigilo Y | Sigil Y | Letra Y do alfabeto ritual. Sozinha não faz nada. | Letter Y of the ritual alphabet. On its own it does nothing. |
| `sigilo_z` | Sigilo Z | Sigil Z | Letra Z do alfabeto ritual. Sozinha não faz nada. | Letter Z of the ritual alphabet. On its own it does nothing. |

### Sigilos de dígito (10)
| ID novo | pt_br | en_us | Tooltip pt_br | Tooltip en_us |
|---|---|---|---|---|
| `sigilo_0` | Sigilo 0 | Sigil 0 | Dígito 0 do código ritual. Sozinho não faz nada. | Digit 0 of the ritual code. On its own it does nothing. |
| `sigilo_1` | Sigilo 1 | Sigil 1 | Dígito 1 do código ritual. Sozinho não faz nada. | Digit 1 of the ritual code. On its own it does nothing. |
| `sigilo_2` | Sigilo 2 | Sigil 2 | Dígito 2 do código ritual. Sozinho não faz nada. | Digit 2 of the ritual code. On its own it does nothing. |
| `sigilo_3` | Sigilo 3 | Sigil 3 | Dígito 3 do código ritual. Sozinho não faz nada. | Digit 3 of the ritual code. On its own it does nothing. |
| `sigilo_4` | Sigilo 4 | Sigil 4 | Dígito 4 do código ritual. Sozinho não faz nada. | Digit 4 of the ritual code. On its own it does nothing. |
| `sigilo_5` | Sigilo 5 | Sigil 5 | Dígito 5 do código ritual. Sozinho não faz nada. | Digit 5 of the ritual code. On its own it does nothing. |
| `sigilo_6` | Sigilo 6 | Sigil 6 | Dígito 6 do código ritual. Sozinho não faz nada. | Digit 6 of the ritual code. On its own it does nothing. |
| `sigilo_7` | Sigilo 7 | Sigil 7 | Dígito 7 do código ritual. Sozinho não faz nada. | Digit 7 of the ritual code. On its own it does nothing. |
| `sigilo_8` | Sigilo 8 | Sigil 8 | Dígito 8 do código ritual. Sozinho não faz nada. | Digit 8 of the ritual code. On its own it does nothing. |
| `sigilo_9` | Sigilo 9 | Sigil 9 | Dígito 9 do código ritual. Sozinho não faz nada. | Digit 9 of the ritual code. On its own it does nothing. |
