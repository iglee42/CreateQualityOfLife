package fr.iglee42.createqualityoflife.mixins;

import fr.iglee42.createqualityoflife.items.tools.refinedradiance.RefinedRadianceShovel;
import fr.iglee42.createqualityoflife.items.tools.shadowradiance.ShadowRadianceShovel;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at =@At("RETURN"),cancellable = true)
    private static void qol$smelting(BlockState p_49875_, ServerLevel p_49876_, BlockPos p_49877_, BlockEntity p_49878_, Entity p_49879_, ItemStack p_49880_, CallbackInfoReturnable<List<ItemStack>> cir){
        if (!(p_49880_.getItem() instanceof ShadowRadianceShovel) && !(p_49880_.getItem() instanceof RefinedRadianceShovel)) return;
        if (!p_49880_.isCorrectToolForDrops(p_49875_)) return;
        if (NBTConstants.getOrDefault(p_49880_,NBTConstants.NBT_SMELTING,false)){
            List<ItemStack> drops = cir.getReturnValue();
            for (int i = 0; i < drops.size(); i++) {
                ItemStack stack = drops.get(i);
                Optional<SmeltingRecipe> optionalRecipe = p_49876_.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING).stream().filter(r -> r.matches(new SimpleContainer(stack), p_49876_)).findFirst();
                if (optionalRecipe.isEmpty()) continue;
                ItemStack result = optionalRecipe.get().assemble(new SimpleContainer(stack),p_49876_.registryAccess());
                drops.set(i,result.copyWithCount(result.getCount() * stack.getCount()));
            }
            cir.setReturnValue(drops);
        }
    }
}
