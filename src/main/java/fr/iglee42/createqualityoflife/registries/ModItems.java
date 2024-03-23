package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import com.simibubi.create.content.equipment.armor.DivingHelmetItem;
import com.simibubi.create.content.equipment.goggles.GogglesModel;
import com.simibubi.create.content.legacy.NoGravMagicalDohickyItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.items.PlayerPaperItem;
import fr.iglee42.createqualityoflife.items.ShadowRadianceArmorItem;
import fr.iglee42.createqualityoflife.items.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.items.ShadowRadianceHelmet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Rarity;

import static com.simibubi.create.AllTags.forgeItemTag;
import static fr.iglee42.createqualityoflife.CreateQOL.REGISTRATE;

public class ModItems {

    static {
        REGISTRATE.creativeModeTab(() -> CreateQOL.TAB);
    }

    public static ItemEntry<PlayerPaperItem> PLAYER_PAPER = REGISTRATE.item("player_paper", PlayerPaperItem::new)
            .properties(properties -> properties.stacksTo(1).rarity(Rarity.RARE))
            .register();

    public static final ItemEntry<NoGravMagicalDohickyItem> SHADOW_RADIANCE = REGISTRATE.item("shadow_radiance", NoGravMagicalDohickyItem::new)
                    .properties(p->p.rarity(Rarity.RARE).tab(CreateQOL.TAB))
                    .register();



    public static final ItemEntry<? extends ShadowRadianceHelmet> SHADOW_RADIANCE_HELMET = REGISTRATE
            .item("shadow_radiance_helmet",
                    p -> new ShadowRadianceHelmet(ModArmorMaterials.SHADOW_RADIANCE, p, CreateQOL.asResource("shadow_radiance")))
            .properties(p -> p.fireResistant().rarity(Rarity.RARE))
            .tag(forgeItemTag("armors/helmets"))
            .register();

    public static final ItemEntry<BacktankItem.BacktankBlockItem> SHADOW_RADIANCE_CHESTPLATE_PLACEABLE = REGISTRATE
            .item("shadow_radiance_chestplate_placeable",
                    p -> new BacktankItem.BacktankBlockItem(ModBlocks.SHADOW_RADIANCE_CHESTPLATE.get(), ModItems.SHADOW_RADIANCE_CHESTPLATE::get, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/barrier")))
            .register();
    public static final ItemEntry<? extends BacktankItem> SHADOW_RADIANCE_CHESTPLATE = REGISTRATE
            .item("shadow_radiance_chestplate",
                    p -> new ShadowRadianceChestplate(ModArmorMaterials.SHADOW_RADIANCE, p, CreateQOL.asResource("shadow_radiance"),
                            SHADOW_RADIANCE_CHESTPLATE_PLACEABLE))
            .model(AssetLookup.customGenericItemModel("_", "item"))
            .properties(p -> p.fireResistant().rarity(Rarity.RARE))
            .tag(AllTags.AllItemTags.PRESSURIZED_AIR_SOURCES.tag)
            .tag(forgeItemTag("armors/chestplates"))
            .register();

    public static final ItemEntry<? extends BaseArmorItem> SHADOW_RADIANCE_LEGGINGS = REGISTRATE
            .item("shadow_radiance_leggings",
                    p -> new ShadowRadianceArmorItem(ModArmorMaterials.SHADOW_RADIANCE, EquipmentSlot.LEGS, p, CreateQOL.asResource("shadow_radiance")))
            .properties(p -> p.fireResistant().rarity(Rarity.RARE))
            .tag(forgeItemTag("armors/leggings"))
            .register();

    public static final ItemEntry<? extends BaseArmorItem> SHADOW_RADIANCE_BOOTS = REGISTRATE
            .item("shadow_radiance_boots",
                    p -> new ShadowRadianceArmorItem(ModArmorMaterials.SHADOW_RADIANCE, EquipmentSlot.FEET, p, CreateQOL.asResource("shadow_radiance")))
            .properties(p -> p.fireResistant().rarity(Rarity.RARE))
            .tag(forgeItemTag("armors/boots"))
            .register();

    public static void register(){}
}
