package fr.iglee42.createqualityoflife.blocks;

import com.simibubi.create.content.equipment.armor.BacktankBlock;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import fr.iglee42.createqualityoflife.registries.QOLBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RefinedRadianceBacktankBlock extends BacktankBlock {

    public RefinedRadianceBacktankBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<BacktankBlockEntity> getBlockEntityClass() {
        return super.getBlockEntityClass();
    }

    @Override
    public BlockEntityType<? extends BacktankBlockEntity> getBlockEntityType() {
        return QOLBlockEntities.REFINED_RADIANCE_CHEST_BE
                .get();
    }
}
