package fr.iglee42.createqualityoflife.events;

import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.drill.CobbleGenOptimisation;
import fr.iglee42.createqualityoflife.CreateQOL;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber
public class CommonEvents {

    @SubscribeEvent
    public static void onLoadWorld(LevelEvent.Load event) {
        LevelAccessor world = event.getLevel();
        CreateQOL.ENDER_PACKAGER_NETWORK_HANDLER.onLoadWorld(world);
    }

    @SubscribeEvent
    public static void onUnloadWorld(LevelEvent.Unload event) {
        LevelAccessor world = event.getLevel();
        CreateQOL.ENDER_PACKAGER_NETWORK_HANDLER.onUnloadWorld(world);
    }
}
