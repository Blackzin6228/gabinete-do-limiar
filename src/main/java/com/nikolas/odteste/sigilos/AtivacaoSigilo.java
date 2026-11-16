package com.nikolas.odteste.sigilos;

import com.nikolas.odteste.historia.DadosHistoria;
import com.nikolas.odteste.historia.Grupos;
import com.nikolas.odteste.registro.Registro;
import com.nikolas.odteste.sigilos.RegrasEfeitoSigilo.Alvo;
import com.nikolas.odteste.sigilos.RegrasEfeitoSigilo.Efeito;
import com.nikolas.odteste.sistemas.Lucidez;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

/**
 * Ativação dos sigilos de elemento (só servidor). O jogador clica no sigilo com a mão vazia: o efeito sai uma vez, numa
 * caixa pequena em volta do sigilo, cobra Sanidade de quem ativou e o desenho se apaga. Quem ativou espera um tempo
 * (config) para ativar outro. Peso: uma busca por ativação, com teto de alvos e poucas partículas; nada por tick.
 */
public final class AtivacaoSigilo {
    /** Última ativação de cada jogador (tempo do mundo). Só memória; some ao sair. */
    private static final Map<UUID, Long> ULTIMA = new HashMap<>();
    /** Partículas por ativação (um punhado, para não pesar). */
    private static final int PARTICULAS = 36;

    private AtivacaoSigilo() {}

