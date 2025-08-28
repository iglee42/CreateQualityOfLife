package fr.iglee42.createqualityoflife.items.tools.refinedradiance;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLTiers;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class RefinedRadianceShovel extends ShovelItem implements QOLConfigurableItem {
    public RefinedRadianceShovel(Properties p_42964_) {
        super(QOLTiers.REFINED_RADIANCE,1.5F, -3.0F, p_42964_);
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
        components.add(Component.translatable("createqol.ability.tools.reach")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.reach.get(), true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_REACH,true), false, true)));
        components.add(Component.translatable("createqol.ability.tools.smelting")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.smelting.get(), true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_SMELTING,false), false, true)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }


    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(Configuration.ofBool("Smelting",NBTConstants.getOrDefault(stack,NBTConstants.NBT_SMELTING,false),NBTConstants.NBT_SMELTING,
                List.of("Should smelt the mined blocks"),(e,oe)->CreateQOLConfigs.server().equipments.tools.smelting.get()));
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),true);
        if (player.isCrouching()) return InteractionResultHolder.pass(player.getItemInHand(hand));
        toggleAbility(player.getItemInHand(hand),player);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    public static void toggleAbility(ItemStack stack, Player p) {
        if (!CreateQOLConfigs.server().equipments.tools.smelting.get()){
            p.displayClientMessage(Component.translatable("createqol.tool.smelting_disabled").withStyle(ChatFormatting.RED),true);
            return;
        }
        boolean enable = !NBTConstants.getOrDefault(stack,NBTConstants.NBT_SMELTING,false);
        stack.getOrCreateTag().putBoolean(NBTConstants.NBT_SMELTING,enable);
        p.displayClientMessage(Component.translatable("createqol.tool.smelting_toggle").append(QOLConfigurableItem.chooseState(true,true,enable,false,true)).withStyle(enable ? ChatFormatting.GREEN : ChatFormatting.RED),true);
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
