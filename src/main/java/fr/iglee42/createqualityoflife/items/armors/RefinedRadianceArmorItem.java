package fr.iglee42.createqualityoflife.items.armors;

import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLArmorMaterials;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class RefinedRadianceArmorItem extends BaseArmorItem implements QOLConfigurableItem {
    public RefinedRadianceArmorItem(ArmorItem.Type type, Properties properties) {
        super(QOLArmorMaterials.REFINED_RADIANCE, type, properties, CreateQOL.asResource("refined_radiance"));
    }
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean offHand) {
        super.inventoryTick(stack, level, entity, slot, offHand);
        invTick(stack, level, entity, slot, offHand);
    }

    @Override
    public QOLConfigurableItem.Type type() {
        return QOLConfigurableItem.Type.ARMOR;
    }

    @Override
    public Holder<MobEffect> providedEffect(ItemStack stack) {
        if (!(stack.getItem() instanceof ArmorItem it)) return QOLConfigurableItem.super.providedEffect(stack);
        return getType().equals(ArmorItem.Type.BOOTS) ? MobEffects.DIG_SPEED : (getType().equals(ArmorItem.Type.LEGGINGS)  ? MobEffects.SATURATION : QOLConfigurableItem.super.providedEffect(stack));
    }

    @Override
    public List<ArmorRenderType> renderTypes(ItemStack stack) {
        return Arrays.asList(ArmorRenderType.ALL,ArmorRenderType.NONE);
    }

    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        if (getType().equals(ArmorItem.Type.BOOTS)){
            list.add(Configuration.ofBool("Enable Diving",
                    stack.getOrDefault(QOLDataComponents.BOOTS_DIVING,false),
                    QOLDataComponents.BOOTS_DIVING,
                    List.of("Enable diving, which makes the player descends quicker in liquids"),
                    (e,oE)-> CreateQOLConfigs.server().bootsDiving.get()
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
