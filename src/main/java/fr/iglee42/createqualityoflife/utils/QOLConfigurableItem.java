package fr.iglee42.createqualityoflife.utils;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.BooleanEntry;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.EnumEntry;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.ValueEntry;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import java.io.InvalidClassException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface QOLConfigurableItem {


    default void invTick(ItemStack stack, Level level, Entity entity, int slot, boolean offHand) {
        if (!(entity instanceof Player player)) return;
        if (doesEffectRequiresAir() && BacktankUtil.getAllWithAir(player).isEmpty()) return;
        if (type().equals(Type.ARMOR)) {
            if (!(stack.getItem() instanceof ArmorItem it)) throw new IllegalArgumentException(BuiltInRegistries.ITEM.getKey(stack.getItem()) + " is defined with Type.ARMOR even if it isn't a ArmorItem");
            ArmorItem.Type type = it.getType();
            if (player.getItemBySlot(type.getSlot()).equals(stack)){
                if (hasEffectEnable(stack) && providedEffect(stack) != null && providedEffect(stack).value() != null){
                    applyEffect(player,providedEffect(stack),effectTime(stack), effectLevel(stack));
                    tick(stack, level, player, slot, offHand);
                }
            }
        } else if (type().equals(Type.ITEM)){
            if (player.getItemInHand(InteractionHand.MAIN_HAND).equals(stack) || player.getItemInHand(InteractionHand.OFF_HAND).equals(stack)) {
                if (hasEffectEnable(stack) && providedEffect(stack) != null && providedEffect(stack).value() != null){
                    applyEffect(player,providedEffect(stack),effectTime(stack), effectLevel(stack));
                    tick(stack, level, player, slot, offHand);
                }
            }
        }
    }

    default void tick(ItemStack stack, Level level, Player player, int slot, boolean offHand){};

    static boolean hasEffectEnable(ItemStack stack){
        return stack.getOrDefault(QOLDataComponents.ARMOR_EFFECT, true) && CreateQOLConfigs.server().equipments.armors.armorEffects.get();
    }

    default String effectAdditionInfos(ItemStack stack) {
        return "";
    }

    //Level 1 is 0
    default int effectLevel(ItemStack stack){
        return 1;
    }

    //In Ticks
    default int effectTime(ItemStack stack){
        return 20;
    }

    static void applyEffect(Player player,Holder<MobEffect> effect,int time, int level){
        if (effect == null || effect.value() == null) throw new RuntimeException(new IllegalAccessException("Trying to give a null effect to a player"));
        player.addEffect(new MobEffectInstance(effect, time, level, false, false));
    }

    default List<Configuration<?>> getConfigurations(ItemStack stack) throws InvalidClassException {
        List<Configuration<?>> list = new ArrayList<>();
        if (providedEffect(stack) != null && providedEffect(stack).value() != null){
            List<String> comments = new ArrayList<>(Arrays.asList(
                    "Enable the potion effect granted by the item",
                    "For this item, the effect is " + Component.translatable(providedEffect(stack).value().getDescriptionId()).getString()));
            if (!effectAdditionInfos(stack).isEmpty()) comments.add(effectAdditionInfos(stack));
            list.add(Configuration.ofBool("Apply Potion Effect",stack.getOrDefault(QOLDataComponents.ARMOR_EFFECT,true),QOLDataComponents.ARMOR_EFFECT,comments,(e,oE)-> CreateQOLConfigs.server().equipments.armors.armorEffects.get()));
        }
        if (type().equals(Type.ARMOR)){
            if ( renderTypes(stack) == null || renderTypes(stack).size() < 2) throw new InvalidClassException("Configurable item with the armor type must declare at least 2 types of render types");
            list.add(new Configuration<>("Render Type",stack.getOrDefault(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL),QOLDataComponents.ARMOR_RENDER_TYPE, Configuration.ConfigType.ENUM,Arrays.asList("Define how the armor piece should be rendered.",
                    "\"Armor only\" renders only the armor",
                    "\"Addition only\" renders only the additions (E.g. Backtank, Goggles)"),(direction,entry)->{
                ArmorRenderType e = (ArmorRenderType) entry.getValue();
                ArmorRenderType[] options = Arrays.stream(ArmorRenderType.values()).filter(it->renderTypes(stack).contains(it)).toArray(ArmorRenderType[]::new);
                e = options[Math.floorMod(e.ordinal() + direction, options.length)];
                return e;
            },(e,oE)->true));
        }
        if (reachType(stack) != ReachType.NONE){
            boolean hasBlock = reachType(stack).attributes.contains(Attributes.BLOCK_INTERACTION_RANGE);
            boolean hasEntity = reachType(stack).attributes.contains(Attributes.ENTITY_INTERACTION_RANGE);
            list.add(Configuration.ofBool("Reach",stack.getOrDefault(QOLDataComponents.REACH, true),QOLDataComponents.REACH, Arrays.asList("Define if this item should give reach.",
                    "This item multiplies your reach on " + (hasBlock ? "blocks" : "") + (hasBlock && hasEntity ? " and " : "") + (hasEntity ? "entities" : "") + " by " + (reachModifier(stack) + 1)),(e,oE)->CreateQOLConfigs.server().equipments.tools.reach.get()));
        }
        addConfigurations(list,stack);
        return list;
    }

    default void addConfigurations(List<Configuration<?>> list, ItemStack stack){}

    default Holder<MobEffect> providedEffect(ItemStack stack){
        return null;
    }

    Type type();

    default boolean doesEffectRequiresAir() { return true; }

    default List<ArmorRenderType> renderTypes(ItemStack stack) { return null; }

    default boolean appliesReach(ItemStack stack) { return CreateQOLConfigs.server().equipments.tools.reach.get() && stack.getOrDefault(QOLDataComponents.REACH,true) && reachType(stack) != ReachType.NONE; }

    default ReachType reachType(ItemStack stack) { return ReachType.NONE; }

    default double reachModifier(ItemStack stack) { return 0.5;}

    default Map<Holder<Attribute>, Map.Entry<Double, AttributeModifier.Operation>> getAppliedAttributes(ItemStack stack){
        Map<Holder<Attribute>, Map.Entry<Double, AttributeModifier.Operation>> attributes = new HashMap<>();
        if (appliesReach(stack)) reachType(stack).getAttributes().forEach(a-> attributes.put(a,new AbstractMap.SimpleEntry<>(reachModifier(stack),AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
        return attributes;
    }

    static void modifyAttributes(ItemAttributeModifierEvent event){
        if (!(event.getItemStack().getItem() instanceof QOLConfigurableItem it)) return;
        Type type = it.type();
        if (type == Type.ARMOR) {
            if (!(event.getItemStack().getItem() instanceof ArmorItem ait)) throw new IllegalArgumentException(BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem()) + " is defined with Type.ARMOR even if it isn't a ArmorItem");
            ArmorItem.Type aType = ait.getType();
            ResourceLocation resourcelocation = CreateQOL.asResource("armors."+ait.getDescriptionId().split("\\.")[2]);
            it.getAppliedAttributes(event.getItemStack()).forEach((a, doe) -> event.addModifier(a,
                    new AttributeModifier(resourcelocation,doe.getKey(),doe.getValue()), EquipmentSlotGroup.bySlot(aType.getSlot())));
        } else if (type == Type.ITEM) {
            TieredItem item = (TieredItem) event.getItemStack().getItem();
            ResourceLocation resourcelocation = CreateQOL.asResource("tools."+item.getDescriptionId().split("\\.")[2]);
            it.getAppliedAttributes(event.getItemStack()).forEach((a, doe) -> event.addModifier(a,
                    new AttributeModifier(resourcelocation,doe.getKey(),doe.getValue()), EquipmentSlotGroup.HAND));

        }
    }

    enum ReachType {
        BLOCK(Attributes.BLOCK_INTERACTION_RANGE),
        ENTITY(Attributes.ENTITY_INTERACTION_RANGE),
        NONE(),
        BOTH(Attributes.BLOCK_INTERACTION_RANGE,Attributes.ENTITY_INTERACTION_RANGE);

        private final List<Holder<Attribute>> attributes;

        @SafeVarargs
        ReachType(Holder<Attribute>... attributes) {
            this.attributes = Arrays.stream(attributes).toList();
        }

        public List<Holder<Attribute>> getAttributes() {
            return attributes;
        }
    }

    enum Type{
        ARMOR,
        ITEM;
    }

    record Configuration<T>(String label, T defaultValue, DataComponentType<T> component, ConfigType type, List<String> description, BiFunction<Integer,ValueEntry<?>,T> cycleValue,BiFunction<ValueEntry<?>,List<ValueEntry<?>>,Boolean> enable){
        public static Configuration<Boolean> ofBool(String label, Boolean defaultValue, DataComponentType<Boolean> component, List<String> description,BiFunction<ValueEntry<?>,List<ValueEntry<?>>,Boolean> enable){
            return new Configuration<>(label, defaultValue ,component, ConfigType.BOOLEAN, description,null, enable);
        }

        public enum ConfigType{
            BOOLEAN((config)->new BooleanEntry(config.label, (Boolean) config.defaultValue, (DataComponentType<Boolean>) config.component, config.description.toArray(new String[]{}),config.enable)),
            ENUM((config)->new EnumEntry(config.label, (Enum<?>) config.defaultValue, config.component,  config.description.toArray(new String[]{}),config.enable){
                @Override
                protected void cycleValue(int direction) {
                    if (config.cycleValue != null) {
                        Enum<?> value = (Enum<?>) config.cycleValue.apply(direction,this);
                        setValue(value);
                        bumpCog(direction * 15f);
                    } else super.cycleValue(direction);
                }
            })
            ;
            private Function<Configuration<?>,ValueEntry<?>> widget;

            ConfigType(Function<Configuration<?>, ValueEntry<?>> widget) {
                this.widget = widget;
            }

            public <B> ValueEntry<B> getWidget(Configuration<B> config) {
                return (ValueEntry<B>) widget.apply(config);
            }
        }
    }

    static Component chooseState(boolean config, boolean installed, boolean active, boolean activeReplaceInstall, boolean activeOnly){
        if (activeOnly) return  Component.literal(!config ? "Disabled By Config" : (active ? "Enable" : "Disable")).withStyle(!config ? ChatFormatting.RED : ChatFormatting.YELLOW);
        return  Component.literal(!config ? "Disabled By Config" : (installed ? (activeReplaceInstall ? (active ? "Enable" : "Disable") : "Installed") : "Not Installed")).withStyle(!config ? ChatFormatting.RED : ChatFormatting.YELLOW);
    }

    static Component cooldownState(boolean config, boolean active, int cooldown){
       return Component.literal(!config ? "Disabled By Config" : (active ? (cooldown > 0 ? cooldown / 20 + "s" :  "Enable") : "Disable")).withStyle(!config ? ChatFormatting.RED : ChatFormatting.YELLOW);
    }

}
