package com.nikolas.odteste.rede;

import com.nikolas.odteste.OdMod;
import com.nikolas.odteste.dados.IdsDados;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Servidor → só o próprio jogador: estas criaturas ficam com contorno na tela dele por {@code ticks}
 * (Óculos de Velório, Sentença Sem Recurso). Ninguém mais vê. Teto de {@link #TETO} criaturas por pacote.
 */
public record Contorno(List<Integer> ids, int ticks) implements CustomPacketPayload {
    public static final int TETO = 16;

    public static final Type<Contorno> TIPO =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OdMod.MODID, IdsDados.PACOTE_CONTORNO));

    public static final StreamCodec<ByteBuf, Contorno> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list(TETO)), Contorno::ids,
            ByteBufCodecs.VAR_INT, Contorno::ticks,
            Contorno::new);

    private static volatile Consumer<Contorno> receptor = m -> {};

    public static void definirReceptor(Consumer<Contorno> r) {
        receptor = r == null ? m -> {} : r;
    }

    /** Manda o contorno destas criaturas (no máximo {@link #TETO}) só para este jogador. */
    public static void mandar(ServerPlayer p, List<? extends Entity> criaturas, int ticks) {
        if (criaturas.isEmpty()) return;
        List<Integer> ids = criaturas.stream().limit(TETO).map(Entity::getId).toList();
        PacketDistributor.sendToPlayer(p, new Contorno(ids, ticks));
    }

    @Override
    public Type<Contorno> type() {
        return TIPO;
    }

    public static void receber(Contorno m, IPayloadContext contexto) {
        contexto.enqueueWork(() -> receptor.accept(m));
    }
}
