package com.nikolas.odteste.artefatos;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Sangramento: dano leve a cada segundo. Só em criaturas (o Facão de Ritual de Sangue e a Luva nunca põem em jogador; se um
 * comando puser, não faz nada). Sem ícone: nunca aparece na tela de um jogador.
 */
public final class Sangramento extends MobEffect {
    public static final float DANO = 1f;

    public Sangramento() {
        super(MobEffectCategory.HARMFUL, 0x6B0000);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duracao, int nivel) {
        return duracao % 20 == 0;
    }

    @Override
    public boolean applyEffectTick(LivingEntity alvo, int nivel) {
        if (alvo instanceof Player || alvo.level().isClientSide()) return true;
        alvo.hurt(alvo.damageSources().magic(), DANO * (nivel + 1));
        return true;
    }
}
