package com.nikolas.odteste.historia;

import com.nikolas.odteste.dados.IdsDados;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * O que a história guarda no mundo (arquivo {@code data/odteste_historia.dat} do overworld). O Gabinete do Limiar
 * só usa os <b>grupos</b> (membros e marcos).
 *
 * <p>O mesmo arquivo do mod antigo tem mais coisas (vilas marcadas, âncoras, estruturas colocadas com o backup dos
 * blocos, aldeões a descongelar...). Elas ficam em {@link #guardado} e voltam iguais para o arquivo a cada save:
 * nada do que o mundo já tem é apagado. Arquivo estragado não crasha: o que não dá para ler é pulado.
 */
public final class DadosHistoria extends SavedData {
    public static final SavedData.Factory<DadosHistoria> FABRICA =
            new SavedData.Factory<>(DadosHistoria::new, DadosHistoria::carregar, null);
    /** Chave dos grupos dentro do arquivo (não muda). */
    static final String CHAVE_GRUPOS = "grupos";

    public final Grupos grupos = new Grupos();
    /** Tudo do arquivo que não é dos grupos: só é guardado e escrito de volta. */
    private CompoundTag guardado = new CompoundTag();

    public static DadosHistoria de(MinecraftServer servidor) {
        return servidor.overworld().getDataStorage().computeIfAbsent(FABRICA, IdsDados.ARQUIVO_HISTORIA);
    }

    @Override
    public CompoundTag save(CompoundTag t, HolderLookup.Provider registros) {
        for (String k : guardado.getAllKeys()) {
            Tag v = guardado.get(k);
            if (v != null) t.put(k, v.copy());
        }
        ListTag lg = new ListTag();
        for (Grupos.Grupo g : grupos.todos()) {
            CompoundTag cg = new CompoundTag();
            cg.putString("nome", g.nome());
            CompoundTag membros = new CompoundTag();
            g.membros().forEach((u, n) -> membros.putString(u.toString(), n));
            cg.put("membros", membros);
            ListTag marcos = new ListTag();
            for (String m : g.marcos()) marcos.add(StringTag.valueOf(m));
            cg.put("marcos", marcos);
            lg.add(cg);
        }
        t.put(CHAVE_GRUPOS, lg);
        return t;
    }

    static DadosHistoria carregar(CompoundTag t, HolderLookup.Provider registros) {
        DadosHistoria d = new DadosHistoria();
        ListTag lg = t.getList(CHAVE_GRUPOS, Tag.TAG_COMPOUND);
        for (int i = 0; i < lg.size(); i++) {
            CompoundTag cg = lg.getCompound(i);
            var nome = Grupos.normalizar(cg.getString("nome"));
            if (nome.isEmpty()) continue;
            Grupos.Grupo g = d.grupos.carregar(nome.get());
            CompoundTag membros = cg.getCompound("membros");
            for (String k : membros.getAllKeys()) {
                uuid(k).ifPresent(u -> Grupos.carregarMembro(g, u, membros.getString(k)));
            }
            ListTag marcos = cg.getList("marcos", Tag.TAG_STRING);
            for (int j = 0; j < marcos.size(); j++) Grupos.carregarMarco(g, marcos.getString(j));
        }
        CompoundTag resto = t.copy();
        resto.remove(CHAVE_GRUPOS);
        d.guardado = resto;
        return d;
    }

    private static Optional<UUID> uuid(String s) {
        try {
            return Optional.of(UUID.fromString(s));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
