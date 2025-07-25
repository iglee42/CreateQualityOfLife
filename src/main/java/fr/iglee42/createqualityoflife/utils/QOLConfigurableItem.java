package fr.iglee42.createqualityoflife.utils;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.BooleanEntry;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.EnumEntry;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.ValueEntry;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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
        return stack.getOrDefault(QOLDataComponents.ARMOR_EFFECT, true) && CreateQOLConfigs.server().armorEffects.get();
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
            list.add(Configuration.ofBool("Apply Potion Effect",stack.getOrDefault(QOLDataComponents.ARMOR_EFFECT,true),QOLDataComponents.ARMOR_EFFECT,Arrays.asList(
                    "Enable the potion effect granted by the item",
                    "For this item, the effect is " + Component.translatable(providedEffect(stack).value().getDescriptionId()).getString()),(e,oE)-> CreateQOLConfigs.server().armorEffects.get()));
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

    static String chooseState(boolean config, boolean installed, boolean active, boolean activeReplaceInstall, boolean activeOnly){
        if (activeOnly) return  !config ? "Disabled By Config" : (active ? "Enable" : "Disable");
        return !config ? "Disabled By Config" : (installed ? (activeReplaceInstall ? (active ? "Enable" : "Disable") : "Installed") : "Not Installed");
    }

}
