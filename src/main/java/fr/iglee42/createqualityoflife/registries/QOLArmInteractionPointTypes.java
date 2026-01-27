package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.Create;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.blocks.TrashCanBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

public class QOLArmInteractionPointTypes {

    static {
        register("trash_can", new TrashType());
    }

    private static <T extends ArmInteractionPointType> void register(String name, T type) {
        Registry.register(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, CreateQOL.asResource(name), type);
    }

    @ApiStatus.Internal
    public static void init() {
    }

    public static class TrashType extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return state.getBlock() instanceof TrashCanBlock;
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new TrashCanInteractionPoint(this, level, pos, state);
        }

    }

    public static class TrashCanInteractionPoint extends AllArmInteractionPointTypes.DepositOnlyArmInteractionPoint{

        public TrashCanInteractionPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        protected Vec3 getInteractionPositionVector() {
            return Vec3.atLowerCornerOf(pos)
                    .add(.5f, 1, .5f);
        }
    }

}
