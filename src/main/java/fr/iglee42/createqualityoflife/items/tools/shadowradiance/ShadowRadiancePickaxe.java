package fr.iglee42.createqualityoflife.items.tools.shadowradiance;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.BooleanEntry;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.items.tools.refinedradiance.RefinedRadiancePickaxe;
import fr.iglee42.createqualityoflife.items.tools.shadowsteel.ShadowSteelPickaxe;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.registries.QOLTiers;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ShadowRadiancePickaxe extends PickaxeItem implements QOLConfigurableItem {
    public ShadowRadiancePickaxe(Properties p_42964_) {
        super(QOLTiers.SHADOW_RADIANCE, p_42964_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),true);
        if (player.isCrouching()){
            if (player.getItemInHand(hand).getOrDefault(QOLDataComponents.VEIN_MINE,false)){
                player.displayClientMessage(Component.translatable("createqol.tool.digging_unavailable").withStyle(ChatFormatting.RED),true);
            }else {
                ShadowSteelPickaxe.toggleAbility(player.getItemInHand(hand),player);
            }
        }
        else {
            if (player.getItemInHand(hand).getOrDefault(QOLDataComponents.DIGGING,false)){
                player.displayClientMessage(Component.translatable("createqol.tool.vein_mine_unavailable").withStyle(ChatFormatting.RED),true);
            }else {
                RefinedRadiancePickaxe.toggleAbility(player.getItemInHand(hand),player);
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
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
        components.add(Component.translatable("createqol.ability.tools.reach")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.reach.get(), true, stack.getOrDefault(QOLDataComponents.REACH, true), false, true)));
        components.add(Component.translatable("createqol.ability.tools.digging")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.digging.get(), true, stack.getOrDefault(QOLDataComponents.DIGGING, false), false, true)));
        components.add(Component.translatable("createqol.ability.tools.vein_mine")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.veinMine.get(), true, stack.getOrDefault(QOLDataComponents.VEIN_MINE,false), false, true)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }

    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(Configuration.ofBool("Digging", stack.getOrDefault(QOLDataComponents.DIGGING, false), QOLDataComponents.DIGGING,
                List.of("Activate the 3x3x3 digging when mining a block"), (entry, oe) ->{
                    boolean flag = oe.stream()
                            .noneMatch(e->e instanceof BooleanEntry oEntry && oEntry.getComponent().equals(QOLDataComponents.VEIN_MINE) && oEntry.getValue());
                return CreateQOLConfigs.server().equipments.tools.digging.get() && flag;
                }));
        list.add(Configuration.ofBool("Vein Mine",stack.getOrDefault(QOLDataComponents.VEIN_MINE,false),QOLDataComponents.VEIN_MINE,
                List.of("Should all the blocks of the same types be destroy when mining"),(entry, oe) ->{
                    boolean flag = oe.stream()
                            .noneMatch(e->e instanceof BooleanEntry oEntry && oEntry.getComponent().equals(QOLDataComponents.DIGGING) && oEntry.getValue());
                    return CreateQOLConfigs.server().equipments.tools.veinMine.get() && flag;
                }));
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
}
