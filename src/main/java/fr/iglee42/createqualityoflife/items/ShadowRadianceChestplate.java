package fr.iglee42.createqualityoflife.items;

import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.tterrag.registrate.util.entry.ItemEntry;
import fr.iglee42.createqualityoflife.registries.ModArmorMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Supplier;

public class ShadowRadianceChestplate extends BacktankItem.Layered{

    public ShadowRadianceChestplate(ArmorMaterial material, Properties properties, ResourceLocation textureLoc, Supplier<BacktankBlockItem> placeable) {
        super(material, properties, textureLoc, placeable);
    }
    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
            if (!player.isCreative() && !BacktankUtil.getAllWithAir(player).isEmpty())BacktankUtil.consumeAir(player, BacktankUtil.getAllWithAir(player).get(0), 0.01f);
            else if (!player.isCreative()) return;
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,20,1,false,false));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level p_41422_, List<Component> components, TooltipFlag p_41424_) {
        components.add(Component.literal("Air : ").withStyle(ChatFormatting.GOLD).append(Component.literal(String.format("%.1f",BacktankUtil.getAir(stack))).withStyle(ChatFormatting.YELLOW)).append(Component.literal("/"+BacktankUtil.maxAir(stack)+",0").withStyle(ChatFormatting.GOLD)));
        super.appendHoverText(stack, p_41422_, components, p_41424_);
    }
}
