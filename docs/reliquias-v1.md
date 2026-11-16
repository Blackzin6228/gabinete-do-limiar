# Relíquias v1

Lore e regras, já com as respostas do Nikolas de 15/11/2026 (19h40 e 20h10). **As 4 espadas e os 12 artefatos viraram
código no pacote 1** (branch `pacote-1/itens`; IDs em `docs/ids.md`). Os outros IDs daqui ainda são sugestão. Nomes de entidades e casos de Ordem Paranormal podem ser
usados; texto, arte, símbolos e **nomes de personagens** do livro não. Onde não tenho certeza do que o livro diz, está
marcado [conferir no livro] e a ficha usa só a nossa versão.

**Nomes oficiais (reforma de estilo, 15/11/2026): `docs/nomes-pacote-1.md`.** Os nomes e IDs poéticos deste documento ficaram para trás.

## O que é cada coisa
- **Relíquias** = as 4 **espadas** + as **máscaras** das entidades (seção "Máscaras"). Sem craft: só saem por comando
  ou pela aba do criativo.
- **Artefatos** = 12 itens que **qualquer jogador** pega e usa (seção "12 artefatos").
- **Materiais** e **máquinas que falham** = apoio.

## Regras que valem para tudo
- **Ligações das espadas** (decisão do Nikolas, mesmo se diferir do livro): Sangue = Diabo, Energia = Anfitrião,
  Morte = Deus da Morte, Conhecimento = Magistrada.
- O **Medo** é a força maior e não tem relíquia.
- Nada aparece em número para o jogador: só som, tela, corpo e fome.
- Efeito de tela passa pela `FxTela` e diminui com `efeitos_fortes`.
- **Sem peso por tick.** Tudo acontece ao bater, usar ou trocar de item. Checagem do portador no máximo a cada 5 s,
  só dele, nunca de todos.

---

## Espadas (4)
**Por enquanto não são dos jogadores.** Só usa quem está no **criativo interpretando a entidade** (o mestre/ator
fazendo o Diabo, o Anfitrião etc.). Sem craft: saem só por comando ou pela aba do criativo. **Só funcionam para quem é
do elemento delas**; para os outros elementos não fazem nada. Os outros pontos (custo, corrupção, selamento, gancho)
saíram desta versão: voltam se o Nikolas decidir que as espadas passam a ser dos jogadores.

### 1. Sede de Domingo (Sangue, o Diabo)
ID `odteste:sede_de_domingo` · espada (facão de desossa)
- **Aparência:** facão de açougue de lâmina larga, afiado tantas vezes que a lâmina virou meia-lua. Cabo de madeira
  escurecido de suor, amarrado com fita adesiva. Cheira a ferro. 16x16: lâmina cinza-clara curva, cabo marrom, três
  pixels vermelhos secos perto da ponta.
- **Origem:** Seu Hélio, açougueiro, matou a esposa numa manhã de domingo, depois de abater a carne da semana. Disse
  que o facão "estava com sede" e que tinha feito um trato num cruzamento. Sumiu do depósito da delegacia. Caso 0412
  (nosso).
- **Entidade:** o Diabo [conferir no livro: como o Diabo se liga a Sangue]. Aqui é quem cobra pelo que deu.
- **Efeito:** dano alto e rápido. A cada 4 acertos em criatura viva, o portador recupera 1 coração (no máximo 2
  corações por 30 s). Acertos seguidos deixam o alvo sangrando (dano leve por alguns segundos). Pedra e ferro não
  sangram.

### 2. Sabre do Último Adeus (Morte, o Deus da Morte)
ID `odteste:sabre_do_ultimo_adeus` · espada (sabre de cerimônia)
- **Aparência:** sabre de gala de guarda de honra, bainha de couro rachada, lâmina manchada de anos em armário. Nome
  riscado a canivete no punho. 16x16: sabre fino e reto, guarda dourada fosca, ponta preta.
- **Origem:** o cabo Antônio carregava o caixão de colegas em enterros de corporação. Numa semana de enchente seguiu
  11 enterros e não saiu mais de casa. Achado ao lado dele, com uma lista de nomes que "ainda faltava". Caso 0388
  (nosso).
- **Entidade:** o Deus da Morte [conferir no livro]. Aqui quer o final de cada coisa, não a dor.
- **Efeito:** o primeiro golpe em criatura com pouca vida a acaba na hora ("o adeus"), recarga longa. Contra o resto, é
  uma espada boa e comum. Nunca funciona em jogador.

### 3. Sentença Sem Recurso (Conhecimento, a Magistrada)
ID `odteste:sentenca_sem_recurso` · espada (espada de fórum, de toga)
- **Aparência:** espada reta de solenidade de tribunal, cabo de bronze com balança gasta, lâmina sem fio. Trechos de
  processo gravados fininhos, quase ilegíveis, que mudam de lugar quando ninguém olha. 16x16: espada cinza-azulada,
  balança de dois pixels amarelos no punho, traços escuros na lâmina.
