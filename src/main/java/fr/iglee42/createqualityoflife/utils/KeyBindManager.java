package fr.iglee42.createqualityoflife.utils;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import fr.iglee42.createqualityoflife.packets.ToggleFansPacket;
import fr.iglee42.createqualityoflife.packets.ToggleHoverPacket;
import fr.iglee42.createqualityoflife.packets.UpdateInputsPacket;
import fr.iglee42.createqualityoflife.registries.ModItems;
import fr.iglee42.createqualityoflife.registries.ModPackets;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

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
                ModPackets.getChannel().sendToServer(new UpdateInputsPacket(flyState, descendState, forwardState, backwardState, leftState, rightState));
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

            Item backtank = BacktankItem.getWornBy(player);

            if (backtank == null) return;
            if (!ModItems.SHADOW_RADIANCE_CHESTPLATE.is(backtank)) return;
            if (FANS_KEY.consumeClick()) {
                ModPackets.getChannel().sendToServer(new ToggleFansPacket());
            }
            if (HOVER_KEY.consumeClick()) {
                ModPackets.getChannel().sendToServer(new ToggleHoverPacket());
            }
            tickEnd();
        }
    }
}