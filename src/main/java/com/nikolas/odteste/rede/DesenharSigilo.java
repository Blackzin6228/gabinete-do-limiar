package com.nikolas.odteste.rede;

import com.nikolas.odteste.OdMod;
import com.nikolas.odteste.sigilos.IdsSigilos;
import com.nikolas.odteste.sigilos.Sigilos;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Jogador → servidor: "risque este sigilo no chão deste bloco, virado para este lado". Nada aqui é confiável: o
 * servidor confere giz na mão, sigilo aprendido, distância, espaço livre e a espera (ver {@link Sigilos#desenhar}).
 */
public record DesenharSigilo(BlockPos pos, String sigilo, int giro) implements CustomPacketPayload {

    public static final Type<DesenharSigilo> TIPO =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OdMod.MODID, IdsSigilos.PACOTE_DESENHAR));

    public static final StreamCodec<ByteBuf, DesenharSigilo> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, DesenharSigilo::pos,
            ByteBufCodecs.stringUtf8(32), DesenharSigilo::sigilo,
            ByteBufCodecs.VAR_INT, DesenharSigilo::giro,
            DesenharSigilo::new);

    @Override
    public Type<DesenharSigilo> type() {
        return TIPO;
    }

    public static void receber(DesenharSigilo m, IPayloadContext contexto) {
        contexto.enqueueWork(() -> {
            if (contexto.player() instanceof ServerPlayer jogador) Sigilos.desenhar(jogador, m.pos(), m.sigilo(), m.giro());
        });
    }
}
