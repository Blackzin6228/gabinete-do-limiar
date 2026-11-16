package com.nikolas.odteste.artefatos;

import com.nikolas.odteste.OdMod;
import com.nikolas.odteste.compat.Compat;
import com.nikolas.odteste.compat.curios.CuriosCompat;
import com.nikolas.odteste.elementos.Elemento;
import com.nikolas.odteste.horror.RegrasSusto;
import com.nikolas.odteste.registro.Registro;
import com.nikolas.odteste.reliquias.Espadas;
import com.nikolas.odteste.sistemas.Exposicao;
import com.nikolas.odteste.sistemas.Fonte;
import com.nikolas.odteste.sistemas.Lucidez;
import com.nikolas.odteste.sistemas.Paranormal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * O poder e o preço dos 12 artefatos. Só servidor. Sem peso por tick: o que é contínuo roda na checagem do portador
 * (a cada 5 s, só dele, espalhada pelo id); o resto roda ao bater, morrer ou usar. Toda busca de criatura tem raio e
 * teto. Nada aparece em número: só som, corpo e fome.
 *
 * <p>Onde o artefato vale: no slot de acessório do Curios ou na mão. Sem o Curios no pack, em qualquer lugar do
 * inventário. Caneta, Fita e Fusível: o poder depende de coisa que ainda não existe ("em breve" na descrição).
 * TODO(magias): a "afinidade" de todos volta quando houver magias (Iron's Spells).
 */
@EventBusSubscriber(modid = OdMod.MODID)
public final class Artefatos {
    /** Quem a Moeda de Defunto fez os monstros esquecerem, e até quando (tempo do mundo). Só memória. */
    private static final Map<UUID, Long> ESQUECIDO_ATE = new HashMap<>();

    private Artefatos() {}

    // ------------------------------------------------------------------ onde está o artefato

    /** O artefato que vale para este jogador agora, ou vazio. */
    public static ItemStack equipado(Player p, TipoArtefato tipo) {
        Item item = Registro.ARTEFATOS.get(tipo).get();
        if (p.getMainHandItem().is(item)) return p.getMainHandItem();
        if (p.getOffhandItem().is(item)) return p.getOffhandItem();
        if (Compat.Integracao.CURIOS.ligada()) return CuriosCompat.achar(p, item);
        var inv = p.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.is(item)) return s;
        }
        return ItemStack.EMPTY;
    }

    public static boolean tem(Player p, TipoArtefato tipo) {
        return !equipado(p, tipo).isEmpty();
    }

    private static long agora(ServerPlayer p) {
        return p.server.overworld().getGameTime();
    }

    /** Número da noite do jogo (igual do anoitecer ao amanhecer). */
    public static long noite(ServerPlayer p) {
        return RegrasSusto.noite(p.server.overworld().getDayTime());
    }

    private static boolean deNoite(ServerPlayer p) {
        long hora = Math.floorMod(p.serverLevel().getDayTime(), RegrasSusto.DIA);
        return hora >= 13_000 && hora < 23_000;
    }

    private static MobEffectInstance escondido(net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> efeito,
                                               int ticks, int nivel) {
        // Sem ícone e sem partícula: nada de tempo nem número na tela.
        return new MobEffectInstance(efeito, ticks, nivel, true, false, false);
    }

    private static <T extends LivingEntity> List<T> perto(ServerPlayer p, Class<T> tipo, double raio,
                                                          java.util.function.Predicate<T> filtro) {
        AABB caixa = p.getBoundingBox().inflate(raio);
        return p.serverLevel().getEntitiesOfClass(tipo, caixa, e -> e != p && e.isAlive() && filtro.test(e)).stream()
                .limit(RegrasArtefatos.TETO_CRIATURAS).toList();
    }

    /**
     * Recarrega aos poucos itens com bateria (energia do NeoForge) do inventário. Usado pela Barra de Sinal e pelo
     * Espeto. @return quantos itens receberam energia
     */
    public static int recarregarEnergia(Player p, int porItem, int maxItens) {
        int carregados = 0;
        var inv = p.getInventory();
        for (int i = 0; i < inv.getContainerSize() && carregados < maxItens; i++) {
            ItemStack s = inv.getItem(i);
            if (s.isEmpty() || s.getCount() != 1) continue;
            IEnergyStorage bateria = s.getCapability(Capabilities.EnergyStorage.ITEM);
            if (bateria != null && bateria.canReceive() && bateria.receiveEnergy(porItem, false) > 0) carregados++;
        }
        return carregados;
    }

    // ------------------------------------------------------------------ usar (clique direito)

    /** @return true se o artefato fez algo (o clique é consumido). */
    static boolean usar(ServerPlayer p, ItemStack item, TipoArtefato tipo) {
        return switch (tipo) {
            case SERINGA_VELHA -> seringa(p, item);
            case UMA_BARRA_DE_SINAL -> pulso(p, item);
            default -> false;
        };
    }

    /** Seringa Velha: 1 coração por Força de 30 s. Cobra vida e Exposição. */
    private static boolean seringa(ServerPlayer p, ItemStack item) {
        if (p.getHealth() <= RegrasArtefatos.SERINGA_VIDA + 1f) {
            p.playNotifySound(SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 0.5f, 0.6f);
            return false;
        }
        p.hurt(p.damageSources().magic(), RegrasArtefatos.SERINGA_VIDA);
        p.addEffect(escondido(MobEffects.DAMAGE_BOOST, RegrasArtefatos.SERINGA_FORCA_TICKS, 0));
        Exposicao.custo(p, Elemento.SANGUE, RegrasArtefatos.SERINGA_EXPOSICAO);
        p.getCooldowns().addCooldown(item.getItem(), RegrasArtefatos.SERINGA_RECARGA_TICKS);
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.HONEY_DRINK, SoundSource.PLAYERS, 0.6f, 0.5f);
        return true;
    }

    /** Uma Barra de Sinal: pulso que atordoa criaturas hostis perto. Gasta o aparelho. */
    private static boolean pulso(ServerPlayer p, ItemStack item) {
        List<Mob> alvos = perto(p, Mob.class, RegrasArtefatos.SINAL_RAIO_PULSO, m -> m instanceof Enemy);
        for (Mob m : alvos) {
            m.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, RegrasArtefatos.SINAL_TONTEIRA_TICKS, 3));
            m.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, RegrasArtefatos.SINAL_TONTEIRA_TICKS, 0));
        }
        item.hurtAndBreak(RegrasArtefatos.SINAL_GASTO_PULSO, p.serverLevel(), p, i -> {});
        p.getCooldowns().addCooldown(item.getItem(), RegrasArtefatos.SINAL_RECARGA_TICKS);
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 0.7f, 1.8f);
        return true;
    }

    // ------------------------------------------------------------------ checagem do portador (a cada 5 s)

    @SubscribeEvent
    public static void aoTick(PlayerTickEvent.Post e) {
        if (!(e.getEntity() instanceof ServerPlayer p) || p.isSpectator()) return;
        if (!RegrasArtefatos.vezDeChecar(p.tickCount, p.getId())) return;
        Espadas.checar(p);
        medalha(p);
        oculos(p);
        vela(p);
        barraDeSinal(p);
        radinho(p);
    }

    /** Medalha de Doador: abaixo de 3 corações regenera aos poucos; cada vez cobra Lucidez. */
    private static void medalha(ServerPlayer p) {
        if (p.getHealth() >= RegrasArtefatos.MEDALHA_VIDA || !tem(p, TipoArtefato.MEDALHA_DE_DOADOR)) return;
        p.addEffect(escondido(MobEffects.REGENERATION, RegrasArtefatos.INTERVALO_TICKS + 20, 0));
        Lucidez.perder(p, RegrasArtefatos.MEDALHA_LUCIDEZ);
    }

    /** Óculos de Velório: contorno do paranormal e dos invisíveis (só nesta tela). Cobra Exposição; visões. */
    private static void oculos(ServerPlayer p) {
        if (!tem(p, TipoArtefato.OCULOS_DE_VELORIO)) return;
        List<LivingEntity> vistos = perto(p, LivingEntity.class, RegrasArtefatos.OCULOS_RAIO,
                c -> !(c instanceof Player) && (c.isInvisible() || c.getType().is(Paranormal.CRIATURA)));
        com.nikolas.odteste.rede.Contorno.mandar(p, vistos, RegrasArtefatos.OCULOS_CONTORNO_TICKS);
        Exposicao.contato(p, Fonte.RELIQUIA_USAR, Elemento.MORTE, RegrasArtefatos.OCULOS_EXPOSICAO);
        // TODO(prévia): visão na tela precisa de prévia aprovada; por enquanto é só som.
        if (p.getRandom().nextInt(RegrasArtefatos.OCULOS_VISAO_UMA_EM) == 0) {
            p.playNotifySound(SoundEvents.AMBIENT_CAVE.value(), SoundSource.AMBIENT, 0.5f, 0.6f);
        }
    }

    /** Vela de Sétimo Dia: acesa (equipada ou na mão), enfraquece mortos-vivos perto. Gasta a vela e cobra Lucidez. */
    private static void vela(ServerPlayer p) {
        ItemStack vela = equipado(p, TipoArtefato.VELA_DE_SETIMO_DIA);
        if (vela.isEmpty()) return;
        List<Mob> mortos = perto(p, Mob.class, RegrasArtefatos.VELA_RAIO, m -> m.getType().is(EntityTypeTags.UNDEAD));
        if (mortos.isEmpty()) return;
        for (Mob m : mortos) m.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, RegrasArtefatos.VELA_FRAQUEZA_TICKS, 0));
        vela.hurtAndBreak(RegrasArtefatos.VELA_GASTO, p.serverLevel(), p, i -> {});
        Lucidez.perder(p, RegrasArtefatos.VELA_LUCIDEZ);
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.CANDLE_EXTINGUISH, SoundSource.PLAYERS, 0.4f, 0.6f);
    }

    /** Uma Barra de Sinal: recarrega itens com bateria (gasta o aparelho); de noite, às vezes toca sozinho. */
    private static void barraDeSinal(ServerPlayer p) {
        ItemStack sinal = equipado(p, TipoArtefato.UMA_BARRA_DE_SINAL);
        if (sinal.isEmpty()) return;
        if (recarregarEnergia(p, RegrasArtefatos.SINAL_ENERGIA, RegrasArtefatos.SINAL_ITENS_MAX) > 0) {
            sinal.hurtAndBreak(1, p.serverLevel(), p, i -> {});
        }
        if (deNoite(p) && p.getRandom().nextInt(RegrasArtefatos.SINAL_TOCA_UMA_EM) == 0) {
            // Todo mundo perto ouve: o celular entrega quem está escondido.
            p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.PLAYERS,
                    0.9f, 1.6f);
        }
    }

    /** Radinho de Pilha: chiado mais alto perto de perigo (só som). Em silêncio cobra Lucidez; de noite, vozes. */
    private static void radinho(ServerPlayer p) {
        if (!tem(p, TipoArtefato.RADINHO_DE_PILHA)) return;
        List<Mob> perigo = perto(p, Mob.class, RegrasArtefatos.RADINHO_RAIO, m -> m instanceof Enemy);
        if (!perigo.isEmpty()) {
            double d = perigo.stream().mapToDouble(m -> m.distanceTo(p)).min().orElse(RegrasArtefatos.RADINHO_RAIO);
            p.playNotifySound(SoundEvents.FIRE_AMBIENT, SoundSource.PLAYERS, RegrasArtefatos.volumeChiado(d), 1.8f);
            return;
        }
        if (p.getRandom().nextInt(RegrasArtefatos.RADINHO_SILENCIO_UMA_EM) != 0) return;
        Lucidez.perder(p, RegrasArtefatos.RADINHO_LUCIDEZ);
        p.playNotifySound(deNoite(p) ? SoundEvents.AMBIENT_CAVE.value() : SoundEvents.FIRE_AMBIENT,
                SoundSource.PLAYERS, 0.35f, deNoite(p) ? 0.5f : 1.9f);
    }

    // ------------------------------------------------------------------ ao bater, morrer e mirar

    /** Luva de Açougueiro: golpes comuns também sangram (nunca em jogador). Cada acerto dá fome. */
    @SubscribeEvent
    public static void aoBater(LivingIncomingDamageEvent e) {
        if (!(e.getSource().getEntity() instanceof ServerPlayer p) || !e.getSource().is(DamageTypes.PLAYER_ATTACK)) return;
        LivingEntity alvo = e.getEntity();
        if (alvo == p || !tem(p, TipoArtefato.LUVA_DE_ACOUGUEIRO)) return;
        p.causeFoodExhaustion(RegrasArtefatos.LUVA_FOME);
        if (alvo instanceof Player || alvo instanceof AbstractGolem) return;
        alvo.addEffect(new MobEffectInstance(Registro.SANGRAMENTO, RegrasArtefatos.LUVA_SANGRAR_TICKS, 0), p);
    }

    /**
     * Moeda de Defunto: 1x por noite, o golpe fatal deixa meio coração e os monstros esquecem o jogador por 5 s.
     * Cobra Lucidez e esfria até a noite seguinte (a noite fica guardada na própria moeda).
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void aoMorrer(LivingDeathEvent e) {
        if (e.isCanceled() || !(e.getEntity() instanceof ServerPlayer p) || p.isSpectator()) return;
        ItemStack moeda = equipado(p, TipoArtefato.MOEDA_DE_DEFUNTO);
        if (moeda.isEmpty()) return;
        long noite = noite(p);
        if (RegrasArtefatos.usadoNestaNoite(moeda.getOrDefault(Registro.NOITE_USADA, -1L), noite)) return;
        e.setCanceled(true);
        p.setHealth(RegrasArtefatos.MOEDA_VIDA_QUE_FICA);
        moeda.set(Registro.NOITE_USADA, noite);
        Lucidez.perder(p, RegrasArtefatos.MOEDA_LUCIDEZ);
        ESQUECIDO_ATE.put(p.getUUID(), agora(p) + RegrasArtefatos.MOEDA_ESQUECER_TICKS);
        for (Mob m : perto(p, Mob.class, RegrasArtefatos.MOEDA_RAIO, m -> m.getTarget() == p)) m.setTarget(null);
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 1f, 0.5f);
    }

    /** Enquanto a Moeda age, nenhum monstro mira o jogador. */
    @SubscribeEvent
    public static void aoMirar(LivingChangeTargetEvent e) {
        if (ESQUECIDO_ATE.isEmpty() || !(e.getNewAboutToBeSetTarget() instanceof ServerPlayer p)) return;
        Long ate = ESQUECIDO_ATE.get(p.getUUID());
        if (ate == null) return;
        if (agora(p) >= ate) {
            ESQUECIDO_ATE.remove(p.getUUID());
            return;
        }
        e.setCanceled(true);
    }

    @SubscribeEvent
    public static void aoSair(PlayerEvent.PlayerLoggedOutEvent e) {
        ESQUECIDO_ATE.remove(e.getEntity().getUUID());
        Espadas.esquecer(e.getEntity().getUUID());
    }

    // ------------------------------------------------------------------ leitura (Lupa e Sentença)

    /**
     * Lupa do Arquivo Morto (e a Sentença Sem Recurso com o livro na outra mão): ao ler um livro do mod, conta como
     * um degrau a mais de Exposição (aparecem trechos escondidos). Só calcula; quem cobra é {@link #revelou}.
     */
    public static int bonusLeitura(ServerPlayer p) {
        if (tem(p, TipoArtefato.LUPA_DO_ARQUIVO_MORTO) || Espadas.sentencaLe(p)) return RegrasArtefatos.LEITURA_BONUS;
        return 0;
    }

    /** O bônus de leitura revelou um trecho: a Lupa cobra Lucidez; a Sentença gasta a vez da noite. */
    public static void revelou(ServerPlayer p) {
        if (tem(p, TipoArtefato.LUPA_DO_ARQUIVO_MORTO)) {
            Lucidez.perder(p, RegrasArtefatos.LUPA_LUCIDEZ);
        } else {
            Espadas.sentencaGastou(p);
        }
        p.playNotifySound(SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 0.8f, 0.5f);
    }
}
