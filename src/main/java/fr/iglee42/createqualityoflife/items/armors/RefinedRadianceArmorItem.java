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
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RefinedRadianceArmorItem extends BaseArmorItem implements QOLConfigurableItem {
    public RefinedRadianceArmorItem(ArmorItem.Type type, Properties properties) {
        super(QOLArmorMaterials.REFINED_RADIANCE, type, properties, CreateQOL.asResource("refined_radiance"));
    }
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean offHand) {
        super.inventoryTick(stack, level, entity, slot, offHand);
        invTick(stack, level, entity, slot, offHand);
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext p_41422_, @NotNull List<Component> components, @NotNull TooltipFlag p_41424_) {
        if (!stack.getOrDefault(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT).isEnable(ItemTooltips.Tooltip.OPTIONS)) return;
        if (getType().equals(ArmorItem.Type.BOOTS)) {
            components.add(Component.translatable("createqol.ability.armor.diving")
                    .withStyle(ChatFormatting.GOLD)
                    .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.armors.bootsDiving.get(),
                            true, stack.getOrDefault(QOLDataComponents.BOOTS_DIVING, false), false, true)));
            components.add(Component.translatable("createqol.ability.armor.belt_blocking")
                    .withStyle(ChatFormatting.GOLD)
                    .append(QOLConfigurableItem.chooseState(true,
                            true, stack.getOrDefault(QOLDataComponents.BOOTS_BELT, true), false, true)));
        } else if (getType().equals(ArmorItem.Type.LEGGINGS)){
            components.add(Component.translatable("createqol.ability.armor.step_height")
                    .withStyle(ChatFormatting.GOLD)
                    .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.armors.stepHeight.get(),
                            true, stack.getOrDefault(QOLDataComponents.STEP_HEIGHT, true), false, true)));
        }
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }
    @Override
    public QOLConfigurableItem.Type type() {
        return QOLConfigurableItem.Type.ARMOR;
    }

    @Override
    public Holder<MobEffect> providedEffect(ItemStack stack) {
        if (!(stack.getItem() instanceof ArmorItem it)) return QOLConfigurableItem.super.providedEffect(stack);
        return getType().equals(ArmorItem.Type.BOOTS) ? MobEffects.DIG_SPEED : (getType().equals(ArmorItem.Type.LEGGINGS)  ? MobEffects.SATURATION : QOLConfigurableItem.super.providedEffect(stack));
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

            list.add(Configuration.ofBool("Enable Belt Blocking",
                    stack.getOrDefault(QOLDataComponents.BOOTS_BELT,true),
                    QOLDataComponents.BOOTS_BELT,
                    List.of("You won't be pushed by belt if enabled"),
                    (e,oE)->true
            ));
        } else if (getType().equals(ArmorItem.Type.LEGGINGS)) {
            list.add(Configuration.ofBool("Enable Step Height",
                    stack.getOrDefault(QOLDataComponents.STEP_HEIGHT,true),
                    QOLDataComponents.STEP_HEIGHT,
                    List.of("Should the leggings add step height"),
                    (e,oE)->CreateQOLConfigs.server().equipments.armors.stepHeight.get()
            ));
        }
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        if (BacktankUtil.canAbsorbDamage(entity, getMaxDamage(stack))) return 0;
        return super.damageItem(stack, amount, entity, onBroken);
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

    @Override
    public Map<Holder<Attribute>, Map.Entry<Double, AttributeModifier.Operation>> getAppliedAttributes(ItemStack stack) {
        if (getType().equals(ArmorItem.Type.LEGGINGS)){
            Map<Holder<Attribute>, Map.Entry<Double, AttributeModifier.Operation>> map = QOLConfigurableItem.super.getAppliedAttributes(stack);
            if (CreateQOLConfigs.server().equipments.armors.stepHeight.get() && stack.getOrDefault(QOLDataComponents.STEP_HEIGHT,true)) map.put(Attributes.STEP_HEIGHT,new AbstractMap.SimpleEntry<>(0.5, AttributeModifier.Operation.ADD_VALUE));
            return map;
        }
        return QOLConfigurableItem.super.getAppliedAttributes(stack);
    }
}
