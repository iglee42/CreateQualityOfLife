package fr.iglee42.createqualityoflife.items.armors;

import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLArmorMaterials;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import fr.iglee42.createqualityoflife.utils.ShadowRadianceEffects;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

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
                    (e,oE)-> CreateQOLConfigs.server().bootsDiving.get()
            ));

            list.add(Configuration.ofBool("Enable Lava Walking",
                    stack.getOrDefault(QOLDataComponents.BOOTS_LAVA,true),
                    QOLDataComponents.BOOTS_LAVA,
                    List.of("Enable walking under lava, which makes the player walks normally under lava"),
                    (e,oE)-> CreateQOLConfigs.server().bootsLavaWalking.get()
            ));

            list.add(Configuration.ofBool("Enable Belt Blocking",
                    stack.getOrDefault(QOLDataComponents.BOOTS_BELT,true),
                    QOLDataComponents.BOOTS_BELT,
                    List.of("You won't be pushed by belt if enabled"),
                    (e,oE)->true
            ));
        }
    }
}
