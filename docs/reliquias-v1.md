# Relíquias v1

Proposta de lore e de regras. **Nada aqui está aprovado nem virou código.** Os IDs são sugestão e só entram em
`docs/ids.md` quando alguém for implementar. Decisão do Nikolas (15/11/2026): as espadas são relíquias; 8 relíquias,
uma por entidade; nomes de entidades e casos de Ordem Paranormal podem ser usados, mas texto, arte e símbolos do livro
não. Tudo abaixo é escrito por nós. Onde não tenho certeza do que o livro diz, está marcado [conferir no livro], e a
ficha só usa a nossa versão.

## Regras que valem para todas
- **Sem craft.** Vêm da história, de entidade, de lugar ou de ritual. O jogador não escolhe o elemento.
- Pegar e usar contam como Exposição (`reliquia_pegar` e `reliquia_usar` já existem em `docs/ids.md`, com freio de
  repetição). Isso evita grind: usar mais rápido não rende mais.
- O **Medo** é a força maior: não tem relíquia própria. Todas cobram "por cima" com ele (um susto, um som, uma visão).
- Nada de número na tela. O custo aparece como corpo, som, tela e fome. Números só para admin.
- Efeito de tela de toda relíquia passa pela `FxTela` e diminui com `efeitos_fortes`.
- Sem peso por tick: tudo acontece **ao bater, usar ou trocar de item**. O estado de "segurando" é checado só quando
  o jogador pega ou solta o item, mais uma checagem a cada 5 segundos só do portador (nunca de todo mundo).
- Cada relíquia é **única no servidor** até o Nikolas dizer o contrário (ver perguntas).
- Todas nascem do mesmo jeito na história: **mochilas de equipes antigas do Gabinete**, que atravessaram antes e não
  voltaram. As fichas dizem onde cada uma está.

---

## 1. Sede de Domingo (Sangue)
ID `odteste:sede_de_domingo` · **arma** (faca de desossa)
- **Aparência:** faca de açougue de lâmina fina, afiada tantas vezes que virou meia-lua. Cabo de madeira escurecido de
  suor, com um barbante amarrado de fita adesiva. Cheira a ferro. Textura 16x16: lâmina cinza-clara curva na diagonal,
  cabo marrom, três pixels vermelhos secos perto da ponta.
- **Origem:** Seu Hélio, açougueiro de uma cidade pequena, matou a esposa numa manhã de domingo, depois de abater a
  carne da semana, e disse que a faca "estava com sede". Ela saiu do processo como prova e sumiu do depósito da
  delegacia. Caso 0412 do Gabinete (nosso, não do livro).
- **Entidade:** Sangue. A relíquia quer o que está dentro do corpo, não o corpo.
- **Efeito:** dano alto e rápido. A cada 4 acertos em criatura viva, o portador recupera 1 coração (no máximo 2
  corações a cada 30 s). Não funciona contra criaturas de pedra ou ferro.
- **Custo:** cada acerto gasta 1 de fome. Com a fome zerada, a faca passa a tirar vida do portador em vez de curar
  (1 coração por acerto). Pegar e usar somam Exposição pelas regras gerais.
- **Corrupção:** a cada ~40 acertos, a Lucidez máxima cai 1 (mínimo 70 por causa desta relíquia). Primeiro vem o som
  de batimento quando a fome está baixa; depois a borda da tela avermelha ao bater; no fim a comida cozida sacia
  pouco e só carne crua sacia direito.
- **Destruição/selamento:** ritual de jejum: ficar uma noite inteira com a faca no inventário sem bater em nada e sem
  comer, e então cravá-la na bancada do açougue abandonado (lugar da história, a definir) com sal em volta. Se bater
  em qualquer coisa durante a noite, o ritual recomeça.
- **Gancho:** a mochila de uma agente (Marta Reis) está no açougue de uma vila vazia do Outro Lado; o diário dela
  diz que a faca "ajuda a andar, não a voltar". Outra pista: um jogador com fome alta encontra nota de açougue com a
  data de hoje.

## 2. Sabre do Último Adeus (Morte)
ID `odteste:sabre_do_ultimo_adeus` · **arma** (sabre de cerimônia)
- **Aparência:** sabre de gala de uma guarda de honra, bainha de couro rachada, lâmina manchada por ficar anos num
  armário. Tem um nome gravado no punho, riscado com canivete. Textura 16x16: sabre fino e reto, guarda dourada
  fosca, ponta preta.
