package fr.iglee42.createqualityoflife.registries.generators;

import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;

import fr.iglee42.createqualityoflife.blocks.SingleBeltBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class SingleBeltGenerator extends SpecialBlockStateGen {

	@Override
	protected int getXRotation(BlockState state) {
		Direction direction = state.getValue(SingleBeltBlock.HORIZONTAL_FACING);
		return direction.getAxisDirection() == AxisDirection.NEGATIVE ? 180 : 0;
	}

	@Override
	protected int getYRotation(BlockState state) {
		Direction direction = state.getValue(SingleBeltBlock.HORIZONTAL_FACING);
		return horizontalAngle(direction);
	}

	@Override
	public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
		BlockState state) {
		Boolean casing = state.getValue(SingleBeltBlock.CASING);


		String path = "block/single_belt";

		if (!casing)
			return prov.models()
				.getExistingFile(prov.modLoc(path+"/particle"));
		



		ResourceLocation location = prov.modLoc(path + "/casing");
		return prov.models()
			.getExistingFile(location);
	}

}
