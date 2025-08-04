package fr.iglee42.createqualityoflife.registries.generators;

import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;

import earth.terrarium.chipped.Chipped;
import fr.iglee42.createqualityoflife.blocks.ChippedSawBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class ChippedSawGenerator extends SpecialBlockStateGen {

	@Override
	protected int getXRotation(BlockState state) {
		return 0;
	}

	@Override
	protected int getYRotation(BlockState state) {
		return horizontalAngle(state.getValue(ChippedSawBlock.HORIZONTAL_FACING)) + (state.getValue(ChippedSawBlock.FLIPPED) ? 180 : 0);
	}

	@Override
	public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
		BlockState state) {
		String path = "block/chipped_workbench/" + ctx.getName().replace("_saw","");

		return prov.models()
			.getExistingFile(prov.modLoc(path));
	}

}
