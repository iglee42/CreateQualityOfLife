package fr.iglee42.createqualityoflife;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.legacy.ChromaticCompoundColor;
import com.simibubi.create.foundation.particle.AirParticleData;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import fr.iglee42.createqualityoflife.client.renderer.GoggleArmorLayer;
import fr.iglee42.createqualityoflife.client.renderer.ArmorsArmsRenderer;
import fr.iglee42.createqualityoflife.client.renderer.EnderRenderer;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.items.armors.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.items.armors.ShadowSteelArmorItem;
import fr.iglee42.createqualityoflife.registries.*;
import fr.iglee42.createqualityoflife.statue.StatueModel;
import fr.iglee42.createqualityoflife.statue.StatueRenderer;
import fr.iglee42.createqualityoflife.utils.CommonKeysHandler;
import fr.iglee42.createqualityoflife.utils.KeyBindManager;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import fr.iglee42.createqualityoflife.utils.Pos3D;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static net.createmod.ponder.PonderClient.isGameActive;

public class CreateQOLClient {

    public static final ModelLayerLocation STATUE = new ModelLayerLocation(CreateQOL.asResource("statue"),"main");
    public static final ModelLayerLocation STATUE_INNER_ARMOR =  new ModelLayerLocation(CreateQOL.asResource("statue"),"inner_armor");
    public static final ModelLayerLocation STATUE_OUTER_ARMOR =  new ModelLayerLocation(CreateQOL.asResource("statue"),"outer_armor");
    private static final Logger log = LoggerFactory.getLogger(CreateQOLClient.class);

    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        QOLPartialModels.init();
        //if (CreateCasing.isExtendedCogsLoaded())CreateExtendedCogwheelsPartials.init();

        modEventBus.addListener(CreateQOLClient::clientInit);
        modEventBus.addListener(CreateQOLClient::addEntityRendererLayers);
        modEventBus.addListener(CreateQOLClient::registerKeys);
        modEventBus.addListener(CreateQOLClient::registerEntityRendererLayers);

        forgeEventBus.addListener(CreateQOLClient::onClientTick);
        forgeEventBus.addListener(CreateQOLClient::onPlayerTick);
        forgeEventBus.addListener(CreateQOLClient::onLivingJump);

    }

    @Mod.EventBusSubscriber(Dist.CLIENT)
    public static class TickEvents {
        @SubscribeEvent
        public static void onTickPre(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.START) return;
            onTick(true);
        }

        @SubscribeEvent
        public static void onTickPost(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.START) return;
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
        event.register(KeyBindManager.DASH_KEY);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        new QOLSprites();

        event.enqueueWork(() -> {
            ItemProperties.register(QOLItems.PLAYER_PAPER.get(),
                    CreateQOL.asResource("has_player"), (stack, level, living, id) -> stack.getOrCreateTag().contains(NBTConstants.NBT_LINKED_PLAYER) ? 1.0f : 0.0f);
        });
        event.enqueueWork(() -> {
            ItemProperties.register(QOLItems.SHADOW_RADIANCE_CHESTPLATE.get(),
                    CreateQOL.asResource("elytra"), (stack, level, living, id) -> ShadowRadianceChestplate.hasElytra(stack) ? 1.0f : 0.0f);
            ItemProperties.register(QOLItems.REFINED_RADIANCE_CHESTPLATE.get(),
                    CreateQOL.asResource("elytra"), (stack, level, living, id) -> ShadowRadianceChestplate.hasElytra(stack) ? 1.0f : 0.0f);
        });

        EntityRenderers.register(QOLEntityTypes.STATUE.get(), StatueRenderer::new);
        //MinecraftForge.EVENT_BUS.register(new KeyBindManager());
        //ModPonderTags.register();
        //PonderIndex.register();

    }


    public static void showPropellers(BlockState renderedState, int light, PoseStack ms, MultiBufferSource buffer, RenderType renderType, LevelAccessor level) {
        PartialModel partial = (AnimationTickHolder.getRenderTime(level)) % 10 >= 5 ? QOLPartialModels.SHADOW_RADIANCE_CHESTPLATE_PROPELLERS : QOLPartialModels.SHADOW_RADIANCE_CHESTPLATE_PROPELLERS_ALT;
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

    public static void onClientTick(TickEvent.ClientTickEvent event){
        Minecraft minecraft = Minecraft.getInstance();
        if (event.phase == TickEvent.Phase.END) {
            ArmorsArmsRenderer.clientTick();
            if (minecraft.player != null && minecraft.level != null) {
                if (!minecraft.isPaused() && !minecraft.player.isSpectator()) {
                    ItemStack chest = minecraft.player.getItemBySlot(EquipmentSlot.CHEST);
                    Item item = chest.getItem();
                    if ((!chest.isEmpty() && item instanceof ShadowRadianceChestplate && isFlying(minecraft.player) && !minecraft.player.isCreative())) {
                        if (minecraft.options.particles().get() != ParticleStatus.MINIMAL) {
                            showJetpackParticles(minecraft);
                        }
                    }
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


    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;

        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        if (!(legs.getItem() instanceof ShadowSteelArmorItem)) return;
        if (!CreateQOLConfigs.server().equipments.armors.voidWalking.get()) return;
        if (!NBTConstants.getOrDefault(legs,NBTConstants.NBT_VOID_WALK,true))return;

        Level level = player.level();
        boolean isOverVoid = (level.isEmptyBlock(player.blockPosition().below()) ||
                !level.loadedAndEntityCanStandOn(player.blockPosition().below(), player)) &&
                (level.getHeight(Heightmap.Types.MOTION_BLOCKING, player.getBlockX(), player.getBlockZ()) == level.getMinBuildHeight() ||
                        player.getY() <= level.getMinBuildHeight());

        boolean isSneaking = player.isCrouching();
        if (isOverVoid && !isSneaking) {
            player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);
            player.setOnGround(true);
            player.hurtMarked = true;
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player p = event.player;
        if (!p.level().isClientSide || !(p instanceof LocalPlayer player)) return;

        if (player.getAbilities().flying) return;

        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        if (!(legs.getItem() instanceof ShadowSteelArmorItem)) return;
        if (!CreateQOLConfigs.server().equipments.armors.voidWalking.get()) return;
        if (!NBTConstants.getOrDefault(legs,NBTConstants.NBT_VOID_WALK,true))return;

        Level level = player.level();

        boolean hasVoidUnder = true;


        for (int y = player.getBlockY(); y >= level.getMinBuildHeight(); y--) {
            if (!level.getBlockState(new BlockPos(player.getBlockX(),y,player.getBlockZ())).isAir()) {
                hasVoidUnder = false;
                break;
            }
        }

        if (hasVoidUnder) {
            player.setDeltaMovement(new Vec3(player.getDeltaMovement().x,0,player.getDeltaMovement().z));
            player.hurtMarked = true;
            player.setOnGround(true);
                float f;
                if (player.onGround() && !player.isDeadOrDying() && !player.isSwimming()) {
                    f = Math.min(0.1F, (float)player.getDeltaMovement().horizontalDistance());
                } else {
                    f = 0.0F;
                }

                player.bob += (f - player.bob) * 0.4F;
        }
    }
}