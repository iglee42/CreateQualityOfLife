package fr.iglee42.createqualityoflife.items.armors;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLArmorMaterials;
import fr.iglee42.createqualityoflife.utils.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ShadowSteelChestplate extends BacktankItem.Layered implements QOLConfigurableItem {

    public ShadowSteelChestplate(Properties properties, Supplier<BacktankBlockItem> placeable) {
        super(QOLArmorMaterials.SHADOW_STEEL, properties, CreateQOL.asResource("shadow_steel"), placeable);
    }

    public static void dash(ItemStack chestplate, ServerPlayer player) {
        if (!NBTConstants.getOrDefault(chestplate,NBTConstants.NBT_DASH,true)) return;
        if (!CreateQOLConfigs.server().equipments.armors.dashAllowed.get()){
            player.displayClientMessage(CreateQOLLang.translateDirect("chestplate.dash_disabled").withStyle(ChatFormatting.RED),true);
            return;
        }
        if (player.getCooldowns().isOnCooldown(chestplate.getItem())){
            player.displayClientMessage(CreateQOLLang.translateDirect("chestplate.dash_reloading").withStyle(ChatFormatting.RED),true);
            return;
        }
        Vec3 look = player.getLookAngle().normalize().scale(2.5D);
        look = new Vec3(look.x, Mth.clamp(look.y,-0.5D,0.5D),look.z);
        player.setDeltaMovement(player.getDeltaMovement().add(look));
        player.hurtMarked = true;
        player.getCooldowns().addCooldown(chestplate.getItem(),CreateQOLConfigs.server().equipments.armors.dashCooldown.get());
    }

    @Override
    public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
        super.inventoryTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
        invTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level p_41422_, List<Component> components, TooltipFlag p_41424_) {
        if (!NBTConstants.getTooltipOrDefault(stack).isEnable(ItemTooltips.Tooltip.OPTIONS)) return;
        components.add(Component.translatable("createqol.ability.armor.air")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal(String.valueOf(BacktankUtil.getAir(stack)))
                        .withStyle(ChatFormatting.YELLOW))
                .append(Component.literal("/" + BacktankUtil.maxAir(stack))
                        .withStyle(ChatFormatting.GOLD)));
        components.add(Component.translatable("createqol.ability.armor.dash")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.cooldownState(CreateQOLConfigs.server().equipments.armors.dashAllowed.get(),
                        NBTConstants.getOrDefault(stack,NBTConstants.NBT_DASH,true), (int) Math.ceil(Minecraft.getInstance().player.getCooldowns().getCooldownPercent(this,0) * CreateQOLConfigs.server().equipments.armors.dashCooldown.get()))));
        components.add(Component.translatable("createqol.ability.armor.arms")
                .withStyle(ChatFormatting.GOLD)
                .append(QOLConfigurableItem.chooseState(true,
                        true, NBTConstants.getOrDefault(stack,NBTConstants.NBT_ARMS,true), false, true)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
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
    public MobEffect providedEffect(ItemStack stack) {
        return MobEffects.DAMAGE_BOOST;
    }

    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(Configuration.ofBool("Enable Custom Arms",
                NBTConstants.getOrDefault(stack,NBTConstants.NBT_ARMS,true),
                NBTConstants.NBT_ARMS,
                List.of("Should the player's arms be replaced with the armor in first person"),
                (e,oe)->true));
        list.add(Configuration.ofBool("Enable Dash",NBTConstants.getOrDefault(stack,NBTConstants.NBT_DASH,true),NBTConstants.NBT_DASH,
                List.of("Should the player dash when pressing "+ KeyBindManager.DASH_KEY.getTranslatedKeyMessage().getString()),
                (o,oe)->CreateQOLConfigs.server().equipments.armors.dashAllowed.get()));


    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return super.isBarVisible(stack) && BacktankUtil.getAir(stack) < BacktankUtil.maxAir(stack);
    }
}
