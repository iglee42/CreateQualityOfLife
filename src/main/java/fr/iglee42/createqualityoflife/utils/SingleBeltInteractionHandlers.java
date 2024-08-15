package fr.iglee42.createqualityoflife.utils;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlock;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlockEntity;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelBlock;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.content.redstone.displayLink.source.AccumulatedItemCountDisplaySource;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.Iterate;
import fr.iglee42.createqualityoflife.blockentitites.SingleBeltBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;

public class SingleBeltInteractionHandlers {

    public static boolean checkForFunnels(SingleBeltInventory beltInventory, TransportedItemStack currentItem,
                                          float nextOffset) {
        boolean beltMovementPositive = beltInventory.beltMovementPositive;
        int firstUpcomingSegment = (int) Math.floor(currentItem.beltPosition);
        int step = beltMovementPositive ? 1 : -1;
        firstUpcomingSegment = Mth.clamp(firstUpcomingSegment, 0, beltInventory.belt.beltLength - 1);

        for (int segment = firstUpcomingSegment; beltMovementPositive ? segment <= nextOffset
                : segment + 1 >= nextOffset; segment += step) {
            BlockPos funnelPos = beltInventory.belt.getPositionForOffset(segment)
                    .above();
            Level world = beltInventory.belt.getLevel();
            BlockState funnelState = world.getBlockState(funnelPos);
            if (!(funnelState.getBlock() instanceof BeltFunnelBlock))
                continue;
            Direction funnelFacing = funnelState.getValue(BeltFunnelBlock.HORIZONTAL_FACING);
            Direction movementFacing = beltInventory.belt.getMovementFacing();
            boolean blocking = funnelFacing == movementFacing.getOpposite();
            if (funnelFacing == movementFacing)
                continue;
            if (funnelState.getValue(BeltFunnelBlock.SHAPE) == BeltFunnelBlock.Shape.PUSHING)
                continue;

            float funnelEntry = segment + .5f;
            if (funnelState.getValue(BeltFunnelBlock.SHAPE) == BeltFunnelBlock.Shape.EXTENDED)
                funnelEntry += .499f * (beltMovementPositive ? -1 : 1);

            boolean hasCrossed = nextOffset > funnelEntry && beltMovementPositive
                    || nextOffset < funnelEntry && !beltMovementPositive;
            if (!hasCrossed)
                return false;
            if (blocking)
                currentItem.beltPosition = funnelEntry;

            if (world.isClientSide || funnelState.getOptionalValue(BeltFunnelBlock.POWERED).orElse(false))
                if (blocking)
                    return true;
                else
                    continue;

            BlockEntity be = world.getBlockEntity(funnelPos);
            if (!(be instanceof FunnelBlockEntity))
                return true;

            FunnelBlockEntity funnelBE = (FunnelBlockEntity) be;
            InvManipulationBehaviour inserting = funnelBE.getBehaviour(InvManipulationBehaviour.TYPE);
            FilteringBehaviour filtering = funnelBE.getBehaviour(FilteringBehaviour.TYPE);

            if (inserting == null || filtering != null && !filtering.test(currentItem.stack))
                if (blocking)
                    return true;
                else
                    continue;

            int amountToExtract = funnelBE.getAmountToExtract();
            ItemHelper.ExtractionCountMode modeToExtract = funnelBE.getModeToExtract();

            ItemStack toInsert = currentItem.stack.copy();
            if (amountToExtract > toInsert.getCount() && modeToExtract != ItemHelper.ExtractionCountMode.UPTO)
                if (blocking)
                    return true;
                else
                    continue;

            if (amountToExtract != -1 && modeToExtract != ItemHelper.ExtractionCountMode.UPTO) {
                toInsert.setCount(Math.min(amountToExtract, toInsert.getCount()));
                ItemStack remainder = inserting.simulate()
                        .insert(toInsert);
                if (!remainder.isEmpty())
                    if (blocking)
                        return true;
                    else
                        continue;
            }

            ItemStack remainder = inserting.insert(toInsert);
            if (toInsert.equals(remainder, false))
                if (blocking)
                    return true;
                else
                    continue;

            int notFilled = currentItem.stack.getCount() - toInsert.getCount();
            if (!remainder.isEmpty()) {
                remainder.grow(notFilled);
            } else if (notFilled > 0)
                remainder = ItemHandlerHelper.copyStackWithSize(currentItem.stack, notFilled);

            funnelBE.flap(true);
            funnelBE.onTransfer(toInsert);
            currentItem.stack = remainder;
            beltInventory.belt.sendData();
            if (blocking)
                return true;
        }

        return false;
    }