- **Origem:** o cabo Antônio carregava o caixão de colegas em enterros de corporação. Em uma semana de enchente,
  seguiu em 11 enterros e depois não saiu mais de casa. O sabre foi encontrado ao lado dele, junto com uma lista de
  nomes que ele disse que "ainda faltava". Caso 0388 (nosso).
- **Entidade:** Morte. Quer o final de cada coisa, não a dor.
- **Efeito:** o primeiro golpe em criatura com pouca vida a acaba na hora ("o adeus"), com recarga longa (2 minutos).
  Contra o resto, é uma espada comum boa. Nunca funciona em jogador.
- **Custo:** cada "adeus" gasta durabilidade grande e rouba Lucidez do portador (3). O sabre não se conserta com
  bigorna, só com ritual.
- **Corrupção:** cada adeus faz o portador ouvir, por 2 segundos, uma lista de nomes baixos. Depois de muitos, a Lucidez
  máxima cai e a hotbar escurece nas pontas. No fim, ao dormir, acorda de pé ao lado da cama.
- **Destruição/selamento:** enterrar com honras: levar o sabre ao cemitério da história e deixar sobre uma cova com
  o nome gravado lido em voz alta (um livro de chamada, item da história). Só vale ao amanhecer.
- **Gancho:** a lista de nomes que o cabo deixou tem os nomes de agentes do Gabinete, e os dois últimos são de
  jogadores do servidor. O sabre foi deixado num cemitério do Outro Lado, onde "faltava" um enterro.

## 3. Fita Sem Etiqueta (Conhecimento)
ID `odteste:fita_sem_etiqueta` · **objeto** (fita cassete)
- **Aparência:** fita cassete de caixa transparente, fita meio puxada e emendada com durex. Etiqueta arrancada. Cheira
  a plástico quente. Textura 16x16: retângulo cinza com dois círculos escuros, um fio marrom saindo.
- **Origem:** a professora Lúcia gravava aulas de uma matéria que nunca ensinou. Alunos dizem que ela falava com
  alguém na sala vazia, sempre à tarde. A gravação só tem a voz dela, com pausas onde "alguém" respondia. Caso 0275
  (nosso).
- **Entidade:** Conhecimento. Dá resposta e cobra o que a pergunta valia.
- **Efeito:** usar com um item na mão esquerda (livro, mapa, bússola, peça de máquina) toca a fita e **revela algo
  escondido sobre ele**: trechos escondidos de livros da história, onde um mapa leva de verdade, o que uma peça
  precisa. Uma pergunta por noite do jogo.
- **Custo:** cada pergunta custa Lucidez (5) e deixa Exposição alta por um tempo. A resposta é sempre verdadeira, e
  nunca vem sozinha: vem com uma frase a mais, sobre o próprio jogador.
- **Corrupção:** a fita passa a tocar sozinha na hotbar de noite (som baixo). A Lucidez máxima cai quando o portador
  ignora uma frase "a mais". No fim, a tela mostra letras quase legíveis nas bordas (efeito leve e curto).
- **Destruição/selamento:** apagar a fita regravando-a: gravar por cima (ritual Occultism com um aparelho de som da
  história) a voz de alguém querido do portador lendo coisas banais. Se a leitura for interrompida, a fita grava a
  interrupção e fica pior.
- **Gancho:** a fita está num gravador em uma sala de aula do Outro Lado, com a lousa cheia de contas que os agentes
  reconhecem como as coordenadas do ponto de travessia. Resposta que ajuda a voltar, com preço.

## 4. Uma Barra de Sinal (Energia)
ID `odteste:uma_barra_de_sinal` · **objeto** (celular)
- **Aparência:** celular com a tela rachada em teia e a capinha de silicone amarela. Bateria em 1% há muito tempo.
  Vibra sem mensagem. Cheira a ozônio. Textura 16x16: retângulo preto fino, rachadura branca, um pixel azul no canto.
- **Origem:** Rafael, técnico de torre de celular, ficou preso no alto durante uma tempestade e ligou para casa 40
  vezes. A família recebeu as ligações **depois** do corpo achado. O celular nunca descarrega. Caso 0501 (nosso).
