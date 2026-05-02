package fr.iglee42.createqualityoflife.items.tools.shadowsteel;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.mixin.accessor.CropBlockAccessor;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import fr.iglee42.createqualityoflife.registries.QOLTiers;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.SpecialPlantable;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ShadowSteelHoe extends HoeItem implements QOLConfigurableItem {
    public ShadowSteelHoe(Properties p_42964_) {
        super(QOLTiers.SHADOW_STEEL, p_42964_);
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
        components.add(Component.translatable("createqol.ability.tool.toggle_message", Component.translatable("createqol.ability.tool.reach").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.reach.get(), true, stack.getOrDefault(QOLDataComponents.REACH,true), false, true)));
        components.add(Component.translatable("createqol.ability.tool.toggle_message", Component.translatable("createqol.ability.tool.ploughing").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.ploughing.get(), true, stack.getOrDefault(QOLDataComponents.PLOUGHING,false), false, true)));
        components.add(Component.translatable("createqol.ability.tool.toggle_message", Component.translatable("createqol.ability.tool.use_air").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.use_air.get(), true, stack.getOrDefault(QOLDataComponents.USE_AIR,false), false, true)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }


    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(Configuration.ofBool("Ploughing",stack.getOrDefault(QOLDataComponents.PLOUGHING,false),QOLDataComponents.PLOUGHING,
                List.of("Should plough dirt in a 3x3 square"),(e,oe)->CreateQOLConfigs.server().equipments.tools.ploughing.get()));
        list.add(Configuration.ofBool("Use Air",stack.getOrDefault(QOLDataComponents.USE_AIR,false),QOLDataComponents.USE_AIR,
                List.of("Define if air should be used (if available) from the backtank instead of the tool's durability."),(e,oe)->CreateQOLConfigs.server().equipments.tools.use_air.get()));

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),true);
        if (player.isCrouching()) return InteractionResultHolder.pass(player.getItemInHand(hand));
        toggleAbility(player.getItemInHand(hand),player);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    public static void toggleAbility(ItemStack stack, Player p) {
        if (!CreateQOLConfigs.server().equipments.tools.ploughing.get()){
            p.displayClientMessage(CreateQOLLang.translateDirect("ability.tool.disabled", CreateQOLLang.translateDirect("ability.tool.ploughing").getString()).withStyle(ChatFormatting.RED),true);
            return;
        }
        boolean enable = !stack.getOrDefault(QOLDataComponents.PLOUGHING,false);
        stack.set(QOLDataComponents.PLOUGHING, enable);
        p.displayClientMessage(CreateQOLLang.translateDirect("ability.tool.toggle_message", CreateQOLLang.translateDirect("ability.tool.ploughing").getString()).append(QOLConfigurableItem.chooseState(true,true,enable,false,true)).withStyle(enable ? ChatFormatting.GREEN : ChatFormatting.RED),true);
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

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return stack.getOrDefault(QOLDataComponents.USE_AIR, false) && BacktankUtil.canAbsorbDamage(entity, getMaxDamage(stack)) ? 0 : super.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return (BacktankUtil.isBarVisible(stack, getMaxDamage(stack)) && stack.getOrDefault(QOLDataComponents.USE_AIR,false)) || super.isBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return stack.getOrDefault(QOLDataComponents.USE_AIR,false) ? BacktankUtil.getBarWidth(stack, getMaxDamage(stack)) : super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return stack.getOrDefault(QOLDataComponents.USE_AIR,false) ? BacktankUtil.getBarColor(stack, getMaxDamage(stack)) : super.getBarColor(stack);
    }

    @Override
    public ReachType reachType(ItemStack stack) {
        return ReachType.BLOCK;
    }


}
