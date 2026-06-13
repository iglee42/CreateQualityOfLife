package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import com.simibubi.create.content.legacy.NoGravMagicalDohickyItem;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.item.ItemDescription;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.items.*;
import fr.iglee42.createqualityoflife.items.armors.*;
import fr.iglee42.createqualityoflife.items.tools.refinedradiance.*;
import fr.iglee42.createqualityoflife.items.tools.shadowradiance.*;
import fr.iglee42.createqualityoflife.items.tools.shadowsteel.*;
import fr.iglee42.createqualityoflife.statue.StatueItem;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.PreferredRender;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static fr.iglee42.createqualityoflife.CreateQOL.REGISTRATE;

public class QOLItems {

    private static final ResourceLocation EMPTY_SLOT_HELMET = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet");
    private static final ResourceLocation EMPTY_SLOT_CHESTPLATE = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate");
    private static final ResourceLocation EMPTY_SLOT_LEGGINGS = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings");
    private static final ResourceLocation EMPTY_SLOT_BOOTS = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots");
    private static final ResourceLocation EMPTY_SLOT_HOE = ResourceLocation.withDefaultNamespace("item/empty_slot_hoe");
    private static final ResourceLocation EMPTY_SLOT_AXE = ResourceLocation.withDefaultNamespace("item/empty_slot_axe");
    private static final ResourceLocation EMPTY_SLOT_SWORD = ResourceLocation.withDefaultNamespace("item/empty_slot_sword");
    private static final ResourceLocation EMPTY_SLOT_SHOVEL = ResourceLocation.withDefaultNamespace("item/empty_slot_shovel");
    private static final ResourceLocation EMPTY_SLOT_PICKAXE = ResourceLocation.withDefaultNamespace("item/empty_slot_pickaxe");
    private static final ResourceLocation EMPTY_SLOT_ELYTRA = CreateQOL.asResource("item/empty_armor_slot_elytra");
    private static final ResourceLocation EMPTY_SLOT_PROPELLER = CreateQOL.asResource("item/empty_slot_propeller");
    private static List<ResourceLocation> createUpgradeIconList() {
        return List.of(
                EMPTY_SLOT_HELMET,
                EMPTY_SLOT_SWORD,
                EMPTY_SLOT_CHESTPLATE,
                EMPTY_SLOT_PICKAXE,
                EMPTY_SLOT_LEGGINGS,
                EMPTY_SLOT_AXE,
                EMPTY_SLOT_BOOTS,
                EMPTY_SLOT_HOE,
                EMPTY_SLOT_SHOVEL
        );
    }

    static {
        REGISTRATE.setCreativeTab(QOLCreativeModeTabs.MAIN_TAB);
    }