    public static boolean flapTunnelsAndCheckIfStuck(SingleBeltInventory beltInventory, TransportedItemStack current,
                                                     float nextOffset) {

        int currentSegment = (int) current.beltPosition;
        int upcomingSegment = (int) nextOffset;

        Direction movementFacing = beltInventory.belt.getMovementFacing();
        if (!beltInventory.beltMovementPositive && nextOffset == 0)
            upcomingSegment = -1;
        if (currentSegment == upcomingSegment)
            return false;

        if (stuckAtTunnel(beltInventory, upcomingSegment, current.stack, movementFacing)) {
            current.beltPosition = currentSegment + (beltInventory.beltMovementPositive ? .99f : .01f);
            return true;
        }

        Level world = beltInventory.belt.getLevel();
        boolean onServer = !world.isClientSide || beltInventory.belt.isVirtual();
        boolean removed = false;
        BeltTunnelBlockEntity nextTunnel = getTunnelOnSegment(beltInventory, upcomingSegment);
        int transferred = current.stack.getCount();

        if (nextTunnel instanceof BrassTunnelBlockEntity) {
            BrassTunnelBlockEntity brassTunnel = (BrassTunnelBlockEntity) nextTunnel;
            if (brassTunnel.hasDistributionBehaviour()) {
                if (!brassTunnel.canTakeItems())
                    return true;
                if (onServer) {
                    brassTunnel.setStackToDistribute(current.stack, movementFacing.getOpposite());
                    current.stack = ItemStack.EMPTY;
                    beltInventory.belt.sendData();
                    beltInventory.belt.setChanged();
                }
                removed = true;
            }
        } else if (nextTunnel != null) {
            BlockState blockState = nextTunnel.getBlockState();
            if (current.stack.getCount() > 1 && AllBlocks.ANDESITE_TUNNEL.has(blockState)
                    && BeltTunnelBlock.isJunction(blockState)
                    && movementFacing.getAxis() == blockState.getValue(BeltTunnelBlock.HORIZONTAL_AXIS)) {

                for (Direction d : Iterate.horizontalDirections) {
                    if (d.getAxis() == blockState.getValue(BeltTunnelBlock.HORIZONTAL_AXIS))
                        continue;
                    if (!nextTunnel.flaps.containsKey(d))
                        continue;
                    BlockPos outpos = nextTunnel.getBlockPos()
                            .below()
                            .relative(d);
                    if (!world.isLoaded(outpos))
                        return true;
                    DirectBeltInputBehaviour behaviour =
                            BlockEntityBehaviour.get(world, outpos, DirectBeltInputBehaviour.TYPE);
                    if (behaviour == null)
                        continue;
                    if (!behaviour.canInsertFromSide(d))
                        continue;

                    ItemStack toinsert = ItemHandlerHelper.copyStackWithSize(current.stack, 1);
                    if (!behaviour.handleInsertion(toinsert, d, false)
                            .isEmpty())
                        return true;
                    if (onServer)
                        flapTunnel(beltInventory, upcomingSegment, d, false);

                    current.stack.shrink(1);
                    beltInventory.belt.sendData();
                    if (current.stack.getCount() <= 1)
                        break;
                }
            }
        }

        if (onServer) {
            flapTunnel(beltInventory, currentSegment, movementFacing, false);
            flapTunnel(beltInventory, upcomingSegment, movementFacing.getOpposite(), true);

            if (nextTunnel != null)
                DisplayLinkBlock.sendToGatherers(world, nextTunnel.getBlockPos(),
                        (dgte, b) -> b.itemReceived(dgte, transferred), AccumulatedItemCountDisplaySource.class);
        }

        if (removed)
            return true;

        return false;
    }

