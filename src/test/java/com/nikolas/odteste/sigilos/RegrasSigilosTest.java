package com.nikolas.odteste.sigilos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nikolas.odteste.dados.IdsDados;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;

class RegrasSigilosTest {
    private static final Path ASSETS = Path.of("src/main/resources/assets/odteste");

    @Test
    void quarentaEDoisNomesSemRepetir() {
        assertEquals(42, IdsSigilos.SIMBOLOS.size());
        assertEquals(42, new HashSet<>(IdsSigilos.SIMBOLOS).size());
    }

    @Test
    void ensinarEEsquecer() {
        List<String> vazia = List.of();
        List<String> um = RegrasSigilos.com(vazia, "letra_a");
        assertEquals(List.of("letra_a"), um);
        assertSame(um, RegrasSigilos.com(um, "letra_a"), "ensinar de novo não muda nada");
        assertSame(vazia, RegrasSigilos.com(vazia, "nao_existe"), "nome inexistente é ignorado");
        assertEquals(List.of("letra_a", "elemento_medo"), RegrasSigilos.com(um, "elemento_medo"));
        assertEquals(List.of(), RegrasSigilos.sem(um, "letra_a"));
        assertSame(um, RegrasSigilos.sem(um, "letra_b"));
    }

    @Test
    void limparTiraDesconhecidoERepetido() {
        assertEquals(List.of("letra_b", "digito_3"),
                RegrasSigilos.limpar(List.of("letra_b", "velho_do_mod_antigo", "letra_b", "digito_3")));
    }

    @Test
    void telaMostraNaOrdemOficial() {
        assertEquals(List.of("letra_a", "elemento_medo"),
                RegrasSigilos.naOrdemOficial(List.of("elemento_medo", "letra_a", "inexistente")));
        assertEquals(List.of(), RegrasSigilos.naOrdemOficial(List.of()));
    }

    @Test
    void servidorConferePedido() {
        assertTrue(RegrasSigilos.giroValido(0) && RegrasSigilos.giroValido(3));
        assertFalse(RegrasSigilos.giroValido(-1) || RegrasSigilos.giroValido(4));
        assertTrue(RegrasSigilos.perto(36.0));
        assertFalse(RegrasSigilos.perto(36.1));
        assertTrue(RegrasSigilos.livre(-1, 0), "nunca pediu");
        assertFalse(RegrasSigilos.livre(100, 119));
        assertTrue(RegrasSigilos.livre(100, 120));
        assertTrue(RegrasSigilos.livre(5000, 10), "servidor reiniciou");
        assertFalse(RegrasSigilos.valido(null));
    }

    @Test
    void idsDocumentadosEmIds() throws Exception {
        String ids = Files.readString(Path.of("docs/ids.md"));
        for (String id : IdsSigilos.OUTROS) assertTrue(ids.contains("`odteste:" + id + "`"), "falta odteste:" + id);
        assertTrue(ids.contains("`odteste:" + IdsDados.SIGILOS_CONHECIDOS + "`"));
        assertTrue(ids.contains("`" + IdsSigilos.PROPRIEDADE_SIMBOLO + "`") && ids.contains("`" + IdsSigilos.PROPRIEDADE_GIRO + "`"));
        for (String s : IdsSigilos.SIMBOLOS) assertTrue(ids.contains(s), "falta o sigilo " + s + " em docs/ids.md");
    }

    @Test
    void cadaSigiloTemTexturaModeloETextoNasDuasLinguas() throws Exception {
        String pt = Files.readString(ASSETS.resolve("lang/pt_br.json"));
        String en = Files.readString(ASSETS.resolve("lang/en_us.json"));
        String estados = Files.readString(ASSETS.resolve("blockstates/sigilo.json"));
        for (String s : IdsSigilos.SIMBOLOS) {
            assertTrue(Files.exists(ASSETS.resolve("textures/block/sigilo/" + s + ".png")), "falta a textura de " + s);
            assertTrue(Files.exists(ASSETS.resolve("models/block/sigilo_" + s + ".json")), "falta o modelo de " + s);
            for (int g = 0; g < RegrasSigilos.GIROS; g++) {
                assertTrue(estados.contains("\"simbolo=" + s + ",giro=" + g + "\""), "falta o estado " + s + " giro " + g);
            }
            assertTrue(pt.contains("\"odteste.sigilo." + s + "\""), "falta o nome em pt_br de " + s);
            assertTrue(en.contains("\"odteste.sigilo." + s + "\""), "falta o nome em en_us de " + s);
        }
    }

    @Test
    void gizTemTexturaEModelo() {
        assertTrue(Files.exists(ASSETS.resolve("textures/item/giz.png")));
        assertTrue(Files.exists(ASSETS.resolve("models/item/giz.json")));
    }

    @Test
    void nenhumNomeDeSigiloMostraNumeroNaTela() throws Exception {
        String pt = Files.readString(ASSETS.resolve("lang/pt_br.json"));
        for (String linha : pt.split("\n")) {
            if (linha.contains("\"odteste.sigilo.") && !linha.contains("odteste.comando")) {
                String valor = linha.substring(linha.indexOf(':') + 1);
                assertFalse(valor.matches(".*[0-9].*"), "texto do jogador com número: " + linha);
            }
        }
    }
}