    public static ItemEntry<PlayerPaperItem> PLAYER_PAPER = REGISTRATE.item("player_paper", PlayerPaperItem::new)
            .properties(properties -> properties.stacksTo(1).rarity(Rarity.RARE))
            .model((c,p)->p.generated(c,CreateQOL.asResource("item/"+c.getName()+"_empty")).override().predicate(CreateQOL.asResource("has_player"),1f)
                    .model(p.getBuilder(p.name(c)+"_full").parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0",CreateQOL.asResource("item/"+c.getName()+"_full")))
                    .end())
            .register();

    public static final ItemEntry<NoGravMagicalDohickyItem> SHADOW_RADIANCE = REGISTRATE.item("shadow_radiance", NoGravMagicalDohickyItem::new)
                    .properties(p->p.rarity(Rarity.RARE))
                    .register();

    public static final ItemEntry<SmithingTemplateItem> SHADOW_RADIANCE_UPGRADE_SMITHING_TEMPLATE = REGISTRATE.item("shadow_radiance_upgrade_smithing_template",p->new SmithingTemplateItem(
            Component.translatable(Util.makeDescriptionId("item", CreateQOL.asResource("smithing_template.shadow_radiance_upgrade.applies_to"))).withStyle(ChatFormatting.BLUE),
            Component.translatable(Util.makeDescriptionId("item", CreateQOL.asResource("smithing_template.shadow_radiance_upgrade.ingredients"))).withStyle(ChatFormatting.BLUE),
            Component.translatable(Util.makeDescriptionId("upgrade", CreateQOL.asResource("shadow_radiance_upgrade"))).withStyle(ChatFormatting.GRAY),
            Component.translatable(Util.makeDescriptionId("item", CreateQOL.asResource("smithing_template.shadow_radiance_upgrade.base_slot_description"))),
            Component.translatable(Util.makeDescriptionId("item", CreateQOL.asResource("smithing_template.shadow_radiance_upgrade.additions_slot_description"))),
            createUpgradeIconList(),
            List.of(EMPTY_SLOT_ELYTRA,EMPTY_SLOT_PROPELLER)
    ))
            .properties(p->p.rarity(Rarity.RARE))
            .register();



    public static final ItemEntry<? extends ShadowRadianceHelmet> SHADOW_RADIANCE_HELMET = REGISTRATE
            .item("shadow_radiance_helmet",
                    ShadowRadianceHelmet::new)
            .properties(p -> p.fireResistant().rarity(Rarity.EPIC).durability(814).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL))
            .tag(ItemTags.HEAD_ARMOR)
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
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
            .model((c,p)->p.generated(c).override().predicate(CreateQOL.asResource("elytra"),1)
                    .model(p.getBuilder(p.name(c)+"_elytra").parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0",CreateQOL.asResource("item/"+c.getName()+"_elytra")))
                    .end())
            .properties(p -> p.fireResistant().rarity(Rarity.EPIC).durability(1184).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL).component(QOLDataComponents.PREFERRED_RENDER, PreferredRender.BOTH))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .tag(AllTags.AllItemTags.PRESSURIZED_AIR_SOURCES.tag)
            .tag(ItemTags.CHEST_ARMOR)
            .register();



