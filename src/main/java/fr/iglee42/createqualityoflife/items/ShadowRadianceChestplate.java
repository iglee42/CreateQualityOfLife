package fr.iglee42.createqualityoflife.items;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.tterrag.registrate.util.entry.ItemEntry;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.registries.ModArmorMaterials;
import fr.iglee42.createqualityoflife.utils.Features;
import fr.iglee42.createqualityoflife.utils.CommonKeysHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ShadowRadianceChestplate extends BacktankItem.Layered{

    private static final double FANS_ACCELERATION = 0.15D;
    private static final double FANS_SPEED = 0.25D;
    private static final double FANS_HOVER_SPEED = 0.20D;

    public ShadowRadianceChestplate(ArmorMaterial material, Properties properties, ResourceLocation textureLoc, Supplier<BacktankBlockItem> placeable) {
        super(material, properties, textureLoc, placeable);
    }
    @Override
    public void fillItemCategory(CreativeModeTab p_41391_, NonNullList<ItemStack> p_41392_) {
        if (CreateQOL.isActivate(Features.SHADOW_RADIANCE))super.fillItemCategory(p_41391_, p_41392_);
    }
    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (!player.isCreative() && !BacktankUtil.getAllWithAir(player).isEmpty())
            BacktankUtil.consumeAir(player, BacktankUtil.getAllWithAir(player).get(0), 0.001f);
        else if (!player.isCreative()) return;
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 1, false, false));
        if (player.isCreative()) return;
        if (isFansEnable(stack) && !BacktankUtil.getAllWithAir(player).isEmpty() && hasPropeller(stack)) {
            boolean hover = isHoverEnable(stack);
            boolean jumpKeyActive = CommonKeysHandler.isHoldingUp(player);
            boolean shiftKeyActive = CommonKeysHandler.isHoldingDown(player);
            if (!player.isSwimming()) {
                if (jumpKeyActive) {
                    if (!hover) {
                        pushVertically(player, Math.min(player.getDeltaMovement().get(Direction.Axis.Y) + FANS_ACCELERATION, FANS_SPEED));
                    } else {
                        if (shiftKeyActive) {
                            pushVertically(player, Math.min(player.getDeltaMovement().get(Direction.Axis.Y) + FANS_ACCELERATION, -0.0D));
                        } else {
                            pushVertically(player, Math.min(player.getDeltaMovement().get(Direction.Axis.Y) + FANS_ACCELERATION, FANS_HOVER_SPEED));
                        }
                    }
                } else {
                    if (hover) {
                        if (shiftKeyActive)
                            pushVertically(player, Math.min(player.getDeltaMovement().get(Direction.Axis.Y) + FANS_ACCELERATION, -(FANS_HOVER_SPEED * 2)));
                        else
                            pushVertically(player, Math.min(player.getDeltaMovement().get(Direction.Axis.Y) + FANS_ACCELERATION, -0.00D));
                    } else {
                        if (shiftKeyActive) {
                            pushVertically(player, Math.min(player.getDeltaMovement().get(Direction.Axis.Y) + FANS_ACCELERATION, -(FANS_HOVER_SPEED * 2)));
                        } else {
                            pushVertically(player,Math.min(player.getDeltaMovement().get(Direction.Axis.Y) + FANS_ACCELERATION, -(FANS_HOVER_SPEED*1.5)));
                        }
                    }
                }

                if (CommonKeysHandler.isHoldingForwards(player)) {
                    player.moveRelative(1, new Vec3(0, 0, player.isSprinting() ? 0.05 * 1.125 : 0.05));
                }
                if (CommonKeysHandler.isHoldingBackwards(player)) {
                    player.moveRelative(1, new Vec3(0, 0, -0.05 * 0.75F));
                }
                if (CommonKeysHandler.isHoldingLeft(player)) {
                    player.moveRelative(1, new Vec3(0.05, 0, 0));
                }
                if (CommonKeysHandler.isHoldingRight(player)) {
                    player.moveRelative(1, new Vec3(-0.05, 0, 0));
                }
                if (!player.getCommandSenderWorld().isClientSide()) {
                    player.fallDistance = 0.0F;
                    if (player instanceof ServerPlayer) {
                        ((ServerPlayer) player).connection.aboveGroundTickCount = 0;
                    }
                }
            }else {
                if (jumpKeyActive) {
                    player.moveRelative(1, new Vec3(0, 0, 0.05 * 1.125));
                }
            }
        }
    }

    private void pushVertically(Player p,double y){
        Vec3 motion = p.getDeltaMovement();
        p.setDeltaMovement(motion.get(Direction.Axis.X), y, motion.get(Direction.Axis.Z));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level p_41422_, List<Component> components, TooltipFlag p_41424_) {
        components.add(Component.literal("Air : ").withStyle(ChatFormatting.GOLD).append(Component.literal(String.format("%.1f",BacktankUtil.getAir(stack))).withStyle(ChatFormatting.YELLOW)).append(Component.literal("/"+BacktankUtil.maxAir(stack)+",0").withStyle(ChatFormatting.GOLD)));
        components.add(Component.literal("Propeller : ").withStyle(ChatFormatting.GOLD).append(Component.literal(hasPropeller(stack) ? "installed" : "not installed").withStyle(ChatFormatting.YELLOW)));
        if (hasPropeller(stack)) {
            components.add(Component.empty());
            components.add(Component.literal("Fans : ").withStyle(ChatFormatting.GOLD).append(Component.literal(String.valueOf(isFansEnable(stack))).withStyle(ChatFormatting.YELLOW)));
            components.add(Component.literal("Hover : ").withStyle(ChatFormatting.GOLD).append(Component.literal(String.valueOf(isHoverEnable(stack))).withStyle(ChatFormatting.YELLOW)));
        }
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }

    public static void toggleFans(ItemStack chestplate,Player p) {
        chestplate.getOrCreateTag().putBoolean("FansEnable", chestplate.getOrCreateTag().contains("FansEnable") && !chestplate.getOrCreateTag().getBoolean("FansEnable"));
        boolean fans = chestplate.getOrCreateTag().getBoolean("FansEnable");
        p.displayClientMessage(Component.literal("Fans : ").append(Component.literal(String.valueOf(fans)).withStyle(fans ? ChatFormatting.GREEN : ChatFormatting.RED)),true);
    }
    public static void toggleHover(ItemStack chestplate,Player p) {
        chestplate.getOrCreateTag().putBoolean("HoverEnable", chestplate.getOrCreateTag().contains("HoverEnable") && !chestplate.getOrCreateTag().getBoolean("HoverEnable"));
        boolean hover = chestplate.getOrCreateTag().getBoolean("HoverEnable");
        p.displayClientMessage(Component.literal("Hover : ").append(Component.literal(String.valueOf(hover)).withStyle(hover ? ChatFormatting.GREEN : ChatFormatting.RED)),true);
    }
    public static boolean hasPropeller(ItemStack chestplate){
        return chestplate.getOrCreateTag().contains("Propeller") && chestplate.getOrCreateTag().getBoolean("Propeller");
    }
    public static boolean isFansEnable(ItemStack chestplate){
        return !chestplate.getOrCreateTag().contains("FansEnable") || chestplate.getOrCreateTag().getBoolean("FansEnable");
    }
    public static boolean isHoverEnable(ItemStack chestplate){
        return chestplate.getOrCreateTag().contains("HoverEnable") && chestplate.getOrCreateTag().getBoolean("HoverEnable");
    }

}
