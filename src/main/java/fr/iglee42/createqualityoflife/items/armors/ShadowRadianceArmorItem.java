package fr.iglee42.createqualityoflife.items.armors;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLArmorMaterials;
import fr.iglee42.createqualityoflife.utils.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
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
    public void appendHoverText(ItemStack stack, Level p_339594_, List<Component> components, TooltipFlag p_41424_) {
        if (!NBTConstants.getTooltipOrDefault(stack).isEnable(ItemTooltips.Tooltip.OPTIONS)) return;
        components.add(Component.translatable("createqol.function.armor.effect")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.translatable(providedEffect(stack).getDescriptionId()).withStyle(ChatFormatting.YELLOW)));

        if (getType().equals(ArmorItem.Type.BOOTS)) {
            components.add(Component.translatable("createqol.function.armor.diving")
                    .withStyle(ChatFormatting.GOLD)
                    .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.armors.bootsDiving.get(),
                            true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_DIVING,false), false, true)));
            components.add(Component.translatable("createqol.function.armor.belt_blocking")
                    .withStyle(ChatFormatting.GOLD)
                    .append(QOLConfigurableItem.chooseState(true,
                            true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_BELT,true), false, true)));
            components.add(Component.translatable("createqol.function.armor.lava_walking")
                    .withStyle(ChatFormatting.GOLD)
                    .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.armors.bootsLavaWalking.get(),
                            true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_LAVA,true), false, true)));
        } else if (getType().equals(ArmorItem.Type.LEGGINGS)){
            components.add(Component.translatable("createqol.function.armor.void_walk")
                    .withStyle(ChatFormatting.GOLD)
                    .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.armors.voidWalking.get(),
                            true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_VOID_WALK,false), false, true)));
            components.add(Component.translatable("createqol.function.armor.step_height")
                    .withStyle(ChatFormatting.GOLD)
                    .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.armors.stepHeight.get(),
                            true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_STEP_HEIGHT,false), false, true)));
        }
        super.appendHoverText(stack, p_339594_, components, p_41424_);
    }

    @Override
    public QOLConfigurableItem.Type type() {
        return QOLConfigurableItem.Type.ARMOR;
    }

    @Override
    public MobEffect providedEffect(ItemStack stack) {
        if (!(stack.getItem() instanceof ArmorItem it)) return QOLConfigurableItem.super.providedEffect(stack);
        return getType().equals(ArmorItem.Type.BOOTS) || getType().equals(ArmorItem.Type.LEGGINGS) ? NBTConstants.getEffectsOrDefault(stack, getType().equals(ArmorItem.Type.BOOTS) ? ShadowRadianceEffects.JUMP_BOOST : ShadowRadianceEffects.SPEED).getEffectHolder() : QOLConfigurableItem.super.providedEffect(stack);
    }

    @Override
    public List<ArmorRenderType> renderTypes(ItemStack stack) {
        return Arrays.asList(ArmorRenderType.ALL,ArmorRenderType.NONE);
    }

    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        ShadowRadianceEffects[] valids = Arrays.stream(ShadowRadianceEffects.values()).filter(ef->ef.isValidForItem(stack)).toArray(ShadowRadianceEffects[]::new);
        list.add(new Configuration<>("Effect", NBTConstants.getEffectsOrDefault(stack, getType().equals(ArmorItem.Type.BOOTS) ? ShadowRadianceEffects.JUMP_BOOST : ShadowRadianceEffects.SPEED),NBTConstants.NBT_CHOOSABLE_EFFECTS,
                Configuration.ConfigType.ENUM,Arrays.asList("Define which mob effect should be provided.",
                "For this item, there is " + Component.translatable(valids[0].getEffectHolder().getDescriptionId()).getString() + " and " + Component.translatable(valids[1].getEffectHolder().getDescriptionId()).getString()),(direction, entry)->{

            ShadowRadianceEffects e = (ShadowRadianceEffects) entry.getValue();
            ShadowRadianceEffects[] options = Arrays.stream(ShadowRadianceEffects.values()).filter(ef->ef.isValidForItem(stack)).toArray(ShadowRadianceEffects[]::new);
            e = options[Math.floorMod(e.ordinal() + direction, options.length)];
            return e;
        },(e,oe)->true));
        if (getType().equals(ArmorItem.Type.BOOTS)){
            list.add(Configuration.ofBool("Enable Diving",
                    NBTConstants.getOrDefault(stack,NBTConstants.NBT_DIVING,false),
                    NBTConstants.NBT_DIVING,
                    List.of("Enable diving, which makes the player descends quicker in liquids"),
                    (e,oE)-> CreateQOLConfigs.server().equipments.armors.bootsDiving.get()
            ));

            list.add(Configuration.ofBool("Enable Lava Walking",
                    NBTConstants.getOrDefault(stack,NBTConstants.NBT_LAVA,true),
                    NBTConstants.NBT_LAVA,
                    List.of("Enable walking under lava, which makes the player walks normally under lava"),
                    (e,oE)-> CreateQOLConfigs.server().equipments.armors.bootsLavaWalking.get()
            ));

            list.add(Configuration.ofBool("Enable Belt Blocking",
                    NBTConstants.getOrDefault(stack,NBTConstants.NBT_BELT,true),
                    NBTConstants.NBT_BELT,
                    List.of("You won't be pushed by belt if enabled"),
                    (e,oE)->true
            ));
        }else if (getType() == ArmorItem.Type.LEGGINGS){
            list.add(Configuration.ofBool("Void Walking",
                    NBTConstants.getOrDefault(stack,NBTConstants.NBT_VOID_WALK,true),
                    NBTConstants.NBT_VOID_WALK,
                    List.of("Enable walking on void"),
                    (e,oE)-> CreateQOLConfigs.server().equipments.armors.voidWalking.get()
            ));
            list.add(Configuration.ofBool("Enable Step Height",
                    NBTConstants.getOrDefault(stack,NBTConstants.NBT_STEP_HEIGHT,true),
                    NBTConstants.NBT_STEP_HEIGHT,
                    List.of("Should the leggings add step height"),
                    (e,oE)->CreateQOLConfigs.server().equipments.armors.stepHeight.get()
            ));
        }
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<T> onBroken) {
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
            if (CreateQOLConfigs.server().equipments.armors.stepHeight.get() && NBTConstants.getOrDefault(stack,NBTConstants.NBT_STEP_HEIGHT,true)) map.put(ForgeMod.STEP_HEIGHT_ADDITION.getHolder().get(), new AbstractMap.SimpleEntry<>(0.5, AttributeModifier.Operation.ADDITION));
            return map;
        }
        return QOLConfigurableItem.super.getAppliedAttributes(stack);
    }
}
