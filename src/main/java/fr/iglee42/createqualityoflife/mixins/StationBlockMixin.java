package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.ScheduleItem;
import com.simibubi.create.content.trains.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.content.trains.schedule.condition.ScheduledDelay;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;
import com.simibubi.create.content.trains.station.StationBlock;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.utils.Features;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;

@Mixin(value = StationBlock.class)
public class StationBlockMixin {

    @Inject(method = "use",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"),cancellable = true,locals = LocalCapture.CAPTURE_FAILSOFT)
    private void createQOL$addStationWhenSneakClick(BlockState pState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult pHit, CallbackInfoReturnable<InteractionResult> cir, ItemStack stack){
        if (stack.is(AllItems.SCHEDULE.asItem()) && level.getBlockEntity(pos) instanceof StationBlockEntity sbe){
            if (CreateQOL.isActivate(Features.PROXIMITY_SCHEDULE)) {
                ScheduleEntry entry = new ScheduleEntry();
                ScheduledDelay delay = new ScheduledDelay();
                ArrayList<ScheduleWaitCondition> initialConditions = new ArrayList<>();
                initialConditions.add(delay);
                CompoundTag instr = new CompoundTag();
                instr.putString("Id", "create:destination");
                CompoundTag data = new CompoundTag();
                data.putString("Text", sbe.getStation().name);
                instr.put("Data", data);
                entry.instruction = DestinationInstruction.fromTag(instr);
                entry.conditions.add(initialConditions);
                ItemStack currentItem = player.getItemInHand(hand);
                if (ScheduleItem.getSchedule(currentItem) == null){
                    Schedule schedule = new Schedule();
                    schedule.entries.add(entry);
                    currentItem.getOrCreateTag().put("Schedule",schedule.write());
                } else {
                    Schedule schedule = ScheduleItem.getSchedule(currentItem);
                    schedule.entries.add(entry);
                    currentItem.getOrCreateTag().put("Schedule",schedule.write());
                }
                player.getCooldowns().addCooldown(currentItem.getItem(),5);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}
