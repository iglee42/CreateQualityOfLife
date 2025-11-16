package fr.iglee42.createqualityoflife.items.armors;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.client.screens.widgets.entries.BooleanEntry;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLArmorMaterials;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.PreferredRender;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RefinedRadianceChestplate extends BacktankItem.Layered implements QOLConfigurableItem {


    public RefinedRadianceChestplate(Properties properties, Supplier<BacktankBlockItem> placeable) {
        super(QOLArmorMaterials.REFINED_RADIANCE, properties, CreateQOL.asResource("refined_radiance"), placeable);
    }

    @Override
    public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
        super.inventoryTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
        invTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
    }

    @Override
    public int effectLevel(ItemStack stack) {
        return 0;
    }

    @Override
    public QOLConfigurableItem.Type type() {
        return QOLConfigurableItem.Type.ARMOR;
    }

    @Override
    public List<ArmorRenderType> renderTypes(ItemStack stack) {
        return Arrays.asList(ArmorRenderType.ALL,ArmorRenderType.NONE);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(Enchantments.MENDING) || enchantment.is(Enchantments.UNBREAKING))
            return true;
        return super.supportsEnchantment(stack, enchantment);
    }

    @Override
    public Holder<MobEffect> providedEffect(ItemStack stack) {
        return MobEffects.REGENERATION;
    }

    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(new Configuration<>("Preferred Render", stack.getOrDefault(QOLDataComponents.PREFERRED_RENDER, PreferredRender.BOTH), QOLDataComponents.PREFERRED_RENDER,
                Configuration.ConfigType.ENUM, Arrays.asList("Define how the additions should be rendered.",
                "\"Elytra\" renders only the elytra",
                "\"Backtank\" renders only the backtank"), (direction, entry) -> {

            PreferredRender e = (PreferredRender) entry.getValue();
            PreferredRender[] options = Arrays.stream(PreferredRender.values()).toArray(PreferredRender[]::new);
            e = options[Math.floorMod(e.ordinal() + direction, options.length)];
            return e;
        }, (e, oe) -> true));

        if (ShadowRadianceChestplate.hasElytra(stack)) {
            list.add(Configuration.ofBool("Enable Elytra",
                    stack.getOrDefault(QOLDataComponents.BACKTANK_ELYTRA_STATE, false),
                    QOLDataComponents.BACKTANK_ELYTRA_STATE,
                    Arrays.asList("Activate the elytra on the backtank", "_Can't be enabled if the fan is enabled_"),
                    (entry, oe) -> {
                        boolean flag = oe.stream()
                                .noneMatch(e -> e instanceof BooleanEntry oEntry && oEntry.getComponent().equals(QOLDataComponents.BACKTANK_FANS) && oEntry.getValue());
                        return CreateQOLConfigs.server().equipments.armors.elytraAllowed.get() && flag;
                    }));

        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext p_41422_, List<Component> components, TooltipFlag p_41424_) {
        if (!stack.getOrDefault(QOLDataComponents.ITEM_TOOLTIPS, ItemTooltips.DEFAULT).isEnable(ItemTooltips.Tooltip.OPTIONS)) return;
        components.add(Component.translatable("createqol.ability.armor.toggle_message", Component.translatable("createqol.ability.armor.air").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal(String.valueOf(BacktankUtil.getAir(stack)))
                        .withStyle(ChatFormatting.YELLOW))
                .append(Component.literal("/" + BacktankUtil.maxAir(stack))
                        .withStyle(ChatFormatting.GOLD)));
        components.add(Component.translatable("createqol.ability.armor.toggle_message", Component.translatable("createqol.ability.armor.elytra").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.armors.elytraAllowed.get() ,ShadowRadianceChestplate.hasElytra(stack) ,
                        ShadowRadianceChestplate.isElytraEnable(stack), true,false)));
        components.add(Component.translatable("createqol.ability.armor.toggle_message", Component.translatable("createqol.ability.armor.arms").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(true,
                        true, stack.getOrDefault(QOLDataComponents.BACKTANK_ARMS, true), false, true)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return super.isBarVisible(stack) && BacktankUtil.getAir(stack) < BacktankUtil.maxAir(stack);
    }

    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return ShadowRadianceChestplate.hasElytra(stack)
                && ShadowRadianceChestplate.isElytraEnable(stack)
                && CreateQOLConfigs.server().equipments.armors.elytraAllowed.get();
    }

    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        return true;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        if (BacktankUtil.canAbsorbDamage(entity, getMaxDamage(stack))) return 0;
        return super.damageItem(stack, amount, entity, onBroken);
    }

}
