package com.nikolas.odteste.dados;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nikolas.odteste.OdMod;
import com.nikolas.odteste.elementos.Elemento;
import com.nikolas.odteste.registro.Registro;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Dados que o mundo do servidor já tem (do mod antigo, Od Teste) e que nada do Gabinete do Limiar usa hoje:
 * Marca, Transcender, recarga dos artefatos, noite da criatura, cansaço do elemento e cenas vistas.
 *
 * <p>Ficam registrados com o mesmo ID e o mesmo formato <b>só para não serem apagados</b>: dado de jogador sem
 * registro some no primeiro save (o NeoForge descarta). Ninguém lê nem escreve aqui. Se um dia uma parte nova
 * precisar de um deles, ela passa a usar este registro (não crie outro com o mesmo ID).
 *
 * <p>Também tira o {@code odteste:dedal_coracao} (−1 coração do Dedal Dentado, que saiu): quem tirava era o Dedal,
 * ao dormir; sem ele, o coração ficaria preso para sempre.
 */
public final class DadosGuardados {
    /** Formato antigo do cansaço do elemento ({dia, brutas}). */
    record Cansaco(long dia, int brutas) {
        static final Codec<Cansaco> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.LONG.fieldOf("dia").forGetter(Cansaco::dia),
                Codec.INT.fieldOf("brutas").forGetter(Cansaco::brutas)
        ).apply(i, Cansaco::new));
    }

    public static final Supplier<AttachmentType<Elemento>> MARCA_ELEMENTO = Registro.DADOS.register(
            IdsDados.MARCA_ELEMENTO,
            () -> AttachmentType.builder(() -> Elemento.NENHUM)
                    .serialize(Codec.STRING.xmap(Elemento::deIdOuNenhum, Elemento::id))
                    .copyOnDeath()
                    .build());
    public static final Supplier<AttachmentType<Integer>> MARCA_ESTAGIO = inteiro(IdsDados.MARCA_ESTAGIO);
    public static final Supplier<AttachmentType<Integer>> TRANSCENDER_NIVEL = inteiro(IdsDados.TRANSCENDER_NIVEL);
    public static final Supplier<AttachmentType<Map<String, Long>>> ARTEFATO_RECARGA = Registro.DADOS.register(
            IdsDados.ARTEFATO_RECARGA,
            () -> AttachmentType.<Map<String, Long>>builder(() -> Map.of())
                    .serialize(Codec.unboundedMap(Codec.STRING, Codec.LONG))
                    .copyOnDeath()
                    .build());
    public static final Supplier<AttachmentType<Long>> CRIATURA_NOITE = Registro.DADOS.register(
            IdsDados.CRIATURA_NOITE,
            () -> AttachmentType.builder(() -> -1L)
                    .serialize(Codec.LONG)
                    .copyOnDeath()
                    .build());
    public static final Supplier<AttachmentType<Cansaco>> CANSACO = Registro.DADOS.register(
            IdsDados.CANSACO_ELEMENTO,
            () -> AttachmentType.builder(() -> new Cansaco(-1L, 0))
                    .serialize(Cansaco.CODEC)
                    .copyOnDeath()
                    .build());
    public static final Supplier<AttachmentType<List<String>>> CENAS_VISTAS = Registro.DADOS.register(
            IdsDados.CENAS_VISTAS,
            () -> AttachmentType.<List<String>>builder(() -> List.of())
                    .serialize(Codec.STRING.listOf())
                    .copyOnDeath()
                    .build());

    private static final ResourceLocation DEDAL =
            ResourceLocation.fromNamespaceAndPath(OdMod.MODID, IdsDados.MODIFICADOR_DEDAL);

    private DadosGuardados() {}

    /** Força o carregamento da classe (os registros entram antes do evento de registro) e liga a limpeza do Dedal. */
    public static void iniciar() {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent e) -> {
            AttributeInstance vida = e.getEntity().getAttribute(Attributes.MAX_HEALTH);
            if (vida != null && vida.getModifier(DEDAL) != null) vida.removeModifier(DEDAL);
        });
    }

    private static Supplier<AttachmentType<Integer>> inteiro(String id) {
        return Registro.DADOS.register(id, () -> AttachmentType.builder(() -> 0)
                .serialize(Codec.INT)
                .copyOnDeath()
                .build());
    }
}
