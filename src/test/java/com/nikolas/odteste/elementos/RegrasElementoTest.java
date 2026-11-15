package com.nikolas.odteste.elementos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class RegrasElementoTest {

    @Test
    void soOProprioElementoEAfim() {
        assertTrue(RegrasElemento.afim(Elemento.SANGUE, Elemento.SANGUE));
        assertFalse(RegrasElemento.afim(Elemento.MORTE, Elemento.SANGUE));
        assertFalse(RegrasElemento.afim(Elemento.NENHUM, Elemento.NENHUM));
        assertFalse(RegrasElemento.afim(null, Elemento.SANGUE));
    }

    @Test
    void custoCai30PorCentoSoNoProprioElemento() {
        assertEquals(7, RegrasElemento.custo(10, Elemento.SANGUE, Elemento.SANGUE));
        assertEquals(18, RegrasElemento.custo(25, Elemento.SANGUE, Elemento.SANGUE)); // 17,5 arredonda para 18
        assertEquals(14, RegrasElemento.custo(20, Elemento.SANGUE, Elemento.SANGUE));
        assertEquals(10, RegrasElemento.custo(10, Elemento.ENERGIA, Elemento.SANGUE));
        assertEquals(10, RegrasElemento.custo(10, Elemento.NENHUM, Elemento.SANGUE));
    }

    @Test
    void custoQueExistiaNuncaZera() {
        assertEquals(1, RegrasElemento.custo(1, Elemento.SANGUE, Elemento.SANGUE));
        assertEquals(0, RegrasElemento.custo(0, Elemento.SANGUE, Elemento.SANGUE));
        assertEquals(-3, RegrasElemento.custo(-3, Elemento.SANGUE, Elemento.SANGUE));
    }

    @Test
    void custoDeVida() {
        assertEquals(4.2f, RegrasElemento.custoVida(6f, Elemento.SANGUE, Elemento.SANGUE), 1e-4);
        assertEquals(6f, RegrasElemento.custoVida(6f, Elemento.MORTE, Elemento.SANGUE), 1e-6);
        assertTrue(Float.isNaN(RegrasElemento.custoVida(Float.NaN, Elemento.SANGUE, Elemento.SANGUE)));
    }

    @Test
    void limiarDeVidaSobeSoNoProprioElemento() {
        assertEquals(0.7f, RegrasElemento.limiarVida(0.7f, Elemento.MORTE, Elemento.SANGUE), 1e-6);
        assertTrue(RegrasElemento.limiarVida(0.7f, Elemento.SANGUE, Elemento.SANGUE) > 0.7f);
    }

    @Test
    void marcadoresDosEventos() {
        assertEquals("escolhido_sangue", RegrasElemento.marcador(Elemento.SANGUE));
        assertEquals("escolhido_conhecimento", RegrasElemento.marcador(Elemento.CONHECIMENTO));
        assertThrows(IllegalArgumentException.class, () -> RegrasElemento.marcador(Elemento.NENHUM));
    }

    /** O elemento é segredo: nada da rede nem do cliente lê o elemento do jogador. */
    @Test
    void elementoNaoVaiParaOCliente() throws Exception {
        String rede = juntar(Path.of("src/main/java/com/nikolas/odteste/rede"));
        assertFalse(rede.contains("DadosJogador.elemento(") || rede.contains("ELEMENTO_REVELADO") || rede.contains("ElementoJogador"),
                "o elemento do jogador não pode ir na rede");
        String cliente = juntar(Path.of("src/main/java/com/nikolas/odteste/cliente"));
        assertFalse(cliente.contains("ElementoJogador") || cliente.contains("DadosJogador.elemento"),
                "o cliente não pode ler o elemento do jogador");
    }

    private static String juntar(Path pasta) throws Exception {
        try (var arquivos = Files.walk(pasta)) {
            return String.join("\n", arquivos.filter(Files::isRegularFile).map(p -> {
                try {
                    return Files.readString(p);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).toList());
        }
    }
}
