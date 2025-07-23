package fr.iglee42.createqualityoflife.items.tools.shadowsteel;

import fr.iglee42.createqualityoflife.registries.QOLTiers;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.Level;

public class ShadowSteelPickaxe extends PickaxeItem implements QOLConfigurableItem {
    public ShadowSteelPickaxe(Properties p_42964_) {
        super(QOLTiers.SHADOW_STEEL, p_42964_);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean offHand) {
        super.inventoryTick(stack, level, entity, slot, offHand);
        invTick(stack, level, entity, slot, offHand);
    }

    @Override
    public Type type() {
        return Type.ITEM;
    }
}
