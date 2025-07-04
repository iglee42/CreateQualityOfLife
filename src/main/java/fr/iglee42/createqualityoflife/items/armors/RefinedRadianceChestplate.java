package fr.iglee42.createqualityoflife.items.armors;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.registries.QOLArmorMaterials;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.PreferredRender;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class RefinedRadianceChestplate extends BacktankItem.Layered implements QOLConfigurableItem {


    public RefinedRadianceChestplate(Properties properties, Supplier<BacktankBlockItem> placeable) {
        super(QOLArmorMaterials.REFINED_RADIANCE, properties, CreateQOL.asResource("refined_radiance"), placeable);
    }

    @Override
    public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
        super.inventoryTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
        invTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
    }

    @Override
    public int effectLevel(ItemStack stack) {
        return 0;
    }

    @Override
    public QOLConfigurableItem.Type type() {
        return QOLConfigurableItem.Type.ARMOR;
    }

    @Override
    public List<ArmorRenderType> renderTypes(ItemStack stack) {
        return Arrays.asList(ArmorRenderType.ALL,ArmorRenderType.NONE);
    }

    @Override
    public Holder<MobEffect> providedEffect(ItemStack stack) {
        return MobEffects.REGENERATION;
    }

    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(new Configuration<>("Preferred Render", stack.getOrDefault(QOLDataComponents.PREFERRED_RENDER, PreferredRender.BOTH),QOLDataComponents.PREFERRED_RENDER,
                Configuration.ConfigType.ENUM,Arrays.asList("Define how the additions should be rendered.",
                "\"Elytra\" renders only the elytra",
                "\"Backtank\" renders only the backtank"),(direction,entry)->{

            PreferredRender e = (PreferredRender) entry.getValue();
            PreferredRender[] options = Arrays.stream(PreferredRender.values()).toArray(PreferredRender[]::new);
            e = options[Math.floorMod(e.ordinal() + direction, options.length)];
            return e;
        },(e,oe)->true));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return super.isBarVisible(stack) && BacktankUtil.getAir(stack) < BacktankUtil.maxAir(stack);
    }
}
