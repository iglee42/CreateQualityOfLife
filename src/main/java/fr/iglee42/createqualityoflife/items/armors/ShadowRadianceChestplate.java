package fr.iglee42.createqualityoflife.items.armors;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.BooleanEntry;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLArmorMaterials;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.utils.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ShadowRadianceChestplate extends BacktankItem.Layered implements QOLConfigurableItem {

    private static final double FANS_ACCELERATION = 0.15D;
    private static final double FANS_SPEED = 0.25D;
    private static final double FANS_HOVER_SPEED = 0.20D;

    public ShadowRadianceChestplate(Properties properties, Supplier<BacktankBlockItem> placeable) {
        super(QOLArmorMaterials.SHADOW_RADIANCE, properties, CreateQOL.asResource("shadow_radiance"), placeable);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        ResourceLocation resourcelocation = ResourceLocation.withDefaultNamespace("armor." + type.getName());
        return super.getDefaultAttributeModifiers()
                .withModifierAdded(Attributes.BLOCK_INTERACTION_RANGE,new AttributeModifier(resourcelocation,1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()))
                .withModifierAdded(Attributes.ENTITY_INTERACTION_RANGE,new AttributeModifier(resourcelocation,1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
    }

    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return hasElytra(stack)
                && isElytraEnable(stack)
                && (!hasPropeller(stack) || !isFansEnable(stack))
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
        invTick(stack, level, entity, slot, offHand);
    }

    @Override
    public void tick(ItemStack stack, Level level, Player player, int slot, boolean offHand) {
        boolean second = level.getGameTime() % 20 == 0;
        if (player.isFallFlying() && isElytraEnable(stack) && !BacktankUtil.getAllWithAir(player).isEmpty() && CreateQOLConfigs.server().elytraAllowed.get() && CreateQOLConfigs.server().elytraBoostAllowed.get() &&  CommonKeysHandler.isHoldingUp(player))
        {
            if (CreateQOLConfigs.server().useFireworksForBoost.get()) {
                if (hasPlayerStackInInventory(player,Items.FIREWORK_ROCKET)) {
                    int rocketSlot = getFirstInventoryIndex(player,Items.FIREWORK_ROCKET);
                    ItemStack firework = player.getInventory().getItem(rocketSlot);
                    if ( (stack.getOrDefault(QOLDataComponents.BACKTANK_BOOST_ON_LAUNCH, false) || player.getFallFlyingTicks() >20) &&
                            player.getFallFlyingTicks() % CreateQOLConfigs.server().fireworkDuration.get() == 0 ||
                            (stack.getOrDefault(QOLDataComponents.BACKTANK_BOOST_ON_LAUNCH, false) && player.getFallFlyingTicks() == 0)) {
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
                QOLConfigurableItem.chooseState(CreateQOLConfigs.server().propellerAllowed.get() ,hasPropeller(stack) ,true,false,false))
                .withStyle(!CreateQOLConfigs.server().propellerAllowed.get()? ChatFormatting.RED : ChatFormatting.YELLOW)));
        if (hasPropeller(stack) && CreateQOLConfigs.server().propellerAllowed.get()) {
            components.add(Component.empty());
            components.add(Component.literal("Fan : ")
                    .withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(QOLConfigurableItem.chooseState(true,true,isFansEnable(stack),false,true))
                            .withStyle(ChatFormatting.YELLOW)));
            components.add(Component.literal("Hover : ")
                    .withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().hoverAllowed.get() ,true,isHoverEnable(stack),false,true))
                            .withStyle(!CreateQOLConfigs.server().hoverAllowed.get()? ChatFormatting.RED :ChatFormatting.YELLOW)));
        }
        components.add(Component.literal("Elytra : ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().elytraAllowed.get() ,hasElytra(stack) ,isElytraEnable(stack), true,false))
                .withStyle(!CreateQOLConfigs.server().elytraAllowed.get()? ChatFormatting.RED : ChatFormatting.YELLOW)));
        components.add(Component.literal("Dash : ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal(
                                QOLConfigurableItem.chooseState(CreateQOLConfigs.server().dashAllowed.get(), true, stack.getOrDefault(QOLDataComponents.DASH,true), false, true))
                        .withStyle(!CreateQOLConfigs.server().dashAllowed.get() ? ChatFormatting.RED : ChatFormatting.YELLOW)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
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
        chestplate.set(QOLDataComponents.BACKTANK_FANS, !chestplate.has(QOLDataComponents.BACKTANK_FANS) || Boolean.FALSE.equals(chestplate.get(QOLDataComponents.BACKTANK_FANS)));
        boolean fans = isFansEnable(chestplate);
        p.displayClientMessage(Component.literal("Fan : ").append(Component.literal(QOLConfigurableItem.chooseState(true,true,fans,false,true)).withStyle(fans ? ChatFormatting.GREEN : ChatFormatting.RED)),true);
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
        chestplate.set(QOLDataComponents.BACKTANK_HOVER, chestplate.has(QOLDataComponents.BACKTANK_HOVER) && Boolean.FALSE.equals(chestplate.get(QOLDataComponents.BACKTANK_HOVER)));
        boolean hover = isHoverEnable(chestplate);
        p.displayClientMessage(Component.literal("Hover : ").append(Component.literal(QOLConfigurableItem.chooseState(true,true,hover,false,true)).withStyle(hover ? ChatFormatting.GREEN : ChatFormatting.RED)),true);
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
        chestplate.set(QOLDataComponents.BACKTANK_ELYTRA_STATE, chestplate.has(QOLDataComponents.BACKTANK_ELYTRA_STATE) && Boolean.FALSE.equals(chestplate.get(QOLDataComponents.BACKTANK_ELYTRA_STATE)));
        boolean elytra = isElytraEnable(chestplate);
        p.displayClientMessage(Component.literal("Elytra : ").append(Component.literal(QOLConfigurableItem.chooseState(true,true,elytra,false,true)).withStyle(elytra ? ChatFormatting.GREEN : ChatFormatting.RED)),true);
    }
    public static boolean hasPropeller(ItemStack chestplate){
        return chestplate.has(QOLDataComponents.BACKTANK_PROPELLERS) && Boolean.TRUE.equals(chestplate.get(QOLDataComponents.BACKTANK_PROPELLERS));
    }

    public static boolean hasElytra(ItemStack chestplate){
        return chestplate.has(QOLDataComponents.BACKTANK_ELYTRA) && Boolean.TRUE.equals(chestplate.get(QOLDataComponents.BACKTANK_ELYTRA));
    }
    public static boolean isElytraEnable(ItemStack chestplate){
        return !chestplate.has(QOLDataComponents.BACKTANK_ELYTRA_STATE) ? !isFansEnable(chestplate) : Boolean.TRUE.equals(chestplate.get(QOLDataComponents.BACKTANK_ELYTRA_STATE));
    }
    public static boolean isFansEnable(ItemStack chestplate){
        if (!hasPropeller(chestplate)) return false;
        return !chestplate.has(QOLDataComponents.BACKTANK_FANS) || Boolean.TRUE.equals(chestplate.get(QOLDataComponents.BACKTANK_FANS));
    }
    public static boolean isHoverEnable(ItemStack chestplate){
        if (!hasPropeller(chestplate)) return false;
        return chestplate.has(QOLDataComponents.BACKTANK_HOVER) && Boolean.TRUE.equals(chestplate.get(QOLDataComponents.BACKTANK_HOVER));
    }

    @Override
    public QOLConfigurableItem.Type type() {
        return QOLConfigurableItem.Type.ARMOR;
    }

    @Override
    public List<ArmorRenderType> renderTypes(ItemStack stack) {
        return Arrays.stream(ArmorRenderType.values()).toList();
    }

    @Override
    public Holder<MobEffect> providedEffect(ItemStack stack) {
        return stack.getOrDefault(QOLDataComponents.EFFECT, ShadowRadianceEffects.REGENERATION).getEffectHolder();
    }

    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(Configuration.ofBool("Enable Custom Arms",
                stack.getOrDefault(QOLDataComponents.BACKTANK_ARMS,true),
                QOLDataComponents.BACKTANK_ARMS,
                Arrays.asList("Should the player's arms be replaced with the armor in first person"),
                (e,oe)->true));
        list.add(Configuration.ofBool("Enable Dash",stack.getOrDefault(QOLDataComponents.DASH,true),QOLDataComponents.DASH,
                List.of("Should the player dash when pressing "+ KeyBindManager.DASH_KEY.getTranslatedKeyMessage().getString()),
                (o,oe)->CreateQOLConfigs.server().dashAllowed.get()));
        if (hasPropeller(stack)){
            list.add(Configuration.ofBool("Enable Fan",
                    stack.getOrDefault(QOLDataComponents.BACKTANK_FANS,true),
                    QOLDataComponents.BACKTANK_FANS,
                    Arrays.asList("Activate the propeller on the backtank", "_Can't be enabled if the elytra are enabled_"),
                    (entry,oe)-> {
                        boolean flag = oe.stream()
                                .noneMatch(e->e instanceof BooleanEntry oEntry && oEntry.getComponent().equals(QOLDataComponents.BACKTANK_ELYTRA_STATE) && oEntry.getValue());
                        return CreateQOLConfigs.server().propellerAllowed.get() && flag;
                    }));
            list.add(Configuration.ofBool("Enable Hover",
                    stack.getOrDefault(QOLDataComponents.BACKTANK_HOVER,false),
                    QOLDataComponents.BACKTANK_HOVER,
                    Arrays.asList("Activate the hover mode"),
                    (e,oe)->CreateQOLConfigs.server().propellerAllowed.get() && CreateQOLConfigs.server().hoverAllowed.get()));
        }

        if (hasElytra(stack)){
            list.add(Configuration.ofBool("Enable Elytra",
                    stack.getOrDefault(QOLDataComponents.BACKTANK_ELYTRA_STATE,false),
                    QOLDataComponents.BACKTANK_ELYTRA_STATE,
                    Arrays.asList("Activate the elytra on the backtank", "_Can't be enabled if the fan is enabled_"),
                    (entry,oe)-> {
                        boolean flag = oe.stream()
                                .noneMatch(e->e instanceof BooleanEntry oEntry && oEntry.getComponent().equals(QOLDataComponents.BACKTANK_FANS) && oEntry.getValue());
                        return CreateQOLConfigs.server().elytraAllowed.get() && flag;
                    }));

            if (CreateQOLConfigs.server().elytraBoostAllowed.get()){
                list.add(Configuration.ofBool("Boost on Launch",
                        stack.getOrDefault(QOLDataComponents.BACKTANK_BOOST_ON_LAUNCH,false),
                        QOLDataComponents.BACKTANK_BOOST_ON_LAUNCH,
                        Arrays.asList("Define if you should be boost when you start flying with elytra","Works only with fireworks","(The air boost doesn't have the same behaviour)"),
                        (e,oe)->CreateQOLConfigs.server().elytraAllowed.get() && CreateQOLConfigs.server().elytraBoostAllowed.get() && CreateQOLConfigs.server().useFireworksForBoost.get()));
            }
        }

        list.add(new Configuration<>("Preferred Render", stack.getOrDefault(QOLDataComponents.PREFERRED_RENDER, PreferredRender.BOTH),QOLDataComponents.PREFERRED_RENDER,
                Configuration.ConfigType.ENUM,Arrays.asList("Define how the additions should be rendered.",
                        "\"Elytra\" renders only the elytra",
                        "\"Backtank\" renders only the backtank"),(direction,entry)->{

            PreferredRender e = (PreferredRender) entry.getValue();
            PreferredRender[] options = Arrays.stream(PreferredRender.values()).toArray(PreferredRender[]::new);
            e = options[Math.floorMod(e.ordinal() + direction, options.length)];
            return e;
        },(e,oe)->true));

        ShadowRadianceEffects[] valids = Arrays.stream(ShadowRadianceEffects.values()).filter(ef->ef.isValidForItem(stack)).toArray(ShadowRadianceEffects[]::new);

        list.add(new Configuration<>("Effect", stack.getOrDefault(QOLDataComponents.EFFECT, ShadowRadianceEffects.REGENERATION),QOLDataComponents.EFFECT,
                Configuration.ConfigType.ENUM,Arrays.asList("Define which mob effect should be provided.",
                "For this item, there is " + Component.translatable(valids[0].getEffectHolder().value().getDescriptionId()).getString() + " and " + Component.translatable(valids[1].getEffectHolder().value().getDescriptionId()).getString()),(direction,entry)->{

            ShadowRadianceEffects e = (ShadowRadianceEffects) entry.getValue();
            ShadowRadianceEffects[] options = Arrays.stream(ShadowRadianceEffects.values()).filter(ef->ef.isValidForItem(stack)).toArray(ShadowRadianceEffects[]::new);
            e = options[Math.floorMod(e.ordinal() + direction, options.length)];
            return e;
        },(e,oe)->true));

    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return super.isBarVisible(stack) && BacktankUtil.getAir(stack) < BacktankUtil.maxAir(stack);
    }
}
