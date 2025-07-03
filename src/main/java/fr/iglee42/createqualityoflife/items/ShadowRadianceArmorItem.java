package fr.iglee42.createqualityoflife.items;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

public class ShadowRadianceArmorItem extends BaseArmorItem {
    public ShadowRadianceArmorItem(Holder<ArmorMaterial> armorMaterial, Type type, Properties properties, ResourceLocation textureLoc) {
        super(armorMaterial, type, properties, textureLoc);
    }
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean offHand) {
        super.inventoryTick(stack, level, entity, slot, offHand);
        if (!(entity instanceof Player player)) return;
        if (!CreateQOLConfigs.server().armorEffects.get()) return;
        if (player.getItemBySlot(EquipmentSlot.LEGS).equals(stack) && ((ShadowRadianceArmorItem)stack.getItem()).getType().equals(Type.LEGGINGS)) {
            if (BacktankUtil.getAllWithAir(player).isEmpty()) return;
            if (stack.getOrDefault(QOLDataComponents.ARMOR_EFFECT,true))player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,20,1,false,false));
        }
        if (player.getItemBySlot(EquipmentSlot.FEET).equals(stack) && ((ShadowRadianceArmorItem)stack.getItem()).getType().equals(Type.BOOTS)) {
            if (BacktankUtil.getAllWithAir(player).isEmpty()) return;
            if (stack.getOrDefault(QOLDataComponents.ARMOR_EFFECT,true))player.addEffect(new MobEffectInstance(MobEffects.JUMP,20,0,false,false));
        }
    }
    
    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        ResourceLocation resourcelocation = ResourceLocation.withDefaultNamespace("armor." + type.getName());
        return super.getDefaultAttributeModifiers()
                .withModifierAdded(Attributes.BLOCK_INTERACTION_RANGE,new AttributeModifier(resourcelocation,1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()))
                .withModifierAdded(Attributes.ENTITY_INTERACTION_RANGE,new AttributeModifier(resourcelocation,1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
    }

}
