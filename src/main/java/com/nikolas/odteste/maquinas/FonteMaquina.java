package com.nikolas.odteste.maquinas;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Um mod cujas máquinas podem falhar. Quem implementa mora em {@code compat/<mod>/} e só é criado quando o mod
 * existe (ver {@code Compat}). O núcleo ({@link FalhaMaquinas}) cuida do resto: quando checar, quem está perto,
 * limites e o susto.
 */
public interface FonteMaquina {
    /** Id do mod (também o namespace dos blocos dele). */
    String modid();

    /** Esta entidade de bloco é uma máquina deste mod que está no meio de um ciclo agora? Tem de ser barato. */
    boolean emCiclo(BlockEntity be);

    /**
     * Pausa o ciclo: o ciclo em andamento recomeça do zero. **Nunca** mexe em item, líquido, energia ou bloco.
     * Se a pausa não for possível (versão do mod diferente), não faz nada: o susto acontece do mesmo jeito.
     */
    void pausarCiclo(BlockEntity be);
}
