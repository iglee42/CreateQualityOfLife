package fr.iglee42.createqualityoflife;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.ContraptionHandler;
import com.simibubi.create.content.contraptions.actors.seat.ContraptionPlayerPassengerRotation;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import com.simibubi.create.content.contraptions.chassis.ChassisRangeDisplay;
import com.simibubi.create.content.contraptions.minecart.CouplingHandlerClient;
import com.simibubi.create.content.contraptions.minecart.CouplingPhysics;
import com.simibubi.create.content.contraptions.minecart.CouplingRenderer;
import com.simibubi.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.simibubi.create.content.contraptions.render.ContraptionRenderInfoManager;
import com.simibubi.create.content.contraptions.wrench.RadialWrenchHandler;
import com.simibubi.create.content.decoration.girder.GirderWrenchBehavior;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.armor.CardboardArmorStealthOverlay;
import com.simibubi.create.content.equipment.armor.NetheriteBacktankFirstPersonRenderer;
import com.simibubi.create.content.equipment.blueprint.BlueprintOverlayRenderer;
import com.simibubi.create.content.equipment.clipboard.ClipboardValueSettingsHandler;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripRenderHandler;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandlerClient;
import com.simibubi.create.content.equipment.zapper.terrainzapper.WorldshaperRenderHandler;
import com.simibubi.create.content.kinetics.KineticDebugger;
import com.simibubi.create.content.kinetics.belt.item.BeltConnectorHandler;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorConnectionHandler;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorInteractionHandler;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorRidingHandler;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointHandler;
import com.simibubi.create.content.logistics.depot.EjectorTargetHandler;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnectionHandler;
import com.simibubi.create.content.logistics.packagePort.PackagePortTargetSelectionHandler;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedClientHandler;
import com.simibubi.create.content.logistics.tableCloth.TableClothOverlayRenderer;
import com.simibubi.create.content.redstone.displayLink.ClickToLinkBlockItem;
import com.simibubi.create.content.redstone.link.LinkRenderer;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerClientHandler;
import com.simibubi.create.content.trains.CameraDistanceModifier;
import com.simibubi.create.content.trains.TrainHUD;
import com.simibubi.create.content.trains.entity.TrainRelocator;
import com.simibubi.create.content.trains.track.CurvedTrackInteraction;
import com.simibubi.create.content.trains.track.TrackPlacement;
import com.simibubi.create.content.trains.track.TrackTargetingClient;
import com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueHandler;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueRenderer;
import com.simibubi.create.foundation.particle.AirParticleData;

import com.simibubi.create.foundation.sound.SoundScapes;
import com.simibubi.create.foundation.utility.CameraAngleAnimationService;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import com.simibubi.create.foundation.utility.TickBasedCache;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import fr.iglee42.createqualityoflife.client.GoggleArmorLayer;
import fr.iglee42.createqualityoflife.client.ShadowRadianceFirstPersonRenderer;
import fr.iglee42.createqualityoflife.client.renderer.EnderRenderer;
import fr.iglee42.createqualityoflife.items.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.registries.*;
import fr.iglee42.createqualityoflife.statue.StatueArmorModel;
import fr.iglee42.createqualityoflife.statue.StatueModel;
import fr.iglee42.createqualityoflife.statue.StatueRenderer;
import fr.iglee42.createqualityoflife.utils.CommonKeysHandler;
import fr.iglee42.createqualityoflife.utils.KeyBindManager;
import fr.iglee42.createqualityoflife.utils.Pos3D;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static fr.iglee42.createqualityoflife.CreateQOL.MODID;
import static net.createmod.ponder.PonderClient.isGameActive;

public class CreateQOLClient {

    public static final ModelLayerLocation STATUE = new ModelLayerLocation(CreateQOL.asResource("statue"),"main");
    public static final ModelLayerLocation STATUE_INNER_ARMOR =  new ModelLayerLocation(CreateQOL.asResource("statue"),"inner_armor");
    public static final ModelLayerLocation STATUE_OUTER_ARMOR =  new ModelLayerLocation(CreateQOL.asResource("statue"),"outer_armor");
    private static final Logger log = LoggerFactory.getLogger(CreateQOLClient.class);

    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        ModPartialModels.init();
        //if (CreateCasing.isExtendedCogsLoaded())CreateExtendedCogwheelsPartials.init();

        modEventBus.addListener(CreateQOLClient::clientInit);
        modEventBus.addListener(CreateQOLClient::addEntityRendererLayers);
        modEventBus.addListener(CreateQOLClient::registerKeys);
        modEventBus.addListener(CreateQOLClient::registerEntityRendererLayers);

