package fr.iglee42.createqualityoflife.blockentitites.instances;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import fr.iglee42.createqualityoflife.blockentitites.InventoryLinkerBlockEntity;
import fr.iglee42.createqualityoflife.registries.ModPartialModels;

public class InventoryLinkerInstance extends SingleRotatingInstance<InventoryLinkerBlockEntity> {
    public InventoryLinkerInstance(MaterialManager materialManager, InventoryLinkerBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    protected Instancer<RotatingData> getModel() {
        return !blockEntity.getPlayerPaperItemStack().isEmpty() ? getRotatingMaterial().getModel(ModPartialModels.INVENTORY_LINKER_ON, blockState) : getRotatingMaterial().getModel(ModPartialModels.INVENTORY_LINKER, blockState);
    }
}
