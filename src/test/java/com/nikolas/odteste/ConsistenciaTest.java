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

    /** Toda tag nossa soma com as de outros mods ("replace": false) e não cita ID nosso que não existe mais. */
    @Test
    void tagsSomamENaoCitamIdAntigo() throws Exception {
        try (var arquivos = Files.walk(Path.of("src/main/resources/data"))) {
            for (Path p : arquivos.filter(f -> f.toString().contains("tags") && f.toString().endsWith(".json")).toList()) {
                String json = Files.readString(p);
                assertTrue(json.matches("(?s).*\"replace\"\\s*:\\s*false.*"), p + " precisa de \"replace\": false");
                assertFalse(json.contains("\"odteste:"), p + " cita um ID odteste: (crie o ID antes de pôr na tag)");
            }
        }
    }

    private static java.util.Set<String> chaves(String json) {
        var m = java.util.regex.Pattern.compile("\"([^\"]+)\"\\s*:").matcher(json);
        var s = new java.util.TreeSet<String>();
        while (m.find()) s.add(m.group(1));
        return s;
    }
}