- **Origem:** a juíza Cecília (nossa) julgava em segredo, de madrugada, processos sem número. Todas as sentenças
  estavam certas. Ninguém achou as partes. Ela deixou a espada sobre a mesa, com a última sentença em branco. Caso 0275
  (nosso).
- **Entidade:** a Magistrada [conferir no livro: natureza da Magistrada e ligação com Conhecimento]. Aqui é quem sabe de
  tudo e julga.
- **Efeito:** o primeiro golpe em uma criatura a "julga": o portador passa a ver o contorno dela (só no cliente dele) e
  as pancadas seguintes nela causam mais dano. Um julgamento por vez. Segurar a espada com livro ou mapa na outra mão
  revela um trecho escondido, uma vez por noite do jogo. Nunca julga jogador.

### 4. Espeto da Casa Cheia (Energia, o Anfitrião)
ID `odteste:espeto_da_casa_cheia` · espada (espeto de churrasco, longo)
- **Aparência:** espeto de churrasco comprido, de aço, cabo de madeira queimado, com um fio de gambiarra enrolado e
  preso por durex. Cheira a carvão e ozônio. 16x16: espeto cinza fino na diagonal, fio laranja no cabo, um pixel azul
  na ponta.
- **Origem:** Seu Dirceu fazia churrasco todo sábado, sempre com a casa cheia, e nunca deixava a luz cair. A gambiarra
  eletrocutou sete convidados, que continuaram na mesa. Os vizinhos só estranharam que ninguém saía. Caso 0501 (nosso).
- **Entidade:** o Anfitrião [conferir no livro: ligação do Anfitrião com Energia]. Aqui é quem convida e faz o convidado
  querer ficar.
- **Efeito:** cada acerto enche uma carga. Com a carga cheia, o próximo acerto solta uma descarga em cadeia (até 3
  criaturas perto, tonteira de 3 s). Com aliados do grupo por perto, a carga enche mais rápido. Segurar o espeto
  recarrega aos poucos itens com energia do inventário (a cada 5 s, só no portador).

---

## Máscaras (aguardando o Nikolas)
Relíquias de rosto: as máscaras das entidades (Diabo, Anfitrião, Magistrada…). O Nikolas vai mandar como quer a
modelagem e depois decide como funcionam. Nada definido aqui: nem lista, nem efeito, nem ID.

---

## 12 artefatos
**Qualquer jogador pega e usa.** Todos são **achados em baús** de dungeons e estruturas (loot): não saem de ritual,
máquina, entidade nem de lugar fixo. A coluna "Onde se acha" só diz que tipo de lugar combina; **não cria
estrutura nem ID**. Não há trava nem penalidade por elemento. Cada artefato tem um **buff** e um
**debuff**. Ficam no **slot de acessório do Curios**: Curios a confirmar no pack (a lista base do Conguitos-ATM10 só
aponta o zip do ATM10 8.5 e não deixa ver os mods). "Afinidade" aumenta a força das magias daquele elemento (escola
própria no Iron's Spells [conferir na versão do Iron's do pack], decisão de 14/11). Dois artefatos do mesmo elemento
somam com freio (o terceiro rende pouco). Nenhum tem nome de personagem.

