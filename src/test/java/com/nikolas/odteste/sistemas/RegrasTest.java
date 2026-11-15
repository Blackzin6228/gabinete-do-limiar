package com.nikolas.odteste.sistemas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nikolas.odteste.dados.IdsDados;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Contas dos sistemas (Java puro): se um número mudar sem querer, o build fica vermelho. */
class RegrasTest {

    // ---------- Degraus ----------

    @Test
    void degrauPelaExposicao() {
        assertEquals(Degrau.COMUM, Degrau.de(0));
        assertEquals(Degrau.COMUM, Degrau.de(19));
        assertEquals(Degrau.TOCADO, Degrau.de(20));
        assertEquals(Degrau.MARCADO, Degrau.de(40));
        assertEquals(Degrau.FUNDO, Degrau.de(79 - 19));
        assertEquals(Degrau.A_BEIRA, Degrau.de(99));
        assertEquals(Degrau.ATRAVESSADO, Degrau.de(100));
        assertEquals(Degrau.COMUM, Degrau.de(-5));
    }

    // ---------- Lucidez ----------

    @Test
    void faixasDaLucidezNasBordas() {
        assertEquals(FaixaLucidez.FIRME, FaixaLucidez.de(100, 100));
        assertEquals(FaixaLucidez.FIRME, FaixaLucidez.de(71, 100));
        assertEquals(FaixaLucidez.INQUIETO, FaixaLucidez.de(70, 100));
        assertEquals(FaixaLucidez.INQUIETO, FaixaLucidez.de(40, 100));
        assertEquals(FaixaLucidez.ABALADO, FaixaLucidez.de(39, 100));
        assertEquals(FaixaLucidez.ABALADO, FaixaLucidez.de(15, 100));
        assertEquals(FaixaLucidez.QUEBRANDO, FaixaLucidez.de(14, 100));
        assertEquals(FaixaLucidez.QUEBRANDO, FaixaLucidez.de(1, 100));
        assertEquals(FaixaLucidez.SURTO, FaixaLucidez.de(0, 100));
    }

    @Test
    void faixaEmFracaoDaMaxima() {
        // Quem transcendeu (máxima 40): 40 é Firme, 16 é Inquieto (40%), 6 é Abalado (15%).
        assertEquals(FaixaLucidez.FIRME, FaixaLucidez.de(40, 40));
        assertEquals(FaixaLucidez.INQUIETO, FaixaLucidez.de(16, 40));
        assertEquals(FaixaLucidez.ABALADO, FaixaLucidez.de(6, 40));
        assertEquals(FaixaLucidez.QUEBRANDO, FaixaLucidez.de(5, 40));
        // Máxima inválida não divide por zero.
        assertEquals(FaixaLucidez.FIRME, FaixaLucidez.de(5, 0));
    }

    @Test
    void danoComumPesaPoucoEParanormalMais() {
        assertEquals(10, RegrasLucidez.perdaPorDano(1f, false));
        assertEquals(60, RegrasLucidez.perdaPorDano(1f, true));
        assertTrue(RegrasLucidez.perdaPorDano(6f, true) > RegrasLucidez.perdaPorDano(6f, false));
        assertEquals(0, RegrasLucidez.perdaPorDano(0f, true));
        assertEquals(0, RegrasLucidez.perdaPorDano(-4f, true));
        assertEquals(0, RegrasLucidez.perdaPorDano(Float.NaN, false));
    }

    @Test
    void golpeTemTeto() {
        assertEquals(RegrasLucidez.TETO_GOLPE_COMUM, RegrasLucidez.perdaPorDano(1000f, false));
        assertEquals(RegrasLucidez.TETO_GOLPE_PARANORMAL, RegrasLucidez.perdaPorDano(Float.POSITIVE_INFINITY, true));
    }

    @Test
    void danoComumSoTiraUmPontoACadaCincoCoracoes() {
        // 10 golpes de 1 de dano comum = 100 centésimos = 1 ponto de Lucidez.
        int resto = 0;
        int pontos = 0;
        for (int i = 0; i < 10; i++) {
            var a = RegrasLucidez.acumular(resto, RegrasLucidez.perdaPorDano(1f, false));
            resto = a.resto();
            pontos += a.pontos();
        }
        assertEquals(1, pontos);
        assertEquals(0, resto);
        assertEquals(new RegrasLucidez.Acumulo(2, 50), RegrasLucidez.acumular(90, 160));
        assertEquals(new RegrasLucidez.Acumulo(0, 0), RegrasLucidez.acumular(-5, -5));
    }