    public static final ItemEntry<? extends BaseArmorItem> SHADOW_RADIANCE_LEGGINGS = REGISTRATE
            .item("shadow_radiance_leggings",
                    p -> new ShadowRadianceArmorItem(ArmorItem.Type.LEGGINGS, p))
            .properties(p -> p.fireResistant().rarity(Rarity.EPIC).durability(1110).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .tag(ItemTags.LEG_ARMOR)
            .onRegisterAfter(Registries.ITEM, it-> ItemDescription.useKey(it,"item.createqol.shadow_armor"))
            .register();

    public static final ItemEntry<? extends BaseArmorItem> SHADOW_RADIANCE_BOOTS = REGISTRATE
            .item("shadow_radiance_boots",
                    p -> new ShadowRadianceArmorItem(ArmorItem.Type.BOOTS, p))
            .properties(p -> p.fireResistant().rarity(Rarity.EPIC).durability(962).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .tag(ItemTags.FOOT_ARMOR)
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
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .tag(ItemTags.HEAD_ARMOR)
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
            .model((c,p)->p.generated(c).override().predicate(CreateQOL.asResource("elytra"),1)
                    .model(p.getBuilder(p.name(c)+"_elytra").parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0",CreateQOL.asResource("item/"+c.getName()+"_elytra")))
                    .end())
            .properties(p -> p.fireResistant().rarity(Rarity.RARE).durability(1184).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL).component(QOLDataComponents.PREFERRED_RENDER, PreferredRender.BOTH))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .tag(AllTags.AllItemTags.PRESSURIZED_AIR_SOURCES.tag)
            .tag(ItemTags.CHEST_ARMOR)
            .register();

    public static final ItemEntry<? extends BaseArmorItem> REFINED_RADIANCE_LEGGINGS = REGISTRATE
            .item("refined_radiance_leggings",
                    p -> new RefinedRadianceArmorItem(ArmorItem.Type.LEGGINGS, p))
            .properties(p -> p.fireResistant().rarity(Rarity.RARE).durability(1110).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .tag(ItemTags.LEG_ARMOR)
            .onRegisterAfter(Registries.ITEM, it-> ItemDescription.useKey(it,"item.createqol.shadow_armor"))
            .register();

    public static final ItemEntry<? extends BaseArmorItem> REFINED_RADIANCE_BOOTS = REGISTRATE
            .item("refined_radiance_boots",
                    p -> new RefinedRadianceArmorItem(ArmorItem.Type.BOOTS, p))
            .properties(p -> p.fireResistant().rarity(Rarity.RARE).durability(962).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .tag(ItemTags.FOOT_ARMOR)
            .onRegisterAfter(Registries.ITEM, it-> ItemDescription.useKey(it,"item.createqol.shadow_armor"))
            .register();


    //SHADOW STEEL
    public static final ItemEntry<? extends ShadowSteelHelmet> SHADOW_STEEL_HELMET = REGISTRATE
            .item("shadow_steel_helmet",
                    ShadowSteelHelmet::new)
            .properties(p -> p.fireResistant().rarity(Rarity.RARE).durability(814).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .tag(ItemTags.HEAD_ARMOR)
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
            .properties(p -> p.fireResistant().rarity(Rarity.RARE).durability(1184).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL).component(QOLDataComponents.PREFERRED_RENDER, PreferredRender.BOTH))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .tag(AllTags.AllItemTags.PRESSURIZED_AIR_SOURCES.tag)
            .tag(ItemTags.CHEST_ARMOR)
            .register();

    public static final ItemEntry<? extends BaseArmorItem> SHADOW_STEEL_LEGGINGS = REGISTRATE
            .item("shadow_steel_leggings",
                    p -> new ShadowSteelArmorItem(ArmorItem.Type.LEGGINGS, p))
            .properties(p -> p.fireResistant().rarity(Rarity.RARE).durability(1110).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .tag(ItemTags.LEG_ARMOR)
            .onRegisterAfter(Registries.ITEM, it-> ItemDescription.useKey(it,"item.createqol.shadow_armor"))
            .register();

    public static final ItemEntry<? extends BaseArmorItem> SHADOW_STEEL_BOOTS = REGISTRATE
            .item("shadow_steel_boots",
                    p -> new ShadowSteelArmorItem(ArmorItem.Type.BOOTS, p))
            .properties(p -> p.fireResistant().rarity(Rarity.RARE).durability(962).component(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .tag(ItemTags.FOOT_ARMOR)
            .onRegisterAfter(Registries.ITEM, it-> ItemDescription.useKey(it,"item.createqol.shadow_armor"))
            .register();

    //TOOLS

    public static final ItemEntry<ShadowSteelSword> SHADOW_STEEL_SWORD = REGISTRATE
            .item("shadow_steel_sword", ShadowSteelSword::new)
            .properties(p -> p.rarity(Rarity.RARE).stacksTo(1))
            .properties(Item.Properties::fireResistant)
            .properties(p -> p.attributes(SwordItem
                    .createAttributes(QOLTiers.SHADOW_STEEL, 3, -2.8f)))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .model((c,p)->p.handheld(c))
            .tag(ItemTags.SWORDS)
            .tag(Tags.Items.MELEE_WEAPON_TOOLS)
            .register();

    public static final ItemEntry<RefinedRadianceSword> REFINED_RADIANCE_SWORD = REGISTRATE
            .item("refined_radiance_sword", RefinedRadianceSword::new)
            .properties(p -> p.rarity(Rarity.RARE).stacksTo(1))
            .properties(Item.Properties::fireResistant)
            .properties(p -> p.attributes(SwordItem
                    .createAttributes(QOLTiers.REFINED_RADIANCE, 3, -2.8f)))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .model((c,p)->p.handheld(c))
            .tag(ItemTags.SWORDS)
            .tag(Tags.Items.MELEE_WEAPON_TOOLS)
            .register();

    public static final ItemEntry<ShadowSteelPickaxe> SHADOW_STEEL_PICKAXE = REGISTRATE
            .item("shadow_steel_pickaxe",ShadowSteelPickaxe::new)
            .properties(p -> p.rarity(Rarity.RARE).stacksTo(1))
            .properties(Item.Properties::fireResistant)
            .properties(p -> p.attributes(PickaxeItem
                    .createAttributes(QOLTiers.SHADOW_STEEL, 1, -2.8f)))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .model((c,p)->p.handheld(c))
            .transform(tool(QOLDataComponents.DIGGING,null))
            .tag(ItemTags.PICKAXES)
            .tag(ItemTags.CLUSTER_MAX_HARVESTABLES)
            .register();

    public static final ItemEntry<RefinedRadiancePickaxe> REFINED_RADIANCE_PICKAXE = REGISTRATE
            .item("refined_radiance_pickaxe", RefinedRadiancePickaxe::new)
            .properties(p -> p.rarity(Rarity.RARE).stacksTo(1))
            .properties(Item.Properties::fireResistant)
            .properties(p -> p.attributes(PickaxeItem
                    .createAttributes(QOLTiers.REFINED_RADIANCE, 1, -2.8f)))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .transform(tool(QOLDataComponents.VEIN_MINE,null))
            .tag(ItemTags.PICKAXES)
            .tag(ItemTags.CLUSTER_MAX_HARVESTABLES)
            .register();

    public static final ItemEntry<ShadowSteelAxe> SHADOW_STEEL_AXE = REGISTRATE
            .item("shadow_steel_axe", ShadowSteelAxe::new)
            .properties(p -> p.rarity(Rarity.RARE).stacksTo(1))
            .properties(Item.Properties::fireResistant)
            .properties(p -> p.attributes(AxeItem
                    .createAttributes(QOLTiers.SHADOW_STEEL, 5.0F, -3.0F)))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .transform(tool(QOLDataComponents.TREE_DECAPITATION,null))
            .tag(ItemTags.AXES)
            .tag(Tags.Items.MELEE_WEAPON_TOOLS)
            .register();

    public static final ItemEntry<RefinedRadianceAxe> REFINED_RADIANCE_AXE = REGISTRATE
            .item("refined_radiance_axe", RefinedRadianceAxe::new)
            .properties(p -> p.rarity(Rarity.RARE).stacksTo(1))
            .properties(Item.Properties::fireResistant)
            .properties(p -> p.attributes(AxeItem
                    .createAttributes(QOLTiers.REFINED_RADIANCE, 5.0F, -3.0F)))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .transform(tool(QOLDataComponents.CASINGIFIER,null))
            .tag(ItemTags.AXES)
            .tag(Tags.Items.MELEE_WEAPON_TOOLS)
            .register();

    public static final ItemEntry<ShadowSteelShovel> SHADOW_STEEL_SHOVEL = REGISTRATE
            .item("shadow_steel_shovel", ShadowSteelShovel::new)
            .properties(p -> p.rarity(Rarity.RARE).stacksTo(1))
            .properties(Item.Properties::fireResistant)
            .properties(p -> p.attributes(ShovelItem
                    .createAttributes(QOLTiers.SHADOW_STEEL, 1.5F, -3.0F)))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .transform(tool(QOLDataComponents.DIGGING,null))
            .tag(ItemTags.SHOVELS)
            .register();

    public static final ItemEntry<RefinedRadianceShovel> REFINED_RADIANCE_SHOVEL = REGISTRATE
            .item("refined_radiance_shovel", RefinedRadianceShovel::new)
            .properties(p -> p.rarity(Rarity.RARE).stacksTo(1))
            .properties(Item.Properties::fireResistant)
            .properties(p -> p.attributes(ShovelItem
                    .createAttributes(QOLTiers.REFINED_RADIANCE, 1.5F, -3.0F)))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .transform(tool(QOLDataComponents.SMELTING,null))
            .tag(ItemTags.SHOVELS)
            .register();

    public static final ItemEntry<ShadowSteelHoe> SHADOW_STEEL_HOE = REGISTRATE
            .item("shadow_steel_hoe", ShadowSteelHoe::new)
            .properties(p -> p.rarity(Rarity.RARE).stacksTo(1))
            .properties(Item.Properties::fireResistant)
            .properties(p -> p.attributes(HoeItem
                    .createAttributes(QOLTiers.SHADOW_STEEL, -4.0F, 0.0F)))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .transform(tool(QOLDataComponents.PLOUGHING,null))
            .tag(ItemTags.HOES)
            .register();

    public static final ItemEntry<RefinedRadianceHoe> REFINED_RADIANCE_HOE = REGISTRATE
            .item("refined_radiance_hoe", RefinedRadianceHoe::new)
            .properties(p -> p.rarity(Rarity.RARE).stacksTo(1))
            .properties(Item.Properties::fireResistant)
            .properties(p -> p.attributes(HoeItem
                    .createAttributes(QOLTiers.REFINED_RADIANCE, -4.0F, 0.0F)))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .transform(tool(QOLDataComponents.HARVESTING,null))
            .tag(ItemTags.HOES)
            .register();

    public static final ItemEntry<ShadowRadianceSword> SHADOW_RADIANCE_SWORD = REGISTRATE
            .item("shadow_radiance_sword", ShadowRadianceSword::new)
            .properties(p -> p.rarity(Rarity.EPIC).stacksTo(1))
            .properties(Item.Properties::fireResistant)
            .properties(p -> p.attributes(SwordItem
                    .createAttributes(QOLTiers.SHADOW_RADIANCE, 3, -2.8f)))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .model((c,p)->p.handheld(c))
            .tag(ItemTags.SWORDS)
            .tag(Tags.Items.MELEE_WEAPON_TOOLS)
            .register();

    public static final ItemEntry<ShadowRadiancePickaxe> SHADOW_RADIANCE_PICKAXE = REGISTRATE
            .item("shadow_radiance_pickaxe",ShadowRadiancePickaxe::new)
            .properties(p -> p.rarity(Rarity.EPIC).stacksTo(1))
            .properties(Item.Properties::fireResistant)
            .properties(p -> p.attributes(PickaxeItem
                    .createAttributes(QOLTiers.SHADOW_RADIANCE, 1, -2.8f)))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .transform(tool(QOLDataComponents.DIGGING,QOLDataComponents.VEIN_MINE))
            .tag(ItemTags.PICKAXES)
            .tag(ItemTags.CLUSTER_MAX_HARVESTABLES)
            .register();

    public static final ItemEntry<ShadowRadianceAxe> SHADOW_RADIANCE_AXE = REGISTRATE
            .item("shadow_radiance_axe", ShadowRadianceAxe::new)
            .properties(p -> p.rarity(Rarity.EPIC).stacksTo(1))
            .properties(Item.Properties::fireResistant)
            .properties(p -> p.attributes(AxeItem
                    .createAttributes(QOLTiers.SHADOW_RADIANCE, 5.0F, -3.0F)))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .transform(tool(QOLDataComponents.CASINGIFIER,QOLDataComponents.TREE_DECAPITATION))
            .tag(ItemTags.AXES)
            .tag(Tags.Items.MELEE_WEAPON_TOOLS)
            .register();
    public static final ItemEntry<ShadowRadianceShovel> SHADOW_RADIANCE_SHOVEL = REGISTRATE
            .item("shadow_radiance_shovel", ShadowRadianceShovel::new)
            .properties(p -> p.rarity(Rarity.EPIC).stacksTo(1))
            .properties(Item.Properties::fireResistant)
            .properties(p -> p.attributes(ShovelItem
                    .createAttributes(QOLTiers.SHADOW_RADIANCE, 1.5F, -3.0F)))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .transform(tool(QOLDataComponents.DIGGING,QOLDataComponents.SMELTING))
            .tag(ItemTags.SHOVELS)
            .register();

    public static final ItemEntry<ShadowRadianceHoe> SHADOW_RADIANCE_HOE = REGISTRATE
            .item("shadow_radiance_hoe", ShadowRadianceHoe::new)
            .properties(p -> p.rarity(Rarity.EPIC).stacksTo(1))
            .properties(Item.Properties::fireResistant)
            .properties(p -> p.attributes(HoeItem
                    .createAttributes(QOLTiers.SHADOW_RADIANCE, -4.0F, 0.0F)))
            .properties(p->p.component(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT))
            .transform(tool(QOLDataComponents.PLOUGHING,QOLDataComponents.HARVESTING))
            .tag(ItemTags.HOES)
            .register();

    public static final ItemEntry<StockManagerBlockItem> EMPTY_STOCK_MANAGER =
            REGISTRATE.item("empty_stock_manager", StockManagerBlockItem::empty)
                    .model(AssetLookup.customBlockItemModel("stock_manager", "block"))
                    .register();


    public static final PackageStyles.PackageStyle IGLEE = new PackageStyles.PackageStyle("rare_iglee", 12, 10, 21f, true);
    public static final ItemEntry<PackageItem> IGLEE_PACKAGE = createRarePackage(IGLEE);
    public static final PackageStyles.PackageStyle FURTI = new PackageStyles.PackageStyle("rare_furti", 12, 10, 21f, true);
    public static final ItemEntry<PackageItem> FURTI_PACKAGE = createRarePackage(FURTI);
    public static final PackageStyles.PackageStyle DELTA = new PackageStyles.PackageStyle("rare_delta", 12, 10, 21f, true);
    public static final ItemEntry<PackageItem> DELTA_PACKAGE = createRarePackage(DELTA);
    public static void register(){
    }


    private static ItemEntry<PackageItem> createRarePackage(PackageStyles.PackageStyle style){
        return REGISTRATE.item(style.getItemId()
                        .getPath(), p -> new PackageItem(p, style))
                .properties(p -> p.stacksTo(1))
                .model(AssetLookup.existingItemModel())
                .tag(AllTags.AllItemTags.PACKAGES.tag)
                .setData(ProviderType.LANG,(c,p)->{})
                .register();
    }

    public static <B extends Item, P> NonNullUnaryOperator<ItemBuilder<B, P>> tool(@NotNull DataComponentType<Boolean> component1, @Nullable DataComponentType<Boolean> component2) {
        return i -> {
                    i = i.model((c, p) -> {
                        ItemModelBuilder builder = p.handheld(c)
                                .override()
                                .predicate(CreateQOL.asResource(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component1).getPath()),1.0f)
                                .model(p.getBuilder(c.getName() + "_special").parent(new ModelFile.UncheckedModelFile("item/handheld")).texture("layer0",c.getId().getNamespace() + ":item/"+c.getId().getPath() + "_special")).end();
                        if (component2 != null) {
                            builder.override()
                                    .predicate(CreateQOL.asResource(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component2).getPath()),1.0f)
                                    .model(p.getBuilder(c.getName() + "_special_2").parent(new ModelFile.UncheckedModelFile("item/handheld")).texture("layer0",c.getId().getNamespace() + ":item/"+c.getId().getPath() + "_special_2")).end();
                        }
                    })
                    .onRegister(it-> RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->ItemProperties.register(it,CreateQOL.asResource(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component1).getPath()),(stack,lvl,entity,index)->stack.getOrDefault(component1,false) ? 1.0f : 0.0f )));
                    if (component2 != null) {
                        i = i.onRegister(it -> RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ItemProperties.register(it, CreateQOL.asResource(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component2).getPath()), (stack, lvl, entity, index) -> stack.getOrDefault(component2, false) ? 1.0f : 0.0f)));
                    }
                    return i;
        };
    }

}
