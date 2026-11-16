package com.nikolas.odteste.sigilos;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Sigilo riscado de giz no chão: fino como um tapete de 1 px, sem colisão, quebra num golpe, brilha de leve e não
 * solta item. Sem block entity: o desenho e o giro são do estado do bloco (custo zero por quadro). Só sobrevive
 * sobre um bloco firme por cima. Os cinco sigilos de elemento ativam com a mão vazia (ver AtivacaoSigilo); os outros só aparecem.
 */
public class SigiloBloco extends Block {
    public static final EnumProperty<Simbolo> SIMBOLO = EnumProperty.create(IdsSigilos.PROPRIEDADE_SIMBOLO, Simbolo.class);
    public static final IntegerProperty GIRO = IntegerProperty.create(IdsSigilos.PROPRIEDADE_GIRO, 0, RegrasSigilos.GIROS - 1);
    private static final VoxelShape FORMA = Block.box(0, 0, 0, 16, 1, 16);

    public SigiloBloco(Properties propriedades) {
        super(propriedades);
        registerDefaultState(stateDefinition.any().setValue(SIMBOLO, Simbolo.SIGILO_DE_MEDO).setValue(GIRO, 0));
    }

    public BlockState estado(Simbolo simbolo, int giro) {
        return defaultBlockState().setValue(SIMBOLO, simbolo).setValue(GIRO, giro);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIMBOLO, GIRO);
    }

    @Override
    protected VoxelShape getShape(BlockState estado, BlockGetter mundo, BlockPos pos, CollisionContext contexto) {
        return FORMA;
    }

    @Override
    protected boolean canSurvive(BlockState estado, LevelReader mundo, BlockPos pos) {
        BlockPos chao = pos.below();
        return mundo.getBlockState(chao).isFaceSturdy(mundo, chao, Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState estado, Direction direcao, BlockState vizinho, LevelAccessor mundo,
            BlockPos pos, BlockPos posVizinho) {
        if (direcao == Direction.DOWN && !estado.canSurvive(mundo, pos)) return Blocks.AIR.defaultBlockState();
        return super.updateShape(estado, direcao, vizinho, mundo, pos, posVizinho);
    }

    /**
     * Clique com a mão vazia num sigilo de elemento: ativa (ver {@link AtivacaoSigilo}). Com algo na mão o clique passa
     * adiante (o giz abre a tela). Letras, dígitos e tipos de ritual não fazem nada sozinhos.
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState estado, Level mundo, BlockPos pos, Player jogador, BlockHitResult mira) {
        if (!jogador.getMainHandItem().isEmpty()) return InteractionResult.PASS;
        Simbolo simbolo = estado.getValue(SIMBOLO);
        if (mundo.isClientSide) {
            return RegrasEfeitoSigilo.Efeito.deSimbolo(simbolo.id()).isPresent() ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        if (jogador instanceof ServerPlayer servidor && AtivacaoSigilo.ativar((ServerLevel) mundo, pos, simbolo, servidor)) {
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader mundo, BlockPos pos, BlockState estado) {
        return ItemStack.EMPTY; // não existe item do sigilo: ele só nasce do giz
    }
}
