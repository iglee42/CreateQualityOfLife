package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import com.simibubi.create.content.legacy.NoGravMagicalDohickyItem;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.util.entry.ItemEntry;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.items.*;
import fr.iglee42.createqualityoflife.items.armors.*;
import fr.iglee42.createqualityoflife.statue.StatueItem;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.PreferredRender;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Rarity;

import static com.simibubi.create.AllTags.commonItemTag;
import static fr.iglee42.createqualityoflife.CreateQOL.REGISTRATE;

public class QOLItems {

    static {
        REGISTRATE.setCreativeTab(QOLCreativeModeTabs.MAIN_TAB);
    }

    public static ItemEntry<PlayerPaperItem> PLAYER_PAPER = REGISTRATE.item("player_paper", PlayerPaperItem::new)
            .properties(properties -> properties.stacksTo(1).rarity(Rarity.RARE))
            .register();

    public static final ItemEntry<NoGravMagicalDohickyItem> SHADOW_RADIANCE = REGISTRATE.item("shadow_radiance", NoGravMagicalDohickyItem::new)
                    .properties(p->p.rarity(Rarity.RARE))
                    .register();



    public static final ItemEntry<? extends ShadowRadianceHelmet> SHADOW_RADIANCE_HELMET = REGISTRATE
            .item("shadow_radiance_helmet",
                    ShadowRadianceHelmet::new)
            .properties(p -> p.fireResistant().rarity(Rarity.EPIC).durability(814).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL))
            .tag(commonItemTag("armors/helmets"))
            .onRegisterAfter(Registries.ITEM, it-> ItemDescription.useKey(it,"item.createqol.shadow_armor"))
            .register();

    public static final ItemEntry<BacktankItem.BacktankBlockItem> SHADOW_RADIANCE_CHESTPLATE_PLACEABLE = REGISTRATE
            .item("shadow_radiance_chestplate_placeable",
                    p -> new BacktankItem.BacktankBlockItem(QOLBlocks.SHADOW_RADIANCE_CHESTPLATE.get(), QOLItems.SHADOW_RADIANCE_CHESTPLATE::get, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/barrier")))
            .register();
    public static final ItemEntry<? extends BacktankItem> SHADOW_RADIANCE_CHESTPLATE = REGISTRATE
            .item("shadow_radiance_chestplate",
                    p -> new ShadowRadianceChestplate(p,
                            SHADOW_RADIANCE_CHESTPLATE_PLACEABLE))
            .model(AssetLookup.customGenericItemModel("_", "item"))
            .properties(p -> p.fireResistant().rarity(Rarity.EPIC).durability(1184).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL).component(QOLDataComponents.PREFERRED_RENDER, PreferredRender.BOTH))
            .tag(AllTags.AllItemTags.PRESSURIZED_AIR_SOURCES.tag)
            .tag(commonItemTag("armors/chestplates"))
            .register();



    public static final ItemEntry<? extends BaseArmorItem> SHADOW_RADIANCE_LEGGINGS = REGISTRATE
            .item("shadow_radiance_leggings",
                    p -> new ShadowRadianceArmorItem(ArmorItem.Type.LEGGINGS, p))
            .properties(p -> p.fireResistant().rarity(Rarity.EPIC).durability(1110).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL))
            .tag(commonItemTag("armors/leggings"))
            .onRegisterAfter(Registries.ITEM, it-> ItemDescription.useKey(it,"item.createqol.shadow_armor"))
            .register();

    public static final ItemEntry<? extends BaseArmorItem> SHADOW_RADIANCE_BOOTS = REGISTRATE
            .item("shadow_radiance_boots",
                    p -> new ShadowRadianceArmorItem(ArmorItem.Type.BOOTS, p))
            .properties(p -> p.fireResistant().rarity(Rarity.EPIC).durability(962).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL))
            .tag(commonItemTag("armors/boots"))
            .onRegisterAfter(Registries.ITEM, it-> ItemDescription.useKey(it,"item.createqol.shadow_armor"))
            .register();

    public static final ItemEntry<StatueItem> STATUE = REGISTRATE.item("statue", StatueItem::new)
            .properties(p->p.stacksTo(16))
            .register();

    //REFINED RADIANCE
    public static final ItemEntry<? extends RefinedRadianceHelmet> REFINED_RADIANCE_HELMET = REGISTRATE
            .item("refined_radiance_helmet",
                    RefinedRadianceHelmet::new)
            .properties(p -> p.fireResistant().rarity(Rarity.RARE).durability(814).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL))
            .tag(commonItemTag("armors/helmets"))
            .onRegisterAfter(Registries.ITEM, it-> ItemDescription.useKey(it,"item.createqol.shadow_armor"))
            .register();
    public static final ItemEntry<BacktankItem.BacktankBlockItem> REFINED_RADIANCE_CHESTPLATE_PLACEABLE = REGISTRATE
            .item("refined_radiance_chestplate_placeable",
                    p -> new BacktankItem.BacktankBlockItem(QOLBlocks.REFINED_RADIANCE_CHESTPLATE.get(), QOLItems.REFINED_RADIANCE_CHESTPLATE::get, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/barrier")))
            .register();
    public static final ItemEntry<? extends BacktankItem> REFINED_RADIANCE_CHESTPLATE = REGISTRATE
            .item("refined_radiance_chestplate",
                    p -> new RefinedRadianceChestplate(p,
                    REFINED_RADIANCE_CHESTPLATE_PLACEABLE))
            .model(AssetLookup.customGenericItemModel("_", "item"))
            .properties(p -> p.fireResistant().rarity(Rarity.RARE).durability(1184).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL).component(QOLDataComponents.PREFERRED_RENDER, PreferredRender.BOTH))
            .tag(AllTags.AllItemTags.PRESSURIZED_AIR_SOURCES.tag)
            .tag(commonItemTag("armors/chestplates"))
            .register();




    //SHADOW STEEL
    public static final ItemEntry<? extends ShadowSteelHelmet> SHADOW_STEEL_HELMET = REGISTRATE
            .item("shadow_steel_helmet",
                    ShadowSteelHelmet::new)
            .properties(p -> p.fireResistant().rarity(Rarity.RARE).durability(814).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL))
            .tag(commonItemTag("armors/helmets"))
            .onRegisterAfter(Registries.ITEM, it-> ItemDescription.useKey(it,"item.createqol.shadow_armor"))
            .register();
    public static final ItemEntry<BacktankItem.BacktankBlockItem> SHADOW_STEEL_CHESTPLATE_PLACEABLE = REGISTRATE
            .item("shadow_steel_chestplate_placeable",
                    p -> new BacktankItem.BacktankBlockItem(QOLBlocks.SHADOW_STEEL_CHESTPLATE.get(), QOLItems.SHADOW_STEEL_CHESTPLATE::get, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/barrier")))
            .register();
    public static final ItemEntry<? extends BacktankItem> SHADOW_STEEL_CHESTPLATE = REGISTRATE
            .item("shadow_steel_chestplate",
                    p -> new ShadowSteelChestplate(p,
                            SHADOW_STEEL_CHESTPLATE_PLACEABLE))
            .model(AssetLookup.customGenericItemModel("_", "item"))
            .properties(p -> p.fireResistant().rarity(Rarity.RARE).durability(1184).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL).component(QOLDataComponents.PREFERRED_RENDER, PreferredRender.BOTH))
            .tag(AllTags.AllItemTags.PRESSURIZED_AIR_SOURCES.tag)
            .tag(commonItemTag("armors/chestplates"))
            .register();


    public static final PackageStyles.PackageStyle FURTI = new PackageStyles.PackageStyle("rare_furti", 12, 10, 21f, true);
    public static final ItemEntry<PackageItem> FURTI_PACKAGE = createRarePackage(FURTI);
    public static final PackageStyles.PackageStyle DELTA = new PackageStyles.PackageStyle("rare_delta", 12, 10, 21f, true);
    public static final ItemEntry<PackageItem> DELTA_PACKAGE = createRarePackage(DELTA);
    public static final PackageStyles.PackageStyle IGLEE = new PackageStyles.PackageStyle("rare_iglee", 12, 10, 21f, true);
    public static final ItemEntry<PackageItem> IGLEE_PACKAGE = createRarePackage(IGLEE);
    public static void register(){
    }


    private static ItemEntry<PackageItem> createRarePackage(PackageStyles.PackageStyle style){
        return REGISTRATE.item(style.getItemId()
                        .getPath(), p -> new PackageItem(p, style))
                .properties(p -> p.stacksTo(1)).register();
    }
}
