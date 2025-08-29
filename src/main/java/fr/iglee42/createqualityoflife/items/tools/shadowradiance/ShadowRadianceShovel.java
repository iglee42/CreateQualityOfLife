package fr.iglee42.createqualityoflife.items.tools.shadowradiance;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.BooleanEntry;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.items.tools.refinedradiance.RefinedRadianceShovel;
import fr.iglee42.createqualityoflife.items.tools.shadowsteel.ShadowSteelShovel;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ShadowRadianceShovel extends ShovelItem implements QOLConfigurableItem {
    public ShadowRadianceShovel(Properties p_42964_) {
        super(QOLTiers.SHADOW_RADIANCE, 1.5F, -3.0F,p_42964_);
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
        components.add(Component.translatable("createqol.ability.tool.toggle_message", Component.translatable("createqol.ability.tool.reach").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.reach.get(), true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_REACH,true), false, true)));
        components.add(Component.translatable("createqol.ability.tool.toggle_message", Component.translatable("createqol.ability.tool.reach").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.smelting.get(), true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_SMELTING,false), false, true)));
        components.add(Component.translatable("createqol.ability.tool.toggle_message", Component.translatable("createqol.ability.tool.reach").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.digging.get(), true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_DIGGING,true), false, true)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }


    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(Configuration.ofBool("Smelting",NBTConstants.getOrDefault(stack,NBTConstants.NBT_SMELTING,false),NBTConstants.NBT_SMELTING,
                List.of("Should smelt the mined blocks"),(entry, oe) ->{
                    boolean flag = oe.stream()
                            .noneMatch(e->e instanceof BooleanEntry oEntry && oEntry.getNbtKey().equals(NBTConstants.NBT_DIGGING) && oEntry.getValue());
                    return CreateQOLConfigs.server().equipments.tools.smelting.get() && flag;
                }));
        list.add(Configuration.ofBool("Digging", NBTConstants.getOrDefault(stack,NBTConstants.NBT_DIGGING,false), NBTConstants.NBT_DIGGING,
                List.of("Activate the 3x3x3 digging when mining a block"), (entry, oe) ->{
                    boolean flag = oe.stream()
                            .noneMatch(e->e instanceof BooleanEntry oEntry && oEntry.getNbtKey().equals(NBTConstants.NBT_SMELTING) && oEntry.getValue());
                    return CreateQOLConfigs.server().equipments.tools.digging.get() && flag;
                }));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),true);
        if (player.isCrouching()){
            if (NBTConstants.getOrDefault(player.getItemInHand(hand),NBTConstants.NBT_SMELTING,false)){
                player.displayClientMessage(CreateQOLLang.translateDirect("ability.tool.unavailable", CreateQOLLang.translateDirect("ability.tool.digging").getString(), CreateQOLLang.translateDirect("ability.tool.smelting").getString()).withStyle(ChatFormatting.RED),true);
            }else {
                ShadowSteelShovel.toggleAbility(player.getItemInHand(hand),player);
            }
        }
        else {
            if (NBTConstants.getOrDefault(player.getItemInHand(hand),NBTConstants.NBT_DIGGING,false)){
                player.displayClientMessage(CreateQOLLang.translateDirect("ability.tool.unavailable", CreateQOLLang.translateDirect("ability.tool.smelting").getString(), CreateQOLLang.translateDirect("ability.tool.digging").getString()).withStyle(ChatFormatting.RED),true);
            }else {
                RefinedRadianceShovel.toggleAbility(player.getItemInHand(hand),player);
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
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

    @Override
    public double reachModifier(ItemStack stack) {
        return 1;
    }
}