    @Test
    void descansoSoParadoESemDanoEDevagar() {
        int semDano = RegrasLucidez.DESCANSO_SEM_DANO;
        assertFalse(RegrasLucidez.recuperaDescansando(semDano - 1, RegrasLucidez.DESCANSO_INTERVALO), "tomou dano há pouco");
        assertFalse(RegrasLucidez.recuperaDescansando(semDano, 0), "andando");
        assertFalse(RegrasLucidez.recuperaDescansando(semDano, RegrasLucidez.DESCANSO_INTERVALO - 1));
        assertTrue(RegrasLucidez.recuperaDescansando(semDano, RegrasLucidez.DESCANSO_INTERVALO));
        // Em 10 minutos parado (300 checagens), volta pouco: devagar.
        int ganhos = 0;
        for (int c = 1; c <= 300; c++) if (RegrasLucidez.recuperaDescansando(semDano + c, c)) ganhos++;
        assertEquals(30, ganhos);
    }

    @Test
    void comidaQuenteSoAcalmaComLuzEGrupo() {
        assertTrue(RegrasLucidez.comidaAcalma(true, () -> true), "tag + luz forte + grupo");
        assertFalse(RegrasLucidez.comidaAcalma(true, () -> false), "sozinho ou no escuro");
        assertFalse(RegrasLucidez.comidaAcalma(false, () -> true), "comida comum");
        // Comida comum nem olha luz e grupo.
        assertFalse(RegrasLucidez.comidaAcalma(false, () -> { throw new AssertionError("não devia checar"); }));
    }

    @Test
    void abaloComIntensidade() {
        assertEquals(10, Abalo.APARICAO.perda(1f));
        assertEquals(50, Abalo.APARICAO.perda(99f), "intensidade presa em 5");
        assertEquals(0, Abalo.APARICAO.perda(0f));
        assertEquals(0, Abalo.APARICAO.perda(Float.NaN));
        assertEquals(1, Abalo.BATALHA_RELIQUIA.perda(0.01f), "abalo de verdade tira pelo menos 1");
        Set<String> ids = new HashSet<>();
        for (Abalo a : Abalo.values()) {
            assertTrue(ids.add(a.id()));
            assertEquals(Optional.of(a), Abalo.deId(a.id()));
        }
    }

    @Test
    void surtoVoltaPara15PorCentoENuncaZero() {
        assertEquals(15, RegrasLucidez.aposSurto(100));
        assertEquals(6, RegrasLucidez.aposSurto(40));
        assertEquals(2, RegrasLucidez.aposSurto(10));
        assertTrue(RegrasLucidez.aposSurto(1) >= 1);
        assertEquals(50, RegrasLucidez.ganhoAoDormir(100));
    }

    // ---------- Exposição ----------

    @Test
    void repetirRendeMenos() {
        assertEquals(50, RegrasExposicao.ganhoCentesimos(50, 0));
        assertEquals(25, RegrasExposicao.ganhoCentesimos(50, 1));
        assertEquals(16, RegrasExposicao.ganhoCentesimos(50, 2));
        assertEquals(0, RegrasExposicao.ganhoCentesimos(0, 0));
        assertEquals(50, RegrasExposicao.ganhoCentesimos(50, -3));
    }

    @Test
    void intensidadeMultiplicaComTeto() {
        assertEquals(150, RegrasExposicao.ganhoCentesimos(100, 1.5f, 0));
        assertEquals(75, RegrasExposicao.ganhoCentesimos(100, 1.5f, 1));
        assertEquals(500, RegrasExposicao.ganhoCentesimos(100, 50f, 0), "intensidade presa em 5");
        assertEquals(0, RegrasExposicao.ganhoCentesimos(100, 0f, 0));
        assertEquals(0, RegrasExposicao.ganhoCentesimos(100, -1f, 0));
        assertEquals(0, RegrasExposicao.ganhoCentesimos(100, Float.NaN, 0));
    }

    @Test
    void exposicaoSoPorContatoParanormal() {
        // Decisão do Nikolas (08/11/2026): escuro, morte comum e mob vanilla não dão Exposição.
        for (String fora : new String[] {"escuro", "morrer", "mob_noturno", "surto"}) {
            assertEquals(Optional.empty(), Fonte.deId(fora), fora + " não pode ser fonte de Exposição");
        }
        for (String dentro : new String[] {"criatura_vista", "criatura_luta", "criatura_morta",
                "reliquia_pegar", "reliquia_usar", "ritual", "dimensao", "aparicao"}) {
            assertTrue(Fonte.deId(dentro).isPresent(), "falta a fonte " + dentro);
        }
    }

    @Test
    void restoCompletaUmPonto() {
        var r = RegrasExposicao.somar(10, 80, 50);
        assertEquals(11, r.exposicao());
        assertEquals(30, r.resto());
        r = RegrasExposicao.somar(10, 0, 250);
        assertEquals(12, r.exposicao());
        assertEquals(50, r.resto());
    }

