package fr.iglee42.createqualityoflife.behaviours;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import fr.iglee42.createqualityoflife.blockentitites.BrassTrashCanBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrashCanMovementBehaviour implements MovementBehaviour {


    private final boolean brass;

    public static TrashCanMovementBehaviour normal(){
        return new TrashCanMovementBehaviour(false);
    }

    public static TrashCanMovementBehaviour brass(){
        return new TrashCanMovementBehaviour(true);
    }

    private TrashCanMovementBehaviour(boolean brass) {
        this.brass = brass;
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        Contraption contraption = context.contraption;
        MountedStorageManager storage =contraption.getStorage();
        if (!brass){
            for (int i = 0; i < storage.getAllItems().getSlots(); i++) {
                storage.getAllItems().extractItem(i,64,false);
            }
        } else {
            FilterItemStack filter = context.getFilterFromBE();

            BrassTrashCanBlockEntity.Mode mode = BrassTrashCanBlockEntity.Mode.getById(context.blockEntityData.getInt("ScrollValue"));
            List<Item> savedItems = new ArrayList<>();
            for (int i = 0; i < storage.getAllItems().getSlots(); i++) {
                ItemStack stack = storage.getAllItems().getStackInSlot(i);
                if (!filter.test(context.world,stack)) continue;
                if (mode == BrassTrashCanBlockEntity.Mode.KEEP_64 && !savedItems.contains(stack.getItem())) savedItems.add(stack.getItem());
                else storage.getAllItems().extractItem(i,64,false);
            }
        }
    }
}
