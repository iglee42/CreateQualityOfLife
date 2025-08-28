package fr.iglee42.createqualityoflife.utils;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import fr.iglee42.createqualityoflife.client.screens.itemsconfig.InventoryConfigScreen;
import fr.iglee42.createqualityoflife.packets.*;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class KeyBindManager {

    private static boolean lastFlyState = false;
    private static boolean lastDescendState = false;
    private static boolean lastForwardState = false;
    private static boolean lastBackwardState = false;
    private static boolean lastLeftState = false;
    private static boolean lastRightState = false;


    public static KeyMapping FANS_KEY = new KeyMapping("keybind.createqol.shadow_radiance_chestplate_fans", GLFW.GLFW_KEY_Y, "keybind.createqol.category");
    public static KeyMapping HOVER_KEY = new KeyMapping("keybind.createqol.shadow_radiance_chestplate_hover", GLFW.GLFW_KEY_H, "keybind.createqol.category");
    public static KeyMapping ELYTRA_KEY = new KeyMapping("keybind.createqol.shadow_radiance_chestplate_elytra", GLFW.GLFW_KEY_I, "keybind.createqol.category");
    public static KeyMapping OPEN_ARMOR_CONFIG = new KeyMapping("keybind.createqol.open_armor_config", GLFW.GLFW_KEY_C, "keybind.createqol.category");
    public static KeyMapping DASH_KEY = new KeyMapping("keybind.createqol.dash", GLFW.GLFW_KEY_W, "keybind.createqol.category");

    public static KeyMapping HELMET_EFFECT_KEY = new KeyMapping("keybind.createqol.helmet_effect", -1, "keybind.createqol.category");
    public static KeyMapping CHESTPLATE_EFFECT_KEY = new KeyMapping("keybind.createqol.chestplate_effect", -1, "keybind.createqol.category");
    public static KeyMapping LEGGINGS_EFFECT_KEY = new KeyMapping("keybind.createqol.leggings_effect", -1, "keybind.createqol.category");
    public static KeyMapping BOOTS_EFFECT_KEY = new KeyMapping("keybind.createqol.boots_effect", -1, "keybind.createqol.category");


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
                QOLPackets.getChannel().sendToServer(new UpdateInputsPacket(flyState, descendState, forwardState, backwardState, leftState, rightState));
                CommonKeysHandler.update(mc.player, flyState, descendState, forwardState, backwardState, leftState, rightState);
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent evt) {
        if (evt.phase == TickEvent.Phase.END) {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }

        if (OPEN_ARMOR_CONFIG.consumeClick()) {
            ScreenOpener.open(new InventoryConfigScreen());
        }

            if (HELMET_EFFECT_KEY.consumeClick()){
                QOLPackets.getChannel().sendToServer(new ToggleArmorEffectPacket(EquipmentSlot.HEAD));
            }
            if (CHESTPLATE_EFFECT_KEY.consumeClick()){
                QOLPackets.getChannel().sendToServer(new ToggleArmorEffectPacket(EquipmentSlot.CHEST));
            }
            if (LEGGINGS_EFFECT_KEY.consumeClick()){
                QOLPackets.getChannel().sendToServer(new ToggleArmorEffectPacket(EquipmentSlot.LEGS));
            }
            if (BOOTS_EFFECT_KEY.consumeClick()){
                QOLPackets.getChannel().sendToServer(new ToggleArmorEffectPacket(EquipmentSlot.FEET));
            }


            Item backtank = BacktankItem.getWornBy(player);

            if (backtank == null) return;
            if (QOLItems.SHADOW_STEEL_CHESTPLATE.is(backtank) || QOLItems.SHADOW_RADIANCE_CHESTPLATE.is(backtank)) {
                if (DASH_KEY.consumeClick()) {
                    QOLPackets.getChannel().sendToServer(DashPacket.INSTANCE);
                }
            }

            if (QOLItems.SHADOW_RADIANCE_CHESTPLATE.is(backtank)) {
                if (FANS_KEY.consumeClick()) {
                    QOLPackets.getChannel().sendToServer(new ToggleFansPacket());
                }
                if (HOVER_KEY.consumeClick()) {
                    QOLPackets.getChannel().sendToServer(new ToggleHoverPacket());
                }
                if (ELYTRA_KEY.consumeClick()) {
                    QOLPackets.getChannel().sendToServer(new ToggleElytraPacket());
                }

            }
            tickEnd();
        }
    }
}