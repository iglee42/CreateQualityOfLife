package fr.iglee42.createqualityoflife.items.tools.refinedradiance;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.content.kinetics.saw.TreeCutter;
import com.simibubi.create.foundation.utility.AbstractBlockBreakQueue;
import com.simibubi.create.foundation.utility.BlockHelper;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import fr.iglee42.createqualityoflife.registries.QOLTiers;
import fr.iglee42.createqualityoflife.utils.DestroyUtils;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class RefinedRadianceAxe extends AxeItem implements QOLConfigurableItem {
    public RefinedRadianceAxe(Properties p_42964_) {
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
        components.add(Component.literal("Tree Decapitation : ")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.treeDecapitation.get(), true, stack.getOrDefault(QOLDataComponents.TREE_DECAPITATION,false), false, true)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }


    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(Configuration.ofBool("Tree Decapitation",stack.getOrDefault(QOLDataComponents.TREE_DECAPITATION,false),QOLDataComponents.TREE_DECAPITATION,
                List.of("Should destroy a tree when a log is broken like a mechanical saw"),(e,oe)->CreateQOLConfigs.server().equipments.tools.treeDecapitation.get()));
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),true);
        if (player.isCrouching()) return InteractionResultHolder.pass(player.getItemInHand(hand));
        toggleAbility(player.getItemInHand(hand),player);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    public static void toggleAbility(ItemStack stack, Player p) {
        if (!CreateQOLConfigs.server().equipments.tools.treeDecapitation.get()){
            p.displayClientMessage(Component.literal("Tree Decapitation is disabled by the config").withStyle(ChatFormatting.RED),true);
            return;
        }
        boolean enable = !stack.getOrDefault(QOLDataComponents.TREE_DECAPITATION,false);
        stack.set(QOLDataComponents.TREE_DECAPITATION, enable);
        p.displayClientMessage(Component.literal("Tree Decapitation : ").append(QOLConfigurableItem.chooseState(true,true,enable,false,true)).withStyle(enable ? ChatFormatting.GREEN : ChatFormatting.RED),true);
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
        if (!event.getPlayer().getMainHandItem().is(QOLItems.REFINED_RADIANCE_AXE.get()) && !event.getPlayer().getMainHandItem().is(QOLItems.SHADOW_RADIANCE_AXE.get())) return;
        if (!event.getPlayer().getMainHandItem().getOrDefault(QOLDataComponents.TREE_DECAPITATION,false)) return;
        if (!event.getPlayer().getMainHandItem().isCorrectToolForDrops(event.getState())) return;
        if (!SawBlockEntity.isSawable(event.getState())) return;
        BlockState stateToBreak = event.getState();
        BlockPos breakingPos = event.getPos();
        Level level = event.getPlayer().level();
        Optional<AbstractBlockBreakQueue> dynamicTree =
                TreeCutter.findDynamicTree(stateToBreak.getBlock(), breakingPos);
        if (dynamicTree.isPresent()) {
            dynamicTree.get()
                    .destroyBlocks(level, null, (pos,stack)->dropItemFromCutTree(level,pos,stack));
            return;
        }

        Vec3 vec = VecHelper.offsetRandomly(VecHelper.getCenterOf(breakingPos), level.random, .125f);
        BlockHelper.destroyBlock(level, breakingPos, 1f, (stack) -> {
            if (stack.isEmpty())
                return;
            if (!level.getGameRules()
                    .getBoolean(GameRules.RULE_DOBLOCKDROPS))
                return;
            if (level.restoringBlockSnapshots)
                return;

            ItemEntity itementity = new ItemEntity(level, vec.x, vec.y, vec.z, stack);
            itementity.setDefaultPickUpDelay();
            itementity.setDeltaMovement(Vec3.ZERO);
            level.addFreshEntity(itementity);
        });
        TreeCutter.findTree(level, breakingPos, stateToBreak)
                .destroyBlocks(level, null, (pos,stack)->dropItemFromCutTree(level,pos,stack));
    }

    private static void dropItemFromCutTree(Level level, BlockPos pos, ItemStack stack) {
        Vec3 dropPos = VecHelper.getCenterOf(pos);
        ItemEntity entity = new ItemEntity(level, dropPos.x, dropPos.y, dropPos.z, stack);
        entity.setDeltaMovement(Vec3.ZERO);
        level.addFreshEntity(entity);
    }

    @Override
    public ReachType reachType(ItemStack stack) {
        return ReachType.BOTH;
    }

}
