package fr.iglee42.createqualityoflife;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import fr.iglee42.createqualityoflife.blockentitites.ChippedSawBlockEntity;
import fr.iglee42.createqualityoflife.blockentitites.InventoryLinkerBlockEntity;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.config.CreateQOLFeaturesConfig;
import fr.iglee42.createqualityoflife.registries.*;
import fr.iglee42.createqualityoflife.utils.Features;
import fr.iglee42.createqualityoflife.utils.IHaveTankMixin;
import fr.iglee42.createqualityoflife.utils.liquidblazeburners.LiquidBlazeBurnerReloadListener;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.slf4j.Logger;

import java.io.IOException;

import static fr.iglee42.createqualityoflife.items.ShadowRadianceChestplate.hasPropeller;
import static fr.iglee42.createqualityoflife.items.ShadowRadianceChestplate.isFansEnable;

@Mod(CreateQOL.MODID)
public class CreateQOL {

    public static final String MODID = "createqol";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }
    public CreateQOL(IEventBus modEventBus, ModContainer container) throws IOException, IllegalAccessException {

        IEventBus forgeEventBus = NeoForge.EVENT_BUS;

        CreateQOLFeaturesConfig.load();

        REGISTRATE.registerEventListeners(modEventBus);

        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();
        ModCreativeModeTabs.register(modEventBus);
        ModPackets.register();
        ModDataComponents.register(modEventBus);
        ModConditions.CONDITIONS.register(modEventBus);
        ModRecipeTypes.register(modEventBus);

        CreateQOLConfigs.register(ModLoadingContext.get(),container);

        RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CreateQOLClient.onCtorClient(modEventBus, forgeEventBus));

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ChippedSawBlockEntity::registerCapabilities);
        modEventBus.addListener(InventoryLinkerBlockEntity::registerCapabilities);
        modEventBus.addListener(this::registerCapabilities);

        forgeEventBus.addListener(this::removeFallDamage);
        forgeEventBus.addListener(this::registerReloadListener);

        //if (isActivate(Features.SHADOW_RADIANCE)){
        //    MysteriousItemConversionCategory.RECIPES.add(BlazeBurnerLiquidRecipe.create(AllItems.CHROMATIC_COMPOUND.asStack(), AllItems.SHADOW_STEEL.asStack()));
        //    MysteriousItemConversionCategory.RECIPES.add(BlazeBurnerLiquidRecipe.create(AllItems.CHROMATIC_COMPOUND.asStack(), AllItems.REFINED_RADIANCE.asStack()));
        //}
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                AllBlockEntityTypes.HEATER.get(),
                (be, context) -> {
                    if (!isActivate(Features.LIQUID_BLAZE_BURNER)) return null;
                    if (!(be instanceof IHaveTankMixin tank)) return null;
                    return tank.createQOL$tank();
                }
        );
    }

    private void registerReloadListener(AddReloadListenerEvent event){
        if (!isActivate(Features.LIQUID_BLAZE_BURNER)) return;
        event.addListener(LiquidBlazeBurnerReloadListener.INSTANCE);
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

    public void removeFallDamage(LivingIncomingDamageEvent event){
        if (!event.getSource().equals(event.getEntity().level().damageSources().fall())) return;
        if (!(event.getEntity() instanceof Player player)) return;

        if (player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.SHADOW_RADIANCE_CHESTPLATE)) {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
            if (BacktankUtil.getAllWithAir(player).isEmpty()) return;
            if (isFansEnable(stack) && !BacktankUtil.getAllWithAir(player).isEmpty() && hasPropeller(stack)) {
                event.setCanceled(true);
            }
        }
    }



}
