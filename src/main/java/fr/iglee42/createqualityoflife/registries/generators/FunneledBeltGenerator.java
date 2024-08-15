package fr.iglee42.createqualityoflife.registries.generators;

import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;

import fr.iglee42.createqualityoflife.blocks.FunneledBeltBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class FunneledBeltGenerator extends SpecialBlockStateGen {

	@Override
	protected int getXRotation(BlockState state) {
		return 0;
	}

	@Override
	protected int getYRotation(BlockState state) {
		return state.getValue(FunneledBeltBlock.HORIZONTAL_AXIS) == Direction.Axis.X ? 0  : 90;
	}

	@Override
	public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
		BlockState state) {


		ResourceLocation location = prov.modLoc("block/funneled_belt/brass");
		return prov.models()
			.getExistingFile(location);
	}

}
