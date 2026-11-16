package com.nikolas.odteste.maquinas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class RegrasFalhaTest {

    @Test
    void chanceSeguraProtecaoEPrendeNaFaixa() {
        assertEquals(0.04, RegrasFalha.chance(4, 1.0), 1e-9);
        assertEquals(0.02, RegrasFalha.chance(4, 0.5), 1e-9);
        assertEquals(0.0, RegrasFalha.chance(4, 0.0), 1e-9);
        assertEquals(0.04, RegrasFalha.chance(4, 7.0), 1e-9, "fator acima de 1 não aumenta a chance");
        assertEquals(1.0, RegrasFalha.chance(500, 1.0), 1e-9);
        assertEquals(0.0, RegrasFalha.chance(-3, 1.0), 1e-9);
    }

    @Test
    void sorteioEscolheFalha() {
        assertTrue(RegrasFalha.falhou(0.04, 0.0399));
        assertFalse(RegrasFalha.falhou(0.04, 0.04));
        assertFalse(RegrasFalha.falhou(0.0, 0.0), "chance zero nunca falha");
    }

    @Test
    void relogioRespeitaAEspera() {
        var r = new RegrasFalha.Relogio<String>();
        assertTrue(r.tentar("a", 1000, 1200), "primeira vez pode");
        assertFalse(r.tentar("a", 1000 + 1199, 1200), "antes da espera não");
        assertTrue(r.tentar("a", 1000 + 1200, 1200), "na hora da espera pode");
        assertTrue(r.tentar("b", 1001, 1200), "chaves diferentes não se atrapalham");
    }

    @Test
    void podeNaoMarcaEMarcarMarca() {
        var r = new RegrasFalha.Relogio<String>();
        assertTrue(r.pode("j", 50, 600));
        assertTrue(r.pode("j", 51, 600), "perguntar não gasta a vez");
        r.marcar("j", 51);
        assertFalse(r.pode("j", 52, 600));
        assertTrue(r.pode("j", 51 + 600, 600));
    }

    @Test
    void relogioDoServidorQueVoltouNoTempoLibera() {
        var r = new RegrasFalha.Relogio<String>();
        r.marcar("j", 5000);
        assertTrue(r.pode("j", 10, 600), "tick menor que a marca (servidor reiniciou) não trava para sempre");
    }

    @Test
    void relogioTemTeto() {
        var r = new RegrasFalha.Relogio<Integer>(3);
        for (int i = 0; i < 10; i++) r.marcar(i, i);
        assertEquals(3, r.tamanho());
        assertTrue(r.pode(0, 11, 600), "a marca mais antiga foi esquecida");
        assertFalse(r.pode(9, 11, 600));
    }

    @Test
    void configEGanchoDocumentadosEmIds() throws Exception {
        String ids = Files.readString(Path.of("docs/ids.md"));
        assertTrue(ids.contains("`odteste:" + IdsMaquinas.TAG_REDUZ_FALHA + "`"), "falta a tag em docs/ids.md");
        assertTrue(ids.contains(ConfigMaquinas.ARQUIVO), "falta o arquivo de config em docs/ids.md");
        for (String k : IdsMaquinas.CHAVES_CONFIG) {
            assertTrue(ids.contains("`" + k + "`"), "falta a chave " + k + " em docs/ids.md");
        }
    }

    @Test
    void tagDoFusivelExisteEJuntaComAsOutras() throws Exception {
        String json = Files.readString(Path.of("src/main/resources/data/odteste/tags/item/"
                + IdsMaquinas.TAG_REDUZ_FALHA + ".json"));
        assertTrue(json.matches("(?s).*\"replace\"\\s*:\\s*false.*"));
    }
}
