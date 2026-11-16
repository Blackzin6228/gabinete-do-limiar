package com.nikolas.odteste;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.Test;

/** Travas baratas que pegam erro antes de virar crash no servidor. */
class ConsistenciaTest {

    private static Properties props() throws Exception {
        Properties p = new Properties();
        try (Reader r = Files.newBufferedReader(Path.of("gradle.properties"))) {
            p.load(r);
        }
        return p;
    }

    @Test
    void modidIgualNoCodigoENoGradle() throws Exception {
        assertEquals(props().getProperty("mod_id"), OdMod.MODID);
    }

    @Test
    void pastaDeAssetsBateComOModid() throws Exception {
        String modid = props().getProperty("mod_id");
        assertTrue(Files.isDirectory(Path.of("src/main/resources/assets", modid)),
                "a pasta assets/" + modid + " precisa existir");
    }

    @Test
    void portuguesEInglesTemAsMesmasChaves() throws Exception {
        String modid = props().getProperty("mod_id");
        Path lang = Path.of("src/main/resources/assets", modid, "lang");
        var pt = chaves(Files.readString(lang.resolve("pt_br.json")));
        var en = chaves(Files.readString(lang.resolve("en_us.json")));
        assertEquals(pt, en, "toda tradução em pt_br precisa existir em en_us (e vice-versa)");
    }

    /** Toda tag nossa soma com as de outros mods ("replace": false) e só cita ID nosso que existe (está em docs/ids.md). */
    @Test
    void tagsSomamENaoCitamIdAntigo() throws Exception {
        String ids = Files.readString(Path.of("docs/ids.md"));
        try (var arquivos = Files.walk(Path.of("src/main/resources/data"))) {
            for (Path p : arquivos.filter(f -> f.toString().contains("tags") && f.toString().endsWith(".json")).toList()) {
                String json = Files.readString(p);
                assertTrue(json.matches("(?s).*\"replace\"\\s*:\\s*false.*"), p + " precisa de \"replace\": false");
                var m = java.util.regex.Pattern.compile("\"(odteste:[a-z0-9_/]+)\"").matcher(json);
                while (m.find()) {
                    assertTrue(ids.contains("`" + m.group(1) + "`"), p + " cita " + m.group(1) + ", que não está em docs/ids.md");
                }
            }
        }
    }

    /** Toda espada e todo artefato tem modelo, textura (placeholder até a da prévia), nome e descrição nos dois idiomas. */
    @Test
    void itensDoPacote1TemModeloTexturaETraducao() throws Exception {
        Path assets = Path.of("src/main/resources/assets/odteste");
        var pt = chaves(Files.readString(assets.resolve("lang/pt_br.json")));
        java.util.List<String> ids = new java.util.ArrayList<>();
        for (var t : com.nikolas.odteste.reliquias.TipoEspada.values()) ids.add(t.id);
        for (var t : com.nikolas.odteste.artefatos.TipoArtefato.values()) ids.add(t.id);
        for (String id : ids) {
            assertTrue(Files.exists(assets.resolve("models/item/" + id + ".json")), "falta o modelo de " + id);
            assertTrue(Files.exists(assets.resolve("textures/item/" + id + ".png")), "falta a textura de " + id);
            assertTrue(pt.contains("item.odteste." + id), "falta o nome de " + id);
            assertTrue(pt.contains("item.odteste." + id + ".desc"), "falta a descrição de " + id);
        }
        for (String id : ids) {
            assertTrue(pt.contains("item.odteste." + id + ".preco"), "falta a segunda linha (preço) de " + id);
        }
    }

    /** O Fusível protege da falha das máquinas pela tag (a máquina lê a tag; o item é nosso). */
    @Test
    void fusivelEstaNaTagQueReduzFalha() throws Exception {
        String tag = Files.readString(Path.of("src/main/resources/data/odteste/tags/item/reduz_falha_maquina.json"));
        assertTrue(tag.contains("\"odteste:" + com.nikolas.odteste.artefatos.TipoArtefato.FUSIVEL_SOBRECARREGADO.id + "\""),
                "o Fusível Sobrecarregado precisa estar na tag odteste:reduz_falha_maquina");
    }

    private static java.util.Set<String> chaves(String json) {
        var m = java.util.regex.Pattern.compile("\"([^\"]+)\"\\s*:").matcher(json);
        var s = new java.util.TreeSet<String>();
        while (m.find()) s.add(m.group(1));
        return s;
    }
}
