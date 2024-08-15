package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import fr.iglee42.createqualityoflife.blocks.SingleBeltBlock;
import fr.iglee42.createqualityoflife.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BeltFunnelBlock.class,remap = false)
public class BeltFunnelBlockMixin {

    @Inject(method = "isOnValidBelt",at = @At("HEAD"),cancellable = true)
    private static void inject(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cir){
        BlockState stateBelow = world.getBlockState(pos.below());
        if (stateBelow.getBlock() instanceof SingleBeltBlock) cir.setReturnValue(SingleBeltBlock.canTransportObjects(stateBelow));
    }
}
