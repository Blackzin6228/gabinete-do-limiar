# Relíquias v1

Proposta de lore e de regras. **Nada aqui está aprovado nem virou código.** Os IDs são sugestão e só entram em
`docs/ids.md` quando alguém for implementar. Decisão do Nikolas (15/11/2026): só **4 relíquias**, e elas são as
**4 espadas** dos jogadores, únicas no servidor; mais **12 artefatos** de afinidade, 3 por elemento. Nomes de
entidades e casos de Ordem Paranormal podem ser usados; texto, arte, símbolos e **nomes de personagens** do livro não.
Onde não tenho certeza do que o livro diz, está marcado [conferir no livro] e a ficha usa só a nossa versão.

## Regras que valem para tudo
- **Sem craft.** Vêm da história, de entidade, de lugar, de ritual ou de máquina com risco.
- **Ligações das espadas** (decisão do Nikolas, mesmo se diferir do livro): Sangue = Diabo, Energia = Anfitrião,
  Morte = Deus da Morte, Conhecimento = Magistrada.
- **Única no servidor.** Só existe uma de cada espada. Vai para quem é daquele elemento. O servidor guarda onde ela está
  (dado salvo do mundo). Ela não some por tempo, não queima, não cai no vazio: volta ao último dono ou ao lugar de
  origem. Se dois jogadores forem do mesmo elemento, ver pergunta 1.
- **Elemento errado cobra caro.** Espada ou artefato de outro elemento funciona, mas pela metade, gasta o dobro de
  Lucidez e durabilidade, sobe Exposição ao equipar e a entidade "olha" (susto do elemento dela, mais frequente). Do
  elemento certo, o custo é o descrito. Nada disso aparece em número para o jogador: só som, tela, corpo e fome.
- O **Medo** é a força maior e não tem relíquia: toda relíquia cobra "por cima" com um susto, som ou visão.
- Pegar e usar contam como Exposição (`reliquia_pegar` e `reliquia_usar`, com freio de repetição em `docs/ids.md`).
- Efeito de tela passa pela `FxTela` e diminui com `efeitos_fortes`.
- **Sem peso por tick.** Tudo acontece ao bater, usar ou trocar de item. Checagem do portador no máximo a cada 5 s,
  só dele, nunca de todos.
- Todas as espadas nascem como **mochilas de equipes antigas do Gabinete**, que atravessaram antes e não voltaram.

---

## 1. Sede de Domingo (Sangue, o Diabo)
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
- **Custo:** cada acerto gasta fome. Com a fome zerada, o facão tira vida do portador em vez de curar. Pegar e usar
  somam Exposição.
- **Corrupção:** a cada ~40 acertos, a Lucidez máxima cai um pouco (com piso, para não travar o jogador). Primeiro,
  batimento quando a fome está baixa; depois a borda da tela avermelha ao bater; no fim só carne crua sacia direito.
- **Destruição/selamento:** jejum de uma noite com o facão no inventário, sem bater e sem comer, e então cravá-lo na
  bancada do açougue abandonado (lugar a definir) com Sal de Velório em volta. Bater em algo reinicia o ritual.
  **Se sumir do mundo:** o Sangue fica sem espada, o Diabo "cobra a dívida" (sustos de Sangue mais frequentes) e o
  jogador desse elemento perde a afinidade extra dela até outra marca da história. Não nasce outra igual.
- **Gancho:** a mochila da agente Marta Reis (nossa) está no açougue de uma vila vazia do Outro Lado; o diário diz que
  o facão "ajuda a andar, não a voltar".

## 2. Sabre do Último Adeus (Morte, o Deus da Morte)
ID `odteste:sabre_do_ultimo_adeus` · espada (sabre de cerimônia)
- **Aparência:** sabre de gala de guarda de honra, bainha de couro rachada, lâmina manchada de anos em armário. Nome
  riscado a canivete no punho. 16x16: sabre fino e reto, guarda dourada fosca, ponta preta.
- **Origem:** o cabo Antônio carregava o caixão de colegas em enterros de corporação. Numa semana de enchente seguiu
  11 enterros e não saiu mais de casa. Achado ao lado dele, com uma lista de nomes que "ainda faltava". Caso 0388
  (nosso).
- **Entidade:** o Deus da Morte [conferir no livro]. Aqui quer o final de cada coisa, não a dor.
- **Efeito:** o primeiro golpe em criatura com pouca vida a acaba na hora ("o adeus"), recarga longa. Contra o resto, é
  uma espada boa e comum. Nunca funciona em jogador.
- **Custo:** cada adeus gasta muita durabilidade e rouba Lucidez do portador. Não se conserta em bigorna, só com ritual.
- **Corrupção:** cada adeus faz o portador ouvir, por 2 s, uma lista de nomes baixos. Depois de muitos, a Lucidez máxima
  cai e a hotbar escurece nas pontas. No fim, ao dormir, acorda de pé ao lado da cama.
