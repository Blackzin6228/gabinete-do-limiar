package com.nikolas.odteste.artefatos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nikolas.odteste.elementos.Elemento;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RegrasArtefatosTest {
    private static final Path LOOT = Path.of("src/main/resources/data/odteste/loot_modifiers");

    @Test
    void dozeArtefatosTresDeCadaElemento() {
        assertEquals(12, TipoArtefato.values().length);
        Map<Elemento, Integer> conta = new EnumMap<>(Elemento.class);
        for (TipoArtefato t : TipoArtefato.values()) conta.merge(t.elemento, 1, Integer::sum);
        assertEquals(Map.of(Elemento.SANGUE, 3, Elemento.MORTE, 3, Elemento.CONHECIMENTO, 3, Elemento.ENERGIA, 3), conta);
    }

    @Test
    void soCanetaFitaEFusivelFicamParaDepois() {
        Set<TipoArtefato> emBreve = new HashSet<>();
        for (TipoArtefato t : TipoArtefato.values()) if (t.emBreve) emBreve.add(t);
        assertEquals(Set.of(TipoArtefato.CANETA_DE_REGISTRO_APAGADO, TipoArtefato.FITA_DE_GRAVACAO_ANOMALA,
                TipoArtefato.FUSIVEL_SOBRECARREGADO), emBreve);
    }

    @Test
    void checagemEspalhadaUmaVezACadaCincoSegundos() {
        for (int id = 0; id < 50; id++) {
            int vezes = 0;
            for (int tick = 0; tick < RegrasArtefatos.INTERVALO_TICKS; tick++) {
                if (RegrasArtefatos.vezDeChecar(tick, id)) vezes++;
            }
            assertEquals(1, vezes, "jogador " + id);
        }
        // dois jogadores de ids vizinhos não checam no mesmo tick
        assertFalse(RegrasArtefatos.vezDeChecar(0, 1) && RegrasArtefatos.vezDeChecar(0, 2));
    }

    @Test
    void umaVezPorNoite() {
        assertFalse(RegrasArtefatos.usadoNestaNoite(-1, 0));
        assertTrue(RegrasArtefatos.usadoNestaNoite(5, 5));
        assertFalse(RegrasArtefatos.usadoNestaNoite(5, 6));
    }

    @Test
    void chanceDaConfigPresaEntreZeroEUm() {
        assertEquals(0, RegrasArtefatos.chance(-3));
        assertEquals(1, RegrasArtefatos.chance(7));
        assertEquals(0.02, RegrasArtefatos.chance(0.02));
        assertEquals(RegrasArtefatos.CHANCE_BAU_PADRAO, RegrasArtefatos.chance(Double.NaN));
    }

    @Test
    void chiadoMaisAltoPertoDoPerigo() {
        assertTrue(RegrasArtefatos.volumeChiado(1) > RegrasArtefatos.volumeChiado(10));
        assertEquals(1f, RegrasArtefatos.volumeChiado(0), 1e-4);
        assertTrue(RegrasArtefatos.volumeChiado(1000) > 0);
    }

    /** Cada artefato tem o seu arquivo de loot, listado no global, só com baús vanilla. */
    @Test
    void cadaArtefatoApareceEmBausVanilla() throws Exception {
        String global = Files.readString(Path.of("src/main/resources/data/neoforge/loot_modifiers/global_loot_modifiers.json"));
        assertTrue(global.replaceAll("\\s", "").contains("\"replace\":false"));
        for (TipoArtefato t : TipoArtefato.values()) {
            Path arquivo = LOOT.resolve(t.id + ".json");
            assertTrue(Files.exists(arquivo), "falta " + arquivo);
            JsonObject j = JsonParser.parseString(Files.readString(arquivo)).getAsJsonObject();
            assertEquals("odteste:artefato_no_bau", j.get("type").getAsString());
            assertEquals("odteste:" + t.id, j.get("artefato").getAsString());
            assertTrue(j.getAsJsonArray("baus").size() > 0, t.id + " sem baú");
            j.getAsJsonArray("baus").forEach(b -> assertTrue(b.getAsString().startsWith("minecraft:chests/"),
                    t.id + ": só baús vanilla (" + b.getAsString() + ")"));
            assertTrue(global.contains("\"odteste:" + t.id + "\""), "global_loot_modifiers sem " + t.id);
        }
    }

    @Test
    void idsDosArtefatosEstaoNoDocumento() throws Exception {
        String ids = Files.readString(Path.of("docs/ids.md"));
        for (TipoArtefato t : TipoArtefato.values()) {
            assertTrue(ids.contains("`odteste:" + t.id + "`"), "falta odteste:" + t.id + " em docs/ids.md");
        }
    }
}
