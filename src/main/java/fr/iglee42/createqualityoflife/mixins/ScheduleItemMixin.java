package fr.iglee42.createqualityoflife.mixins;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(value = ScheduleItem.class)
public class ScheduleItemMixin {

    @Inject(method = "use",at = @At("HEAD"))
    private void inject(Level world, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir){
        if (!world.isClientSide) {
            if (player.isCrouching() && hand == InteractionHand.MAIN_HAND) {
                boolean ended = false;
                for (int x = -5; x <= 5; x++) {
                    for (int y = -5; y <= 5; y++) {
                        for (int z = -5; z <= 5; z++) {
                            if (world.getBlockState(player.getOnPos().above().offset(x, y, z)).getBlock() instanceof StationBlock && CreateQOL.isActivate(Features.PROXIMITY_SCHEDULE)) {
                                ScheduleEntry entry = new ScheduleEntry();
                                ScheduledDelay delay = new ScheduledDelay();
                                ArrayList<ScheduleWaitCondition> initialConditions = new ArrayList<>();
                                initialConditions.add(delay);
                                CompoundTag instr = new CompoundTag();
                                instr.putString("Id", "create:destination");
                                CompoundTag data = new CompoundTag();
                                data.putString("Text", ((StationBlockEntity) world.getBlockEntity(player.getOnPos().above().offset(x, y, z))).getStation().name);
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
                                    currentItem.getOrCreateTag().remove("Schedule");
                                    currentItem.getOrCreateTag().put("Schedule",schedule.write());
                                }
                                ended = true;
                                player.getCooldowns().addCooldown(currentItem.getItem(),5);
                                break;
                            }
                        }
                        if (ended) break;
                    }
                    if (ended) break;
                }
            }
        }
    }
}
