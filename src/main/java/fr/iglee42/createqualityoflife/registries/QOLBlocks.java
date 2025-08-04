package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.saw.SawGenerator;
import com.simibubi.create.content.legacy.ChromaticCompoundColor;
import com.simibubi.create.content.logistics.packager.PackagerGenerator;
import com.simibubi.create.foundation.block.render.ReducedDestroyEffects;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.behaviours.TrashCanMovementBehaviour;
import fr.iglee42.createqualityoflife.blocks.*;
import fr.iglee42.createqualityoflife.config.CQOLStress;
import fr.iglee42.createqualityoflife.items.ChromaticCompoundBlockItem;
import fr.iglee42.createqualityoflife.items.NoGravMagicalDohickyBlockItem;
import fr.iglee42.createqualityoflife.items.RefinedRadianceBlockItem;
import fr.iglee42.createqualityoflife.items.ShadowSteelBlockItem;
import fr.iglee42.createqualityoflife.registries.generators.ChippedSawGenerator;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

import static com.simibubi.create.AllTags.*;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.foundation.data.BlockStateGen.simpleCubeAll;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.*;
import static fr.iglee42.createqualityoflife.CreateQOL.REGISTRATE;

public class QOLBlocks {

    static {
        REGISTRATE.setCreativeTab(QOLCreativeModeTabs.MAIN_TAB);
    }

