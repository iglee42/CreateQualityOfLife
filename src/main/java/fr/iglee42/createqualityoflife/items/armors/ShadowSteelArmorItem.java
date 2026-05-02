package fr.iglee42.createqualityoflife.items.armors;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLArmorMaterials;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import fr.iglee42.createqualityoflife.utils.ShadowRadianceEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ShadowSteelArmorItem extends BaseArmorItem implements QOLConfigurableItem {
    public ShadowSteelArmorItem(ArmorItem.Type type, Properties properties) {
        super(QOLArmorMaterials.SHADOW_STEEL, type, properties, CreateQOL.asResource("shadow_steel"));
    }
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean offHand) {
        super.inventoryTick(stack, level, entity, slot, offHand);
        invTick(stack, level, entity, slot, offHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext p_41422_, List<Component> components, TooltipFlag p_41424_) {
        if (!stack.getOrDefault(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT).isEnable(ItemTooltips.Tooltip.OPTIONS)) return;
        if (getType().equals(ArmorItem.Type.BOOTS)) {
            components.add(Component.translatable("createqol.ability.armor.toggle_message", Component.translatable("createqol.ability.armor.diving").getString())
                    .withStyle(ChatFormatting.GOLD)
                    .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.armors.bootsDiving.get(),
                            true, stack.getOrDefault(QOLDataComponents.BOOTS_DIVING, false), false, true)));
            components.add(Component.translatable("createqol.ability.armor.toggle_message", Component.translatable("createqol.ability.armor.lava_walking").getString())
                    .withStyle(ChatFormatting.GOLD)
                    .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.armors.bootsLavaWalking.get(),
                            true, stack.getOrDefault(QOLDataComponents.BOOTS_LAVA, true), false, true)));
        } else if (getType().equals(ArmorItem.Type.LEGGINGS)){
            components.add(Component.translatable("createqol.ability.armor.toggle_message", Component.translatable("createqol.ability.armor.void_walk").getString())
                    .withStyle(ChatFormatting.GOLD)
                    .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.armors.voidWalking.get(),
                            true, stack.getOrDefault(QOLDataComponents.VOID_WALK, true), false, true)));
        }
        components.add(Component.translatable("createqol.ability.armor.toggle_message", Component.translatable("createqol.ability.armor.use_air").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.armors.use_air.get(),
                        true, stack.getOrDefault(QOLDataComponents.USE_AIR, false), false, true)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }


    @Override
    public QOLConfigurableItem.Type type() {
        return QOLConfigurableItem.Type.ARMOR;
    }

    @Override
    public Holder<MobEffect> providedEffect(ItemStack stack) {
        if (!(stack.getItem() instanceof ArmorItem it)) return QOLConfigurableItem.super.providedEffect(stack);
        return getType().equals(ArmorItem.Type.BOOTS) ? MobEffects.JUMP : (getType().equals(ArmorItem.Type.LEGGINGS)  ? MobEffects.MOVEMENT_SPEED : QOLConfigurableItem.super.providedEffect(stack));
    }

    @Override
    public List<ArmorRenderType> renderTypes(ItemStack stack) {
        return Arrays.asList(ArmorRenderType.ALL,ArmorRenderType.NONE);
    }

    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        if (getType().equals(ArmorItem.Type.BOOTS)){
            list.add(Configuration.ofBool("Enable Diving",
                    stack.getOrDefault(QOLDataComponents.BOOTS_DIVING,false),
                    QOLDataComponents.BOOTS_DIVING,
                    List.of("Enable diving, which makes the player descends quicker in liquids"),
                    (e,oE)-> CreateQOLConfigs.server().equipments.armors.bootsDiving.get()
            ));

            list.add(Configuration.ofBool("Enable Lava Walking",
                    stack.getOrDefault(QOLDataComponents.BOOTS_LAVA,true),
                    QOLDataComponents.BOOTS_LAVA,
                    List.of("Enable walking under lava, which makes the player walks normally under lava"),
                    (e,oE)-> CreateQOLConfigs.server().equipments.armors.bootsLavaWalking.get()
            ));
        } else if (getType() == ArmorItem.Type.LEGGINGS){
            list.add(Configuration.ofBool("Void Walking",
                    stack.getOrDefault(QOLDataComponents.VOID_WALK,true),
                    QOLDataComponents.VOID_WALK,
                    List.of("Enable walking on void"),
                    (e,oE)-> CreateQOLConfigs.server().equipments.armors.voidWalking.get()
            ));
        }
        list.add(Configuration.ofBool("Use Air",stack.getOrDefault(QOLDataComponents.USE_AIR,false),QOLDataComponents.USE_AIR,
                List.of("Define if air should be used (if available) from the backtank instead of the armor's durability."),(e,oe)->CreateQOLConfigs.server().equipments.armors.use_air.get()));

    }
    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return stack.getOrDefault(QOLDataComponents.USE_AIR, false) && BacktankUtil.canAbsorbDamage(entity, getMaxDamage(stack)) ? 0 : super.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return BacktankUtil.isBarVisible(stack, getMaxDamage(stack));
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return BacktankUtil.getBarWidth(stack, getMaxDamage(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BacktankUtil.getBarColor(stack, getMaxDamage(stack));
    }
}
