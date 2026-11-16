package com.nikolas.odteste.reliquias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nikolas.odteste.elementos.Elemento;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import org.junit.jupiter.api.Test;

class RegrasEspadasTest {

    @Test
    void cadaEspadaSoFuncionaParaOProprioElemento() {
        for (TipoEspada t : TipoEspada.values()) {
            for (Elemento e : Elemento.values()) {
                assertEquals(e == t.elemento, RegrasEspadas.funciona(e, t), t + " com " + e);
            }
            assertFalse(RegrasEspadas.funciona(null, t));
        }
    }

    @Test
    void asQuatroEspadasSaoUmaDeCadaElemento() {
        EnumSet<Elemento> vistos = EnumSet.noneOf(Elemento.class);
        for (TipoEspada t : TipoEspada.values()) assertTrue(vistos.add(t.elemento), "elemento repetido: " + t.elemento);
        assertEquals(EnumSet.of(Elemento.SANGUE, Elemento.MORTE, Elemento.CONHECIMENTO, Elemento.ENERGIA), vistos);
        assertEquals("facao_de_ritual_de_sangue", TipoEspada.FACAO_DE_RITUAL_DE_SANGUE.id);
        assertEquals("sabre_de_ritual_de_morte", TipoEspada.SABRE_DE_RITUAL_DE_MORTE.id);
        assertEquals("espada_de_ritual_de_conhecimento", TipoEspada.ESPADA_DE_RITUAL_DE_CONHECIMENTO.id);
        assertEquals("espeto_de_ritual_de_energia", TipoEspada.ESPETO_DE_RITUAL_DE_ENERGIA.id);
    }

    @Test
    void foraDoElementoOGolpeValeComoSoco() {
        assertEquals(1f, RegrasEspadas.DANO_FORA_DO_ELEMENTO);
    }

    @Test
    void adeusSoComPoucaVida() {
        assertTrue(RegrasEspadas.adeus(5, 20));
        assertTrue(RegrasEspadas.adeus(1, 20));
        assertFalse(RegrasEspadas.adeus(6, 20));
        assertFalse(RegrasEspadas.adeus(0, 20));
        assertFalse(RegrasEspadas.adeus(1, 0));
    }

    @Test
    void cargaDoEspetoEnchePresoNoMaximoEMaisRapidoComAliado() {
        int sozinho = 0;
        int acertos = 0;
        while (!RegrasEspadas.cheia(sozinho)) {
            sozinho = RegrasEspadas.carregar(sozinho, false);
            acertos++;
        }
        assertEquals(RegrasEspadas.ESPETO_CARGA_CHEIA, acertos);
        int comAliado = 0;
        int acertosAliado = 0;
        while (!RegrasEspadas.cheia(comAliado)) {
            comAliado = RegrasEspadas.carregar(comAliado, true);
            acertosAliado++;
        }
        assertTrue(acertosAliado < acertos);
        assertEquals(RegrasEspadas.ESPETO_CARGA_CHEIA, RegrasEspadas.carregar(RegrasEspadas.ESPETO_CARGA_CHEIA, true));
    }

    @Test
    void curaDaSedeTemTetoDeDoisCoracoesPorTrintaSegundos() {
        RegrasEspadas.FreioCura f = new RegrasEspadas.FreioCura();
        assertEquals(2f, f.curar(0, 2f));
        assertEquals(2f, f.curar(100, 2f));
        assertEquals(0f, f.curar(200, 2f));
        assertEquals(2f, f.curar(RegrasEspadas.SEDE_JANELA_TICKS, 2f));
        // relógio voltou (mundo trocado): recomeça a janela em vez de travar
        assertEquals(2f, f.curar(10, 2f));
    }

    @Test
    void idsDasEspadasEstaoNoDocumento() throws Exception {
        String ids = Files.readString(Path.of("docs/ids.md"));
        for (TipoEspada t : TipoEspada.values()) {
            assertTrue(ids.contains("`odteste:" + t.id + "`"), "falta odteste:" + t.id + " em docs/ids.md");
        }
    }
}
