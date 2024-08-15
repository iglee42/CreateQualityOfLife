package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.equipment.armor.BacktankInstance;
import com.simibubi.create.content.equipment.armor.BacktankRenderer;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltInstance;
import com.simibubi.create.content.kinetics.belt.BeltRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.blockentitites.*;
import fr.iglee42.createqualityoflife.blockentitites.instances.FunneledBeltInstance;
import fr.iglee42.createqualityoflife.blockentitites.instances.SingleBeltInstance;
import fr.iglee42.createqualityoflife.blockentitites.renderers.ChippedSawRenderer;
import fr.iglee42.createqualityoflife.blockentitites.instances.ChippedSawInstance;
import fr.iglee42.createqualityoflife.blockentitites.renderers.FunneledBeltRenderer;
import fr.iglee42.createqualityoflife.blockentitites.renderers.InventoryLinkerRenderer;
import fr.iglee42.createqualityoflife.blockentitites.renderers.SingleBeltRenderer;
import fr.iglee42.createqualityoflife.blocks.SingleBeltBlock;
import fr.iglee42.createqualityoflife.utils.Features;

import static fr.iglee42.createqualityoflife.CreateQOL.REGISTRATE;


public class ModBlockEntities {

    public static  BlockEntityEntry<InventoryLinkerBlockEntity> INVENTORY_LINKER = REGISTRATE
            .blockEntity("inventory_linker", InventoryLinkerBlockEntity::new)
            //.instance(() -> InventoryLinkerInstance::new, false)
            .validBlocks(ModBlocks.INVENTORY_LINKER)
            .renderer(() -> InventoryLinkerRenderer::new)
            .register();

    public static  BlockEntityEntry<FunneledBeltBlockEntity> FUNNELED_BELT = REGISTRATE
            .blockEntity("funneled_belt", FunneledBeltBlockEntity::new)
            .instance(() -> FunneledBeltInstance::new, false)
            .validBlocks(ModBlocks.FUNNELED_BELT)
            .renderer(() -> FunneledBeltRenderer::new)
            .register();

    public static BlockEntityEntry<ChippedSawBlockEntity> CHIPPED_SAW = REGISTRATE.blockEntity("chipped_saw", ChippedSawBlockEntity::new)
            .instance(() -> ChippedSawInstance::new)
            .validBlocks(ModBlocks.ALCHEMY_SAW,ModBlocks.CARPENTERS_SAW,ModBlocks.BOTANIST_SAW,ModBlocks.GLASSBLOWER_SAW,ModBlocks.LOOM_SAW,ModBlocks.MASON_SAW,ModBlocks.TINKERING_SAW)
            .renderer(() -> ChippedSawRenderer::new)
            .register();

    public static final BlockEntityEntry<SingleBeltBlockEntity> SINGLE_BELT = REGISTRATE
            .blockEntity("single_belt", SingleBeltBlockEntity::new)
            .instance(() -> SingleBeltInstance::new, SingleBeltBlockEntity::shouldRenderNormally)
            .validBlocks(ModBlocks.SINGLE_BELT)
            .renderer(() -> SingleBeltRenderer::new)
            .register();

    public static final BlockEntityEntry<ShadowRadianceBacktankBE> SHADOW_CHEST_BE = REGISTRATE
            .blockEntity("shadow_back", ShadowRadianceBacktankBE::new)
            .instance(() -> BacktankInstance::new)
            .validBlocks(ModBlocks.SHADOW_RADIANCE_CHESTPLATE)
            .renderer(() -> BacktankRenderer::new)
            .register();
    public static void register() {}
}
