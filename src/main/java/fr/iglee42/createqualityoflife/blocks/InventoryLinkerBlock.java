package fr.iglee42.createqualityoflife.blocks;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import fr.iglee42.createqualityoflife.blockentitites.InventoryLinkerBlockEntity;
import fr.iglee42.createqualityoflife.registries.ModBlockEntities;
import fr.iglee42.createqualityoflife.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class InventoryLinkerBlock extends KineticBlock implements IBE<InventoryLinkerBlockEntity>, IWrenchable {
    public InventoryLinkerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return AllShapes.MILLSTONE;
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.FAST;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public Class<InventoryLinkerBlockEntity> getBlockEntityClass() {
        return InventoryLinkerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends InventoryLinkerBlockEntity> getBlockEntityType() {
        return ModBlockEntities.INVENTORY_LINKER.get();
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand p_60507_, BlockHitResult p_60508_) {
        if (level.isClientSide) return InteractionResult.sidedSuccess(level.isClientSide);
        if (level.getBlockEntity(pos) instanceof InventoryLinkerBlockEntity be){
            if (player.isCrouching()){
                if (!be.getPlayerPaperItemStack().isEmpty() && player.getMainHandItem().isEmpty()){
                    player.setItemInHand(InteractionHand.MAIN_HAND,be.getPlayerPaperItemStack().copy());
                    be.setPlayerPaperItemStack(ItemStack.EMPTY);
                    level.sendBlockUpdated(pos,blockState,blockState,2);
                }
            } else {
                if (be.getPlayerPaperItemStack().isEmpty() && player.getMainHandItem().is(ModItems.PLAYER_PAPER.get())){
                    if (player.getMainHandItem().getOrCreateTag().contains("linkedPlayer")){
                        be.setPlayerPaperItemStack(player.getMainHandItem().copy());
                        player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);
                        level.sendBlockUpdated(pos,blockState,blockState,2);
                    }
                }
            }
        }
        return super.use(blockState, level, pos, player, p_60507_, p_60508_);
    }
}