- **Entidade:** Energia. Quer circular, não parar.
- **Efeito:** segurar em qualquer mão **recarrega aos poucos** itens com energia do inventário (só dispara ao
  trocar de item ou a cada 5 s, sem laço). Usar (clique direito) emite um pulso curto que **atordoa criaturas
  próximas** por 3 s (recarga de 60 s).
- **Custo:** o pulso gasta durabilidade do celular e baixa Lucidez (2). Com máquinas ligadas perto, os sustos de
  falha ficam mais frequentes (ver "Máquinas").
- **Corrupção:** o celular toca sozinho, às vezes com a voz de um jogador conhecido. A Lucidez máxima cai com ligações
  atendidas. No fim, a tela do jogador ganha chuvisco leve ao sair de perto de máquinas.
- **Destruição/selamento:** deixar a bateria zerar: levar o celular a um lugar sem energia nenhuma (caverna
  profunda da história, com tudo desligado ao redor) e esperar. Rápido não existe: dá para tentar durante uma noite.
- **Gancho:** a torre de Rafael existe também no Outro Lado e ainda tem sinal: uma barra. É a única forma de ligar
  para casa, e quem atende não é quem os agentes esperam.

## 5. Maria Remendada (Carne)
ID `odteste:maria_remendada` · **objeto** (boneca de pano)
- **Aparência:** boneca de pano de uns 30 cm, com cabelo de lã e remendos de tecido diferentes, costura grossa de
  linha preta, nenhum olho igual ao outro. Quente ao toque. Textura 16x16: bonequinha bege com remendos de cores
  diferentes e linha preta em X.
- **Origem:** uma mãe costurava a boneca da filha toda vez que a menina se machucava. A menina sobreviveu a quatro
  acidentes seguidos e a família, aliviada, parou de ver a diferença entre a pele da filha e o pano. Caso 0166 (nosso).
- **Entidade:** Carne [conferir no livro: natureza canônica de Carne]. A ficha usa só a ideia nossa: o corpo que
  se remenda e não aceita parar.
- **Efeito:** segurada na mão esquerda, **quem bate no portador sofre uma parte do dano de volta**, e feridas
  graves fecham sozinhas aos poucos (regeneração lenta). Se o portador for derrubado, ela o levanta com meio coração
  (uma vez por 10 minutos).
- **Custo:** o portador perde fome mais rápido e **não pode mais usar poções**. A cada "levantada", Exposição sobe.
- **Corrupção:** a Lucidez máxima cai com cada levantada. Aparecem marcas de costura visuais na pele do portador
  (cosmético). No fim, a boneca fica de olho aberto no inventário, o que é só som e textura.
- **Destruição/selamento:** desfazer as costuras, uma por noite, em seis noites (ritual Occultism com agulha
  da história), sempre com alguém do grupo segurando a mão do portador. Se o portador se negar, a costura volta.
- **Gancho:** a boneca está no berço de uma casa do Outro Lado onde há marca de uma criança que não existe nos
  registros. Os agentes podem escolher entre cuidar da boneca ou deixá-la, e a escolha muda o final do caso.

## 6. Frasco Sem Receita (Parasita)
ID `odteste:frasco_sem_receita` · **objeto** (remédio)
- **Aparência:** frasco de remédio de farmácia de bairro, tampa de rosca, etiqueta com a letra do médico impossível de
  ler. Dentro, comprimidos brancos que nunca acabam. Cheira a hospital. Textura 16x16: frasquinho laranja, tampa
  branca, pontinhos brancos dentro.
- **Origem:** seu Valdomiro tomava um comprimido por dia para uma dor que ninguém achava. O médico dizia que era
  "nervoso". O seguro pagou o remédio por 11 anos e a farmácia nunca registrou a venda. Caso 0630 (nosso).
- **Entidade:** Parasita [conferir no livro: natureza canônica de Parasita]. Aqui é a ideia de algo que vive do
  hospedeiro e o mantém de pé para continuar vivendo.
- **Efeito:** usar (comer) dá um "dia bom": fome cheia, ferimentos curados e vigor por 5 minutos. Só cabe um
  comprimido por dia do jogo.
