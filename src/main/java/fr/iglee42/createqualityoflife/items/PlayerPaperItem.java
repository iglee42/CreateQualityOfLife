package fr.iglee42.createqualityoflife.items;

import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.utils.Features;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerPaperItem extends Item {
    public PlayerPaperItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public void fillItemCategory(CreativeModeTab p_41391_, NonNullList<ItemStack> p_41392_) {
        if (CreateQOL.isActivate(Features.INVENTORY_LINKER))super.fillItemCategory(p_41391_, p_41392_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player player, InteractionHand p_41434_) {
        if (p_41432_.isClientSide) return InteractionResultHolder.sidedSuccess(player.getMainHandItem(),p_41432_.isClientSide);
        ItemStack handItem = player.getMainHandItem();
        if (player instanceof FakePlayer) return InteractionResultHolder.pass(handItem);
        if (handItem.getOrCreateTag().contains("linkedPlayer")){
            if(player.isCrouching()){
                handItem.getOrCreateTag().remove("linkedPlayer");
                return InteractionResultHolder.success(handItem);
            }
        } else {
            if (!player.isCrouching()){
                handItem.getOrCreateTag().putString("linkedPlayer",player.getName().getString());
                return InteractionResultHolder.success(handItem);
            }
        }
        return super.use(p_41432_, player, p_41434_);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> components, TooltipFlag p_41424_) {
        if (p_41421_.getOrCreateTag().contains("linkedPlayer")){
            components.add(Component.translatable("tooltip.createqol.player_paper.linked_player",p_41421_.getOrCreateTag().getString("linkedPlayer")));
        } else {
            components.add(Component.translatable("tooltip.createqol.player_paper.no_linked_player"));
        }
        super.appendHoverText(p_41421_, p_41422_, components, p_41424_);

    }
}
