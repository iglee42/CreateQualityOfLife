package fr.iglee42.createqualityoflife.ponder;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.RegistryEntry;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.registries.QOLBlocks;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class QOLPonderTags {


	private static ResourceLocation loc(String id) {
		return CreateQOL.asResource(id);
	}


	public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
		PonderTagRegistrationHelper<RegistryEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);
		PonderTagRegistrationHelper<ItemLike> itemHelper = helper.withKeyFunction(
				CatnipServices.REGISTRIES::getKeyOrThrow);


		HELPER.addToTag(AllCreatePonderTags.HIGH_LOGISTICS)
						.add(QOLBlocks.ENDER_PACKAGER);


	}

}