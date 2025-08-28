package fr.iglee42.createqualityoflife.items.tools.shadowsteel;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLTiers;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ShadowSteelHoe extends HoeItem implements QOLConfigurableItem {
    public ShadowSteelHoe(Properties p_42964_) {
        super(QOLTiers.SHADOW_STEEL,-3, 0.0F, p_42964_);
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
    public void appendHoverText(ItemStack stack, @Nullable Level p_41422_, List<Component> components, TooltipFlag p_41424_) {
        if (!NBTConstants.getTooltipOrDefault(stack).isEnable(ItemTooltips.Tooltip.OPTIONS)) return;
        components.add(Component.literal("Reach : ")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.reach.get(), true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_REACH,true), false, true)));
        components.add(Component.literal("Ploughing : ")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.ploughing.get(), true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_PLOUGHING,false), false, true)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }


    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(Configuration.ofBool("Ploughing",NBTConstants.getOrDefault(stack,NBTConstants.NBT_PLOUGHING,false),NBTConstants.NBT_PLOUGHING,
                List.of("Should plough dirt in a 3x3 square"),(e,oe)->CreateQOLConfigs.server().equipments.tools.ploughing.get()));
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
            p.displayClientMessage(Component.literal("Ploughing is disabled by the config").withStyle(ChatFormatting.RED),true);
            return;
        }
        boolean enable = !NBTConstants.getOrDefault(stack,NBTConstants.NBT_PLOUGHING,false);
        stack.getOrCreateTag().putBoolean(NBTConstants.NBT_PLOUGHING,enable);
        p.displayClientMessage(Component.literal("Ploughing : ").append(QOLConfigurableItem.chooseState(true,true,enable,false,true)).withStyle(enable ? ChatFormatting.GREEN : ChatFormatting.RED),true);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (!CreateQOLConfigs.server().equipments.tools.ploughing.get()
                || !NBTConstants.getOrDefault(ctx.getItemInHand(),NBTConstants.NBT_PLOUGHING,false)) return super.useOn(ctx);
        Level level = ctx.getLevel();
        BlockPos basePos = ctx.getClickedPos();
        for (int x = -1; x <= 1; x++){
            for (int z = -1; z <= 1; z++){
                BlockPos blockpos = basePos.offset(x,0,z);
                BlockState toolModifiedState = level.getBlockState(blockpos).getToolModifiedState(ctx, ToolActions.HOE_TILL, false);
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
                                ctx.getItemInHand().hurtAndBreak(1, player, e->{});
                            }
                        }
                    }
                }
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<T> onBroken) {
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
