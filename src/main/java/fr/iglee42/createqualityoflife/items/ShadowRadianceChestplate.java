package fr.iglee42.createqualityoflife.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.utils.CommonKeysHandler;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ShadowRadianceChestplate extends BacktankItem.Layered{

    private static final double FANS_ACCELERATION = 0.15D;
    private static final double FANS_SPEED = 0.25D;
    private static final double FANS_HOVER_SPEED = 0.20D;

    public ShadowRadianceChestplate(ArmorMaterial material, Properties properties, ResourceLocation textureLoc, Supplier<BacktankBlockItem> placeable) {
        super(material, properties, textureLoc, placeable);
    }


    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot p_40390_) {
        if (p_40390_.equals(EquipmentSlot.CHEST)){
            ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes = ImmutableMultimap.builder();
            attributes.putAll(super.getDefaultAttributeModifiers(p_40390_));
            attributes.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier("shadow_radiance_"+p_40390_.name().toLowerCase()+"_block",1, AttributeModifier.Operation.ADDITION));
            attributes.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier("shadow_radiance_"+p_40390_.name().toLowerCase()+"_entity",1, AttributeModifier.Operation.ADDITION));
            return attributes.build();
        }
        return super.getDefaultAttributeModifiers(p_40390_);
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

    private static boolean hasPlayerStackInInventory(Player player, Item item) {
        for(int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack currentStack = player.getInventory().getItem(i);
            if (!currentStack.isEmpty() && currentStack.is(item)) {
                return true;
            }
        }

        return false;
    }

    private static int getFirstInventoryIndex(Player player, Item item) {
        for(int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack currentStack = player.getInventory().getItem(i);
            if (!currentStack.isEmpty() && currentStack.is(item)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean offHand) {
        super.inventoryTick(stack, level, entity, slot, offHand);
        if (!(entity instanceof Player player)) return;
        if (player.getItemBySlot(EquipmentSlot.CHEST).equals(stack)){
            boolean second = level.getGameTime() % 20 == 0;
            if (BacktankUtil.getAllWithAir(player).isEmpty()) return;
            if (NBTConstants.getOrDefault(stack,NBTConstants.NBT_EFFECTS,true) && CreateQOLConfigs.server().armorEffects.get())
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 1, false, false));
            if (player.isFallFlying() && isElytraEnable(stack) && !BacktankUtil.getAllWithAir(player).isEmpty() && CreateQOLConfigs.server().elytraAllowed.get() && CreateQOLConfigs.server().elytraBoostAllowed.get() && CommonKeysHandler.isHoldingUp(player))
            {
                if (CreateQOLConfigs.server().useFireworksForBoost.get()) {
                    if (hasPlayerStackInInventory(player,Items.FIREWORK_ROCKET)) {
                        int rocketSlot = getFirstInventoryIndex(player,Items.FIREWORK_ROCKET);
                        ItemStack firework = player.getInventory().getItem(rocketSlot);
                        if (player.getFallFlyingTicks() % CreateQOLConfigs.server().fireworkDuration.get() == 0 || player.getFallFlyingTicks() == 0) {
                            FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(level, firework, player);
                            level.addFreshEntity(fireworkrocketentity);
                            player.getInventory().removeItem(rocketSlot,1);
                        }
                    }
                } else {
                    if ( level.getGameTime() % 10==0 ){
                        Vec3 vec31 = player.getLookAngle();
                        double d0 = 1.5F;
                        double d1 = 0.1;
                        Vec3 vec32 = player.getDeltaMovement();
                        player.setDeltaMovement(vec32.add(vec31.x * d1 + (vec31.x * (double) d0 - vec32.x) * (double) 0.5F, vec31.y * d1 + (vec31.y * (double) d0 - vec32.y) * (double) 0.5F, vec31.z * d1 + (vec31.z * (double) d0 - vec32.z) * (double) 0.5F));
                        if (!player.isCreative()) BacktankUtil.consumeAir(player, stack, 1);
                    }
                }
            }
            if (player.isCreative() || player.isSpectator()) return;
            if (isFansEnable(stack) && !BacktankUtil.getAllWithAir(player).isEmpty() && hasPropeller(stack) && CreateQOLConfigs.server().propellerAllowed.get()) {
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
    public void appendHoverText(ItemStack stack, @Nullable Level p_41422_, List<Component> components, TooltipFlag p_41424_) {
        components.add(Component.literal("Air : ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal(String.valueOf(BacktankUtil.getAir(stack)))
                        .withStyle(ChatFormatting.YELLOW))
                .append(Component.literal("/"+BacktankUtil.maxAir(stack))
                        .withStyle(ChatFormatting.GOLD)));
        components.add(Component.literal("Propeller : ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal(
                chooseState(CreateQOLConfigs.server().propellerAllowed.get() ,hasPropeller(stack) ,true,false,false))
                .withStyle(!CreateQOLConfigs.server().propellerAllowed.get()? ChatFormatting.RED : ChatFormatting.YELLOW)));
        if (hasPropeller(stack) && CreateQOLConfigs.server().propellerAllowed.get()) {
            components.add(Component.empty());
            components.add(Component.literal("Fan : ")
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
        if (!CreateQOLConfigs.server().propellerAllowed.get()){
            p.displayClientMessage(Component.literal("Propeller is disabled by the config").withStyle(ChatFormatting.RED),true);
            return;
        }
        if (isElytraEnable(chestplate)){
            p.displayClientMessage(Component.literal("Fan can't be enabled if the elytra are enabled").withStyle(ChatFormatting.RED),true);
            return;
        }
        chestplate.getOrCreateTag().putBoolean(NBTConstants.NBT_FANS, chestplate.getOrCreateTag().contains(NBTConstants.NBT_FANS) && !chestplate.getOrCreateTag().getBoolean(NBTConstants.NBT_FANS));
        boolean fans = chestplate.getOrCreateTag().getBoolean(NBTConstants.NBT_FANS);
        p.displayClientMessage(Component.literal("Fan : ").append(Component.literal(chooseState(true,true,fans,false,true)).withStyle(fans ? ChatFormatting.GREEN : ChatFormatting.RED)),true);
    }
    public static void toggleHover(ItemStack chestplate,Player p) {
        if (!CreateQOLConfigs.server().propellerAllowed.get()){
            p.displayClientMessage(Component.literal("Propeller is disabled by the config").withStyle(ChatFormatting.RED),true);
            return;
        }
        if (!CreateQOLConfigs.server().hoverAllowed.get()){
            p.displayClientMessage(Component.literal("Hover is disabled by the config").withStyle(ChatFormatting.RED),true);
            return;
        }
        chestplate.getOrCreateTag().putBoolean(NBTConstants.NBT_HOVER, chestplate.getOrCreateTag().contains(NBTConstants.NBT_HOVER) && !chestplate.getOrCreateTag().getBoolean(NBTConstants.NBT_HOVER));
        boolean hover = chestplate.getOrCreateTag().getBoolean(NBTConstants.NBT_HOVER);
        p.displayClientMessage(Component.literal("Hover : ").append(Component.literal(chooseState(true,true,hover,false,true)).withStyle(hover ? ChatFormatting.GREEN : ChatFormatting.RED)),true);
    }

    public static void toggleElytra(ItemStack chestplate,Player p) {
        if (!CreateQOLConfigs.server().elytraAllowed.get()){
            p.displayClientMessage(Component.literal("Elytra are disabled by the config").withStyle(ChatFormatting.RED),true);
            return;
        }
        if (isFansEnable(chestplate)){
            p.displayClientMessage(Component.literal("Elytra can't be enabled if the fans is enabled").withStyle(ChatFormatting.RED),true);
            return;
        }
        chestplate.getOrCreateTag().putBoolean(NBTConstants.NBT_ELYTRA_STATE, chestplate.getOrCreateTag().contains(NBTConstants.NBT_ELYTRA_STATE) && !chestplate.getOrCreateTag().getBoolean(NBTConstants.NBT_ELYTRA_STATE));
        boolean elytra = isElytraEnable(chestplate);
        p.displayClientMessage(Component.literal("Elytra : ").append(Component.literal(chooseState(true,true,elytra,false,true)).withStyle(elytra ? ChatFormatting.GREEN : ChatFormatting.RED)),true);
    }
    public static boolean hasPropeller(ItemStack chestplate){
        return chestplate.getOrCreateTag().contains(NBTConstants.NBT_PROPELLERS) && chestplate.getOrCreateTag().getBoolean(NBTConstants.NBT_PROPELLERS);
    }

    public static boolean hasElytra(ItemStack chestplate){
        return chestplate.getOrCreateTag().contains(NBTConstants.NBT_ELYTRA) && chestplate.getOrCreateTag().getBoolean(NBTConstants.NBT_ELYTRA);
    }
    public static boolean isElytraEnable(ItemStack chestplate){
        return !chestplate.getOrCreateTag().contains(NBTConstants.NBT_ELYTRA_STATE) ? !isFansEnable(chestplate) : chestplate.getOrCreateTag().getBoolean(NBTConstants.NBT_ELYTRA_STATE);
    }
    public static boolean isFansEnable(ItemStack chestplate){
        return !chestplate.getOrCreateTag().contains(NBTConstants.NBT_FANS) || chestplate.getOrCreateTag().getBoolean(NBTConstants.NBT_FANS);
    }
    public static boolean isHoverEnable(ItemStack chestplate){
        return chestplate.getOrCreateTag().contains(NBTConstants.NBT_HOVER) && chestplate.getOrCreateTag().getBoolean(NBTConstants.NBT_HOVER);
    }
}
