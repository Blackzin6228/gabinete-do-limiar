---
name: gabinete-item
description: Use para criar ou mudar um item do gabinete-do-limiar (relíquia/espada, artefato, material, ferramenta, giz). Passo a passo de registro, IDs, traduções, modelo, textura aprovada, regras de elemento e custo, e testes.
---

# Criar um item no Gabinete do Limiar (NeoForge 1.21.1)

Leia junto: `gabinete-mesa` (fluxo) e `minecraft-modding` (API; confira sempre com o código do projeto).

## Onde fica cada coisa
- Registro: `src/main/java/com/nikolas/odteste/registro/Registro.java` (`ITEMS = DeferredRegister.createItems`,
  `COMPONENTES` para dados do item). Nomes de ID como constantes em `dados/IdsDados.java`.
- Classe do item: pacote do assunto (ex.: `reliquias/`, `artefatos/`, `materiais/`, `rituais/`), nunca em `cliente/`.
- Regras puras (sem Minecraft) em classes `Regras*` testáveis (veja `cracha/RegrasCracha.java` + teste).
- Modelo: `src/main/resources/assets/odteste/models/item/<id>.json` (1.21.1: **não** existe `assets/<mod>/items/`).
- Textura: `src/main/resources/assets/odteste/textures/item/<id>.png` — só a da prévia aprovada (`gabinete-previa`).
- Texto: `assets/odteste/lang/pt_br.json` e `en_us.json` (`item.odteste.<id>` e a descrição).
- Lista de IDs: `docs/ids.md` (o `ConsistenciaTest` confere).

## Passo a passo
1. Pegue nome, ID e regras do documento aprovado (`docs/reliquias-v1.md`, `docs/rituais.md`…). ID é para sempre:
   não invente; se o documento não tiver, pare e pergunte.
2. Constante do ID em `IdsDados` → registro em `Registro` → classe do item.
3. Dados que o item guarda (dono, carga, corrupção): `DataComponentType` com `CODEC` (persistente) e
   `STREAM_CODEC` só se o cliente precisar ver. Nunca guardar o elemento do jogador no cliente antes de revelado.
4. Modelo JSON (`item/generated` para ícone; `item/handheld` para espada/ferramenta) + `display` conferido.
5. Traduções nos dois idiomas. Descrição curta, tom sério, sem número.
6. `docs/ids.md` no mesmo PR.
7. Teste das regras (elemento certo x errado, custo, limite). `./gradlew build`.

## Regras do jogo para itens
- **Relíquias (as 4 espadas):** únicas no servidor, sem craft, vêm da história ou do mestre
  (`/odteste dar …`, só admin/mestre). Funcionam por inteiro só para quem tem o elemento revelado
  (`sistemas/` / `elementos/RegrasElemento`). Elemento errado: metade da força, custo maior (confirme no documento).
- **Artefatos:** dão afinidade com um elemento (força das magias daquele elemento) e às vezes um poder; cobram preço.
- **Todo poder cobra:** Lucidez (≈ Sanidade), Exposição (≈ NEX), vida, fome, durabilidade, XP. Nada de graça.
- Custo e efeito mostrados por som, partícula, tremor e tela — **nunca por número** para o jogador.
- Efeito especial ao segurar/usar segue `gabinete-fx` (som + partícula + resposta do braço).
- Sem tick pesado: efeito ao segurar roda a cada N ticks, com teto; nada de busca de entidades sem raio e limite.
- Integração com outro mod (Iron's Spells, Curios, Occultism…) só em `compat/<mod>/` atrás de `Compat`.

## Checklist do PR (cole marcado na descrição)
- [ ] ID em `IdsDados`, `Registro`, `docs/ids.md`
- [ ] pt_br + en_us
- [ ] Modelo e textura da prévia aprovada (link da prévia)
- [ ] Regras testadas; Actions verde
- [ ] Respeita efeitos_fortes / Iris / sem número na tela
- [ ] O que não foi testado no jogo
