package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.kinetics.saw.SawGenerator;
import com.simibubi.create.content.kinetics.saw.SawMovementBehaviour;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.blocks.ChippedSawBlock;
import fr.iglee42.createqualityoflife.blocks.InventoryLinkerBlock;
import fr.iglee42.createqualityoflife.utils.Features;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.AllMovementBehaviours.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static fr.iglee42.createqualityoflife.CreateQOL.REGISTRATE;

public class ModBlocks {

    static {
        REGISTRATE.setCreativeTab(ModCreativeModeTabs.MAIN_TAB);
    }

    public static BlockEntry<InventoryLinkerBlock> INVENTORY_LINKER;


    public static BlockEntry<ChippedSawBlock> ALCHEMY_SAW, BOTANIST_SAW, CARPENTERS_SAW, GLASSBLOWER_SAW,LOOM_SAW,MASON_SAW,TINKERING_SAW;

    public static void register(){
        if (CreateQOL.isActivate(Features.INVENTORY_LINKER)){
            INVENTORY_LINKER = REGISTRATE.block("inventory_linker", InventoryLinkerBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.METAL))
                    .properties(BlockBehaviour.Properties::noOcclusion)
                    .transform(pickaxeOnly())
                    //.onRegisterAfter(Registry.ITEM_REGISTRY,v-> ItemDescription.useKey(v,"block.createqol.inventory_linker"))
                    .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
                    .transform(BlockStressDefaults.setImpact(8.0))
                    .addLayer(()-> RenderType::cutoutMipped)
                    .item()
                    .properties(p->p.rarity(Rarity.UNCOMMON))
                    .transform(customItemModel())
                    .register();
        }
        if (CreateQOL.isChippedLoaded() && CreateQOL.isActivate(Features.CHIPPED_SAW)){
            ALCHEMY_SAW = REGISTRATE.block("alchemy_saw", ChippedSawBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .addLayer(() -> RenderType::cutoutMipped)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(axeOrPickaxe())
                    .blockstate(new SawGenerator()::generate)
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .item()
                    .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                    .transform(customItemModel())
                    .register();
            BOTANIST_SAW = REGISTRATE.block("botanist_saw", ChippedSawBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .addLayer(() -> RenderType::cutoutMipped)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(axeOrPickaxe())
                    .blockstate(new SawGenerator()::generate)
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .item()
                    .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                    .transform(customItemModel())
                    .register();
            CARPENTERS_SAW = REGISTRATE.block("carpenters_saw", ChippedSawBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .addLayer(() -> RenderType::cutoutMipped)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(axeOrPickaxe())
                    .blockstate(new SawGenerator()::generate)
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .item()
                    .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                    .transform(customItemModel())
                    .register();
            GLASSBLOWER_SAW = REGISTRATE.block("glassblower_saw", ChippedSawBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .addLayer(() -> RenderType::cutoutMipped)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(axeOrPickaxe())
                    .blockstate(new SawGenerator()::generate)
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .item()
                    .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                    .transform(customItemModel())
                    .register();
            LOOM_SAW = REGISTRATE.block("loom_saw", ChippedSawBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .addLayer(() -> RenderType::cutoutMipped)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(axeOrPickaxe())
                    .blockstate(new SawGenerator()::generate)
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .item()
                    .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                    .transform(customItemModel())
                    .register();
            MASON_SAW = REGISTRATE.block("mason_saw", ChippedSawBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .addLayer(() -> RenderType::cutoutMipped)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(axeOrPickaxe())
                    .blockstate(new SawGenerator()::generate)
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .item()
                    .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                    .transform(customItemModel())
                    .register();
            TINKERING_SAW = REGISTRATE.block("tinkering_saw", ChippedSawBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .addLayer(() -> RenderType::cutoutMipped)
                    .properties(p -> p.mapColor(MapColor.PODZOL))
                    .transform(axeOrPickaxe())
                    .blockstate(new SawGenerator()::generate)
                    .transform(BlockStressDefaults.setImpact(4.0))
                    .addLayer(() -> RenderType::cutoutMipped)
                    .item()
                    .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                    .transform(customItemModel())
                    .register();



        }
    }
}
