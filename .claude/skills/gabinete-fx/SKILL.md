---
name: gabinete-fx
description: Use para efeitos visuais e sonoros do gabinete-do-limiar (efeito de tela, partículas, shader, susto, efeito de item, ritual, magia). Régua de qualidade, técnicas aprendidas (Apex Arcana, Lodestone, Malum, AAA Particles, Iron's Spells) e limites de desempenho.
---

# FX do Gabinete do Limiar

Base que já funciona e **não pode quebrar**: `cliente/FxTela.java` (efeito de tela que roda com e sem shader do
Iris: copia a tela, lê a profundidade quando o pack deixa, desenha depois do pack — técnica aprendida no Apex Arcana,
MIT), `cliente/FxCorHorror`, `cliente/CamadaHorrorCliente` (névoa e camada de horror: o Nikolas gostou, preservar).

## Régua (todo efeito)
- **Começo, meio e fim**: cresce, vive, some devagar (curva; nada liga/desliga seco).
- Cor do elemento, no máximo 2 tons; brilho aditivo **só no acento**. Brilho raro assusta mais.
- Lê de longe e não esconde a luta. **Som junto** (efeito sem som parece quebrado).
- Respeita `efeitos_fortes` (diminui, não desliga) e a opção de partículas do jogador. Nada pisca >3x por segundo.
- Funciona com Sodium + Iris + shader pack. Efeito de tela passa pela `FxTela`.
- Mod opcional (Lodestone, AAA Particles) só via `compat/`, com versão simples quando ele não está.

## Técnicas que aprendemos
- **Magia/poder em 4 tempos (Iron's Spells):** preparo (pose + partícula na mão) → solta → viagem (rastro) →
  impacto (partícula + som + às vezes tremor). A cor do elemento é constante nos 4.
- **Malum:** fundo escuro e dessaturado + um acento saturado que brilha; partículas que **convergem** para o centro
  (sensação de "puxar") — ótimo para ritual.
- **Lodestone:** partículas com curva de tamanho/cor/transparência e pós-processo ligado por evento.
- **AAA Particles + Effekseer:** efeitos grandes (camadas, anéis, distorção) feitos por nós no editor `.efkefc`;
  só cliente; limite de quantos tocam juntos.
- **Apex Arcana:** referência de nível para magia; técnica de tela já está na `FxTela`.
- **Hand of God:** ainda não estudado no repositório novo. Se a tarefa pedir, estude o jar que o Nikolas indicar
  e acrescente aqui o que presta (técnica, não arte).

## Limites de desempenho
- Efeito comum até ~50 partículas vivas; grande até ~300; no máximo 2 grandes por jogador ao mesmo tempo.
- O servidor manda **um pacote por efeito**, não um por partícula. Cálculo pesado só no cliente.
- Efeito ao segurar item: a cada N ticks, nunca todo tick com busca de entidades.
- Shader de tela: dura pouco, saída suave.

## Prévia
Efeito também precisa de prévia aprovada: GIF ou vídeo curto (com e sem shader) ou, se não der para gravar,
diga exatamente como o Nikolas testa no jogo (comando, onde, o que deve ver).
