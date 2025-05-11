package fr.iglee42.createqualityoflife.items;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.ModDataComponents;
import fr.iglee42.createqualityoflife.utils.CommonKeysHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ShadowRadianceChestplate extends BacktankItem.Layered{

    private static final double FANS_ACCELERATION = 0.15D;
    private static final double FANS_SPEED = 0.25D;
    private static final double FANS_HOVER_SPEED = 0.20D;

    public ShadowRadianceChestplate(Holder<ArmorMaterial> material, Properties properties, ResourceLocation textureLoc, Supplier<BacktankBlockItem> placeable) {
        super(material, properties, textureLoc, placeable);
    }


    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return hasElytra(stack)
                && isElytraEnable(stack)
                && (!hasPropeller(stack) || !isFansEnable(stack))
                && !BacktankUtil.getAllWithAir(entity).isEmpty()
                && CreateQOLConfigs.server().elytraAllowed.get();
    }

    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean offHand) {
        super.inventoryTick(stack, level, entity, slot, offHand);
        if (!(entity instanceof Player player)) return;
        if (player.getItemBySlot(EquipmentSlot.CHEST).equals(stack)){
            boolean second = level.getGameTime() % 20 == 0;
            if (BacktankUtil.getAllWithAir(player).isEmpty()) return;
            if (stack.getOrDefault(ModDataComponents.ARMOR_EFFECT,true) && CreateQOLConfigs.server().armorEffects.get())
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 1, false, false));
            if (player.isFallFlying() && isElytraEnable(stack) && !BacktankUtil.getAllWithAir(player).isEmpty() && CreateQOLConfigs.server().elytraAllowed.get() && CreateQOLConfigs.server().elytraBoostAllowed.get() && level.getGameTime() % 10==0 && CommonKeysHandler.isHoldingUp(player)){
                Vec3 vec31 =  player.getLookAngle();
                double d0 = 1.5F;
                double d1 = 0.1;
                Vec3 vec32 = player.getDeltaMovement();
                player.setDeltaMovement(vec32.add(vec31.x * d1 + (vec31.x * (double)d0 - vec32.x) * (double)0.5F, vec31.y * d1 + (vec31.y * (double)d0 - vec32.y) * (double)0.5F, vec31.z * d1 + (vec31.z * (double)d0 - vec32.z) * (double)0.5F));
                BacktankUtil.consumeAir(player,stack,1);
            }
            if (player.isCreative() || player.isSpectator()) return;
            if (isFansEnable(stack) && !BacktankUtil.getAllWithAir(player).isEmpty() && hasPropeller(stack) && CreateQOLConfigs.server().propellersAllowed.get()) {
                boolean hover = isHoverEnable(stack) && CreateQOLConfigs.server().hoverAllowed.get();
                boolean jumpKeyActive = CommonKeysHandler.isHoldingUp(player);
                boolean shiftKeyActive = CommonKeysHandler.isHoldingDown(player);
                player.resetFallDistance();
                if (player instanceof ServerPlayer) {
                    ((ServerPlayer) player).connection.aboveGroundTickCount = 0;
                }
                if (!player.isSwimming()) {
                    if (jumpKeyActive) {
                        if (!hover) {
                            if (shiftKeyActive)
                                pushVertically(player, Math.min(player.getDeltaMovement().get(Direction.Axis.Y) + FANS_ACCELERATION, -0.0D));
                            else
                                pushVertically(player, Math.min(player.getDeltaMovement().get(Direction.Axis.Y) + FANS_ACCELERATION, FANS_SPEED));
                        } else if (CreateQOLConfigs.server().hoverAllowed.get()){
                            pushVertically(player, Math.min(player.getDeltaMovement().get(Direction.Axis.Y) + FANS_ACCELERATION, FANS_HOVER_SPEED));
                        }
                        if (second)BacktankUtil.consumeAir(player,stack,1);
                    } else {
                        if (hover) {
                            if (CreateQOLConfigs.server().hoverAllowed.get()) {
                                if (shiftKeyActive)
                                    pushVertically(player, Math.min(player.getDeltaMovement().get(Direction.Axis.Y) + FANS_ACCELERATION, -(FANS_HOVER_SPEED * 2)));
                                else {
                                    pushVertically(player, Math.min(player.getDeltaMovement().get(Direction.Axis.Y) + FANS_ACCELERATION, -0.00D));
                                    if (second) BacktankUtil.consumeAir(player, stack, 1);
                                }
                            }
                        } else {
                            if (shiftKeyActive) {
                                pushVertically(player, Math.min(player.getDeltaMovement().get(Direction.Axis.Y) + FANS_ACCELERATION, -(FANS_HOVER_SPEED * 2)));
                            } else {
                                pushVertically(player,Math.min(player.getDeltaMovement().get(Direction.Axis.Y) + FANS_ACCELERATION, -(FANS_HOVER_SPEED*1.5)));
                            }
                        }
                    }

                    if (CommonKeysHandler.isHoldingForwards(player)) {
                        player.moveRelative(1, new Vec3(0, 0, player.isSprinting() ? 0.05 * 1.125 : 0.05));
                    }
                    if (CommonKeysHandler.isHoldingBackwards(player)) {
                        player.moveRelative(1, new Vec3(0, 0, -0.05 * 0.75F));
                    }
                    if (CommonKeysHandler.isHoldingLeft(player)) {
                        player.moveRelative(1, new Vec3(0.05, 0, 0));
                    }
                    if (CommonKeysHandler.isHoldingRight(player)) {
                        player.moveRelative(1, new Vec3(-0.05, 0, 0));
                    }

                }else {
                    if (jumpKeyActive) {
                        player.moveRelative(1, new Vec3(0, 0, 0.05 * 1.125));
                        if (second)BacktankUtil.consumeAir(player,stack,1);
                    }
                }
            }
        }
    }

    private void pushVertically(Player p,double y){
        Vec3 motion = p.getDeltaMovement();
        p.setDeltaMovement(motion.get(Direction.Axis.X), y, motion.get(Direction.Axis.Z));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext p_41422_, List<Component> components, TooltipFlag p_41424_) {
        components.add(Component.literal("Air : ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal(String.valueOf(BacktankUtil.getAir(stack)))
                        .withStyle(ChatFormatting.YELLOW))
                .append(Component.literal("/"+BacktankUtil.maxAir(stack))
                        .withStyle(ChatFormatting.GOLD)));
        components.add(Component.literal("Propeller : ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal(
                chooseState(CreateQOLConfigs.server().propellersAllowed.get() ,hasPropeller(stack) ,true,false,false))
                .withStyle(!CreateQOLConfigs.server().propellersAllowed.get()? ChatFormatting.RED : ChatFormatting.YELLOW)));
        if (hasPropeller(stack) && CreateQOLConfigs.server().propellersAllowed.get()) {
            components.add(Component.empty());
            components.add(Component.literal("Fans : ")
                    .withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(chooseState(true,true,isFansEnable(stack),false,true))
                            .withStyle(ChatFormatting.YELLOW)));
            components.add(Component.literal("Hover : ")
                    .withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(chooseState(CreateQOLConfigs.server().hoverAllowed.get() ,true,isHoverEnable(stack),false,true))
                            .withStyle(!CreateQOLConfigs.server().hoverAllowed.get()? ChatFormatting.RED :ChatFormatting.YELLOW)));
        }
        components.add(Component.literal("Elytra : ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal(chooseState(CreateQOLConfigs.server().elytraAllowed.get() ,hasElytra(stack) ,isElytraEnable(stack), true,false))
                .withStyle(!CreateQOLConfigs.server().elytraAllowed.get()? ChatFormatting.RED : ChatFormatting.YELLOW)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }

    private static String chooseState(boolean config,boolean installed, boolean active, boolean activeReplaceInstall, boolean activeOnly){
        if (activeOnly) return  !config ? "Disabled By Config" : (active ? "Enable" : "Disable");
        return !config ? "Disabled By Config" : (installed ? (activeReplaceInstall ? (active ? "Enable" : "Disable") : "Installed") : "Not Installed");
    }


    public static void toggleFans(ItemStack chestplate,Player p) {
        if (!CreateQOLConfigs.server().propellersAllowed.get()){
            p.displayClientMessage(Component.literal("Propellers are disabled by the config").withStyle(ChatFormatting.RED),true);
            return;
        }
        chestplate.set(ModDataComponents.BACKTANK_FANS, chestplate.has(ModDataComponents.BACKTANK_FANS) ? !chestplate.get(ModDataComponents.BACKTANK_FANS) : true);
        boolean fans = isFansEnable(chestplate);
        p.displayClientMessage(Component.literal("Fans : ").append(Component.literal(chooseState(true,true,fans,false,true)).withStyle(fans ? ChatFormatting.GREEN : ChatFormatting.RED)),true);
    }
    public static void toggleHover(ItemStack chestplate,Player p) {
        if (!CreateQOLConfigs.server().propellersAllowed.get()){
            p.displayClientMessage(Component.literal("Propellers are disabled by the config").withStyle(ChatFormatting.RED),true);
            return;
        }
        if (!CreateQOLConfigs.server().hoverAllowed.get()){
            p.displayClientMessage(Component.literal("Hover is disabled by the config").withStyle(ChatFormatting.RED),true);
            return;
        }
        chestplate.set(ModDataComponents.BACKTANK_HOVER, chestplate.has(ModDataComponents.BACKTANK_HOVER) ? !chestplate.get(ModDataComponents.BACKTANK_HOVER) : false);
        boolean hover = isHoverEnable(chestplate);
        p.displayClientMessage(Component.literal("Hover : ").append(Component.literal(chooseState(true,true,hover,false,true)).withStyle(hover ? ChatFormatting.GREEN : ChatFormatting.RED)),true);
    }

    public static void toggleElytra(ItemStack chestplate,Player p) {
        if (!CreateQOLConfigs.server().elytraAllowed.get()){
            p.displayClientMessage(Component.literal("Elytra are disabled by the config").withStyle(ChatFormatting.RED),true);
            return;
        }
        chestplate.set(ModDataComponents.BACKTANK_ELYTRA_STATE, chestplate.has(ModDataComponents.BACKTANK_ELYTRA_STATE) ? !chestplate.get(ModDataComponents.BACKTANK_ELYTRA_STATE) : false);
        boolean elytra = isElytraEnable(chestplate);
        p.displayClientMessage(Component.literal("Elytra : ").append(Component.literal(chooseState(true,true,elytra,false,true)).withStyle(elytra ? ChatFormatting.GREEN : ChatFormatting.RED)),true);
    }
    public static boolean hasPropeller(ItemStack chestplate){
        return chestplate.has(ModDataComponents.BACKTANK_PROPELLERS) && Boolean.TRUE.equals(chestplate.get(ModDataComponents.BACKTANK_PROPELLERS));
    }

    public static boolean hasElytra(ItemStack chestplate){
        return chestplate.has(ModDataComponents.BACKTANK_ELYTRA) && Boolean.TRUE.equals(chestplate.get(ModDataComponents.BACKTANK_ELYTRA));
    }
    public static boolean isElytraEnable(ItemStack chestplate){
        return !chestplate.has(ModDataComponents.BACKTANK_ELYTRA_STATE) ? !isFansEnable(chestplate) : Boolean.TRUE.equals(chestplate.get(ModDataComponents.BACKTANK_ELYTRA_STATE));
    }
    public static boolean isFansEnable(ItemStack chestplate){
        return !chestplate.has(ModDataComponents.BACKTANK_FANS) || Boolean.TRUE.equals(chestplate.get(ModDataComponents.BACKTANK_FANS));
    }
    public static boolean isHoverEnable(ItemStack chestplate){
        return chestplate.has(ModDataComponents.BACKTANK_HOVER) && Boolean.TRUE.equals(chestplate.get(ModDataComponents.BACKTANK_HOVER));
    }
}