- **Destruição/selamento:** enterrar com honras: levar o sabre ao cemitério da história, sobre uma cova, ao amanhecer,
  lendo o nome gravado em voz alta (livro de chamada, item da história). **Se sumir:** a Morte fica sem espada; a
  entidade passa a "buscar a lista" (sustos de Morte sobem) e o jogador desse elemento fica sem o adeus. Não nasce outra.
- **Gancho:** a lista tem nomes de agentes do Gabinete e os dois últimos são de jogadores do servidor. O sabre ficou em
  um cemitério do Outro Lado onde "faltava" um enterro.

## 3. Sentença Sem Recurso (Conhecimento, a Magistrada)
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
- **Custo:** cada julgamento custa Lucidez e eleva Exposição por um tempo. A revelação é sempre verdadeira e vem com uma
  frase a mais sobre o próprio portador.
- **Corrupção:** passa a "ler o portador": em silêncio, a espada mostra no chat do portador falhas dele (mortes, quem
  deixou para trás), só para ele. A Lucidez máxima cai se ele ignora. No fim, letras quase legíveis nas bordas da tela
  (leve e curto).
- **Destruição/selamento:** pedir **recurso**: o grupo inteiro lê, em voz alta, a sentença em branco (livro da
  história) no tribunal do Outro Lado e a espada é lacrada com Cera Negra. Se alguém mentir na leitura, a lâmina
  acrescenta o nome dele. **Se sumir:** o Conhecimento fica sem espada; a Magistrada julga sem ela (livros da história
  perdem trechos escondidos, falta de resposta). Não nasce outra.
- **Gancho:** a lousa da sala do tribunal tem as coordenadas do ponto de travessia. A resposta ajuda a voltar, com preço.

## 4. Espeto da Casa Cheia (Energia, o Anfitrião)
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
- **Custo:** cada descarga gasta durabilidade e Lucidez. Sozinho, o espeto enfraquece e cobra mais: ele não quer
  portador sem convidados.
- **Corrupção:** o espeto zumbe e faz o portador "ouvir" um churrasco ao fundo. A Lucidez máxima cai a cada descarga
  seguida. No fim, ao se afastar do grupo, vem um susto de "melhor ficar aqui" (a tela clareia e o som aperta).
- **Destruição/selamento:** recusar o convite: três jogadores saem juntos da casa de Seu Dirceu no Outro Lado, sem olhar
  para trás, e o espeto é dobrado ao ar livre, à luz do dia. Se alguém olhar, o espeto se refaz reto. **Se sumir:** a
  Energia fica sem espada, o Anfitrião passa a "abrir mesa" nos lugares seguros (abrigos perdem a proteção por uma noite
  de cada vez). Não nasce outra.
- **Gancho:** a casa de Seu Dirceu é o primeiro abrigo do Outro Lado, e os agentes só descobrem o que ele custa depois de
  usar. Serve de "casa" até alguém perceber que ninguém sai.

---

## 12 artefatos (afinidade)
Um artefato **aumenta o elemento do jogador** (mais força nas magias daquele elemento, que será uma escola própria no
Iron's Spells [conferir na versão do Iron's do pack], decisão de 14/11) e às vezes dá um poder. Dois artefatos do mesmo
elemento somam com freio (o terceiro rende pouco). Elemento errado: ver regras gerais. Nenhum tem nome de personagem.