- **Custo:** o dia seguinte cobra: lentidão e Lucidez menor (3) até dormir. Pular um dia dá "abstinência": a visão
  fica turva até tomar de novo.
- **Corrupção:** o frasco passa a pesar mais. Os comprimidos vão ficando em número menor e outros jogadores veem
  o portador com a mão sempre na boca. A Lucidez máxima cai a cada dia seguido de uso. No fim, "dia bom" só dura
  enquanto o frasco está no inventário.
- **Destruição/selamento:** parar de tomar por três dias seguidos, sem trapaça (dormindo em cama com o grupo ao
  lado), e jogar o frasco **fechado** em água corrente. Aberto, os comprimidos voltam.
- **Gancho:** o frasco está num consultório do Outro Lado com a ficha do paciente: o nome é de um jogador. Pergunta
  aberta: a doença é dele ou do lugar?

## 7. Volta Seis (Espiral)
ID `odteste:volta_seis` · **arma** (chave de roda)
- **Aparência:** chave de roda de carro, cromada e opaca de ferrugem, com a marca de dedos gastos no meio. Na ponta
  tem uma gravação em espiral feita à mão. Textura 16x16: cruz de ferro cinza diagonal, espiral de dois pixels brilhantes.
- **Origem:** Dona Zélia dirigia todo dia pela mesma estrada em volta de um lago. Um dia, o carro foi achado com tanque
  vazio, 14 mil km a mais no hodômetro, e ela dentro, sem fome, com a chave de roda no colo. Caso 0719 (nosso).
- **Entidade:** Espiral [conferir no livro: natureza canônica de Espiral]. Aqui é a ideia de repetir até perder
  o começo.
- **Efeito:** arma de impacto pesada. Cada golpe no **mesmo alvo** soma dano; trocar de alvo zera. O 6º golpe seguido
  derruba o alvo (recarga de 30 s). Ao ser derrubado, o portador volta ao último ponto seguro (bloco que ele pisou
  parado por 10 s) uma vez a cada 10 minutos.
- **Custo:** o retorno gasta fome e Lucidez (4). Cada retorno repete a última fala do portador no chat do local,
  e cada repetição é cobrada com Exposição.
- **Corrupção:** os caminhos passam a se repetir: o portador sente que já fez aquilo (som de passos e portas em
  eco), a Lucidez máxima cai por retorno. No fim, o mesmo trecho de mata aparece duas vezes na tela de caminhada.
- **Destruição/selamento:** quebrar o ciclo: ir de A para B por um caminho que nunca tenha pisado, sem voltar, carregando
  a chave, e deixá-la em B. Se pisar em bloco já pisado por ele, recomeça.
- **Gancho:** a estrada do lago existe no Outro Lado e faz uma curva de 360 graus. O único caminho de volta passa
  por ela, e quem usa a chave pode "dobrar" a estrada, com preço.

## 8. Boas-Vindas (Anfitrião)
ID `odteste:boas_vindas` · **objeto** (espelho de hall)
- **Aparência:** espelho de mão de moldura dourada, vidro limpo demais, com um cartão de "seja bem-vindo" preso atrás.
  O reflexo mostra o cômodo um pouco mais arrumado do que ele está. Cheira a flor velha. Textura 16x16: moldura
  amarela quadrada com vidro azul-claro e um cartão branco.
- **Origem:** Dona Íris recebia hóspedes numa pousada de beira de estrada, e nenhum hóspede foi visto saindo. Os
  vizinhos só notaram porque ela nunca comprou comida. Ela dizia que "a casa gosta de visita". Caso 0093 (nosso).
- **Entidade:** Anfitrião [conferir no livro: natureza canônica de Anfitrião]. Aqui é a ideia de quem convida
  para entrar e faz o convidado querer ficar.
- **Efeito:** olhar para o espelho (clique direito) abre uma **sala segura temporária** (3 minutos): criaturas hostis
  não entram, jogadores do grupo perto recuperam Lucidez. Só em lugar coberto.
- **Custo:** é o preço da hospitalidade: quem sai da sala volta com menos Lucidez que a que ganhou (saldo negativo).
  O portador não pode abandonar o grupo: longe dele, a Lucidez cai.