    @Test
    void ganhoAutomaticoParaEm99() {
        var r = RegrasExposicao.somar(98, 90, 500);
        assertEquals(99, r.exposicao());
        assertEquals(0, r.resto());
        r = RegrasExposicao.somar(99, 50, 100);
        assertEquals(99, r.exposicao());
        // 100 só por evento/comando, e o ganho automático nunca baixa quem já está em 100.
        assertEquals(100, RegrasExposicao.somar(100, 0, 100).exposicao());
    }

    @Test
    void exposicaoNuncaDesce() {
        assertEquals(30, RegrasExposicao.somar(30, 10, -100).exposicao());
        assertEquals(10, RegrasExposicao.somar(30, 10, -100).resto());
    }

    @Test
    void janelaDeRepeticao() {
        var rep = new RegrasExposicao.Repeticao();
        assertEquals(0, rep.registrar("escuro/nenhum", 0));
        assertEquals(1, rep.registrar("escuro/nenhum", 100));
        assertEquals(2, rep.registrar("escuro/nenhum", 200));
        assertEquals(0, rep.registrar("morrer/nenhum", 200), "outra fonte conta separado");
        assertEquals(0, rep.registrar("escuro/nenhum", RegrasExposicao.JANELA_REPETICAO), "janela passou: rende inteiro");
        assertEquals(0, rep.registrar("morrer/nenhum", 10), "relógio voltou (servidor reiniciou): recomeça");
    }

    @Test
    void repeticaoSobreviveAoSalvar() {
        var rep = new RegrasExposicao.Repeticao();
        rep.registrar("criatura_vista/sangue", 1000);
        rep.registrar("criatura_vista/sangue", 1100);
        rep.registrar("aparicao/nenhum", 0);
        var salvo = rep.paraSalvar(1200 + RegrasExposicao.JANELA_REPETICAO - 1000);
        assertFalse(salvo.containsKey("aparicao/nenhum"), "janela fechada não fica salva");
        var lida = RegrasExposicao.Repeticao.de(salvo);
        assertEquals(2, lida.registrar("criatura_vista/sangue", 1300), "relogar não zera o freio");
        // Dado estragado é ignorado, sem erro.
        var estragado = RegrasExposicao.Repeticao.de(java.util.Map.of("x", java.util.List.of(5L), "y", java.util.List.of(5L, -1L)));
        assertEquals(0, estragado.tamanho());
        assertEquals(0, RegrasExposicao.Repeticao.de(null).tamanho());
    }

    @Test
    void protecaoDepoisDoSurtoCobreUmGolpeSeguinte() {
        assertTrue(RegrasLucidez.PROTECAO_SURTO_TICKS > RegrasLucidez.SURTO_TICKS, "protege além do desmaio");
    }

    @Test
    void fontesTemIdUnicoEGanhoPositivo() {
        Set<String> ids = new HashSet<>();
        for (Fonte f : Fonte.values()) {
            assertTrue(ids.add(f.id()), "id repetido: " + f.id());
            assertTrue(f.base > 0);
            assertEquals(Optional.of(f), Fonte.deId(f.id().toUpperCase(java.util.Locale.ROOT)));
        }
        assertEquals(Optional.empty(), Fonte.deId("nada"));
        assertEquals(Optional.empty(), Fonte.deId(null));
    }

    // ---------- IDs ----------

    @Test
    void todosOsIdsNovosEstaoNoDocumento() throws Exception {
        String ids = Files.readString(Path.of("docs/ids.md"));
        for (String id : IdsDados.OUTROS) {
            assertTrue(ids.contains("`odteste:" + id + "`") || ids.contains("`" + id + "`"),
                    "falta " + id + " em docs/ids.md");
        }
    }

    @Test
    void tagDeComidaQuenteSomaETemEnsopadosBatataECarnes() throws Exception {
        // Pasta "item" (singular) em 1.21.1; plural é ignorado em silêncio pelo jogo.
        Path tag = Path.of("src/main/resources/data/odteste/tags/item", IdsDados.TAG_COMIDA_QUENTE + ".json");
        assertTrue(Files.exists(tag), "falta a tag " + tag);
        String json = Files.readString(tag).replaceAll("\\s", "");
        assertTrue(json.contains("\"replace\":false"), "a tag precisa somar (replace false) para outros mods entrarem");
        for (String id : new String[] {"mushroom_stew", "rabbit_stew", "beetroot_soup", "suspicious_stew",
                "baked_potato", "cooked_beef", "cooked_porkchop", "cooked_chicken", "cooked_mutton", "cooked_rabbit"}) {
            assertTrue(json.contains("\"minecraft:" + id + "\""), "falta minecraft:" + id + " na comida quente");
        }
    }
}
