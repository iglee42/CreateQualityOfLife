package fr.iglee42.createqualityoflife.items.tools.refinedradiance;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import fr.iglee42.createqualityoflife.registries.QOLTiers;
import fr.iglee42.createqualityoflife.utils.DestroyUtils;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.createmod.catnip.data.Iterate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.stats.Stats;
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
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class RefinedRadiancePickaxe extends PickaxeItem implements QOLConfigurableItem {
    public RefinedRadiancePickaxe(Properties p_42964_) {
        super(QOLTiers.REFINED_RADIANCE, p_42964_);
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
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.reach.get(), true, stack.getOrDefault(QOLDataComponents.REACH,true), false, true)));
        components.add(Component.literal("Vein Mine : ")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.veinMine.get(), true, stack.getOrDefault(QOLDataComponents.VEIN_MINE,false), false, true)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }


    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(Configuration.ofBool("Vein Mine",stack.getOrDefault(QOLDataComponents.VEIN_MINE,false),QOLDataComponents.VEIN_MINE,
                List.of("Should all the blocks of the same types be destroy when mining"),(e,oe)->CreateQOLConfigs.server().equipments.tools.veinMine.get()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),true);
        if (player.isCrouching()) return InteractionResultHolder.pass(player.getItemInHand(hand));
        toggleAbility(player.getItemInHand(hand),player);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    public static void toggleAbility(ItemStack stack, Player p) {
        if (!CreateQOLConfigs.server().equipments.tools.veinMine.get()){
            p.displayClientMessage(Component.literal("Vein Mine is disabled by the config").withStyle(ChatFormatting.RED),true);
            return;
        }
        boolean enable = !stack.getOrDefault(QOLDataComponents.VEIN_MINE,false);
        stack.set(QOLDataComponents.VEIN_MINE, enable);
        p.displayClientMessage(Component.literal("Vein Mine : ").append(QOLConfigurableItem.chooseState(true,true,enable,false,true)).withStyle(enable ? ChatFormatting.GREEN : ChatFormatting.RED),true);
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

    public static void mineBlock(BlockEvent.BreakEvent event){
        if (event.getLevel().isClientSide())return;
        if (event.isCanceled()) return;
        if (!event.getPlayer().getMainHandItem().is(QOLItems.REFINED_RADIANCE_PICKAXE.get())) return;
        if (!event.getPlayer().getMainHandItem().getOrDefault(QOLDataComponents.VEIN_MINE,false)) return;
        if (!event.getPlayer().getMainHandItem().isCorrectToolForDrops(event.getState())) return;
        int max = CreateQOLConfigs.server().equipments.tools.veinMineMaxBlocks.get();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toExplore = new ArrayDeque<>();

        List<BlockPos> first = getValidBlocksNextTo(event.getLevel(), event.getPos(), event.getPlayer().getMainHandItem());
        for (BlockPos pos : first) {
            if (event.getLevel().getBlockState(pos).is(event.getState().getBlock())) {
                visited.add(pos);
                toExplore.add(pos);
                if (visited.size() >= max) break;
            }
        }

        while (!toExplore.isEmpty() && visited.size() < max) {
            BlockPos current = toExplore.poll();

            List<BlockPos> neighbors = getValidBlocksNextTo(event.getLevel(), current, event.getPlayer().getMainHandItem());
            for (BlockPos neighbor : neighbors) {
                if (visited.size() >= max) break;
                if (!visited.contains(neighbor) && event.getLevel().getBlockState(neighbor).is(event.getState().getBlock())) {
                    visited.add(neighbor);
                    toExplore.add(neighbor);
                }
            }
        }

        visited.forEach(p-> DestroyUtils.destroyBlock(event.getPlayer().level(),((ServerPlayer) event.getPlayer()).gameMode.getGameModeForPlayer(),event.getPlayer(),((ServerPlayer) event.getPlayer()).gameMode,p,event.getPos()));
    }



    private static List<BlockPos> getValidBlocksNextTo(LevelAccessor level, BlockPos pos, ItemStack stack){
        List<BlockPos> poses = new ArrayList<>();
        for (Direction direction : Iterate.directions) {
            if (stack.isCorrectToolForDrops(level.getBlockState(pos.relative(direction)))){
                poses.add(pos.relative(direction));
            }
        }
        return poses;
    }

    @Override
    public ReachType reachType(ItemStack stack) {
        return ReachType.BLOCK;
    }
}
