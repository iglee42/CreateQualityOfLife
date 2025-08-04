package fr.iglee42.createqualityoflife.items;

import com.simibubi.create.content.legacy.NoGravMagicalDohickyItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Block;

public class ShadowSteelBlockItem extends NoGravMagicalDohickyBlockItem {

	public ShadowSteelBlockItem(Block block, Properties p_i48487_1_) {
		super(block, p_i48487_1_);
	}

	@Override
	protected void onCreated(ItemEntity entity, CompoundTag persistentData) {
		super.onCreated(entity, persistentData);
		float yMotion = (entity.fallDistance + 3) / 50f;
		entity.setDeltaMovement(0, yMotion, 0);
	}
	
	@Override
	protected float getIdleParticleChance(ItemEntity entity) {
		return (float) (Mth.clamp(entity.getItem()
			.getCount() - 10, Mth.clamp(entity.getDeltaMovement().y * 20, 5, 20), 100) / 64f);
	}

}
