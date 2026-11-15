package com.nikolas.odteste.sistemas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Faixas, frases de /exposicao e /lucidez e a lentidão da Lucidez (Java puro). */
class RegrasHorrorTest {

    private static RegrasHorror.Sorteio sorteio(long semente) {
        Random r = new Random(semente);
        return r::nextInt;
    }

    @Test
    void faixasDeExposicaoEDeLucidez() {
        assertEquals(0, RegrasHorror.faixa(Degrau.COMUM));
        assertEquals(1, RegrasHorror.faixa(Degrau.TOCADO));
        assertEquals(2, RegrasHorror.faixa(Degrau.MARCADO));
        assertEquals(3, RegrasHorror.faixa(Degrau.FUNDO));
        assertEquals(4, RegrasHorror.faixa(Degrau.A_BEIRA));
        assertEquals(4, RegrasHorror.faixa(Degrau.ATRAVESSADO));
        assertEquals(0, RegrasHorror.faixa((Degrau) null));
        assertEquals(0, RegrasHorror.faixa(FaixaLucidez.FIRME));
        assertEquals(3, RegrasHorror.faixa(FaixaLucidez.SURTO), "o surto conta como Baixa");
    }

    @Test
    void lentidaoSoNaLucidezBaixaComPoucaLuz() {
        for (int luz = 0; luz <= 15; luz++) {
            assertEquals(0, RegrasLucidez.lentidao(FaixaLucidez.FIRME, luz));
            assertEquals(0, RegrasLucidez.lentidao(FaixaLucidez.INQUIETO, luz));
            assertEquals(0, RegrasLucidez.lentidao(FaixaLucidez.ABALADO, luz));
        }
        assertEquals(-0.08, RegrasLucidez.lentidao(FaixaLucidez.QUEBRANDO, 0));
        assertEquals(-0.08, RegrasLucidez.lentidao(FaixaLucidez.QUEBRANDO, RegrasLucidez.LUZ_FRACA));
        assertEquals(0, RegrasLucidez.lentidao(FaixaLucidez.QUEBRANDO, RegrasLucidez.LUZ_FRACA + 1), "com luz, nada");
        assertEquals(-0.08, RegrasLucidez.lentidao(FaixaLucidez.SURTO, 3));
    }

    @Test
    void todaFaixaTemTresFrasesNasDuasLinguas() throws Exception {
        String pt = Files.readString(Path.of("src/main/resources/assets/odteste/lang/pt_br.json"));
        String en = Files.readString(Path.of("src/main/resources/assets/odteste/lang/en_us.json"));
        Set<String> simbolos = new HashSet<>();
        for (Degrau d : Degrau.values()) {
            for (int v = 1; v <= FrasesFaixa.VARIACOES; v++) {
                assertTrue(pt.contains("\"" + FrasesFaixa.chave(d, v) + "\""), FrasesFaixa.chave(d, v));
                assertTrue(en.contains("\"" + FrasesFaixa.chave(d, v) + "\""), FrasesFaixa.chave(d, v));
            }
            simbolos.add(FrasesFaixa.simbolo(d));
        }
        assertEquals(Degrau.values().length, simbolos.size(), "um símbolo diferente por degrau");
        simbolos.clear();
        for (FaixaLucidez f : FaixaLucidez.values()) {
            for (int v = 1; v <= FrasesFaixa.VARIACOES; v++) {
                assertTrue(pt.contains("\"" + FrasesFaixa.chave(f, v) + "\""), FrasesFaixa.chave(f, v));
                assertTrue(en.contains("\"" + FrasesFaixa.chave(f, v) + "\""), FrasesFaixa.chave(f, v));
            }
            simbolos.add(FrasesFaixa.simbolo(f));
        }
        assertEquals(FaixaLucidez.values().length, simbolos.size(), "um símbolo diferente por faixa");
        String k = "odteste.horror.sussurro.nome"; // o nome no chat do susto "sussurro"
        assertTrue(pt.contains("\"" + k + "\"") && en.contains("\"" + k + "\""), k);
        assertEquals("odteste.horror.exposicao.critica.2", FrasesFaixa.chave(Degrau.ATRAVESSADO, 2));
        assertEquals("odteste.horror.lucidez.baixa.1", FrasesFaixa.chave(FaixaLucidez.QUEBRANDO, 1));
    }

    @Test
    void fraseNuncaRepeteDuasVezesSeguidas() {
        RegrasHorror.Sorteio s = sorteio(17);
        Set<Integer> vistas = new HashSet<>();
        int anterior = 0;
        for (int i = 0; i < 300; i++) {
            int v = FrasesFaixa.sortear(anterior, s);
            assertTrue(v >= 1 && v <= FrasesFaixa.VARIACOES);
            assertNotEquals(anterior, v);
            vistas.add(v);
            anterior = v;
        }
        assertEquals(3, vistas.size(), "as três aparecem");
    }
}
