package com.nikolas.odteste.reliquias;

import com.nikolas.odteste.OdMod;
import com.nikolas.odteste.artefatos.Artefatos;
import com.nikolas.odteste.historia.DadosHistoria;
import com.nikolas.odteste.registro.Registro;
import com.nikolas.odteste.rede.Contorno;
import com.nikolas.odteste.sistemas.ElementoJogador;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * O poder das 4 espadas (docs/reliquias-v1.md). Só servidor. Tudo acontece ao bater; o Espeto também recarrega
 * baterias na checagem do portador (a cada 5 s, chamada por {@link Artefatos}). Só vale para quem é do elemento da
 * espada: para os outros, o golpe vale como um soco. Os poderes nunca agem em jogador.
 * O estado (acertos, carga, julgado) é só memória: reiniciar zera.
 */
@EventBusSubscriber(modid = OdMod.MODID)
public final class Espadas {
    private static final class Estado {
        int sedeAcertos;
        int sedeUltimoAlvo = -1;
        int sedeSeguidos;
        final RegrasEspadas.FreioCura freio = new RegrasEspadas.FreioCura();
        long sabrePronto;
        int julgado = -1;
        int carga;
    }

    private static final Map<UUID, Estado> ESTADOS = new HashMap<>();
    /** Trava: a descarga do Espeto não dispara outra descarga. */
    private static boolean descarregando;

    private Espadas() {}

    private static Estado estado(Player p) {
        return ESTADOS.computeIfAbsent(p.getUUID(), u -> new Estado());
    }

    public static void esquecer(UUID jogador) {
        ESTADOS.remove(jogador);
    }

    private static TipoEspada espadaNaMao(Player p) {
        return p.getMainHandItem().getItem() instanceof EspadaReliquia e ? e.tipo : null;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void aoBater(LivingIncomingDamageEvent e) {
        if (descarregando || !e.getSource().is(DamageTypes.PLAYER_ATTACK)) return;
        if (!(e.getSource().getEntity() instanceof ServerPlayer p)) return;
        TipoEspada tipo = espadaNaMao(p);
        if (tipo == null) return;
        if (!RegrasEspadas.funciona(ElementoJogador.de(p), tipo)) {
            e.setAmount(Math.min(e.getAmount(), RegrasEspadas.DANO_FORA_DO_ELEMENTO));
            return;
        }
        LivingEntity alvo = e.getEntity();
        if (alvo instanceof Player || alvo == p) return; // espada boa e comum contra jogador; nenhum poder
        switch (tipo) {
            case SEDE_DE_DOMINGO -> sede(p, alvo);
            case SABRE_DO_ULTIMO_ADEUS -> sabre(p, alvo, e);
            case SENTENCA_SEM_RECURSO -> sentenca(p, alvo, e);
            case ESPETO_DA_CASA_CHEIA -> espeto(p, alvo);
        }
    }

    /** Pedra e ferro (golens, shulkers) não sangram nem dão vida. */
    private static boolean vivo(LivingEntity alvo) {
        return !(alvo instanceof AbstractGolem);
    }

    private static void sede(ServerPlayer p, LivingEntity alvo) {
        if (!vivo(alvo)) return;
        Estado s = estado(p);
        s.sedeSeguidos = alvo.getId() == s.sedeUltimoAlvo ? s.sedeSeguidos + 1 : 1;
        s.sedeUltimoAlvo = alvo.getId();
        if (s.sedeSeguidos >= RegrasEspadas.SEDE_SEGUIDOS_PARA_SANGRAR) {
            alvo.addEffect(new MobEffectInstance(Registro.SANGRAMENTO, RegrasEspadas.SANGRAR_TICKS, 0), p);
        }
        if (++s.sedeAcertos % RegrasEspadas.SEDE_ACERTOS_PARA_CURAR == 0) {
            float cura = s.freio.curar(p.server.overworld().getGameTime(), RegrasEspadas.SEDE_CURA);
            if (cura > 0) {
                p.heal(cura);
                p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.HONEY_DRINK, SoundSource.PLAYERS, 0.5f, 0.5f);
            }
        }
    }

    private static void sabre(ServerPlayer p, LivingEntity alvo, LivingIncomingDamageEvent e) {
        Estado s = estado(p);
        long agora = p.server.overworld().getGameTime();
        if (agora < s.sabrePronto && agora >= s.sabrePronto - RegrasEspadas.SABRE_RECARGA_TICKS) return;
        if (alvo.getType().is(Tags.EntityTypes.BOSSES) || !RegrasEspadas.adeus(alvo.getHealth(), alvo.getMaxHealth())) return;
        s.sabrePronto = agora + RegrasEspadas.SABRE_RECARGA_TICKS;
        // "O adeus": acaba na hora (armadura e absorção não seguram).
        e.setAmount(alvo.getMaxHealth() * 100f + alvo.getAbsorptionAmount() + 100f);
        p.serverLevel().playSound(null, alvo.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 0.8f, 0.5f);
    }

