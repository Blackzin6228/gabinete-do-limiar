package com.nikolas.odteste.compat.reavaritia;

import com.nikolas.odteste.OdMod;
import com.nikolas.odteste.compat.Compat;
import com.nikolas.odteste.maquinas.FalhaMaquinas;
import com.nikolas.odteste.maquinas.FonteMaquina;
import java.lang.reflect.Field;
import java.util.Optional;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Falhas nas máquinas do Re:Avaritia (Coletor e Compressor de Nêutrons, em todas as camadas). Só é carregada quando
 * o mod existe ({@code Compat.Integracao.REAVARITIA.ligada()}).
 *
 * <b>Por reflexão, de propósito:</b> o jar do Re:Avaritia não está no pack do GitHub (não dá para compilar contra
 * ele). O mod guarda o ciclo no campo {@code int progress} (privado) dos blocos dele (visto no código da versão
 * 1.21.1). Se uma versão nova mudar isso, a pausa não acontece e o aviso sai uma vez no log; o susto continua.
 */
public final class ReAvaritiaFalhas implements FonteMaquina {
    private static final String PACOTE_TILES = "committee.nova.mods.avaritia.common.tile.";

    /** Campo {@code progress} por classe (achado uma vez; o mod tem poucas classes de máquina). */
    private static final ClassValue<Optional<Field>> CAMPOS = new ClassValue<>() {
        @Override
        protected Optional<Field> computeValue(Class<?> tipo) {
            if (!tipo.getName().startsWith(PACOTE_TILES)) return Optional.empty();
            for (Class<?> c = tipo; c != null && c != Object.class; c = c.getSuperclass()) {
                try {
                    Field f = c.getDeclaredField("progress");
                    if (f.getType() == int.class && f.trySetAccessible()) return Optional.of(f);
                } catch (NoSuchFieldException | SecurityException ignorado) {
                    // procura na classe de cima
                }
            }
            return Optional.empty();
        }
    };

    private boolean avisouErro;

    private ReAvaritiaFalhas() {}

    /** Chamado pela {@code Compat} só quando o mod está no jogo. */
    public static void iniciar() {
        FalhaMaquinas.registrarFonte(new ReAvaritiaFalhas());
    }

    @Override
    public String modid() {
        return Compat.Integracao.REAVARITIA.modid;
    }

    @Override
    public boolean emCiclo(BlockEntity be) {
        Optional<Field> f = CAMPOS.get(be.getClass());
        if (f.isEmpty()) return false;
        try {
            return f.get().getInt(be) > 0;
        } catch (ReflectiveOperationException | RuntimeException erro) {
            avisar(erro);
            return false;
        }
    }

    @Override
    public void pausarCiclo(BlockEntity be) {
        Optional<Field> f = CAMPOS.get(be.getClass());
        if (f.isEmpty()) return;
        try {
            f.get().setInt(be, 0); // só o progresso do ciclo; materiais e resultado ficam onde estão
            be.setChanged();
        } catch (ReflectiveOperationException | RuntimeException erro) {
            avisar(erro);
        }
    }

    private void avisar(Throwable erro) {
        if (avisouErro) return;
        avisouErro = true;
        OdMod.LOGGER.warn("Re:Avaritia: não consegui pausar o ciclo da máquina (versão diferente?): {}", erro.toString());
    }
}
