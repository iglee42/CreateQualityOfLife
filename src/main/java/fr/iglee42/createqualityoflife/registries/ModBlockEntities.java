package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.content.equipment.armor.BacktankRenderer;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import fr.iglee42.createqualityoflife.blockentitites.ChippedSawBlockEntity;
import fr.iglee42.createqualityoflife.blockentitites.InventoryLinkerBlockEntity;
import fr.iglee42.createqualityoflife.blockentitites.ShadowRadianceBacktankBE;
import fr.iglee42.createqualityoflife.blockentitites.renderers.ChippedSawRenderer;
import fr.iglee42.createqualityoflife.blockentitites.renderers.InventoryLinkerRenderer;
import fr.iglee42.createqualityoflife.blockentitites.visuals.ChippedSawVisual;

import static fr.iglee42.createqualityoflife.CreateQOL.REGISTRATE;


public class ModBlockEntities {

    public static  BlockEntityEntry<InventoryLinkerBlockEntity> INVENTORY_LINKER = REGISTRATE
            .blockEntity("inventory_linker", InventoryLinkerBlockEntity::new)
            //.instance(() -> InventoryLinkerInstance::new, false)
            .validBlocks(ModBlocks.INVENTORY_LINKER)
            .renderer(() -> InventoryLinkerRenderer::new)
            .register();

    public static BlockEntityEntry<ChippedSawBlockEntity> CHIPPED_SAW = REGISTRATE.blockEntity("chipped_saw", ChippedSawBlockEntity::new)
            .visual(() -> ChippedSawVisual::new)
            .validBlocks(ModBlocks.ALCHEMY_SAW,ModBlocks.CARPENTERS_SAW,ModBlocks.BOTANIST_SAW,ModBlocks.GLASSBLOWER_SAW,ModBlocks.LOOM_SAW,ModBlocks.MASON_SAW,ModBlocks.TINKERING_SAW)
            .renderer(() -> ChippedSawRenderer::new)
            .register();


    public static final BlockEntityEntry<ShadowRadianceBacktankBE> SHADOW_CHEST_BE = REGISTRATE
            .blockEntity("shadow_back", ShadowRadianceBacktankBE::new)
            .visual(() -> SingleAxisRotatingVisual::backtank)
            .validBlocks(ModBlocks.SHADOW_RADIANCE_CHESTPLATE)
            .renderer(() -> BacktankRenderer::new)
            .register();
    public static void register() {}
}
