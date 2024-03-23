package fr.iglee42.createqualityoflife;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.compat.jei.category.MysteriousItemConversionCategory;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import fr.iglee42.createqualityoflife.conditions.FeatureLoadedCondition;
import fr.iglee42.createqualityoflife.config.CreateQOLCommonConfig;
import fr.iglee42.createqualityoflife.registries.ModBlockEntities;
import fr.iglee42.createqualityoflife.registries.ModBlocks;
import fr.iglee42.createqualityoflife.registries.ModItems;
import fr.iglee42.createqualityoflife.registries.ModPackets;
import fr.iglee42.createqualityoflife.utils.Features;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;

@Mod(CreateQOL.MODID)
public class CreateQOL {

    public static final String MODID = "createqol";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final CreativeModeTab TAB = new CreativeModeTab(MODID) {
        @Override
        public @NotNull ItemStack makeIcon() {return ModItems.SHADOW_RADIANCE.asStack();}
    };

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }
    public CreateQOL() throws IOException, IllegalAccessException {

        IEventBus modEventBus = FMLJavaModLoadingContext.get()
                .getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        CreateQOLCommonConfig.load();

        REGISTRATE.registerEventListeners(modEventBus);

        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();
        ModPackets.registerPackets();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CreateQOLClient.onCtorClient(modEventBus, forgeEventBus));

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        //if (isActivate(Features.SHADOW_RADIANCE)){
        //    MysteriousItemConversionCategory.RECIPES.add(ConversionRecipe.create(AllItems.CHROMATIC_COMPOUND.asStack(), AllItems.SHADOW_STEEL.asStack()));
        //    MysteriousItemConversionCategory.RECIPES.add(ConversionRecipe.create(AllItems.CHROMATIC_COMPOUND.asStack(), AllItems.REFINED_RADIANCE.asStack()));
        //}
    }

    public static boolean isChippedLoaded() {
        return ModList.get().isLoaded("chipped");
    }

    public static boolean isActivate(Features feature){
        return feature.getConfig();
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID,path);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(()-> CraftingHelper.register(FeatureLoadedCondition.Serializer.INSTANCE));
    }


}
