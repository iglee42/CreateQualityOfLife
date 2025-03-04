package fr.iglee42.createqualityoflife.items;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import fr.iglee42.createqualityoflife.registries.ModArmorMaterials;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Locale;

public class ShadowRadianceArmorItem extends BaseArmorItem {
    public ShadowRadianceArmorItem(ArmorMaterial armorMaterial, Type type, Properties properties, ResourceLocation textureLoc) {
        super(armorMaterial, type, properties,textureLoc);
    }
    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (stack.getItem() instanceof ShadowRadianceArmorItem it){
            if (BacktankUtil.getAllWithAir(player).isEmpty()) return;
            if (!NBTConstants.getOrDefault(stack,NBTConstants.NBT_EFFECTS,true)) return;
            switch (it.getType()){
                case LEGGINGS -> player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,20,1,false,false));
                case BOOTS -> player.addEffect(new MobEffectInstance(MobEffects.JUMP,20,0,false,false));
            }
        }
    }

}
