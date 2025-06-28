package fr.iglee42.createqualityoflife.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.armor.DivingHelmetItem;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.ModItems;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.ForgeMod;
import java.util.UUID;

public class ShadowRadianceHelmet extends DivingHelmetItem {
    public ShadowRadianceHelmet(ArmorMaterial material, Properties properties, ResourceLocation textureLoc) {
        super(material, properties, textureLoc);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }
    static {
        GogglesItem.addIsWearingPredicate(player -> ModItems.SHADOW_RADIANCE_HELMET.isIn(player.getItemBySlot(EquipmentSlot.HEAD)) && NBTConstants.getOrDefault( player.getItemBySlot(EquipmentSlot.HEAD),NBTConstants.NBT_GOGGLES,true) && CreateQOLConfigs.server().helmetHaveGoggles.get());
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (BacktankUtil.getAllWithAir(player).isEmpty()) return;
        if (!CreateQOLConfigs.server().armorEffects.get()) return;
        if (NBTConstants.getOrDefault(stack,NBTConstants.NBT_EFFECTS,true))player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,20*11,1,false,false));
    }


    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot p_40390_) {
        if (p_40390_.equals(EquipmentSlot.HEAD)){
            ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes = ImmutableMultimap.builder();
            attributes.putAll(super.getDefaultAttributeModifiers(p_40390_));
			
			String reference = "shadow_radiance_"+p_40390_.name().toLowerCase();
			
			UUID block_uuid = UUID.nameUUIDFromBytes((reference+"_block").getBytes());
			UUID entity_uuid = UUID.nameUUIDFromBytes((reference+"_entity").getBytes());
			
            attributes.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(block_uuid,reference+"_block",1, AttributeModifier.Operation.ADDITION));
            attributes.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(entity_uuid,reference+"_entity",1, AttributeModifier.Operation.ADDITION));
            return attributes.build();
        }
        return super.getDefaultAttributeModifiers(p_40390_);
    }


}
