package fr.iglee42.createqualityoflife.items.tools.shadowradiance;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.BooleanEntry;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.items.tools.refinedradiance.RefinedRadianceHoe;
import fr.iglee42.createqualityoflife.items.tools.refinedradiance.RefinedRadiancePickaxe;
import fr.iglee42.createqualityoflife.items.tools.shadowsteel.ShadowSteelHoe;
import fr.iglee42.createqualityoflife.items.tools.shadowsteel.ShadowSteelPickaxe;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.registries.QOLTiers;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ShadowRadianceHoe extends HoeItem implements QOLConfigurableItem {
    public ShadowRadianceHoe(Properties p_42964_) {
        super(QOLTiers.SHADOW_RADIANCE, p_42964_);
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
        components.add(Component.literal("Harvesting : ")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.harvesting.get(), true, stack.getOrDefault(QOLDataComponents.HARVESTING,false), false, true)));
        components.add(Component.literal("Ploughing : ")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.ploughing.get(), true, stack.getOrDefault(QOLDataComponents.PLOUGHING,false), false, true)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }


    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(Configuration.ofBool("Harvesting",stack.getOrDefault(QOLDataComponents.HARVESTING,false),QOLDataComponents.HARVESTING,
                List.of("Should replant destroyed crops"),(entry, oe) ->{
                    boolean flag = oe.stream()
                            .noneMatch(e->e instanceof BooleanEntry oEntry && oEntry.getComponent().equals(QOLDataComponents.PLOUGHING) && oEntry.getValue());
                    return CreateQOLConfigs.server().equipments.tools.harvesting.get() && flag;
                }));
        list.add(Configuration.ofBool("Ploughing",stack.getOrDefault(QOLDataComponents.PLOUGHING,false),QOLDataComponents.PLOUGHING,
                List.of("Should plough dirt in a 3x3 square"),(entry, oe) ->{
                    boolean flag = oe.stream()
                            .noneMatch(e->e instanceof BooleanEntry oEntry && oEntry.getComponent().equals(QOLDataComponents.HARVESTING) && oEntry.getValue());
                    return CreateQOLConfigs.server().equipments.tools.ploughing.get() && flag;
                }));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),true);
        if (player.isCrouching()){
            if (player.getItemInHand(hand).getOrDefault(QOLDataComponents.HARVESTING,false)){
                player.displayClientMessage(Component.literal("Ploughing can't be enabled if harvesting is enabled").withStyle(ChatFormatting.RED),true);
            }else {
                ShadowSteelHoe.toggleAbility(player.getItemInHand(hand),player);
            }
        }
        else {
            if (player.getItemInHand(hand).getOrDefault(QOLDataComponents.PLOUGHING,false)){
                player.displayClientMessage(Component.literal("Harvesting can't be enabled if ploughing is enabled").withStyle(ChatFormatting.RED),true);
            }else {
                RefinedRadianceHoe.toggleAbility(player.getItemInHand(hand),player);
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
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

    @Override
    public double reachModifier(ItemStack stack) {
        return 1;
    }
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (!CreateQOLConfigs.server().equipments.tools.ploughing.get()
                || !ctx.getItemInHand().getOrDefault(QOLDataComponents.PLOUGHING,false)) return super.useOn(ctx);
        Level level = ctx.getLevel();
        BlockPos basePos = ctx.getClickedPos();
        for (int x = -1; x <= 1; x++){
            for (int z = -1; z <= 1; z++){
                BlockPos blockpos = basePos.offset(x,0,z);
                BlockState toolModifiedState = level.getBlockState(blockpos).getToolModifiedState(ctx, ItemAbilities.HOE_TILL, false);
                Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = toolModifiedState == null ? null : Pair.of((Predicate)(c) -> true, c->{
                    c.getLevel().setBlock(blockpos, toolModifiedState, 11);
                    c.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(c.getPlayer(), toolModifiedState));
                });
                if (pair != null) {
                    Predicate<UseOnContext> predicate = (Predicate)pair.getFirst();
                    Consumer<UseOnContext> consumer = (Consumer)pair.getSecond();
                    if (predicate.test(ctx)) {
                        Player player = ctx.getPlayer();
                        level.playSound(player, blockpos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                        if (!level.isClientSide) {
                            consumer.accept(ctx);
                            if (player != null) {
                                ctx.getItemInHand().hurtAndBreak(1, player, LivingEntity.getSlotForHand(ctx.getHand()));
                            }
                        }
                    }
                }
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
