package fr.iglee42.createqualityoflife.utils;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import fr.iglee42.createqualityoflife.client.screen.ArmorConfigScreen;
import fr.iglee42.createqualityoflife.packets.ToggleFansPacket;
import fr.iglee42.createqualityoflife.packets.ToggleHoverPacket;
import fr.iglee42.createqualityoflife.packets.UpdateInputsPacket;
import fr.iglee42.createqualityoflife.registries.ModArmorMaterials;
import fr.iglee42.createqualityoflife.registries.ModItems;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicBoolean;

@EventBusSubscriber(Dist.CLIENT)
public class KeyBindManager {

    private static boolean lastFlyState = false;
    private static boolean lastDescendState = false;
    private static boolean lastForwardState = false;
    private static boolean lastBackwardState = false;
    private static boolean lastLeftState = false;
    private static boolean lastRightState = false;



    public static KeyMapping FANS_KEY = new KeyMapping("keybind.createqol.shadow_radiance_chestplate_fans", GLFW.GLFW_KEY_Y, "keybind.createqol.category");
    public static KeyMapping HOVER_KEY = new KeyMapping("keybind.createqol.shadow_radiance_chestplate_hover", GLFW.GLFW_KEY_H, "keybind.createqol.category");
    public static KeyMapping OPEN_ARMOR_CONFIG = new KeyMapping("keybind.createqol.open_armor_config", GLFW.GLFW_KEY_C, "keybind.createqol.category");


    private static void tickEnd() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            boolean flyState = mc.options.keyJump.isDown();
            boolean descendState = mc.player.input.shiftKeyDown;
            boolean forwardState = mc.player.input.up;
            boolean backwardState = mc.player.input.down;
            boolean leftState = mc.player.input.left;
            boolean rightState = mc.player.input.right;
            if (flyState != lastFlyState || descendState != lastDescendState || forwardState != lastForwardState || backwardState != lastBackwardState || leftState != lastLeftState || rightState != lastRightState) {
                lastFlyState = flyState;
                lastDescendState = descendState;
                lastForwardState = forwardState;
                lastBackwardState = backwardState;
                lastLeftState = leftState;
                lastRightState = rightState;
                PacketDistributor.sendToServer(new UpdateInputsPacket(flyState, descendState, forwardState, backwardState, leftState, rightState));
                CommonKeysHandler.update(mc.player, flyState, descendState, forwardState, backwardState, leftState, rightState);
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post evt) {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }

            if (OPEN_ARMOR_CONFIG.consumeClick()){
                AtomicBoolean hasArmor = new AtomicBoolean(false);
                player.getArmorSlots().forEach(it->{
                    if (!(it.getItem() instanceof ArmorItem))return;
                    if (((ArmorItem)it.getItem()).getMaterial().equals(ModArmorMaterials.SHADOW_RADIANCE)) hasArmor.set(true);
                });
                if (hasArmor.get()) {
                    ScreenOpener.open(new ArmorConfigScreen());
                }
            }

            Item backtank = BacktankItem.getWornBy(player);

            if (backtank == null) return;
            if (!ModItems.SHADOW_RADIANCE_CHESTPLATE.is(backtank)) return;
            if (FANS_KEY.consumeClick()) {
                PacketDistributor.sendToServer(ToggleFansPacket.INSTANCE);
            }
            if (HOVER_KEY.consumeClick()) {
                PacketDistributor.sendToServer(ToggleHoverPacket.INSTANCE);
            }
            tickEnd();
    }
}