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
import fr.iglee42.createqualityoflife.registries.*;
import fr.iglee42.createqualityoflife.utils.Features;
import fr.iglee42.createqualityoflife.utils.IHaveTankMixin;
import fr.iglee42.createqualityoflife.utils.liquidblazeburners.LiquidBlazeBurnerReloadListener;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
    public CreateQOL() throws IOException, IllegalAccessException {

        IEventBus modEventBus = FMLJavaModLoadingContext.get()
                .getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        CreateQOLFeaturesConfig.load();

        REGISTRATE.registerEventListeners(modEventBus);

        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();
        ModCreativeModeTabs.register(modEventBus);
        ModPackets.registerPackets();
        ModRecipeTypes.register(modEventBus);
        ModEntityDataSerializers.ENTITY_SERIALIZERS.register(modEventBus);
        ModEntityTypes.ENTITIES.register(modEventBus);
        ModMenuTypes.register();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CreateQOLClient.onCtorClient(modEventBus, forgeEventBus));
        CreateQOLConfigs.register(ModLoadingContext.get());

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModEntityTypes::registerEntityAttributes);

        forgeEventBus.addListener(this::removeFallDamage);
        forgeEventBus.addListener(this::registerReloadListener);
        forgeEventBus.addListener(this::playerJoin);

        //if (isActivate(Features.SHADOW_RADIANCE)){
        //    MysteriousItemConversionCategory.RECIPES.add(BlazeBurnerLiquidRecipe.create(AllItems.CHROMATIC_COMPOUND.asStack(), AllItems.SHADOW_STEEL.asStack()));
        //    MysteriousItemConversionCategory.RECIPES.add(BlazeBurnerLiquidRecipe.create(AllItems.CHROMATIC_COMPOUND.asStack(), AllItems.REFINED_RADIANCE.asStack()));
        //}
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
    }

    public void removeFallDamage(LivingDamageEvent event){
        if (!event.getSource().equals(event.getEntity().level().damageSources().fall())) return;
        if (!(event.getEntity() instanceof Player player)) return;

        if (player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.SHADOW_RADIANCE_CHESTPLATE.asItem())) {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
            if (BacktankUtil.getAllWithAir(player).isEmpty()) return;
            if (isFansEnable(stack) && !BacktankUtil.getAllWithAir(player).isEmpty() && hasPropeller(stack)) {
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



}
