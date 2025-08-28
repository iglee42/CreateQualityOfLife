package fr.iglee42.createqualityoflife.events;

import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.drill.CobbleGenOptimisation;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.utils.LogisticsNetworkExtension;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.TreeMap;

@Mod.EventBusSubscriber
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void mineBlock(BlockEvent.BreakEvent event){
        if (event.getPlayer().hasPermissions(2)) return;
        if (event.getLevel().isClientSide()) return;
        LogisticallyLinkedBehaviour behaviour = BlockEntityBehaviour.get(event.getLevel(),event.getPos(),LogisticallyLinkedBehaviour.TYPE);
        if (behaviour != null){
            LogisticsNetwork network = Create.LOGISTICS.logisticsNetworks.get(behaviour.freqId);
            if (network != null){
                LogisticsNetworkExtension extension = (LogisticsNetworkExtension) network;
                if (!extension.createQOL$getDestructionLevel().canDestroy(network.id,event.getPlayer())){
                    event.setCanceled(true);
                    event.getPlayer().displayClientMessage(CreateQOLLang.translate("tooltip.network.cant_break").style(ChatFormatting.RED).component(),true);
                }
            }
        }
    }
}
