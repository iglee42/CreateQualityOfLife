package fr.iglee42.createqualityoflife.items;

import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerPaperItem extends Item {
    public PlayerPaperItem(Properties p_41383_) {
        super(p_41383_);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player player, InteractionHand p_41434_) {
        if (p_41432_.isClientSide) return InteractionResultHolder.sidedSuccess(player.getMainHandItem(),p_41432_.isClientSide);
        ItemStack handItem = player.getMainHandItem();
        if (player instanceof FakePlayer) return InteractionResultHolder.pass(handItem);
        if (handItem.has(QOLDataComponents.LINKED_PLAYER)){
            if(player.isCrouching()){
                handItem.remove(QOLDataComponents.LINKED_PLAYER);
                return InteractionResultHolder.success(handItem);
            }
        } else {
            if (!player.isCrouching()){
                handItem.set(QOLDataComponents.LINKED_PLAYER,player.getUUID());
                return InteractionResultHolder.success(handItem);
            }
        }
        return super.use(p_41432_, player, p_41434_);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext p_41422_, List<Component> components, TooltipFlag p_41424_) {
        if (stack.has(QOLDataComponents.LINKED_PLAYER)){
            components.add(Component.translatable("tooltip.createqol.player_paper.linked_player",p_41422_.level().getPlayerByUUID(stack.get(QOLDataComponents.LINKED_PLAYER)).getName()));
        } else {
            components.add(Component.translatable("tooltip.createqol.player_paper.no_linked_player"));
        }
        super.appendHoverText(stack, p_41422_, components, p_41424_);

    }
}
