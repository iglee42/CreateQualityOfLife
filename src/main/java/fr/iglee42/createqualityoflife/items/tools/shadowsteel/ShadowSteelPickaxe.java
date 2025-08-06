package fr.iglee42.createqualityoflife.items.tools.shadowsteel;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import fr.iglee42.createqualityoflife.registries.QOLTiers;
import fr.iglee42.createqualityoflife.utils.DestroyUtils;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ShadowSteelPickaxe extends PickaxeItem implements QOLConfigurableItem {
    public ShadowSteelPickaxe(Properties p_42964_) {
        super(QOLTiers.SHADOW_STEEL, p_42964_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),true);
        if (player.isCrouching()) return InteractionResultHolder.pass(player.getItemInHand(hand));
        toggleAbility(player.getItemInHand(hand),player);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    public static void toggleAbility(ItemStack stack, Player p) {
        if (!CreateQOLConfigs.server().equipments.tools.digging.get()){
            p.displayClientMessage(Component.literal("Digging is disabled by the config").withStyle(ChatFormatting.RED),true);
            return;
        }
        boolean enable = !stack.getOrDefault(QOLDataComponents.DIGGING,false);
        stack.set(QOLDataComponents.DIGGING, enable);
        p.displayClientMessage(Component.literal("Digging : ").append(QOLConfigurableItem.chooseState(true,true,enable,false,true)).withStyle(enable ? ChatFormatting.GREEN : ChatFormatting.RED),true);
    }

    public static void mineBlock(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.isCanceled()) return;
        if (!event.getPlayer().getMainHandItem().is(QOLItems.SHADOW_STEEL_PICKAXE.get()) && !event.getPlayer().getMainHandItem().is(QOLItems.SHADOW_RADIANCE_PICKAXE.get())) return;
        if (!event.getPlayer().getMainHandItem().getOrDefault(QOLDataComponents.DIGGING, false)) return;
        if (!event.getPlayer().getMainHandItem().isCorrectToolForDrops(event.getState())) return;
        BlockPos startPos = event.getPos();
        
        Direction dir = getTargetedBlockFace((ServerPlayer) event.getPlayer());

        if (dir == null) return;
        for (int y = 0; y <= 2; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos offsetPos = startPos
                            .relative(dir.getOpposite(), y);

                    if (dir.getAxis().equals(Direction.Axis.Y)) {
                        offsetPos = offsetPos.offset(x, 0, z);
                    } else if (dir.getAxis().equals(Direction.Axis.Z)) {
                        offsetPos = offsetPos.offset(x, z, 0);
                    } else if (dir.getAxis().equals(Direction.Axis.X)) {
                        offsetPos = offsetPos.offset(0, x, z);
                    }

                    BlockState cState = event.getLevel().getBlockState(offsetPos);
                    if (event.getPlayer().getMainHandItem().isCorrectToolForDrops(cState)) {
                        DestroyUtils.destroyBlock(event.getPlayer().level(),((ServerPlayer) event.getPlayer()).gameMode.getGameModeForPlayer(),event.getPlayer(),((ServerPlayer) event.getPlayer()).gameMode,offsetPos,event.getPos());
                    }
                }
            }
        }
    }

    public static Direction getTargetedBlockFace(ServerPlayer player) {
        Level level = player.level();
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 lookVec = player.getViewVector(1.0F);
        double reach = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);

        Vec3 reachVec = eyePos.add(lookVec.scale(reach));
        ClipContext context = new ClipContext(
                eyePos, reachVec,
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player
        );

        BlockHitResult hitResult = level.clip(context);

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            return hitResult.getDirection();
        }

        return null;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean offHand) {
        super.inventoryTick(stack, level, entity, slot, offHand);
        invTick(stack, level, entity, slot, offHand);
    }

    @Override
    public Type type() {
        return Type.ITEM;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext p_41422_, List<Component> components, TooltipFlag p_41424_) {
        if (!stack.getOrDefault(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT).isEnable(ItemTooltips.Tooltip.OPTIONS)) return;
        components.add(Component.literal("Reach : ")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.reach.get(), true, stack.getOrDefault(QOLDataComponents.REACH, true), false, true)));
        components.add(Component.literal("Digging : ")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.digging.get(), true, stack.getOrDefault(QOLDataComponents.DIGGING, false), false, true)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }

    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(Configuration.ofBool("Digging", stack.getOrDefault(QOLDataComponents.DIGGING, false), QOLDataComponents.DIGGING,
                List.of("Activate the 3x3x3 digging when mining a block"), (e, oe) -> CreateQOLConfigs.server().equipments.tools.digging.get()));
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        if (BacktankUtil.canAbsorbDamage(entity, getMaxDamage(stack))) return 0;
        return super.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return BacktankUtil.isBarVisible(stack, getMaxDamage(stack));
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return BacktankUtil.getBarWidth(stack, getMaxDamage(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BacktankUtil.getBarColor(stack, getMaxDamage(stack));
    }

    @Override
    public ReachType reachType(ItemStack stack) {
        return ReachType.BLOCK;
    }
}
