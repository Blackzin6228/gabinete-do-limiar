package com.nikolas.odteste.cracha;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * O que o crachá guarda sobre o dono. {@code elemento} só vem preenchido se o elemento já foi revelado
 * (vazio = não revelado): assim o cliente nunca recebe um elemento escondido.
 */
public record CrachaDados(UUID dono, String nome, boolean mestre, String elemento) {
    public static final Codec<CrachaDados> CODEC = RecordCodecBuilder.create(i -> i.group(
            UUIDUtil.CODEC.fieldOf("dono").forGetter(CrachaDados::dono),
            Codec.STRING.fieldOf("nome").forGetter(CrachaDados::nome),
            Codec.BOOL.optionalFieldOf("mestre", false).forGetter(CrachaDados::mestre),
            Codec.STRING.optionalFieldOf("elemento", "").forGetter(CrachaDados::elemento)
    ).apply(i, CrachaDados::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrachaDados> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, CrachaDados::dono,
            ByteBufCodecs.STRING_UTF8, CrachaDados::nome,
            ByteBufCodecs.BOOL, CrachaDados::mestre,
            ByteBufCodecs.STRING_UTF8, CrachaDados::elemento,
            CrachaDados::new);

    public boolean revelado() {
        return !elemento.isEmpty();
    }
}
