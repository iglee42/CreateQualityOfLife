package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.legacy.ChromaticCompoundItem;
import com.simibubi.create.content.legacy.NoGravMagicalDohickyItem;
import com.simibubi.create.infrastructure.config.AllConfigs;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.utils.Features;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChromaticCompoundItem.class)
public class ChromaticCompoundItemMixin {

    @Inject(method = "onEntityItemUpdate", at = @At("HEAD"),cancellable = true,remap = false)
    private void onEntityItemUpdate(ItemStack stack, ItemEntity entity, CallbackInfoReturnable<Boolean> cir){
        Level world = entity.level();

        if (AllConfigs.server().recipes.enableRefinedRadianceRecipe.get()) {
            boolean isOverBeacon = false;
            int entityX = Mth.floor(entity.getX());
            int entityZ = Mth.floor(entity.getZ());
            int localWorldHeight = world.getHeight(Heightmap.Types.WORLD_SURFACE, entityX, entityZ);

            BlockPos.MutableBlockPos testPos =
                    new BlockPos.MutableBlockPos(entityX, Math.min(Mth.floor(entity.getY()), localWorldHeight), entityZ);

            while (testPos.getY() > -64) {
                testPos.move(Direction.DOWN);
                BlockState state = world.getBlockState(testPos);
                if (state.getBlock() == Blocks.BEACON) {
                    BlockEntity be = world.getBlockEntity(testPos);

                    if (!(be instanceof BeaconBlockEntity))
                        break;


                    BeaconBlockEntity bte = (BeaconBlockEntity) be;

                    if (!bte.getBeamSections().isEmpty()) {
                        isOverBeacon = true;
                    }

                    break;
                }
            }
            CompoundTag data = entity.getPersistentData();

            if (isOverBeacon) {
                ItemStack newStack = AllItems.REFINED_RADIANCE.asStack();
                newStack.setCount(stack.getCount());
                data.putBoolean("JustCreated", true);
                entity.setItem(newStack);
                cir.setReturnValue(false);
            }
        }
    }


}
