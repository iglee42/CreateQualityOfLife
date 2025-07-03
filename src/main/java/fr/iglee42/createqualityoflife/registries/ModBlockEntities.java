package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.content.equipment.armor.BacktankRenderer;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import fr.iglee42.createqualityoflife.blockentitites.*;
import fr.iglee42.createqualityoflife.blockentitites.renderers.ChippedSawRenderer;
import fr.iglee42.createqualityoflife.blockentitites.renderers.EnderPackagerRenderer;
import fr.iglee42.createqualityoflife.blockentitites.renderers.InventoryLinkerRenderer;
import fr.iglee42.createqualityoflife.blockentitites.visuals.ChippedSawVisual;
import fr.iglee42.createqualityoflife.blockentitites.visuals.EnderPackagerVisual;

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

    public static final BlockEntityEntry<TrashCanBlockEntity> TRASH_CAN = REGISTRATE
            .blockEntity("trash_can", TrashCanBlockEntity::new)
            .validBlocks(ModBlocks.TRASH_CAN)
            .register();

    public static final BlockEntityEntry<BrassTrashCanBlockEntity> BRASS_TRASH_CAN = REGISTRATE
            .blockEntity("brass_trash_can", BrassTrashCanBlockEntity::new)
            .validBlocks(ModBlocks.BRASS_TRASH_CAN)
            .renderer(()->SmartBlockEntityRenderer::new)
            .register();

    public static final BlockEntityEntry<EnderPackagerBlockEntity> ENDER_PACKAGER = REGISTRATE
            .blockEntity("ender_packager", EnderPackagerBlockEntity::new)
            .visual(() -> EnderPackagerVisual::new, true)
            .validBlocks(ModBlocks.ENDER_PACKAGER)
            .renderer(() -> EnderPackagerRenderer::new)
            .register();
    public static void register() {}
}
