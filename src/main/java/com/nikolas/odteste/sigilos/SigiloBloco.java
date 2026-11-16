package com.nikolas.odteste.sigilos;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
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
 * sobre um bloco firme por cima. Por enquanto só aparece (as magias vêm depois).
 */
public class SigiloBloco extends Block {
    public static final EnumProperty<Simbolo> SIMBOLO = EnumProperty.create(IdsSigilos.PROPRIEDADE_SIMBOLO, Simbolo.class);
    public static final IntegerProperty GIRO = IntegerProperty.create(IdsSigilos.PROPRIEDADE_GIRO, 0, RegrasSigilos.GIROS - 1);
    private static final VoxelShape FORMA = Block.box(0, 0, 0, 16, 1, 16);

    public SigiloBloco(Properties propriedades) {
        super(propriedades);
        registerDefaultState(stateDefinition.any().setValue(SIMBOLO, Simbolo.ELEMENTO_MEDO).setValue(GIRO, 0));
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

    @Override
    public ItemStack getCloneItemStack(LevelReader mundo, BlockPos pos, BlockState estado) {
        return ItemStack.EMPTY; // não existe item do sigilo: ele só nasce do giz
    }
}