    private static void sentenca(ServerPlayer p, LivingEntity alvo, LivingIncomingDamageEvent e) {
        Estado s = estado(p);
        Entity julgado = s.julgado < 0 ? null : p.serverLevel().getEntity(s.julgado);
        boolean livre = julgado == null || !julgado.isAlive();
        if (livre) {
            s.julgado = alvo.getId();
            p.playNotifySound(SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1f, 0.5f);
        } else if (julgado == alvo) {
            e.setAmount(e.getAmount() * RegrasEspadas.SENTENCA_MULTIPLICADOR);
        } else {
            return; // um julgamento por vez
        }
        Contorno.mandar(p, List.of(alvo), RegrasEspadas.CONTORNO_TICKS);
    }

    private static void espeto(ServerPlayer p, LivingEntity alvo) {
        Estado s = estado(p);
        if (!RegrasEspadas.cheia(s.carga)) {
            s.carga = RegrasEspadas.carregar(s.carga, aliadoPerto(p));
            return;
        }
        s.carga = 0;
        ServerLevel mundo = p.serverLevel();
        List<LivingEntity> cadeia = mundo.getEntitiesOfClass(LivingEntity.class,
                        alvo.getBoundingBox().inflate(RegrasEspadas.ESPETO_RAIO),
                        c -> c != p && c.isAlive() && !(c instanceof Player))
                .stream().limit(RegrasEspadas.ESPETO_ALVOS + 1L).toList();
        descarregando = true;
        try {
            for (LivingEntity c : cadeia) {
                c.hurt(p.damageSources().indirectMagic(p, p), RegrasEspadas.ESPETO_DANO_DESCARGA);
                c.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, RegrasEspadas.ESPETO_TONTEIRA_TICKS, 3), p);
                c.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, RegrasEspadas.ESPETO_TONTEIRA_TICKS, 0), p);
                mundo.sendParticles(ParticleTypes.ELECTRIC_SPARK, c.getX(), c.getY(0.6), c.getZ(), 8, 0.3, 0.4, 0.3, 0.05);
            }
        } finally {
            descarregando = false;
        }
        mundo.playSound(null, alvo.blockPosition(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 0.4f, 1.6f);
    }

    /** Alguém do grupo do jogador está perto? Laço curto: só os membros do grupo dele. */
    private static boolean aliadoPerto(ServerPlayer p) {
        var grupo = DadosHistoria.de(p.server).grupos.grupoDe(p.getUUID());
        if (grupo.isEmpty()) return false;
        double raio2 = RegrasEspadas.ALIADO_RAIO * RegrasEspadas.ALIADO_RAIO;
        for (UUID u : grupo.get().membros().keySet()) {
            if (u.equals(p.getUUID())) continue;
            ServerPlayer outro = p.server.getPlayerList().getPlayer(u);
            if (outro != null && outro.level() == p.level() && outro.distanceToSqr(p) <= raio2) return true;
        }
        return false;
    }

    /** Checagem do portador (a cada 5 s): o Espeto na mão recarrega aos poucos as baterias do inventário. */
    public static void checar(ServerPlayer p) {
        if (espadaNaMao(p) != TipoEspada.ESPETO_DA_CASA_CHEIA) return;
        if (!RegrasEspadas.funciona(ElementoJogador.de(p), TipoEspada.ESPETO_DA_CASA_CHEIA)) return;
        Artefatos.recarregarEnergia(p, RegrasEspadas.ESPETO_ENERGIA, RegrasEspadas.ESPETO_ITENS_MAX);
    }

    // ------------------------------------------------------------------ Sentença com livro ou mapa

    /**
     * A Sentença na mão principal, de quem é de Conhecimento, com livro na outra mão, ainda sem ter revelado nesta
     * noite? Então o livro mostra um trecho escondido. TODO(mapa): com mapa na outra mão, "em breve".
     */
    public static boolean sentencaLe(ServerPlayer p) {
        ItemStack espada = p.getMainHandItem();
        if (espadaNaMao(p) != TipoEspada.SENTENCA_SEM_RECURSO) return false;
        if (!RegrasEspadas.funciona(ElementoJogador.de(p), TipoEspada.SENTENCA_SEM_RECURSO)) return false;
        if (!p.getOffhandItem().is(Items.WRITTEN_BOOK)) return false;
        long usada = espada.getOrDefault(Registro.NOITE_USADA, -1L);
        return !com.nikolas.odteste.artefatos.RegrasArtefatos.usadoNestaNoite(usada, Artefatos.noite(p));
    }

    public static void sentencaGastou(ServerPlayer p) {
        if (espadaNaMao(p) == TipoEspada.SENTENCA_SEM_RECURSO) {
            p.getMainHandItem().set(Registro.NOITE_USADA, Artefatos.noite(p));
        }
    }
}
