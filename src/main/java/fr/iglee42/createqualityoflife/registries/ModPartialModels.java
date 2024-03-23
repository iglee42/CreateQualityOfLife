package fr.iglee42.createqualityoflife.registries;

import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.resources.ResourceLocation;

import static fr.iglee42.createqualityoflife.CreateQOL.MODID;

public class ModPartialModels {

    public static final PartialModel

            INVENTORY_LINKER = block("inventory_linker/inner_off"), INVENTORY_LINKER_ON = block("inventory_linker/inner_on"),

            SHADOW_RADIANCE_TANK_COGS = block("shadow_radiance_chestplate/cogs")

            ;

    private static PartialModel block(String path) {
        return new PartialModel(new ResourceLocation(MODID,"block/"+path));
    }

    public static void init(){}

}