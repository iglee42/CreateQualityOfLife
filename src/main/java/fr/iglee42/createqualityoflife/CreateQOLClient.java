package fr.iglee42.createqualityoflife;

import fr.iglee42.createqualityoflife.registries.ModPartialModels;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class CreateQOLClient {

    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        ModPartialModels.init();
        //if (CreateCasing.isExtendedCogsLoaded())CreateExtendedCogwheelsPartials.init();

        modEventBus.addListener(CreateQOLClient::clientInit);

    }

    public static void clientInit(final FMLClientSetupEvent event) {

        //ModPonderTags.register();
        //PonderIndex.register();

    }
}