    public static void iniciar() {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent e) -> ULTIMA.remove(e.getEntity().getUUID()));
        NeoForge.EVENT_BUS.addListener((ServerStoppedEvent e) -> ULTIMA.clear());
    }

    /**
     * @return false se esse desenho não faz nada sozinho (letra, dígito, tipo de ritual): quem chama deixa o clique passar.
     *         true se o clique foi tratado (ativou, ou o jogador ainda está sem forças para outro rito).
     */
    public static boolean ativar(ServerLevel nivel, BlockPos pos, Simbolo simbolo, ServerPlayer p) {
        Optional<Efeito> achado = Efeito.deSimbolo(simbolo.id());
        if (achado.isEmpty()) return false;
        Efeito efeito = achado.get();

        long agora = nivel.getGameTime();
        Long ultimo = ULTIMA.get(p.getUUID());
        if (!RegrasEfeitoSigilo.livre(ultimo == null ? -1 : ultimo, agora, ConfigSigilos.esperaTicks())) {
            p.displayClientMessage(Component.translatable("odteste.sigilo.frio")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), true);
            return true;
        }
        ULTIMA.put(p.getUUID(), agora);

        Lucidez.perder(p, ConfigSigilos.custoSanidade());
        nivel.removeBlock(pos, false); // o sigilo se apaga

        int duracao = ConfigSigilos.duracaoTicks();
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.1;
        double cz = pos.getZ() + 0.5;
        double raio = ConfigSigilos.raio();

        efeitoEmQuemAtivou(efeito, p, duracao);

        // Uma busca só, numa caixa em volta do sigilo; o teto de alvos vale para o resto da lista.
        AABB caixa = new AABB(cx - raio, cy - 1, cz - raio, cx + raio, cy + raio, cz + raio);
        List<LivingEntity> perto = nivel.getEntitiesOfClass(LivingEntity.class, caixa,
                e -> e != p && e.isAlive() && !e.isSpectator() && e.distanceToSqr(cx, cy, cz) <= raio * raio);
        Grupos grupos = DadosHistoria.de(nivel.getServer()).grupos;
        int afetados = 0;
        for (LivingEntity alvo : perto) {
            if (!RegrasEfeitoSigilo.cabeMais(afetados, ConfigSigilos.maxAlvos())) break;
            Alvo tipo = classificar(alvo);
            boolean mesmoGrupo = tipo == Alvo.JOGADOR && mesmoGrupo(grupos, p, alvo);
            if (!RegrasEfeitoSigilo.afeta(efeito, tipo, mesmoGrupo)) continue;
            efeitoNoAlvo(efeito, alvo, p, duracao);
            afetados++;
        }
        // O Medo não escolhe lado: pega até em quem ativou.
        if (efeito == Efeito.MEDO) efeitoNoAlvo(efeito, p, p, duracao);

        som(nivel, pos, efeito);
        particulas(nivel, cx, cy, cz, raio, efeito);
        return true;
    }

    private static Alvo classificar(LivingEntity e) {
        if (e instanceof Player) return Alvo.JOGADOR;
        if (e instanceof OwnableEntity dono && dono.getOwnerUUID() != null) return Alvo.OUTRO; // bicho de estimação
        if (e instanceof Enemy) return Alvo.CRIATURA_HOSTIL;
        if (e instanceof Mob) return Alvo.CRIATURA;
        return Alvo.OUTRO;
    }

    private static boolean mesmoGrupo(Grupos grupos, ServerPlayer quemAtivou, LivingEntity outro) {
        Optional<Grupos.Grupo> g = grupos.grupoDe(quemAtivou.getUUID());
        return g.isPresent() && g.get().membros().containsKey(outro.getUUID());
    }

    /** Força, Velocidade e Visão Noturna: só para quem ativou. */
    private static void efeitoEmQuemAtivou(Efeito efeito, ServerPlayer p, int duracao) {
        switch (efeito) {
            case SANGUE -> p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duracao, 0));
            case CONHECIMENTO -> p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,
                    RegrasEfeitoSigilo.duracaoVisaoNoturna(duracao), 0, false, false));
            case ENERGIA -> p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duracao, 1));
            default -> { }
        }
    }

    private static void efeitoNoAlvo(Efeito efeito, LivingEntity alvo, ServerPlayer origem, int duracao) {
        switch (efeito) {
            case SANGUE -> alvo.addEffect(new MobEffectInstance(Registro.SANGRAMENTO, duracao, 0), origem);
            case MORTE -> alvo.addEffect(new MobEffectInstance(MobEffects.WITHER, duracao, 0), origem);
            case CONHECIMENTO -> alvo.addEffect(new MobEffectInstance(MobEffects.GLOWING, duracao, 0, false, false), origem);
            case ENERGIA -> {
                alvo.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, RegrasEfeitoSigilo.ATORDOAR_TICKS, 3), origem);
                alvo.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, RegrasEfeitoSigilo.ATORDOAR_TICKS, 0), origem);
            }
            case MEDO -> {
                alvo.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duracao, 0), origem);
                alvo.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duracao, 1), origem);
            }
        }
    }

    private static void som(ServerLevel nivel, BlockPos pos, Efeito efeito) {
        SoundEvent som = switch (efeito) {
            case SANGUE -> SoundEvents.WARDEN_HEARTBEAT;
            case MORTE -> SoundEvents.WITHER_AMBIENT;
            case CONHECIMENTO -> SoundEvents.ENCHANTMENT_TABLE_USE;
            case ENERGIA -> SoundEvents.LIGHTNING_BOLT_IMPACT;
            case MEDO -> SoundEvents.WARDEN_NEARBY_CLOSE;
        };
        nivel.playSound(null, pos, som, SoundSource.BLOCKS, 1.0f, efeito == Efeito.ENERGIA ? 1.6f : 0.8f);
    }

    private static void particulas(ServerLevel nivel, double x, double y, double z, double raio, Efeito efeito) {
        ParticleOptions tipo = switch (efeito) {
            case SANGUE -> ParticleTypes.DAMAGE_INDICATOR;
            case MORTE -> ParticleTypes.SOUL;
            case CONHECIMENTO -> ParticleTypes.ENCHANT;
            case ENERGIA -> ParticleTypes.ELECTRIC_SPARK;
            case MEDO -> ParticleTypes.SQUID_INK;
        };
        nivel.sendParticles(tipo, x, y + 0.6, z, PARTICULAS, raio * 0.35, 0.5, raio * 0.35, 0.02);
    }
}
