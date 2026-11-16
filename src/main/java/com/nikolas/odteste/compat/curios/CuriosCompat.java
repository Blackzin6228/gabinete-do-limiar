package com.nikolas.odteste.compat.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

/**
 * Única classe que toca a API do Curios. Só é chamada atrás de {@code Compat.Integracao.CURIOS.ligada()}: sem o
 * Curios, a JVM nunca carrega esta classe. Os artefatos entram no slot "charm" (tag {@code curios:charm}).
 */
public final class CuriosCompat {
    private CuriosCompat() {}

    /** O artefato equipado num slot do Curios, ou vazio. */
    public static ItemStack achar(LivingEntity quem, Item item) {
        return CuriosApi.getCuriosInventory(quem)
                .flatMap(inv -> inv.findFirstCurio(item))
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }
}
