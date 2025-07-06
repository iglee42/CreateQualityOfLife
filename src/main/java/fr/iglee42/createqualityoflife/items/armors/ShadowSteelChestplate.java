package fr.iglee42.createqualityoflife.items.armors;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLArmorMaterials;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.PreferredRender;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ShadowSteelChestplate extends BacktankItem.Layered implements QOLConfigurableItem {


    public ShadowSteelChestplate(Properties properties, Supplier<BacktankBlockItem> placeable) {
        super(QOLArmorMaterials.SHADOW_STEEL, properties, CreateQOL.asResource("shadow_steel"), placeable);
    }

    public static void dash(ItemStack chestplate, ServerPlayer player) {
        if (!CreateQOLConfigs.server().dashAllowed.get()){
            player.displayClientMessage(Component.literal("Dashing is disabled on this server !").withStyle(ChatFormatting.RED),true);
            return;
        }
        Vec3 look = player.getLookAngle().normalize().scale(2.5D);
        look = new Vec3(look.x,Math.clamp(look.y,-0.5D,0.5D),look.z);
        player.setDeltaMovement(player.getDeltaMovement().add(look));
        player.hurtMarked = true;
    }

    @Override
    public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
        super.inventoryTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
        invTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
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
    public Holder<MobEffect> providedEffect(ItemStack stack) {
        return MobEffects.DAMAGE_BOOST;
    }

    @Override
    public void addConfigurations(List<Configuration<?>> list, ItemStack stack) {
        list.add(new Configuration<>("Preferred Render", stack.getOrDefault(QOLDataComponents.PREFERRED_RENDER, PreferredRender.BOTH),QOLDataComponents.PREFERRED_RENDER,
                Configuration.ConfigType.ENUM,Arrays.asList("Define how the additions should be rendered.",
                "\"Elytra\" renders only the elytra",
                "\"Backtank\" renders only the backtank"),(direction,entry)->{

            PreferredRender e = (PreferredRender) entry.getValue();
            PreferredRender[] options = Arrays.stream(PreferredRender.values()).toArray(PreferredRender[]::new);
            e = options[Math.floorMod(e.ordinal() + direction, options.length)];
            return e;
        },(e,oe)->true));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return super.isBarVisible(stack) && BacktankUtil.getAir(stack) < BacktankUtil.maxAir(stack);
    }
}
