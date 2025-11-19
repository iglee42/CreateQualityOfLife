package fr.iglee42.createqualityoflife;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import fr.iglee42.createqualityoflife.conditions.FeatureLoadedCondition;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
    public CreateQOL() throws IOException, IllegalAccessException {

        IEventBus modEventBus = FMLJavaModLoadingContext.get()
                .getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        CreateQOLFeaturesConfig.load();

        REGISTRATE.registerEventListeners(modEventBus);

        QOLBlocks.register();
        QOLBlockEntities.register();
        QOLItems.register();
        QOLFluids.register();
        QOLCreativeModeTabs.register(modEventBus);
        QOLPackets.registerPackets();
        QOLRecipeTypes.register(modEventBus);
        QOLEntityTypes.ENTITIES.register(modEventBus);
        QOLMenuTypes.register();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CreateQOLClient.onCtorClient(modEventBus, forgeEventBus));
        CreateQOLConfigs.register(ModLoadingContext.get());

        modEventBus.addListener(this::commonSetup);
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
        forgeEventBus.addListener(e->{
            if (e instanceof BlockEvent.BreakEvent)RefinedRadiancePickaxe.mineBlock((BlockEvent.BreakEvent) e);
        });
        forgeEventBus.addListener(e->{
            if (e instanceof BlockEvent.BreakEvent)ShadowSteelPickaxe.mineBlock((BlockEvent.BreakEvent) e);
        });
        forgeEventBus.addListener(e->{
            if (e instanceof BlockEvent.BreakEvent)ShadowSteelAxe.mineBlock((BlockEvent.BreakEvent) e);
        });
        forgeEventBus.addListener(e->{
            if (e instanceof BlockEvent.BreakEvent)RefinedRadianceHoe.mineBlock((BlockEvent.BreakEvent) e);
        });
        forgeEventBus.addListener(e->{
            if (e instanceof BlockEvent.BreakEvent)ShadowSteelShovel.mineBlock((BlockEvent.BreakEvent) e);
        });
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
        return new ResourceLocation(MODID,path);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(()-> CraftingHelper.register(FeatureLoadedCondition.Serializer.INSTANCE));
        QOLFluids.registerFluidInteractions();
    }

    public void removeFallDamage(LivingDamageEvent event){
        if (!event.getSource().equals(event.getEntity().level().damageSources().fall())) return;
        if (!(event.getEntity() instanceof Player player)) return;

        if (player.getItemBySlot(EquipmentSlot.CHEST).is(QOLItems.SHADOW_RADIANCE_CHESTPLATE.asItem())) {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
            if (BacktankUtil.getAllWithAir(player).isEmpty()) return;
            if ((isFansEnable(stack) && !BacktankUtil.getAllWithAir(player).isEmpty() && hasPropeller(stack)) || CreateQOLConfigs.server().equipments.armors.dashAllowed.get()) {
                event.setCanceled(true);
            }
        }
        if (player.getItemBySlot(EquipmentSlot.CHEST).is(QOLItems.SHADOW_STEEL_CHESTPLATE.asItem())) {
            if (BacktankUtil.getAllWithAir(player).isEmpty()) return;
            if (!CreateQOLConfigs.server().equipments.armors.dashAllowed.get()) {
                event.setCanceled(true);
            }
        }
    }

    private void playerJoin(PlayerEvent.PlayerLoggedInEvent event){
        Player p = event.getEntity();
        if (p.level().isClientSide) return;
        ServerPlayer player = (ServerPlayer) p;
        if (!p.hasPermissions(1)) return;
        if (isActivate(Features.STOCK_MANAGER) && CreateQOLConfigs.server().experimentalWarning.get())
            player.displayClientMessage(CreateQOLLang.translateDirect("client_message.header").withStyle(ChatFormatting.YELLOW).append(CreateQOLLang.translateDirect("client_message.warning").withStyle(ChatFormatting.WHITE)),false);
    }





    public void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.level.isClientSide) {
            return;
        }

        PublishedAnimationsManager manager = PublishedAnimationsManager.get(event.level);
        manager.tick((ServerLevel) event.level);
    }

    public void itemTooltips(ItemTooltipEvent event){
        if (!event.getItemStack().is(Items.FIREWORK_ROCKET)) return;
        if (isActivate(Features.SHADOW_RADIANCE) && CreateQOLConfigs.server().equipments.armors.elytraBoostAllowed.get() && CreateQOLConfigs.server().equipments.armors.useFireworksForBoost.get()){
            event.getToolTip().add(2,CreateQOLLang.translateDirect("armor.ability.use_fireworks").withStyle(ChatFormatting.YELLOW));
        }
    }
}
