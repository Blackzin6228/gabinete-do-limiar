package com.nikolas.odteste.cracha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParser;
import com.nikolas.odteste.historia.livros.Livro;
import com.nikolas.odteste.historia.livros.LeitorLivros;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class RegrasKitTest {
    @Test
    void daSoUmaVez() {
        boolean recebeu = false;
        int entregas = 0;
        for (int login = 0; login < 3; login++) {
            if (RegrasKit.deveDar(recebeu)) {
                entregas++;
                recebeu = true;
            }
        }
        assertEquals(1, entregas);
    }

    @Test
    void esquecerFazDarDeNovo() {
        assertTrue(RegrasKit.deveDar(false));
        assertFalse(RegrasKit.deveDar(true));
    }

    @Test
    void dossieAbreNoLeitorDeLivros() throws Exception {
        String json = Files.readString(Path.of("src/main/resources/data/odteste/odteste_livros/dossie.json"));
        Livro l = LeitorLivros.ler("dossie", JsonParser.parseString(json));
        assertEquals("Dossiê de campo", l.titulo());
        assertEquals("Gabinete do Limiar", l.autor());
        assertEquals(6, l.paginas().size());
        assertTrue(l.paginas().get(0).texto().contains("{jogador}"));
        assertTrue(l.paginas().get(0).texto().contains("{grupo}"));
    }
}