| # | Nome | Elemento | Objeto (16x16) | O que faz | Custo | Onde se acha |
|---|------|----------|----------------|-----------|-------|--------------|
| 1 | Seringa Velha | Sangue | seringa de vidro, êmbolo marrom, líquido escuro | afinidade; usar sacrifica 1 coração para dar Força por 30 s | vida; Exposição | banco de sangue do hospital do Outro Lado |
| 2 | Luva de Açougueiro | Sangue | luva de borracha amarela, dedos manchados | afinidade; golpes comuns também sangram | fome em cada acerto | ritual de Sangue; mochila de açougue |
| 3 | Medalha de Doador | Sangue | medalha com gota vermelha, fita gasta | afinidade; abaixo de 3 corações regenera aos poucos | Lucidez sobe de custo quando regenera | recompensa de entidade de Sangue vencida |
| 4 | Óculos de Velório | Morte | óculos de grau, aro preto, lente rachada | afinidade; vê o paranormal (contorno só no cliente, invisíveis aparecem) | Exposição enquanto usa; visões | cemitério da história |
| 5 | Moeda de Defunto | Morte | moeda escura sobre dois pixels de olho | afinidade; 1x por noite, golpe fatal deixa meio coração e monstros esquecem você 5 s | Lucidez; esfria até a noite seguinte | corpo achado no Outro Lado |
| 6 | Vela de Sétimo Dia | Morte | toco de vela branca, chama azul | afinidade; acesa, enfraquece mortos-vivos perto | vela gasta durabilidade; Lucidez em cova | ritual de Morte com Sal de Velório |
| 7 | Lupa do Arquivo Morto | Conhecimento | lupa de latão, vidro embaçado | afinidade; revela trechos escondidos de livros | Lucidez por revelação | arquivo da história |
| 8 | Caneta Sem Tinta | Conhecimento | caneta esferográfica seca, tampa mordida | afinidade; magias de Conhecimento recarregam mais rápido | esquece uma coisa: perde uma marcação de mapa ao usar | mesa de escola do Outro Lado |
| 9 | Fita Sem Etiqueta | Conhecimento | fita cassete emendada com durex | afinidade; tocar com livro ou peça na mão revela uma verdade, 1 por noite | resposta traz uma frase a mais; Lucidez | gravador em sala de aula do Outro Lado |
| 10 | Uma Barra de Sinal | Energia | celular de tela rachada, capinha amarela | afinidade; recarrega itens com energia; pulso atordoa criaturas perto | durabilidade; toca sozinho de noite | torre de celular do Outro Lado |
| 11 | Fusível Queimado | Energia | fusível de vidro, fio preto | afinidade; máquinas de Avaritia e Mekanism por perto falham menos | Exposição quando uma máquina falha | falha grande de máquina (rara) |
| 12 | Radinho de Pilha | Energia | rádio pequeno, antena torta | afinidade; chiado mais alto perto de perigo (só som) | Lucidez em silêncio; vozes no chiado | casa abandonada do Outro Lado |

---

## Materiais paranormais
1. **Estilhaço do Limiar** (`odteste:estilhaco_do_limiar`): resto sólido de uma falha grande de máquina. **Vai no
   Catalisador do Infinito** (Re:Avaritia; custo decidido: 1 ATM Star + singularidades + este material). Sem ele não
   há volta para casa. **Raro, mas não tanto**: alguns por semana no servidor, com esforço (uma máquina grande ligada
   por muito tempo, em risco, de noite). Teto por máquina (1 a cada 2 dias) e teto do servidor na config do admin
   (sugestão: 4 a 6 por semana). Ver pergunta 4.
2. **Cinza de Relíquia** (`odteste:cinza_de_reliquia`): sobra do selamento de uma espada. Prova o ritual até o fim.
3. **Sal de Velório** (`odteste:sal_de_velorio`): sal de lugares onde alguém foi velado. Círculo de proteção (impede
   susto grande por uma noite). Achado em casas de apoio do Outro Lado.
4. **Cera Negra** (`odteste:cera_negra`): vela derretida de entidade vencida. Lacre: guarda uma espada e para sua
   corrupção enquanto selada, no baú de uma pessoa só.

## Máquinas que falham
- Perto de máquinas do Re:Avaritia e do Mekanism, elas **falham**: pausam um ciclo e geram um susto ou visão leve (luz,
  sussurro, passos) em quem está a 12 blocos. Nunca perdem item nem quebram.
- Sem lag: a falha é decidida **quando a máquina termina um ciclo** (evento), com sorteio barato e **uma checagem por
  máquina a cada 60 s**, no máximo. Teto: 1 falha por jogador a cada 10 min e no máximo 4 máquinas arriscadas por
  jogador na conta (as outras são seguras). Código de outro mod só em `compat/mekanism` e `compat/reavaritia`.
- Mais energia movida, mais risco. Sal de Velório, Cera Negra e o Fusível Queimado reduzem o risco.
- O Estilhaço e o Fusível nascem só das falhas das máquinas maiores, não de toda falha.

## Armadura do Limiar (só ideia)
- Protege do paranormal (menos Exposição, Lucidez cai mais devagar), **não do dano comum**. Peças vêm da história.
- Muda com a Marca do jogador (arquivada; precisa ser redesenhada): o elemento fica escondido e a armadura "responde"
  com um brilho sutil.
- A armadura do **Infinito** (Re:Avaritia) protege do dano e o paranormal passa por ela. Usar as duas juntas é decisão
  do jogador e custa peso (lentidão). Cera Negra e Cinza servem para consertar e selar.

## Perguntas para o Nikolas
1. Se dois jogadores forem do mesmo elemento (ou ninguém for de um deles), quem recebe a espada? Sugestão: o mestre
   entrega no momento da história, para quem estiver no grupo.
2. Se uma espada for destruída: some para sempre (a entidade cobra) ou volta mais fraca em outro lugar? Escrevi "some".
3. Jogador de elemento errado pode usar espada e artefato (metade da força, o dobro do custo) ou prefere que nem
   consiga equipar?
4. Quantos Estilhaços o Catalisador pede, e o teto semanal pode ser 4 a 6?
5. Os artefatos vão num espaço próprio de acessório (o pack tem o mod Curios [conferir no pack]) ou na mão e na cabeça?
