package com.nikolas.odteste.maquinas;

import com.nikolas.odteste.OdMod;
import com.nikolas.odteste.horror.IdsHorror;
import com.nikolas.odteste.horror.TipoSusto;
import com.nikolas.odteste.rede.Susto;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Máquinas que falham: de vez em quando, uma máquina do Mekanism ou do Re:Avaritia em pleno ciclo para, e quem está
 * por perto leva um susto leve (som, faíscas e, às vezes, a luz falhando ou um sussurro). **Nunca** solta nem
 * apaga item: a falha só recomeça o ciclo.
 *
 * <b>Sem lag:</b> não há código em máquina nenhuma e nada roda por tick. A cada 5 s olhamos só em volta de
 * jogadores que ainda podem levar uma falha (teto {@link #MAX_JOGADORES}), só nos chunks já carregados dentro do raio,
 * com teto de {@link #MAX_ENTIDADES} entidades de bloco olhadas e {@link #MAX_SORTEIOS} sorteios por jogador.
 * Cada máquina sorteia no máximo uma vez a cada 60 s (config) e cada jogador vê no máximo uma falha a cada 10 min (config).
 *
 * <b>Mod novo:</b> implemente {@link FonteMaquina} em {@code compat/<mod>/} e chame {@link #registrarFonte}.
 * <b>Proteção (Fusível Queimado):</b> item na tag {@code odteste:reduz_falha_maquina} no inventário do jogador, ou
 * {@link #registrarReducao} para outras formas (ex.: acessório).
 */
public final class FalhaMaquinas {
    /** Varredura a cada 5 s, em tick diferente dos outros sistemas (0, 10 e 15 já são usados). */
    static final int PERIODO = 100;
    static final int FASE = 40;
    static final int MAX_JOGADORES = 64;
    static final int MAX_ENTIDADES = 512;
    static final int MAX_SORTEIOS = 4;
    /** Quantos jogadores perto da máquina entram na mesma falha (e na conta da espera). */
    static final int MAX_AFETADOS = 16;

    public static final TagKey<Item> TAG_PROTECAO = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(OdMod.MODID, IdsMaquinas.TAG_REDUZ_FALHA));

    private record Chave(ResourceKey<Level> dimensao, long pos) {}

    private static final List<FonteMaquina> FONTES = new ArrayList<>();
    private static final List<Function<ServerPlayer, Double>> REDUCOES = new ArrayList<>();
    /** Tipo de entidade de bloco → fonte dona dele (ou {@link #NENHUMA}). Comparação por identidade. */
    private static final Map<BlockEntityType<?>, FonteMaquina> POR_TIPO = new IdentityHashMap<>();
    private static final FonteMaquina NENHUMA = new FonteMaquina() {
        @Override
        public String modid() {
            return "";
        }

        @Override
        public boolean emCiclo(BlockEntity be) {
            return false;
        }

        @Override
        public void pausarCiclo(BlockEntity be) {}
    };
    private static final RegrasFalha.Relogio<Chave> MAQUINAS = new RegrasFalha.Relogio<>();
    private static final RegrasFalha.Relogio<UUID> JOGADORES = new RegrasFalha.Relogio<>();

    private FalhaMaquinas() {}

    public static void iniciar() {
        NeoForge.EVENT_BUS.addListener(FalhaMaquinas::aoTick);
        NeoForge.EVENT_BUS.addListener((ServerStoppedEvent e) -> {
            MAQUINAS.limpar();
            JOGADORES.limpar();
            POR_TIPO.clear();
        });
    }

    /** Uma integração diz que cuida das máquinas de um mod. Chame no início do jogo (ver {@code Compat.iniciar}). */
    public static synchronized void registrarFonte(FonteMaquina fonte) {
        FONTES.removeIf(f -> f.modid().equals(fonte.modid()));
        FONTES.add(fonte);
        POR_TIPO.clear();
    }

    /**
     * Outra forma de proteger o jogador. A função devolve o quanto da chance sobra (1 = nada muda, 0,5 = metade,
     * 0 = imune). Roda só na hora de um sorteio, nunca por tick.
     */
    public static synchronized void registrarReducao(Function<ServerPlayer, Double> fator) {
        REDUCOES.add(fator);
    }

    /** Quanto da chance sobra para este jogador (1 = sem proteção). */
    public static double fatorDe(ServerPlayer p) {
        double f = 1.0;
        if (p.getInventory().contains(TAG_PROTECAO)) f = Math.min(f, ConfigMaquinas.fatorProtecao());
        for (Function<ServerPlayer, Double> r : REDUCOES) {
            Double v = r.apply(p);
            if (v != null) f = Math.min(f, v);
        }
        return f;
    }

    // ---------- varredura ----------

    private static void aoTick(ServerTickEvent.Post e) {
        if (FONTES.isEmpty()) return;
        MinecraftServer s = e.getServer();
        long agora = s.getTickCount();
        if (agora % PERIODO != FASE || !ConfigMaquinas.ligado()) return;
        int n = 0;
        for (ServerPlayer p : s.getPlayerList().getPlayers()) {
            if (n++ >= MAX_JOGADORES) break;
            if (podeVerFalha(p, agora)) varrer(p, agora);
        }
    }

    /** Só quem joga de verdade, e que não viu falha há pouco: nada para mestre, criativo ou espectador. */
    static boolean podeVerFalha(ServerPlayer p, long agora) {
        if (p.isSpectator() || p.isCreative() || p.getTags().contains(IdsHorror.TAG_MESTRE)) return false;
        return JOGADORES.pode(p.getUUID(), agora, ConfigMaquinas.esperaJogador());
    }

    private static void varrer(ServerPlayer p, long agora) {
        ServerLevel nivel = p.serverLevel();
        BlockPos c = p.blockPosition();
        int raio = ConfigMaquinas.raio();
        long raioSq = (long) raio * raio;
        int vistas = 0;
        int sorteios = 0;
        for (int cx = (c.getX() - raio) >> 4; cx <= (c.getX() + raio) >> 4; cx++) {
            for (int cz = (c.getZ() - raio) >> 4; cz <= (c.getZ() + raio) >> 4; cz++) {
                LevelChunk chunk = nivel.getChunkSource().getChunkNow(cx, cz); // não carrega chunk
                if (chunk == null) continue;
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (++vistas > MAX_ENTIDADES) return;
                    FonteMaquina fonte = fonteDe(be);
                    if (fonte == NENHUMA || be.isRemoved() || be.getBlockPos().distSqr(c) > raioSq) continue;
                    if (!fonte.emCiclo(be)) continue; // parada não gasta a vez dela
                    Chave chave = new Chave(nivel.dimension(), be.getBlockPos().asLong());
                    if (!MAQUINAS.tentar(chave, agora, ConfigMaquinas.intervaloMaquina())) continue;
                    if (sortear(nivel, p, be, fonte, raioSq, agora)) return;
                    if (++sorteios >= MAX_SORTEIOS) return;
                }
            }
        }
    }

    private static FonteMaquina fonteDe(BlockEntity be) {
        BlockEntityType<?> tipo = be.getType();
        FonteMaquina f = POR_TIPO.get(tipo);
        if (f != null) return f;
        ResourceLocation id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(tipo);
        f = NENHUMA;
        for (FonteMaquina candidata : FONTES) {
            if (id != null && id.getNamespace().equals(candidata.modid())) {
                f = candidata;
                break;
            }
        }
        POR_TIPO.put(tipo, f);
        return f;
    }

    /** Sorteia uma máquina. Devolve true se falhou. */
    private static boolean sortear(ServerLevel nivel, ServerPlayer principal, BlockEntity be, FonteMaquina fonte,
            long raioSq, long agora) {
        BlockPos pos = be.getBlockPos();
        List<ServerPlayer> afetados = new ArrayList<>();
        double fator = 1.0;
        for (ServerPlayer o : nivel.players()) {
            if (afetados.size() >= MAX_AFETADOS) break;
            if (o.blockPosition().distSqr(pos) > raioSq || !podeVerFalha(o, agora)) continue;
            afetados.add(o);
            fator = Math.min(fator, fatorDe(o));
        }
        if (afetados.isEmpty()) return false;
        double chance = RegrasFalha.chance(ConfigMaquinas.chancePercentual(), fator);
        if (!RegrasFalha.falhou(chance, nivel.random.nextDouble())) return false;

        fonte.pausarCiclo(be);
        efeitos(nivel, pos, afetados);
        for (ServerPlayer o : afetados) JOGADORES.marcar(o.getUUID(), agora);
        OdMod.LOGGER.debug("Máquina falhou em {} ({}), {} jogador(es) por perto.", pos.toShortString(),
                fonte.modid(), afetados.size());
        return true;
    }

    /** Som, faísca e, às vezes, um susto leve para um jogador. Tudo que já existe no mod; nenhum número na tela. */
    private static void efeitos(ServerLevel nivel, BlockPos pos, List<ServerPlayer> afetados) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.7;
        double z = pos.getZ() + 0.5;
        nivel.playSound(null, pos, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.8f, 0.6f);
        nivel.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 10, 0.3, 0.3, 0.3, 0.15);
        nivel.sendParticles(ParticleTypes.SMOKE, x, y + 0.3, z, 6, 0.2, 0.2, 0.2, 0.01);
        for (ServerPlayer o : afetados) {
            o.playNotifySound(SoundEvents.BEACON_DEACTIVATE, SoundSource.AMBIENT, 0.7f, 0.5f);
        }
        // Um só leva o susto: luz falhando (mais comum) ou sussurro. O cliente respeita efeitos_fortes.
        if (nivel.random.nextInt(100) < ConfigMaquinas.chanceSusto()) {
            ServerPlayer alvo = afetados.get(nivel.random.nextInt(afetados.size()));
            TipoSusto tipo = nivel.random.nextInt(3) == 0 ? TipoSusto.SUSSURRO : TipoSusto.LUZ;
            PacketDistributor.sendToPlayer(alvo, new Susto(tipo.id));
        }
    }
}