| # | Nome | Elemento | Objeto (16x16) | Buff | Debuff | Onde se acha (baú) |
|---|------|----------|----------------|------|--------|--------------|
| 1 | Seringa Velha | Sangue | seringa de vidro, êmbolo marrom, líquido escuro | afinidade; usar sacrifica 1 coração para dar Força por 30 s | vida; Exposição | baú de dungeon/estrutura (combina: hospital ou clínica abandonada) |
| 2 | Luva de Açougueiro | Sangue | luva de borracha amarela, dedos manchados | afinidade; golpes comuns também sangram | fome em cada acerto | baú de dungeon/estrutura (combina: açougue ou frigorífico abandonado) |
| 3 | Medalha de Doador | Sangue | medalha com gota vermelha, fita gasta | afinidade; abaixo de 3 corações regenera aos poucos | Lucidez sobe de custo quando regenera | baú de dungeon/estrutura (combina: posto de coleta, abrigo) |
| 4 | Óculos de Velório | Morte | óculos de grau, aro preto, lente rachada | afinidade; vê o paranormal (contorno só no cliente, invisíveis aparecem) | Exposição enquanto usa; visões | baú de dungeon/estrutura (combina: cemitério, capela) |
| 5 | Moeda de Defunto | Morte | moeda escura sobre dois pixels de olho | afinidade; 1x por noite, golpe fatal deixa meio coração e monstros esquecem você 5 s | Lucidez; esfria até a noite seguinte | baú de dungeon/estrutura (combina: ruína, vala, casa de velório) |
| 6 | Vela de Sétimo Dia | Morte | toco de vela branca, chama azul | afinidade; acesa, enfraquece mortos-vivos perto | vela gasta durabilidade; Lucidez em cova | baú de dungeon/estrutura (combina: capela, casa de velório) |
| 7 | Lupa do Arquivo Morto | Conhecimento | lupa de latão, vidro embaçado | afinidade; revela trechos escondidos de livros | Lucidez por revelação | baú de dungeon/estrutura (combina: arquivo, biblioteca) |
| 8 | Caneta Sem Tinta | Conhecimento | caneta esferográfica seca, tampa mordida | afinidade; magias de Conhecimento recarregam mais rápido | esquece uma coisa: perde uma marcação de mapa ao usar | baú de dungeon/estrutura (combina: escola abandonada) |
| 9 | Fita Sem Etiqueta | Conhecimento | fita cassete emendada com durex | afinidade; tocar com livro ou peça na mão revela uma verdade, 1 por noite | resposta traz uma frase a mais; Lucidez | baú de dungeon/estrutura (combina: escola, rádio) |
| 10 | Uma Barra de Sinal | Energia | celular de tela rachada, capinha amarela | afinidade; recarrega itens com energia; pulso atordoa criaturas perto | durabilidade; toca sozinho de noite | baú de dungeon/estrutura (combina: torre, subestação) |
| 11 | Fusível Queimado | Energia | fusível de vidro, fio preto | afinidade; máquinas de Avaritia e Mekanism por perto falham menos | Exposição quando uma máquina falha | baú de dungeon/estrutura (combina: oficina, subestação) |
| 12 | Radinho de Pilha | Energia | rádio pequeno, antena torta | afinidade; chiado mais alto perto de perigo (só som) | Lucidez em silêncio; vozes no chiado | baú de dungeon/estrutura (combina: casa abandonada) |

---

## Materiais paranormais
Ligas paranormais: um passo acima da ATM Star, com crafts paranormais, sem craft definido por enquanto. O Estilhaço fica sem receita.

1. **Estilhaço do Limiar** (`odteste:estilhaco_do_limiar`): vai no Catalisador do Infinito (Re:Avaritia; custo decidido:
   1 ATM Star + singularidades + este material). Origem: **a decidir** (ver "Em aberto").
2. **Cinza de Relíquia** (`odteste:cinza_de_reliquia`): nascia do selamento de uma espada, que saiu desta versão. **Sem uso por
   enquanto** (fica registrada).
3. **Sal de Velório** (`odteste:sal_de_velorio`): sal de lugares onde alguém foi velado. Reduz o risco das máquinas (sem ritual). Achado em casas de apoio do Outro Lado.
4. **Cera Negra** (`odteste:cera_negra`): vela derretida de entidade vencida. Era o lacre das espadas, que saiu desta
   versão. **Sem uso por enquanto** (fica registrada).

## Máquinas que falham (Re:Avaritia e Mekanism)
- Por enquanto elas **só falham**, como evento do paranormal influenciando a máquina: pausam um ciclo e geram um susto ou
  visão leve (luz, sussurro, passos) em quem está a 12 blocos. **Não soltam nenhum material.** Nunca perdem item nem
  quebram. "Algo com monstros" fica para depois.
- Sem lag: a falha é decidida **quando a máquina termina um ciclo** (evento), com sorteio barato e **uma checagem por
  máquina a cada 60 s**, no máximo. Teto: 1 falha por jogador a cada 10 min e no máximo 4 máquinas arriscadas por
  jogador na conta (as outras são seguras). Código de outro mod só em `compat/mekanism` e `compat/reavaritia`.
- Mais energia movida, mais risco. Sal de Velório e o Fusível Queimado (artefato achado em baú: buff nas máquinas,
  debuff quando uma falha acontece) reduzem o risco. A falha da máquina não gera item.

## Giz e sigilos
Giz único. O ritual com giz serve **só para magias**: não faz craft e não gera item. Os sigilos são aprendidos aos
poucos, achando coisas de eventos espalhados pelo mundo. Detalhes em `docs/rituais.md`.

## Armadura do Limiar (só ideia)
- Protege do paranormal (menos Exposição, Lucidez cai mais devagar), **não do dano comum**. Peças vêm da história.
- Muda com a Marca do jogador (arquivada; precisa ser redesenhada): o elemento fica escondido e a armadura "responde"
  com um brilho sutil.
- A armadura do **Infinito** (Re:Avaritia) protege do dano e o paranormal passa por ela. Usar as duas juntas é decisão
  do jogador e custa peso (lentidão).

## Em aberto
De onde sai o **Estilhaço do Limiar**? Ele nascia da falha das máquinas, que agora não soltam material. **A decidir.**
