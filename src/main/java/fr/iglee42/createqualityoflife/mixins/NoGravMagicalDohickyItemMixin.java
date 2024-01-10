package fr.iglee42.createqualityoflife.mixins;

import com.simibubi.create.content.legacy.NoGravMagicalDohickyItem;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.utils.Features;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NoGravMagicalDohickyItem.class)
public class NoGravMagicalDohickyItemMixin {


    @Inject(method = "fillItemCategory(Lnet/minecraft/world/item/CreativeModeTab;Lnet/minecraft/core/NonNullList;)V",at = @At("HEAD"))
    private void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems, CallbackInfo ci){
        Item item = (Item) (Object)this;
        if (createQualityOfLife$allowdedIn(pCategory,item)) pItems.add(new ItemStack(item));
    }

    @Unique
    public boolean createQualityOfLife$allowdedIn(CreativeModeTab p_41390_, Item it) {
        if (!CreateQOL.isActivate(Features.SHADOW_RADIANCE)) return false;
        if (it.getCreativeTabs().stream().anyMatch((tab) -> {
            return tab == p_41390_;
        })) {
            return true;
        } else {
            CreativeModeTab creativemodetab = it.getItemCategory();
            return creativemodetab != null && (p_41390_ == CreativeModeTab.TAB_SEARCH || p_41390_ == creativemodetab);
        }
    }


}
