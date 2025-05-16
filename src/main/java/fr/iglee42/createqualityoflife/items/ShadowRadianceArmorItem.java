package fr.iglee42.createqualityoflife.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class ShadowRadianceArmorItem extends BaseArmorItem {
    public ShadowRadianceArmorItem(ArmorMaterial armorMaterial, Type type, Properties properties, ResourceLocation textureLoc) {
        super(armorMaterial, type, properties,textureLoc);
    }
    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (stack.getItem() instanceof ShadowRadianceArmorItem it){
            if (BacktankUtil.getAllWithAir(player).isEmpty()) return;
            if (!CreateQOLConfigs.server().armorEffects.get()) return;
            if (!NBTConstants.getOrDefault(stack,NBTConstants.NBT_EFFECTS,true)) return;
            switch (it.getType()){
                case LEGGINGS -> player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,20,1,false,false));
                case BOOTS -> player.addEffect(new MobEffectInstance(MobEffects.JUMP,20,0,false,false));
            }
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot p_40390_) {
        if (p_40390_.equals(getEquipmentSlot())){
            ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes = ImmutableMultimap.builder();
            attributes.putAll(super.getDefaultAttributeModifiers(p_40390_));
            attributes.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier("shadow_radiance_"+p_40390_.name().toLowerCase()+"_block",1, AttributeModifier.Operation.ADDITION));
            attributes.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier("shadow_radiance_"+p_40390_.name().toLowerCase()+"_entity",1, AttributeModifier.Operation.ADDITION));
            return attributes.build();
        }
        return super.getDefaultAttributeModifiers(p_40390_);
    }

}
