package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import fr.iglee42.createqualityoflife.behaviours.TrashCanMovementBehaviour;
import fr.iglee42.createqualityoflife.blocks.*;
import fr.iglee42.createqualityoflife.config.CQOLStress;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
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
            //.onRegisterAfter(Registry.ITEM_REGISTRY,v-> ItemDescription.useKey(v,"block.createqol.inventory_linker"))
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
            .onRegister(movementBehaviour(TrashCanMovementBehaviour.normal()))
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
            .onRegister(movementBehaviour(TrashCanMovementBehaviour.brass()))
            .transform(pickaxeOnly())
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
            //.blockstate(new PackagerGenerator()::generate)
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


    private static BlockEntry<ChippedSawBlock> createChippedSaw(String name){
        return REGISTRATE.block(name +"_saw", ChippedSawBlock::new)
                .initialProperties(SharedProperties::stone)
                .addLayer(() -> RenderType::cutoutMipped)
                .properties(p -> p.mapColor(MapColor.PODZOL))
                .transform(axeOrPickaxe())
                //.blockstate(new SawGenerator()::generate)
                .transform(CQOLStress.setImpact(4.0))
                .addLayer(() -> RenderType::cutoutMipped)
                .item()
                .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                .transform(customItemModel())
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
