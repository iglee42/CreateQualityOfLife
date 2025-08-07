package fr.iglee42.createqualityoflife.ponder;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.registries.QOLBlocks;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class QOLPonderScenes {

	public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {

		PonderSceneRegistrationHelper<ItemProviderEntry<?,?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);


		HELPER.forComponents(QOLBlocks.ENDER_PACKAGER)
				.addStoryBoard(CreateQOL.asResource("ender_packager"), QOLScenes::enderPackager, AllCreatePonderTags.HIGH_LOGISTICS)
				.addStoryBoard(CreateQOL.asResource("ender_packager_addresses"), QOLScenes::enderPackagerAddresses, AllCreatePonderTags.HIGH_LOGISTICS);

	}


}