        forgeEventBus.addListener(CreateQOLClient::onClientTick);

    }

    @EventBusSubscriber(Dist.CLIENT)
    public static class TickEvents {
        @SubscribeEvent
        public static void onTickPre(ClientTickEvent.Pre event) {
            onTick(true);
        }

        @SubscribeEvent
        public static void onTickPost(ClientTickEvent.Post event) {
            onTick(false);
        }

        public static void onTick(boolean isPreEvent) {
            if (!isGameActive())
                return;

            Level world = Minecraft.getInstance().level;
            if (isPreEvent) {
                return;
            }
            EnderRenderer.tick();
        }
    }
    public static void registerKeys(RegisterKeyMappingsEvent event){
        event.register(KeyBindManager.FANS_KEY);
        event.register(KeyBindManager.HOVER_KEY);
        event.register(KeyBindManager.ELYTRA_KEY);
        event.register(KeyBindManager.OPEN_ARMOR_CONFIG);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        new ModSprites();

        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.PLAYER_PAPER.get(),
                    CreateQOL.asResource("hasplayer"), (stack, level, living, id) -> stack.has(ModDataComponents.LINKED_PLAYER) ? 1.0f : 0.0f);
        });

        EntityRenderers.register(ModEntityTypes.STATUE.get(), StatueRenderer::new);
        //MinecraftForge.EVENT_BUS.register(new KeyBindManager());
        //ModPonderTags.register();
        //PonderIndex.register();

    }


    public static void showPropellers(BlockState renderedState, int light, PoseStack ms, MultiBufferSource buffer, RenderType renderType, LevelAccessor level) {
        PartialModel partial = (AnimationTickHolder.getRenderTime(level)) % 10 >= 5 ? ModPartialModels.SHADOW_RADIANCE_CHESTPLATE_PROPELLERS : ModPartialModels.SHADOW_RADIANCE_CHESTPLATE_PROPELLERS_ALT;
        SuperByteBuffer propellers = CachedBuffers.partial(partial,renderedState);
        propellers
                .light(light)
                .renderInto(ms, buffer.getBuffer(renderType));
    }

    public static void addEntityRendererLayers(EntityRenderersEvent.AddLayers event){
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

        GoggleArmorLayer.registerOnAll(dispatcher);
    }

    public static void registerEntityRendererLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(STATUE,StatueModel::createBodyLayer);
        event.registerLayerDefinition(STATUE_INNER_ARMOR, ()-> ArmorStandArmorModel.createBodyLayer(LayerDefinitions.INNER_ARMOR_DEFORMATION));
        event.registerLayerDefinition(STATUE_OUTER_ARMOR, ()-> ArmorStandArmorModel.createBodyLayer(LayerDefinitions.OUTER_ARMOR_DEFORMATION));

    }

    public static void onClientTick(ClientTickEvent.Post event){
        ShadowRadianceFirstPersonRenderer.clientTick();
        Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null && minecraft.level != null) {
                if (!minecraft.isPaused() && !minecraft.player.isSpectator()) {
                    ItemStack chest = minecraft.player.getItemBySlot(EquipmentSlot.CHEST);
                    Item item = chest.getItem();
                    if ((!chest.isEmpty() && item instanceof ShadowRadianceChestplate && isFlying(minecraft.player) && !minecraft.player.isCreative())) {
                        if (minecraft.options.particles().get() != ParticleStatus.MINIMAL) {
                            showJetpackParticles(minecraft);
                        }
                        // Play sounds:
                        //if (SimplyJetpacksConfig.enableJetpackSounds.get() && !JetpackSound.playing(minecraft.player.getId())) {
                        //    minecraft.getSoundManager().play(new JetpackSound(minecraft.player));
                        //}
                    }
                }
        }
    }
    private static void showJetpackParticles(Minecraft minecraft) {
        Player player = minecraft.player;
        Random rand = new Random();
        float random = (rand.nextFloat() - 0.5F) * 0.1F;
        double[] sneakBonus = player.isCrouching() ? new double[]{-0.30, -0.10} : new double[]{0, 0};
        Pos3D playerPos = new Pos3D(player).translate(0, 1.5, 0);
        Pos3D vCenter = new Pos3D((rand.nextFloat() - 0.5F) * 0.25F, -0.90 + sneakBonus[1], -0.5 + sneakBonus[0]).rotate(player.yBodyRot, 0);
        Pos3D v = playerPos.translate(vCenter).translate(new Pos3D(player.getDeltaMovement()));
        ParticleOptions particle = player.isUnderWater() ? ParticleTypes.BUBBLE : new AirParticleData(0,0.002F);
        minecraft.particleEngine.createParticle(particle, v.x, v.y, v.z, random, -0.2D, random);
    }

    public static boolean isFlying(Player player) {
        ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof ShadowRadianceChestplate) {
                if (ShadowRadianceChestplate.isFansEnable(stack) && !BacktankUtil.getAllWithAir(player).isEmpty()) {
                    if (ShadowRadianceChestplate.isHoverEnable(stack)) {
                        return !player.onGround();
                    } else {
                        return CommonKeysHandler.isHoldingUp(player);
                    }
                }
            }
        }
        return false;
    }
}