package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.redstone.displayLink.source.ItemNameDisplaySource;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.blocks.*;
import fr.iglee42.createqualityoflife.client.FunneledBeltModel;
import fr.iglee42.createqualityoflife.client.SingleBeltModel;
import fr.iglee42.createqualityoflife.registries.generators.FunneledBeltGenerator;
import fr.iglee42.createqualityoflife.registries.generators.SingleBeltGenerator;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;

import static com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours.assignDataBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static fr.iglee42.createqualityoflife.CreateQOL.REGISTRATE;

public class ModBlocks {

    static {
        REGISTRATE.creativeModeTab(() -> CreateQOL.TAB);
    }

    public static BlockEntry<InventoryLinkerBlock> INVENTORY_LINKER = REGISTRATE.block("inventory_linker", InventoryLinkerBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.METAL))
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

    public static final BlockEntry<ShadowRadianceBacktankBlock> SHADOW_RADIANCE_CHESTPLATE =
            REGISTRATE.block("shadow_radiance_chestplate", ShadowRadianceBacktankBlock::new)
                    .initialProperties(SharedProperties::netheriteMetal)
                    .transform(BuilderTransformers.backtank(ModItems.SHADOW_RADIANCE_CHESTPLATE::get))
                    .register();

    public static BlockEntry<FunneledBeltBlock> FUNNELED_BELT = REGISTRATE.block("funneled_belt", FunneledBeltBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.METAL))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .blockstate(new FunneledBeltGenerator()::generate)
            .transform(BlockStressDefaults.setNoImpact())
            .addLayer(()-> RenderType::cutoutMipped)
            .onRegister(CreateRegistrate.blockModel(()-> FunneledBeltModel::new))
            .item()
            .properties(p->p.rarity(Rarity.UNCOMMON))
            .transform(customItemModel())
            .register();

    public static final BlockEntry<SingleBeltBlock> SINGLE_BELT = REGISTRATE.block("single_belt", SingleBeltBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .blockstate(new SingleBeltGenerator()::generate)
            .transform(BlockStressDefaults.setNoImpact())
            .onRegister(assignDataBehaviour(new ItemNameDisplaySource(), "combine_item_names"))
            .onRegister(CreateRegistrate.blockModel(() -> SingleBeltModel::new))
            .item()
            .transform(customItemModel())
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
                .properties(p -> p.color(MaterialColor.PODZOL))
                .transform(axeOrPickaxe())
                //.blockstate(new SawGenerator()::generate)
                .transform(BlockStressDefaults.setImpact(4.0))
                .addLayer(() -> RenderType::cutoutMipped)
                .item()
                .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                .transform(customItemModel())
                .register();
    }
    public static void register(){
    }
}
