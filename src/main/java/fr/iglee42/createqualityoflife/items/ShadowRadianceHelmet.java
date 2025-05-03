package fr.iglee42.createqualityoflife.items;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.armor.DivingHelmetItem;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import fr.iglee42.createqualityoflife.registries.ModArmorMaterials;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.ModItems;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

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
}
