package fr.iglee42.createqualityoflife;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import fr.iglee42.createqualityoflife.blockentitites.*;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.config.CreateQOLFeaturesConfig;
import fr.iglee42.createqualityoflife.items.armors.RefinedRadianceArmorItem;
import fr.iglee42.createqualityoflife.items.tools.refinedradiance.*;
import fr.iglee42.createqualityoflife.items.tools.shadowsteel.ShadowSteelAxe;
import fr.iglee42.createqualityoflife.items.tools.shadowsteel.ShadowSteelPickaxe;
import fr.iglee42.createqualityoflife.items.tools.shadowsteel.ShadowSteelShovel;
import fr.iglee42.createqualityoflife.items.tools.shadowsteel.ShadowSteelSword;
import fr.iglee42.createqualityoflife.registries.*;
import fr.iglee42.createqualityoflife.statue.animation.PublishedAnimationsManager;
import fr.iglee42.createqualityoflife.utils.EnderPackagersNetworkHandler;
import fr.iglee42.createqualityoflife.utils.Features;
import fr.iglee42.createqualityoflife.utils.IHaveTankMixin;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import fr.iglee42.createqualityoflife.utils.liquidblazeburners.LiquidBlazeBurnerReloadListener;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.slf4j.Logger;

import java.io.IOException;

import static fr.iglee42.createqualityoflife.items.armors.ShadowRadianceChestplate.hasPropeller;
import static fr.iglee42.createqualityoflife.items.armors.ShadowRadianceChestplate.isFansEnable;

@Mod(CreateQOL.MODID)
public class CreateQOL {

    public static final String MODID = "createqol";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final EnderPackagersNetworkHandler ENDER_PACKAGER_NETWORK_HANDLER = new EnderPackagersNetworkHandler();


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

        QOLBlocks.register();
        QOLBlockEntities.register();
        QOLItems.register();
        QOLCreativeModeTabs.register(modEventBus);
        QOLPackets.register();
        QOLDataComponents.register(modEventBus);
        QOLConditions.CONDITIONS.register(modEventBus);
        QOLRecipeTypes.register(modEventBus);
        QOLEntityDataSerializers.ENTITY_SERIALIZERS.register(modEventBus);
        QOLEntityTypes.ENTITIES.register(modEventBus);
        QOLMenuTypes.register();

        CreateQOLConfigs.register(ModLoadingContext.get(),container);

        RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CreateQOLClient.onCtorClient(modEventBus, forgeEventBus));

        container.registerExtensionPoint(IConfigScreenFactory.class,((modContainer, screen) -> new BaseConfigScreen(screen, MODID)));

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ChippedSawBlockEntity::registerCapabilities);
        modEventBus.addListener(InventoryLinkerBlockEntity::registerCapabilities);
        modEventBus.addListener(TrashCanBlockEntity::registerCapabilities);
        modEventBus.addListener(BrassTrashCanBlockEntity::registerCapabilities);
        modEventBus.addListener(EnderPackagerBlockEntity::registerCapabilities);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(QOLEntityTypes::registerEntityAttributes);

        forgeEventBus.addListener(this::removeFallDamage);
        forgeEventBus.addListener(this::registerReloadListener);
        forgeEventBus.addListener(this::playerJoin);
        forgeEventBus.addListener(this::onWorldTick);
        forgeEventBus.addListener(this::itemTooltips);

        registerToolsEvents(modEventBus,forgeEventBus);


        //if (isActivate(Features.SHADOW_RADIANCE)){
        //    MysteriousItemConversionCategory.RECIPES.add(BlazeBurnerLiquidRecipe.create(AllItems.CHROMATIC_COMPOUND.asStack(), AllItems.SHADOW_STEEL.asStack()));
        //    MysteriousItemConversionCategory.RECIPES.add(BlazeBurnerLiquidRecipe.create(AllItems.CHROMATIC_COMPOUND.asStack(), AllItems.REFINED_RADIANCE.asStack()));
        //}
    }

    private void registerToolsEvents(IEventBus modEventBus, IEventBus forgeEventBus) {
        forgeEventBus.addListener(QOLConfigurableItem::modifyAttributes);
        forgeEventBus.addListener(BlockEvent.BreakEvent.class, RefinedRadiancePickaxe::mineBlock);
        forgeEventBus.addListener(BlockEvent.BreakEvent.class, ShadowSteelPickaxe::mineBlock);
        forgeEventBus.addListener(BlockEvent.BreakEvent.class, RefinedRadianceAxe::mineBlock);
        forgeEventBus.addListener(BlockEvent.BreakEvent.class, RefinedRadianceHoe::mineBlock);
        forgeEventBus.addListener(BlockEvent.BreakEvent.class, ShadowSteelShovel::mineBlock);
        forgeEventBus.addListener(RefinedRadianceShovel::blockDrops);
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

        if (player.getItemBySlot(EquipmentSlot.CHEST).is(QOLItems.SHADOW_RADIANCE_CHESTPLATE)) {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
            if (BacktankUtil.getAllWithAir(player).isEmpty()) return;
            if ((isFansEnable(stack) && !BacktankUtil.getAllWithAir(player).isEmpty() && hasPropeller(stack)) || CreateQOLConfigs.server().equipments.armors.dashAllowed.get()) {
                event.setCanceled(true);
            }
        }
        if (player.getItemBySlot(EquipmentSlot.CHEST).is(QOLItems.SHADOW_STEEL_CHESTPLATE)) {
            if (BacktankUtil.getAllWithAir(player).isEmpty()) return;
            if (!CreateQOLConfigs.server().equipments.armors.dashAllowed.get()) {
                event.setCanceled(true);
            }
        }
    }

    private void playerJoin(EntityJoinLevelEvent event){
        if (!(event.getEntity() instanceof Player p))return;
        if (event.getLevel().isClientSide) return;
        ServerPlayer player = (ServerPlayer) p;
        if ( event.getLevel().getServer() == null) return;
        if (event.getLevel().getServer().getProfilePermissions(p.getGameProfile()) < 1) return;
        if (isActivate(Features.STATUE) && CreateQOLConfigs.server().experimentalWarning.get())
            player.displayClientMessage(Component.literal("Warning: Statue are still a beta feature, some bugs and crash might appear.\nPlease report them on https://issues-qol.iglee.fr").withStyle(ChatFormatting.YELLOW),false);
    }




    public void onWorldTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide) {
            return;
        }

        PublishedAnimationsManager manager = PublishedAnimationsManager.get(event.getLevel());
        manager.tick();
    }

    public void itemTooltips(ItemTooltipEvent event){
        if (!event.getItemStack().is(Items.FIREWORK_ROCKET)) return;
        if (isActivate(Features.SHADOW_RADIANCE) && CreateQOLConfigs.server().equipments.armors.elytraBoostAllowed.get() && CreateQOLConfigs.server().equipments.armors.useFireworksForBoost.get()){
            event.getToolTip().add(2,CreateQOLLang.translateDirect("chestplate.use_fireworks").withStyle(ChatFormatting.YELLOW));
        }
    }
}
