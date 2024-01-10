package fr.iglee42.createqualityoflife.events;

import fr.iglee42.createqualityoflife.registries.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static fr.iglee42.createqualityoflife.CreateQOL.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventClientBusEvents {

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {

        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.PLAYER_PAPER.get(),
                    new ResourceLocation(MODID, "hasplayer"), (stack, level, living, id) -> stack.getOrCreateTag().contains("linkedPlayer") ? 1.0f : 0.0f);
        });
    }


}