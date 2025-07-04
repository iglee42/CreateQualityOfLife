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
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class RefinedRadianceHelmet extends DivingHelmetItem implements QOLConfigurableItem {
    static {
        GogglesItem.addIsWearingPredicate(player -> QOLItems.REFINED_RADIANCE_HELMET.isIn(player.getItemBySlot(EquipmentSlot.HEAD)) && player.getItemBySlot(EquipmentSlot.HEAD).getOrDefault(QOLDataComponents.HELMET_GOGGLES,true) && CreateQOLConfigs.server().helmetHaveGoggles.get());
    }

    public RefinedRadianceHelmet(Properties properties) {
        super(QOLArmorMaterials.REFINED_RADIANCE, properties, CreateQOL.asResource("refined_radiance"));
    }

    @Override
    public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
        super.inventoryTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
        invTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
    }

    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(Configuration.ofBool("Enable Googles",stack.getOrDefault(QOLDataComponents.HELMET_GOGGLES,true),QOLDataComponents.HELMET_GOGGLES,Arrays.asList("Should engineer's goggle's information be displayed"),(e,oe)->CreateQOLConfigs.server().helmetHaveGoggles.get()));
    }

    @Override
    public Holder<MobEffect> providedEffect(ItemStack stack) {
        return MobEffects.NIGHT_VISION;
    }

    @Override
    public int effectTime(ItemStack stack) {
        return 200;
    }

    @Override
    public QOLConfigurableItem.Type type() {
        return QOLConfigurableItem.Type.ARMOR;
    }

    @Override
    public List<ArmorRenderType> renderTypes(ItemStack stack) {
        return Arrays.stream(ArmorRenderType.values()).toList();
    }
}
