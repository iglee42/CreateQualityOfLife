package fr.iglee42.createqualityoflife;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.recipe.DeployingRecipeGen;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import fr.iglee42.createqualityoflife.conditions.FeatureLoadedCondition;
import fr.iglee42.createqualityoflife.config.CreateQOLCommonConfig;
import fr.iglee42.createqualityoflife.registries.*;
import fr.iglee42.createqualityoflife.utils.Features;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.slf4j.Logger;

import java.io.IOException;

@Mod(CreateQOL.MODID)
public class CreateQOL {

    public static final String MODID = "createqol";
    private static final Logger LOGGER = LogUtils.getLogger();

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
        ModCreativeModeTabs.register(modEventBus);
        ModPackets.register();
        ModDataComponents.register(modEventBus);
        ModConditions.CONDITIONS.register(modEventBus);

        RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CreateQOLClient.onCtorClient(modEventBus, forgeEventBus));

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ChippedSawBlockEntity::registerCapabilities);
        modEventBus.addListener(InventoryLinkerBlockEntity::registerCapabilities);

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
        return ResourceLocation.fromNamespaceAndPath(MODID,path);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        //event.enqueueWork(()-> CraftingHelper.register(FeatureLoadedCondition.Serializer.INSTANCE));
    }



}