    public static boolean stuckAtTunnel(SingleBeltInventory beltInventory, int offset, ItemStack stack,
                                        Direction movementDirection) {
        SingleBeltBlockEntity belt = beltInventory.belt;
        BlockPos pos = belt.getPositionForOffset(offset)
                .above();
        if (!(belt.getLevel()
                .getBlockState(pos)
                .getBlock() instanceof BrassTunnelBlock))
            return false;
        BlockEntity be = belt.getLevel()
                .getBlockEntity(pos);
        if (be == null || !(be instanceof BrassTunnelBlockEntity))
            return false;
        BrassTunnelBlockEntity tunnel = (BrassTunnelBlockEntity) be;
        return !tunnel.canInsert(movementDirection.getOpposite(), stack);
    }

    public static void flapTunnel(SingleBeltInventory beltInventory, int offset, Direction side, boolean inward) {
        BeltTunnelBlockEntity be = getTunnelOnSegment(beltInventory, offset);
        if (be == null)
            return;
        be.flap(side, inward);
    }

    protected static BeltTunnelBlockEntity getTunnelOnSegment(SingleBeltInventory beltInventory, int offset) {
        SingleBeltBlockEntity belt = beltInventory.belt;
        return getTunnelOnPosition(belt.getLevel(), belt.getPositionForOffset(offset));
    }

    public static BeltTunnelBlockEntity getTunnelOnPosition(Level world, BlockPos pos) {
        pos = pos.above();
        if (!(world.getBlockState(pos)
                .getBlock() instanceof BeltTunnelBlock))
            return null;
        BlockEntity be = world.getBlockEntity(pos);
        if (be == null || !(be instanceof BeltTunnelBlockEntity))
            return null;
        return ((BeltTunnelBlockEntity) be);
    }


    public static boolean checkForCrushers(SingleBeltInventory beltInventory, TransportedItemStack currentItem,
                                           float nextOffset) {

        boolean beltMovementPositive = beltInventory.beltMovementPositive;
        int firstUpcomingSegment = (int) Math.floor(currentItem.beltPosition);
        int step = beltMovementPositive ? 1 : -1;
        firstUpcomingSegment = Mth.clamp(firstUpcomingSegment, 0, beltInventory.belt.beltLength - 1);

        for (int segment = firstUpcomingSegment; beltMovementPositive ? segment <= nextOffset
                : segment + 1 >= nextOffset; segment += step) {
            BlockPos crusherPos = beltInventory.belt.getPositionForOffset(segment)
                    .above();
            Level world = beltInventory.belt.getLevel();
            BlockState crusherState = world.getBlockState(crusherPos);
            if (!(crusherState.getBlock() instanceof CrushingWheelControllerBlock))
                continue;
            Direction crusherFacing = crusherState.getValue(CrushingWheelControllerBlock.FACING);
            Direction movementFacing = beltInventory.belt.getMovementFacing();
            if (crusherFacing != movementFacing)
                continue;

            float crusherEntry = segment + .5f;
            crusherEntry += .399f * (beltMovementPositive ? -1 : 1);
            float postCrusherEntry = crusherEntry + .799f * (!beltMovementPositive ? -1 : 1);

            boolean hasCrossed = nextOffset > crusherEntry && nextOffset < postCrusherEntry && beltMovementPositive
                    || nextOffset < crusherEntry && nextOffset > postCrusherEntry && !beltMovementPositive;
            if (!hasCrossed)
                return false;
            currentItem.beltPosition = crusherEntry;

            BlockEntity be = world.getBlockEntity(crusherPos);
            if (!(be instanceof CrushingWheelControllerBlockEntity))
                return true;

            CrushingWheelControllerBlockEntity crusherBE = (CrushingWheelControllerBlockEntity) be;

            ItemStack toInsert = currentItem.stack.copy();

            ItemStack remainder = ItemHandlerHelper.insertItemStacked(crusherBE.inventory, toInsert, false);
            if (toInsert.equals(remainder, false))
                return true;

            int notFilled = currentItem.stack.getCount() - toInsert.getCount();
            if (!remainder.isEmpty()) {
                remainder.grow(notFilled);
            } else if (notFilled > 0)
                remainder = ItemHandlerHelper.copyStackWithSize(currentItem.stack, notFilled);

            currentItem.stack = remainder;
            beltInventory.belt.sendData();
            return true;
        }

        return false;
    }

}
