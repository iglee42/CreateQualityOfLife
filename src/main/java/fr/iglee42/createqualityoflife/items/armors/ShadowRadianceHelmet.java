package fr.iglee42.createqualityoflife.items.armors;

import com.simibubi.create.content.equipment.armor.DivingHelmetItem;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLArmorMaterials;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import fr.iglee42.createqualityoflife.utils.ShadowRadianceEffects;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.Arrays;
import java.util.List;

public class ShadowRadianceHelmet extends DivingHelmetItem implements QOLConfigurableItem {
    public ShadowRadianceHelmet(Properties properties) {
        super(QOLArmorMaterials.SHADOW_RADIANCE, properties, CreateQOL.asResource("shadow_radiance"));
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }
    static {
        GogglesItem.addIsWearingPredicate(player -> QOLItems.SHADOW_RADIANCE_HELMET.isIn(player.getItemBySlot(EquipmentSlot.HEAD)) && player.getItemBySlot(EquipmentSlot.HEAD).getOrDefault(QOLDataComponents.HELMET_GOGGLES,true) && CreateQOLConfigs.server().helmetHaveGoggles.get());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean offHand) {
        super.inventoryTick(stack, level, entity, slot, offHand);
        invTick(stack, level, entity, slot, offHand);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        ResourceLocation resourcelocation = ResourceLocation.withDefaultNamespace("armor." + type.getName());
        return super.getDefaultAttributeModifiers()
                .withModifierAdded(Attributes.BLOCK_INTERACTION_RANGE,new AttributeModifier(resourcelocation,1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()))
                .withModifierAdded(Attributes.ENTITY_INTERACTION_RANGE,new AttributeModifier(resourcelocation,1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
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
        return stack.getOrDefault(QOLDataComponents.EFFECT, ShadowRadianceEffects.NIGHT_VISION).getEffectHolder();
    }

    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(Configuration.ofBool("Enable Googles",stack.getOrDefault(QOLDataComponents.HELMET_GOGGLES,true),QOLDataComponents.HELMET_GOGGLES,Arrays.asList("Should engineer's goggle's information be displayed"),(e,oe)->CreateQOLConfigs.server().helmetHaveGoggles.get()));
        ShadowRadianceEffects[] valids = Arrays.stream(ShadowRadianceEffects.values()).filter(ef->ef.isValidForItem(stack)).toArray(ShadowRadianceEffects[]::new);
        list.add(new Configuration<>("Effect", stack.getOrDefault(QOLDataComponents.EFFECT, ShadowRadianceEffects.NIGHT_VISION),QOLDataComponents.EFFECT,
                Configuration.ConfigType.ENUM,Arrays.asList("Define which mob effect should be provided.",
                "For this item, there is " + Component.translatable(valids[0].getEffectHolder().value().getDescriptionId()).getString() + " and " + Component.translatable(valids[1].getEffectHolder().value().getDescriptionId()).getString()),(direction, entry)->{

            ShadowRadianceEffects e = (ShadowRadianceEffects) entry.getValue();
            ShadowRadianceEffects[] options = Arrays.stream(ShadowRadianceEffects.values()).filter(ef->ef.isValidForItem(stack)).toArray(ShadowRadianceEffects[]::new);
            e = options[Math.floorMod(e.ordinal() + direction, options.length)];
            return e;
        },(e,oe)->true));
    }

    @Override
    public int effectTime(ItemStack stack) {
        return 200;
    }
}