    public static BlockEntry<InventoryLinkerBlock> INVENTORY_LINKER = REGISTRATE.block("inventory_linker", InventoryLinkerBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.METAL))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .transform(CQOLStress.setImpact(8.0))
            .addLayer(()-> RenderType::cutoutMipped)
            .item()
            .properties(p->p.rarity(Rarity.UNCOMMON))
            .transform(customItemModel())
            .register();

    public static final BlockEntry<ShadowRadianceBacktankBlock> SHADOW_RADIANCE_CHESTPLATE =
            REGISTRATE.block("shadow_radiance_chestplate", ShadowRadianceBacktankBlock::new)
                    .initialProperties(SharedProperties::netheriteMetal)
                    .transform(backtank(QOLItems.SHADOW_RADIANCE_CHESTPLATE::get))
                    .blockstate((c,p)->p.horizontalBlock(c.get(),bs-> p.models().getExistingFile(CreateQOL.asResource("block/shadow_radiance_chestplate/block" + (bs.getValue(ShadowRadianceBacktankBlock.PROPELLER) ? "_jetpack": "")))))
                    .register();
    public static final BlockEntry<RefinedRadianceBacktankBlock> REFINED_RADIANCE_CHESTPLATE =
            REGISTRATE.block("refined_radiance_chestplate", RefinedRadianceBacktankBlock::new)
                    .initialProperties(SharedProperties::netheriteMetal)
                    .transform(backtank(QOLItems.REFINED_RADIANCE_CHESTPLATE::get))
                    .register();
    public static final BlockEntry<ShadowSteelBacktankBlock> SHADOW_STEEL_CHESTPLATE =
            REGISTRATE.block("shadow_steel_chestplate", ShadowSteelBacktankBlock::new)
                    .initialProperties(SharedProperties::netheriteMetal)
                    .transform(backtank(QOLItems.SHADOW_STEEL_CHESTPLATE::get))
                    .register();

    public static final BlockEntry<TrashCanBlock> TRASH_CAN = REGISTRATE.block("trash_can", TrashCanBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()
                    .isSuffocating((level, pos, state) -> false)
                    .isRedstoneConductor((level, pos, state) -> false))
            .transform(pickaxeOnly())
            .addLayer(() -> RenderType::cutoutMipped)
            .clientExtension(() -> ReducedDestroyEffects::new)
            .onRegister(movementBehaviour(TrashCanMovementBehaviour.normal()))
            .blockstate((c,p)->p.simpleBlock(c.get(),AssetLookup.partialBaseModel(c,p)))
            .item()
            .transform(customItemModel("_", "block"))
            .register();

    public static final BlockEntry<BrassTrashCanBlock> BRASS_TRASH_CAN = REGISTRATE.block("brass_trash_can", BrassTrashCanBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()
                    .isSuffocating((level, pos, state) -> false)
                    .isRedstoneConductor((level, pos, state) -> false))
            .addLayer(() -> RenderType::cutoutMipped)
            .clientExtension(() -> () -> new ReducedDestroyEffects())
            .onRegister(movementBehaviour(TrashCanMovementBehaviour.brass()))
            .transform(pickaxeOnly())
            .blockstate((c,p)->p.getVariantBuilder(c.get()).forAllStates(bs->{
                boolean open = bs.getValue(BrassTrashCanBlock.OPEN);
                boolean powered = bs.getValue(BrassTrashCanBlock.POWERED);
                return ConfiguredModel.builder().modelFile(p.models().getExistingFile(CreateQOL.asResource("block/trash_can/block_brass" + (open ? "_open" :"") + (powered ? "_powered":"")))).build();
            }))
            .item()
            .transform(customItemModel("trash_can", "block_brass"))
            .register();

    public static final BlockEntry<EnderPackagerBlock> ENDER_PACKAGER = REGISTRATE.block("ender_packager", EnderPackagerBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .properties(p -> p.isRedstoneConductor(($1, $2, $3) -> false))
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE)
            .sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .addLayer(() -> RenderType::cutoutMipped)
            .blockstate(new PackagerGenerator()::generate)
            .item()
			.model(AssetLookup::customItemModel)
			.build()
            .register();


    public static BlockEntry<ChippedSawBlock>
            ALCHEMY_SAW = createChippedSaw("alchemy"),
            BOTANIST_SAW = createChippedSaw("botanist"),
            CARPENTERS_SAW = createChippedSaw("carpenters"),
            GLASSBLOWER_SAW = createChippedSaw("glassblower"),
            LOOM_SAW = createChippedSaw("loom"),
            MASON_SAW = createChippedSaw("mason"),
            TINKERING_SAW = createChippedSaw("tinkering");


    public static final BlockEntry<Block> REFINED_RADIANCE_BLOCK = REGISTRATE.block("refined_radiance_block", Block::new)
            .initialProperties(()->Blocks.ANDESITE)
            .properties(p -> p.mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(simpleCubeAll("refined_radiance_block"))
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(commonBlockTag("storage_blocks/refined_radiance"))
            .item(RefinedRadianceBlockItem::new)
            .tag(commonItemTag("storage_blocks/refined_radiance"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .properties(p->p.rarity(Rarity.UNCOMMON))
            .build()
            .register();

    public static final BlockEntry<Block> SHADOW_STEEL_BLOCK = REGISTRATE.block("shadow_steel_block", Block::new)
            .initialProperties(()->Blocks.ANDESITE)
            .properties(p -> p.mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(commonBlockTag("storage_blocks/shadow_steel"))
            .item(ShadowSteelBlockItem::new)
            .tag(commonItemTag("storage_blocks/shadow_steel"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .properties(p->p.rarity(Rarity.UNCOMMON))
            .build()
            .register();
    public static final BlockEntry<Block> CHROMATIC_COMPOUND_BLOCK = REGISTRATE.block("chromatic_compound_block", Block::new)
            .initialProperties(()->Blocks.ANDESITE)
            .properties(p -> p.mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops())
            .blockstate((c,p)->p.simpleBlock(c.get(),p.models().getExistingFile(p.modLoc("block/chromatic_compound_block"))))
            .transform(pickaxeOnly())
            .color(()->()-> (BlockColor) (blockState, blockAndTintGetter, blockPos, i) -> new ChromaticCompoundColor().getColor(new ItemStack(blockState.getBlock().asItem()),i))
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(commonBlockTag("storage_blocks/chromatic_compound"))
            .item(ChromaticCompoundBlockItem::new)
            .tag(commonItemTag("storage_blocks/chromatic_compound"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .properties(p->p.rarity(Rarity.UNCOMMON))
            .color(()->ChromaticCompoundColor::new)
            .build()
            .register();

    public static final BlockEntry<Block> SHADOW_RADIANCE_BLOCK = REGISTRATE.block("shadow_radiance_block", Block::new)
            .initialProperties(()->Blocks.ANDESITE)
            .properties(p -> p.mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops())
            .blockstate((c,p)->p.simpleBlock(c.get(),p.models().getExistingFile(p.modLoc("block/shadow_radiance_block"))))
            .transform(pickaxeOnly())
            .tag(Tags.Blocks.STORAGE_BLOCKS)
            .tag(commonBlockTag("storage_blocks/shadow_radiance"))
            .item(NoGravMagicalDohickyBlockItem::new)
            .tag(commonItemTag("storage_blocks/shadow_radiance"))
            .tag(Tags.Items.STORAGE_BLOCKS)
            .properties(p->p.rarity(Rarity.RARE))
            .build()
            .register();



    private static BlockEntry<ChippedSawBlock> createChippedSaw(String name){
        return REGISTRATE.block(name +"_saw", ChippedSawBlock::new)
                .initialProperties(SharedProperties::stone)
                .addLayer(() -> RenderType::cutoutMipped)
                .properties(p -> p.mapColor(MapColor.PODZOL))
                .transform(axeOrPickaxe())
                .blockstate(new ChippedSawGenerator()::generate)
                .transform(CQOLStress.setImpact(4.0))
                .addLayer(() -> RenderType::cutoutMipped)
                .item()
                .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                .model(AssetLookup.existingItemModel())
                .build()
                .register();
    }
    public static void register(){
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> backtank(Supplier<ItemLike> drop) {
        return b -> b.blockstate((c, p) -> p.horizontalBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
                .transform(pickaxeOnly())
                .addLayer(() -> RenderType::cutoutMipped)
                .transform(CQOLStress.setImpact(4.0))
                ;
    }

}
