package fr.iglee42.createqualityoflife.items.armors;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLArmorMaterials;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import fr.iglee42.createqualityoflife.utils.ShadowRadianceEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
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
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ShadowRadianceArmorItem extends BaseArmorItem implements QOLConfigurableItem {
    public ShadowRadianceArmorItem(ArmorItem.Type type, Properties properties) {
        super(QOLArmorMaterials.SHADOW_RADIANCE, type, properties, CreateQOL.asResource("shadow_radiance"));
    }
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean offHand) {
        super.inventoryTick(stack, level, entity, slot, offHand);
        invTick(stack, level, entity, slot, offHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext p_339594_, List<Component> components, TooltipFlag p_41424_) {
        if (!stack.getOrDefault(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT).isEnable(ItemTooltips.Tooltip.OPTIONS)) return;
        components.add(Component.translatable("createqol.function.armor.effect")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.translatable(providedEffect(stack).value().getDescriptionId()).withStyle(ChatFormatting.YELLOW)));

        if (getType().equals(ArmorItem.Type.BOOTS)) {
            components.add(Component.translatable("createqol.function.armor.diving")
                    .withStyle(ChatFormatting.GOLD)
                    .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.armors.bootsDiving.get(),
                            true, stack.getOrDefault(QOLDataComponents.BOOTS_DIVING, false), false, true)));
            components.add(Component.translatable("createqol.function.armor.belt_blocking")
                    .withStyle(ChatFormatting.GOLD)
                    .append(QOLConfigurableItem.chooseState(true,
                            true, stack.getOrDefault(QOLDataComponents.BOOTS_BELT, true), false, true)));
            components.add(Component.translatable("createqol.function.armor.lava_walking")
                    .withStyle(ChatFormatting.GOLD)
                    .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.armors.bootsLavaWalking.get(),
                            true, stack.getOrDefault(QOLDataComponents.BOOTS_LAVA, true), false, true)));
        } else if (getType().equals(ArmorItem.Type.LEGGINGS)){
            components.add(Component.literal("Void Walk : ")
                    .withStyle(ChatFormatting.GOLD)
                    .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.armors.voidWalking.get(),
                            true, stack.getOrDefault(QOLDataComponents.VOID_WALK, true), false, true)));
            components.add(Component.literal("Step Height : ")
                    .withStyle(ChatFormatting.GOLD)
                    .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.armors.stepHeight.get(),
                            true, stack.getOrDefault(QOLDataComponents.STEP_HEIGHT, true), false, true)));
        }
        super.appendHoverText(stack, p_339594_, components, p_41424_);
    }

    @Override
    public QOLConfigurableItem.Type type() {
        return QOLConfigurableItem.Type.ARMOR;
    }

    @Override
    public Holder<MobEffect> providedEffect(ItemStack stack) {
        if (!(stack.getItem() instanceof ArmorItem it)) return QOLConfigurableItem.super.providedEffect(stack);
        return getType().equals(ArmorItem.Type.BOOTS) || getType().equals(ArmorItem.Type.LEGGINGS) ? stack.getOrDefault(QOLDataComponents.EFFECT, getType().equals(ArmorItem.Type.BOOTS) ? ShadowRadianceEffects.JUMP_BOOST : ShadowRadianceEffects.SPEED).getEffectHolder() : QOLConfigurableItem.super.providedEffect(stack);
    }

    @Override
    public List<ArmorRenderType> renderTypes(ItemStack stack) {
        return Arrays.asList(ArmorRenderType.ALL,ArmorRenderType.NONE);
    }

    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        ShadowRadianceEffects[] valids = Arrays.stream(ShadowRadianceEffects.values()).filter(ef->ef.isValidForItem(stack)).toArray(ShadowRadianceEffects[]::new);
        list.add(new Configuration<>("Effect", stack.getOrDefault(QOLDataComponents.EFFECT, getType().equals(ArmorItem.Type.BOOTS) ? ShadowRadianceEffects.JUMP_BOOST : ShadowRadianceEffects.SPEED),QOLDataComponents.EFFECT,
                Configuration.ConfigType.ENUM,Arrays.asList("Define which mob effect should be provided.",
                "For this item, there is " + Component.translatable(valids[0].getEffectHolder().value().getDescriptionId()).getString() + " and " + Component.translatable(valids[1].getEffectHolder().value().getDescriptionId()).getString()),(direction, entry)->{

            ShadowRadianceEffects e = (ShadowRadianceEffects) entry.getValue();
            ShadowRadianceEffects[] options = Arrays.stream(ShadowRadianceEffects.values()).filter(ef->ef.isValidForItem(stack)).toArray(ShadowRadianceEffects[]::new);
            e = options[Math.floorMod(e.ordinal() + direction, options.length)];
            return e;
        },(e,oe)->true));
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

            list.add(Configuration.ofBool("Enable Belt Blocking",
                    stack.getOrDefault(QOLDataComponents.BOOTS_BELT,true),
                    QOLDataComponents.BOOTS_BELT,
                    List.of("You won't be pushed by belt if enabled"),
                    (e,oE)->true
            ));
        }else if (getType() == ArmorItem.Type.LEGGINGS){
            list.add(Configuration.ofBool("Void Walking",
                    stack.getOrDefault(QOLDataComponents.VOID_WALK,true),
                    QOLDataComponents.VOID_WALK,
                    List.of("Enable walking on void"),
                    (e,oE)-> CreateQOLConfigs.server().equipments.armors.voidWalking.get()
            ));
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
