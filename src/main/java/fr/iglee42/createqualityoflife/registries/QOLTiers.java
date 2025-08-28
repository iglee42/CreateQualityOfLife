package fr.iglee42.createqualityoflife.registries;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;

import com.simibubi.create.AllItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;

public enum QOLTiers implements Tier {
    SHADOW_STEEL(4063, 10.0F, 6.0F, 20, () -> Ingredient.of(AllItems.SHADOW_STEEL)),
    REFINED_RADIANCE( 4063, 10.0F, 6.0F, 20, () -> Ingredient.of(AllItems.REFINED_RADIANCE)),
    SHADOW_RADIANCE(5119, 11.0F, 7.0F, 25, () -> Ingredient.of(QOLItems.SHADOW_RADIANCE));

    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;

    private QOLTiers( int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = Suppliers.memoize(repairIngredient::get);
    }

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.damage;
    }

    @Override
    public int getLevel() {
        return 4;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Nullable
    public TagKey<Block> getTag() { return Tags.Blocks.NEEDS_NETHERITE_TOOL; }
}
