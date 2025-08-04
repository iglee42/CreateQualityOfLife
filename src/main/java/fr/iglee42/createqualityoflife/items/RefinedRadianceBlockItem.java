package fr.iglee42.createqualityoflife.items;

import com.simibubi.create.content.legacy.NoGravMagicalDohickyItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class RefinedRadianceBlockItem extends NoGravMagicalDohickyBlockItem {

	public RefinedRadianceBlockItem(Block block, Properties p_i48487_1_) {
		super(block, p_i48487_1_);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	protected void onCreated(ItemEntity entity, CompoundTag persistentData) {
		super.onCreated(entity, persistentData);
		entity.setDeltaMovement(entity.getDeltaMovement()
			.add(0, .25f, 0));
	}

}
