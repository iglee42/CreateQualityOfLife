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
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ShadowSteelSword extends SwordItem implements QOLConfigurableItem {
    public ShadowSteelSword(Properties p_42964_) {
        super(QOLTiers.SHADOW_STEEL, p_42964_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.success(itemstack);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack p_41452_) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack p_41454_, LivingEntity p_344979_) {
        return CreateQOLConfigs.server().equipments.tools.swordsChargeTime.get();
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity lvEntity, int remainingTime) {
        if (!(lvEntity instanceof Player player)) return;
        if (!CreateQOLConfigs.server().equipments.tools.swordsAbilities.get()) return;
        if (CreateQOLConfigs.server().equipments.tools.swordsAirConsumption.get() > 0 && !player.isCreative()) {
            ItemStack backtank = BacktankUtil.getAllWithAir(player).stream().filter(i -> BacktankUtil.getAir(i) >= CreateQOLConfigs.server().equipments.tools.swordsAirConsumption.get()).findFirst().orElse(ItemStack.EMPTY);
            if (backtank.isEmpty()) return;
            BacktankUtil.consumeAir(player,backtank,CreateQOLConfigs.server().equipments.tools.swordsAirConsumption.get());
        }
        int usedTime = getUseDuration(stack,lvEntity) - remainingTime;
        float timeRatio = (float) usedTime / getUseDuration(stack,lvEntity);
        if (!level.isClientSide) {
            Vec3 playerPos = player.position();

            double radius = CreateQOLConfigs.server().equipments.tools.swordsRadius.get();
            double strength = CreateQOLConfigs.server().equipments.tools.swordsStrength.get() * timeRatio;
            AABB area = new AABB(
                    playerPos.x - radius, playerPos.y - radius, playerPos.z - radius,
                    playerPos.x + radius, playerPos.y + radius, playerPos.z + radius
            );

            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, entity ->
                    entity != player && entity.isAlive()
            );

            for (LivingEntity entity : entities) {
                Vec3 toPlayer = entity.position().subtract(playerPos);
                double distance = toPlayer.length();
                if (distance <= 0.1) continue;

                double scaledStrength = (1.0f-(distance / radius)) * strength;

                Vec3 direction = entity.position().subtract(playerPos).normalize().scale(scaledStrength);
                entity.push(direction.x, 0.3, direction.z);
            }

            player.swing(InteractionHand.MAIN_HAND, true);
            player.getCooldowns().addCooldown(this, CreateQOLConfigs.server().equipments.tools.swordsCooldowns.get());
        } else {
            Vec3 baseMotion = new Vec3(CreateQOLConfigs.server().equipments.tools.swordsRadius.get() / 4, 0.1, 0);
            for (int i = 0; i < 360; i += 10) {
                Vec3 m = VecHelper.rotate(baseMotion, i, Direction.Axis.Y);
                Vec3 v = player.position().add(m.normalize()
                        .scale(.25f));

                level.addParticle(ParticleTypes.SPIT, v.x, v.y, v.z, m.x, m.y, m.z);
            }
        }
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
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.reach.get(), true, stack.getOrDefault(QOLDataComponents.REACH, true), false, true)));
        components.add(Component.translatable("createqol.ability.tool.toggle_message", Component.translatable("createqol.ability.tool.repulsion").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.cooldownState(CreateQOLConfigs.server().equipments.tools.swordsAbilities.get(),
                        true, (int) Math.ceil(Minecraft.getInstance().player.getCooldowns().getCooldownPercent(this,0) * CreateQOLConfigs.server().equipments.tools.swordsCooldowns.get()))));
        components.add(Component.translatable("createqol.ability.tool.toggle_message", Component.translatable("createqol.ability.tool.use_air").getString())
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.use_air.get(), true, stack.getOrDefault(QOLDataComponents.USE_AIR, false), false, true)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }

    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(Configuration.ofBool("Use Air", stack.getOrDefault(QOLDataComponents.USE_AIR, false), QOLDataComponents.USE_AIR,
                List.of("Define if air should be used (if available) from the backtank instead of the tool's durability."), (e, oe) -> CreateQOLConfigs.server().equipments.tools.use_air.get()));

    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return stack.getOrDefault(QOLDataComponents.USE_AIR, false) && BacktankUtil.canAbsorbDamage(entity, getMaxDamage(stack)) ? 0 : super.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return (BacktankUtil.isBarVisible(stack, getMaxDamage(stack)) && stack.getOrDefault(QOLDataComponents.USE_AIR, false)) || super.isBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return stack.getOrDefault(QOLDataComponents.USE_AIR, false) ? BacktankUtil.getBarWidth(stack, getMaxDamage(stack)) : super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return stack.getOrDefault(QOLDataComponents.USE_AIR, false) ? BacktankUtil.getBarColor(stack, getMaxDamage(stack)) : super.getBarColor(stack);
    }

    @Override
    public ReachType reachType(ItemStack stack) {
        return ReachType.ENTITY;
    }
}
