package fr.iglee42.createqualityoflife.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class DestroyUtils {

    public static void destroyBlock(Level level, GameType gameModeForPlayer, Player player, ServerPlayerGameMode mode, BlockPos p_9281_, BlockPos itPos) {
        BlockState blockstate = level.getBlockState(p_9281_);
        BlockEntity blockentity = level.getBlockEntity(p_9281_);
        Block block = blockstate.getBlock();
        if (block instanceof GameMasterBlock && !player.canUseGameMasterBlocks()) {
            level.sendBlockUpdated(p_9281_, blockstate, blockstate, 3);
            return;
        }
        if (player.getMainHandItem().onBlockStartBreak(p_9281_, player)) {
            return;
        }
        if (player.blockActionRestricted(level, p_9281_, gameModeForPlayer)) {
            return;
        }
        if (mode.isCreative()) {
            removeBlock(level,player,p_9281_,blockstate, false);
        } else {
            ItemStack itemstack = player.getMainHandItem();
            ItemStack itemstack1 = itemstack.copy();
            boolean flag1 = blockstate.canHarvestBlock(level, p_9281_, player); // previously player.hasCorrectToolForDrops(blockstate)
            itemstack.mineBlock(level, blockstate, p_9281_, player);
            if (itemstack.isEmpty() && !itemstack1.isEmpty())
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, itemstack1, InteractionHand.MAIN_HAND);
            boolean flag = removeBlock(level,player,p_9281_,blockstate, flag1);
            if (flag && flag1) {
                block.playerDestroy(level, player, p_9281_, blockstate, blockentity, itemstack1);
            }
        }
    }

    private static boolean removeBlock(Level level, Player player,BlockPos pos, BlockState state, boolean canHarvest) {
        boolean removed = state.onDestroyedByPlayer(level, pos, player, canHarvest, level.getFluidState(pos));
        if (removed)
            state.getBlock().destroy(level, pos, state);
        return removed;
    }
}