- **Corrupção:** o reflexo passa a mostrar o portador mais bem arrumado do que está. Cada uso novo, o grupo quer
  ficar um pouco mais (sustos de "melhor ficar aqui" ao se afastar). A Lucidez máxima cai por sala aberta.
- **Destruição/selamento:** recusar o convite: três jogadores saem da sala ao mesmo tempo, sem olhar para trás, e o
  espelho é quebrado fora de qualquer teto, à luz do dia. Se alguém olhar para trás, o espelho se refaz.
- **Gancho:** a pousada de Dona Íris é o primeiro abrigo seguro do Outro Lado, e os agentes só descobrem o que ele
  custa depois de usar. Serve de "casa" até alguém perceber que não sai.

---

## Materiais paranormais
1. **Estilhaço do Limiar** (`odteste:estilhaco_do_limiar`): resto sólido de uma falha de máquina grande (ver
   "Máquinas"). Uma por falha, no máximo 1 por semana por máquina. **Vai no Catalisador do Infinito** do Re:Avaritia
   (custo decidido: 1 ATM Star + singularidades + este material). Sem ele não há volta para casa.
2. **Cinza de Relíquia** (`odteste:cinza_de_reliquia`): sobra do selamento de uma relíquia. Para rituais de
   selamento das outras. Prova que o portador fez o ritual até o fim.
3. **Sal de Velório** (`odteste:sal_de_velorio`): sal de lugares onde alguém morreu e foi velado. Achado em casas
   de apoio do Outro Lado, em baú de história. Faz círculo de proteção (impede susto grande por uma noite).
4. **Cera Negra** (`odteste:cera_negra`): vela derretida de entidade vencida. Lacre de selamento: fecha uma relíquia
   guardada e para sua corrupção enquanto selada, mas ela fica no baú de uma pessoa só.
5. **Fio de Costura** (`odteste:fio_de_costura`): fio preto da Carne. Para consertar a Armadura do Limiar e
   Maria Remendada.

## Máquinas que falham
- Perto de máquinas do Re:Avaritia e do Mekanism, elas **falham**: pausam um ciclo e geram um susto ou visão leve
  (luz, sussurro, passos) em quem está a 12 blocos. Nunca perdem item nem quebram.
- Sem lag: nada de laço em toda máquina. A falha é decidida **quando a máquina termina um ciclo** (evento), com sorteio
  barato e **uma checagem por máquina a cada 60 s**, no máximo.
- Teto: 1 falha por jogador a cada 10 min e no máximo 4 máquinas arriscadas por jogador na conta (as outras são
  seguras). Códigos de outro mod só em `compat/mekanism` e `compat/reavaritia`, atrás de `Compat.Integracao`.
- Quanto mais energia a máquina move, mais arriscada. Perto de um Sal de Velório ou Cera Negra, o risco cai.
- O Estilhaço do Limiar nasce só das falhas das máquinas maiores, não de cada falha.

## Armadura do Limiar (só ideia)
- Protege do paranormal (menos Exposição, Lucidez cai mais devagar), **não do dano comum**. Peças vêm da história.
- Muda com a Marca do jogador (a Marca hoje está arquivada; precisa ser redesenhada antes): o elemento fica escondido,
  e a armadura "responde" com um brilho sutil.
- A armadura do **Infinito** (Re:Avaritia) protege do dano, e o paranormal passa por ela. Usar as duas juntas é
  decisão do jogador, e custa peso (lentidão).
- Fio de Costura e Cera Negra servem para consertar e selar.

## Perguntas para o Nikolas
1. Aprova a escolha das 8 (Sangue, Morte, Conhecimento, Energia, Carne, Parasita, Espiral, Anfitrião)? Deus da
   Morte, Diabo e Tempestade ficam para a v2.
2. Cada relíquia é **única no servidor** (quem acha, tem) ou cada jogador pode achar a sua?
3. Destruir uma relíquia a apaga para sempre ou ela volta, mais fraca, em outro lugar?
4. O Estilhaço do Limiar pode ser raro de verdade (1 por semana)? Ou prefere um limite diferente, por exemplo
   até a história chegar na volta para casa?
5. Os casos novos (0412, 0388, 0275 etc.) podem ficar com número de caso do Gabinete, ou prefere só nomes?
