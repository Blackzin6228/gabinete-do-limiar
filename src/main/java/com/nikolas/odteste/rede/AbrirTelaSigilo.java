package com.nikolas.odteste.rede;

import com.nikolas.odteste.OdMod;
import com.nikolas.odteste.sigilos.IdsSigilos;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Servidor → só o jogador que usou o giz: abre a tela dos sigilos. Leva o bloco clicado e <b>só os sigilos que ele já
 * aprendeu</b> (nunca receitas nem o que ele não sabe).
 */
public record AbrirTelaSigilo(BlockPos pos, List<String> sigilos) implements CustomPacketPayload {

    public static final Type<AbrirTelaSigilo> TIPO =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OdMod.MODID, IdsSigilos.PACOTE_ABRIR));

    public static final StreamCodec<ByteBuf, AbrirTelaSigilo> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, AbrirTelaSigilo::pos,
            ByteBufCodecs.stringUtf8(32).apply(ByteBufCodecs.list(64)), AbrirTelaSigilo::sigilos,
            AbrirTelaSigilo::new);

    private static volatile Consumer<AbrirTelaSigilo> receptor = m -> {};

    public static void definirReceptor(Consumer<AbrirTelaSigilo> r) {
        receptor = r == null ? m -> {} : r;
    }

    @Override
    public Type<AbrirTelaSigilo> type() {
        return TIPO;
    }

    public static void receber(AbrirTelaSigilo m, IPayloadContext contexto) {
        contexto.enqueueWork(() -> receptor.accept(m));
    }
}
