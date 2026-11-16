package com.nikolas.odteste.compat.mekanism;

import com.nikolas.odteste.OdMod;
import com.nikolas.odteste.compat.Compat;
import com.nikolas.odteste.maquinas.FalhaMaquinas;
import com.nikolas.odteste.maquinas.FonteMaquina;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Falhas nas máquinas de receita do Mekanism (Câmara de Enriquecimento, Esmagador, Fundidor e parentes: todas
 * derivam de {@code TileEntityProgressMachine}). Só é carregada quando o Mekanism existe
 * ({@code Compat.Integracao.MEKANISM.ligada()}). Fábricas e geradores ficam de fora.
 *
 * <b>Por reflexão, de propósito:</b> compilar contra o Mekanism puxaria um monte de bibliotecas dele para o build, e
 * qualquer mudança de versão deixaria o NOSSO mod sem abrir. Os nomes usados (versão 10.7 para 1.21.1):
 * {@code getOperatingTicks()}, {@code setOperatingTicks(int)} e, no monitor de receita da máquina,
 * {@code invalidateCache()} (faz a receita em andamento ser refeita do zero, senão o Mekanism devolve o progresso
 * antigo no próximo tick). Se algo não existir, a pausa não acontece, o aviso sai uma vez no log, e o susto continua.
 * Itens, energia e fluidos nunca são tocados.
 */
public final class MekanismFalhas implements FonteMaquina {
    private static final String CLASSE_MAQUINA = "mekanism.common.tile.prefab.TileEntityProgressMachine";
    private static final String SUFIXO_MONITOR = "RecipeCacheLookupMonitor";

    private final Class<?> maquina;
    private final Method lerTicks;
    private final Method gravarTicks;
    /** Campo do monitor de receita (procurado pelo tipo) e o método que descarta a receita em andamento. */
    private Field campoMonitor;
    private Method invalidar;
    private boolean monitorProcurado;
    private boolean avisouErro;

    private MekanismFalhas(Class<?> maquina, Method lerTicks, Method gravarTicks) {
        this.maquina = maquina;
        this.lerTicks = lerTicks;
        this.gravarTicks = gravarTicks;
    }

    /** Chamado pela {@code Compat} só quando o Mekanism está no jogo. Sem a classe esperada, não liga (e avisa). */
    public static void iniciar() {
        try {
            Class<?> c = Class.forName(CLASSE_MAQUINA);
            Method ler = c.getMethod("getOperatingTicks");
            Method gravar = c.getMethod("setOperatingTicks", int.class);
            if (ler.getReturnType() != int.class) throw new NoSuchMethodException("getOperatingTicks não devolve int");
            FalhaMaquinas.registrarFonte(new MekanismFalhas(c, ler, gravar));
        } catch (ReflectiveOperationException | LinkageError erro) {
            OdMod.LOGGER.warn("Mekanism: as máquinas não vão falhar (versão diferente do esperado): {}", erro.toString());
        }
    }

    @Override
    public String modid() {
        return Compat.Integracao.MEKANISM.modid;
    }

    @Override
    public boolean emCiclo(BlockEntity be) {
        if (!maquina.isInstance(be)) return false;
        try {
            return (int) lerTicks.invoke(be) > 0;
        } catch (ReflectiveOperationException | RuntimeException erro) {
            avisar(erro);
            return false;
        }
    }

    @Override
    public void pausarCiclo(BlockEntity be) {
        if (!maquina.isInstance(be)) return;
        try {
            procurarMonitor(be.getClass());
            if (campoMonitor != null && invalidar != null) {
                Object monitor = campoMonitor.get(be);
                if (monitor != null) invalidar.invoke(monitor);
            }
            gravarTicks.invoke(be, 0);
            be.setChanged();
        } catch (ReflectiveOperationException | RuntimeException erro) {
            avisar(erro);
        }
    }

    /** Acha, uma vez, o campo do monitor de receita e o {@code invalidateCache()} dele. */
    private void procurarMonitor(Class<?> tipo) {
        if (monitorProcurado) return;
        monitorProcurado = true;
        for (Class<?> c = tipo; c != null && c != Object.class && campoMonitor == null; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                if (f.getType().getName().endsWith(SUFIXO_MONITOR) && f.trySetAccessible()) {
                    campoMonitor = f;
                    break;
                }
            }
        }
        if (campoMonitor == null) return;
        for (Class<?> c = campoMonitor.getType(); c != null && c != Object.class && invalidar == null; c = c.getSuperclass()) {
            try {
                Method m = c.getDeclaredMethod("invalidateCache");
                if (m.trySetAccessible()) invalidar = m;
            } catch (NoSuchMethodException ignorado) {
                // procura na classe de cima
            }
        }
        if (invalidar == null) {
            OdMod.LOGGER.warn("Mekanism: sem invalidateCache(); a pausa pode não segurar o ciclo (o susto continua).");
        }
    }

    private void avisar(Throwable erro) {
        if (avisouErro) return;
        avisouErro = true;
        OdMod.LOGGER.warn("Mekanism: não consegui mexer no ciclo da máquina (versão diferente?): {}", erro.toString());
    }
}
