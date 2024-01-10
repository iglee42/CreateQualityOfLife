package fr.iglee42.createqualityoflife.registries;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.blockentitites.ChippedSawBlockEntity;
import fr.iglee42.createqualityoflife.blockentitites.renderers.ChippedSawRenderer;
import fr.iglee42.createqualityoflife.blockentitites.InventoryLinkerBlockEntity;
import fr.iglee42.createqualityoflife.blockentitites.instances.ChippedSawInstance;
import fr.iglee42.createqualityoflife.blockentitites.renderers.InventoryLinkerRenderer;
import fr.iglee42.createqualityoflife.utils.Features;

import static fr.iglee42.createqualityoflife.CreateQOL.REGISTRATE;


public class ModBlockEntities {

    public static  BlockEntityEntry<InventoryLinkerBlockEntity> INVENTORY_LINKER;

    public static BlockEntityEntry<ChippedSawBlockEntity> CHIPPED_SAW;
    public static void register() {
        if (CreateQOL.isActivate(Features.INVENTORY_LINKER)){
            INVENTORY_LINKER = REGISTRATE
                    .blockEntity("inventory_linker", InventoryLinkerBlockEntity::new)
                    //.instance(() -> InventoryLinkerInstance::new, false)
                    .validBlocks(ModBlocks.INVENTORY_LINKER)
                    .renderer(() -> InventoryLinkerRenderer::new)
                    .register();
        }
        if (CreateQOL.isChippedLoaded() && CreateQOL.isActivate(Features.CHIPPED_SAW)){
             CHIPPED_SAW = REGISTRATE.blockEntity("chipped_saw", ChippedSawBlockEntity::new)
                    .instance(() -> ChippedSawInstance::new)
                    .validBlocks(ModBlocks.ALCHEMY_SAW,ModBlocks.CARPENTERS_SAW,ModBlocks.BOTANIST_SAW,ModBlocks.GLASSBLOWER_SAW,ModBlocks.LOOM_SAW,ModBlocks.MASON_SAW,ModBlocks.TINKERING_SAW)
                    .renderer(() -> ChippedSawRenderer::new)
                    .register();
        }
    }
}
