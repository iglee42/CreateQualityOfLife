package fr.iglee42.createqualityoflife.registries;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;

import fr.iglee42.createqualityoflife.CreateQOL;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public enum ModArmorMaterials implements ArmorMaterial {

    SHADOW_RADIANCE(CreateQOL.asResource("shadow_radiance").toString(), 40, new int[] { 4, 9, 7, 4 }, 25, () -> SoundEvents.ARMOR_EQUIP_NETHERITE, 4.0F, 0.2F,
            () -> Ingredient.of(ModItems.SHADOW_RADIANCE.asItem()))

    ;

    private static final int[] MAX_DAMAGE_ARRAY = new int[] { 11, 16, 15, 13 };
    private final String name;
    private final int maxDamageFactor;
    private final int[] damageReductionAmountArray;
    private final int enchantability;
    private final Supplier<SoundEvent> soundEvent;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairMaterial;

    private ModArmorMaterials(String name, int maxDamageFactor, int[] damageReductionAmountArray, int enchantability,
                              Supplier<SoundEvent> soundEvent, float toughness, float knockbackResistance, Supplier<Ingredient> repairMaterial) {
        this.name = name;
        this.maxDamageFactor = maxDamageFactor;
        this.damageReductionAmountArray = damageReductionAmountArray;
        this.enchantability = enchantability;
        this.soundEvent = soundEvent;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairMaterial = Suppliers.memoize(repairMaterial::get);
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.soundEvent.get();
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairMaterial.get();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    @Override
    public int getDurabilityForType(Type pType) {
        return MAX_DAMAGE_ARRAY[pType.ordinal()] * this.maxDamageFactor;
    }

    @Override
    public int getDefenseForType(Type pType) {
        return this.damageReductionAmountArray[pType.ordinal()];
    }

}
