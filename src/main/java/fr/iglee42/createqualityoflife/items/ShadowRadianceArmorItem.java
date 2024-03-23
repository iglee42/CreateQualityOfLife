package fr.iglee42.createqualityoflife.items;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.registries.ModArmorMaterials;
import fr.iglee42.createqualityoflife.utils.Features;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class ShadowRadianceArmorItem extends BaseArmorItem {
    public ShadowRadianceArmorItem(ArmorMaterial armorMaterial, EquipmentSlot type, Properties properties, ResourceLocation textureLoc) {
        super(armorMaterial, type, properties, textureLoc);
    }

    @Override
    public void fillItemCategory(CreativeModeTab p_41391_, NonNullList<ItemStack> p_41392_) {
        if (CreateQOL.isActivate(Features.SHADOW_RADIANCE))super.fillItemCategory(p_41391_, p_41392_);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (stack.getItem() instanceof ShadowRadianceArmorItem it){
            if (!player.isCreative() && !BacktankUtil.getAllWithAir(player).isEmpty())BacktankUtil.consumeAir(player, BacktankUtil.getAllWithAir(player).get(0), 0.01f);
            else if (!player.isCreative()) return;
            switch (it.getSlot()){
                case LEGS -> player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,20,1,false,false));
                case FEET -> player.addEffect(new MobEffectInstance(MobEffects.JUMP,20,0,false,false));
            }
        }
    }
}
