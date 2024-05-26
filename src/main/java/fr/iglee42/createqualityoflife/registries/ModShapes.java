package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.belt.BeltShapes;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;
import static net.minecraft.world.level.block.Block.box;

public class ModShapes {



    public static final VoxelShaper FUNNELED_BELT = shape(makeFlatFull()).add(shape(2, 14, 14, 14, 30, 18).add(0, 11, 8, 16, 32, 14)
            .forHorizontal(NORTH).get(NORTH)).add(shape(2, 14, 14, 14, 30, 18).add(0, 11, 8, 16, 32, 14)
            .forHorizontal(NORTH).get(SOUTH)).forHorizontalAxis();


    private static AllShapes.Builder shape(VoxelShape shape) {
        return new AllShapes.Builder(shape);
    }

    private static AllShapes.Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }


    private static VoxelShape makeFlatFull(){
        return box(1,3,0,15,13,16);
    }
}
