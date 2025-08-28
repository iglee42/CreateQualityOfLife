package fr.iglee42.createqualityoflife.items.tools.refinedradiance;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLTiers;
import fr.iglee42.createqualityoflife.utils.ItemTooltips;
import fr.iglee42.createqualityoflife.utils.NBTConstants;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class RefinedRadianceSword extends SwordItem implements QOLConfigurableItem {
    public RefinedRadianceSword(Properties p_42964_) {
        super(QOLTiers.REFINED_RADIANCE,3, -2.4F, p_42964_);
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
    public int getUseDuration(ItemStack p_41454_) {
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
        int usedTime = getUseDuration(stack) - remainingTime;
        float timeRatio = (float) usedTime / getUseDuration(stack);
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
                Vec3 toPlayer = playerPos.subtract(entity.position());
                double distance = toPlayer.length();
                if (distance <= 0.1) continue;

                double scaledStrength = (distance / radius) * strength;

                Vec3 direction = toPlayer.normalize().scale(scaledStrength);

                entity.push(direction.x, 0.3, direction.z);
            }

            player.swing(InteractionHand.MAIN_HAND, true);
            player.getCooldowns().addCooldown(this, CreateQOLConfigs.server().equipments.tools.swordsCooldowns.get());
        } else {
            Vec3 baseMotion = new Vec3(CreateQOLConfigs.server().equipments.tools.swordsRadius.get(), 0.1, 0);
            for (int i = 0; i < 360; i += 10) {
                Vec3 m = VecHelper.rotate(baseMotion, i, Direction.Axis.Y);
                Vec3 v = player.position().add(m.x,0,m.z);
                Vec3 mo = m.normalize().scale(-0.5f).add(0,0.3,0);

                level.addParticle(ParticleTypes.SPIT, v.x, v.y, v.z,mo.x,mo.y,mo.z);
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
    public void appendHoverText(ItemStack stack, @Nullable Level p_41422_, List<Component> components, TooltipFlag p_41424_) {
        if (!NBTConstants.getTooltipOrDefault(stack).isEnable(ItemTooltips.Tooltip.OPTIONS)) return;
        components.add(Component.literal("Reach : ")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(CreateQOLConfigs.server().equipments.tools.reach.get(), true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_REACH,true), false, true)));
        components.add(Component.literal("Attraction : ")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.cooldownState(CreateQOLConfigs.server().equipments.tools.swordsAbilities.get(),
                        true, (int) Math.ceil(Minecraft.getInstance().player.getCooldowns().getCooldownPercent(this,0) * CreateQOLConfigs.server().equipments.tools.swordsCooldowns.get()))));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
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
        return ReachType.ENTITY;
    }
}
