package com.nikolas.odteste.cracha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nikolas.odteste.cracha.RegrasCracha.Cartao;
import java.util.List;
import org.junit.jupiter.api.Test;

class RegrasCrachaTest {
    @Test
    void mestreVenceTudo() {
        assertEquals(Cartao.MESTRE, RegrasCracha.escolher(true, true));
        assertEquals(Cartao.MESTRE, RegrasCracha.escolher(true, false));
    }

    @Test
    void elementoReveladoUsaCartaoLimpo() {
        assertEquals(Cartao.LIMPO, RegrasCracha.escolher(false, true));
    }

    @Test
    void semNadaUsaLotacaoBorrada() {
        assertEquals(Cartao.LOTACAO, RegrasCracha.escolher(false, false));
    }

    @Test
    void nomesDasTexturas() {
        assertEquals("cracha_cartao_mestre", Cartao.MESTRE.textura());
        assertEquals("cracha_cartao_limpo", Cartao.LIMPO.textura());
        assertEquals("cracha_cartao", Cartao.LOTACAO.textura());
    }

    @Test
    void mestreVemDaListaSemDiferencaDeMaiuscula() {
        assertTrue(RegrasCracha.ehMestre("blackzin6228", List.of("Blackzin6228")));
        assertFalse(RegrasCracha.ehMestre("Outro", List.of("Blackzin6228")));
        assertFalse(RegrasCracha.ehMestre("Blackzin6228", List.of())); // lista vazia = ninguém
        assertFalse(RegrasCracha.ehMestre(null, List.of("a")));
    }

    @Test
    void escalaEOMaiorInteiroQueCabe() {
        assertEquals(1, RegrasCracha.escala(100, 50));   // não cabe: mínimo 1
        assertEquals(2, RegrasCracha.escala(427, 240));  // GUI pequena
        assertEquals(3, RegrasCracha.escala(480, 300));
        assertEquals(2, RegrasCracha.escala(1000, 200)); // a altura limita
        assertEquals(1, RegrasCracha.escala(0, 0));
    }

    @Test
    void elementoSoSeGuardaSeRevelado() {
        assertEquals("", RegrasCracha.elementoParaGuardar("sangue", false));
        assertEquals("sangue", RegrasCracha.elementoParaGuardar("sangue", true));
        assertEquals("", RegrasCracha.elementoParaGuardar("nenhum", true));
        assertEquals("", RegrasCracha.elementoParaGuardar(null, true));
    }
}
