package fr.iglee42.createqualityoflife;

import fr.iglee42.createqualityoflife.client.GoggleArmorLayer;
import fr.iglee42.createqualityoflife.client.ShadowRadianceFirstPersonRenderer;
import fr.iglee42.createqualityoflife.registries.ModPartialModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class CreateQOLClient {

    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        ModPartialModels.init();
        //if (CreateCasing.isExtendedCogsLoaded())CreateExtendedCogwheelsPartials.init();

        modEventBus.addListener(CreateQOLClient::clientInit);
        modEventBus.addListener(CreateQOLClient::addEntityRendererLayers);

        forgeEventBus.addListener(CreateQOLClient::onClientTick);

    }

    public static void clientInit(final FMLClientSetupEvent event) {

        //ModPonderTags.register();
        //PonderIndex.register();

    }

    public static void addEntityRendererLayers(EntityRenderersEvent.AddLayers event){
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

        GoggleArmorLayer.registerOnAll(dispatcher);
    }

    public static void onClientTick(TickEvent.ClientTickEvent event){
        ShadowRadianceFirstPersonRenderer.clientTick();
    }
}