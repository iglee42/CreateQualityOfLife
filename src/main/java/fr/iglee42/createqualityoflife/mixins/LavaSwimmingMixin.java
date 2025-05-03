package fr.iglee42.createqualityoflife.mixins;

import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.DivingBootsItem;
import fr.iglee42.createqualityoflife.registries.ModItems;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LavaSwimmingMixin extends Entity {
	private LavaSwimmingMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@Inject(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInLava()Z")), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V", shift = Shift.AFTER, ordinal = 0))
	private void create$onLavaTravel(Vec3 travelVector, CallbackInfo ci) {
		ItemStack bootsStack = DivingBootsItem.getWornItem(this);
		if (ModItems.SHADOW_RADIANCE_BOOTS.isIn(bootsStack) && NBTConstants.getOrDefault(bootsStack,NBTConstants.NBT_LAVA,true) && CreateQOLConfigs.server().bootsLavaWalking.get())
			setDeltaMovement(getDeltaMovement().multiply(DivingBootsItem.getMovementMultiplier((LivingEntity) (Object) this)));
	}
}
