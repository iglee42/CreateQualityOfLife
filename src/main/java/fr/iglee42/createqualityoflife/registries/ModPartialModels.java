package fr.iglee42.createqualityoflife.registries;

import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.resources.ResourceLocation;

import static fr.iglee42.createqualityoflife.CreateQOL.MODID;

public class ModPartialModels {

    public static final PartialModel

            INVENTORY_LINKER = block("inventory_linker/inner_off"), INVENTORY_LINKER_ON = block("inventory_linker/inner_on"),

            SHADOW_RADIANCE_TANK_COGS = block("shadow_radiance_chestplate/cogs"),
            SHADOW_RADIANCE_CHESTPLATE_PROPELLERS = block("shadow_radiance_chestplate/propellers"),SHADOW_RADIANCE_CHESTPLATE_PROPELLERS_ALT = block("shadow_radiance_chestplate/propellers_alt"),
            FUNNELED_BELT_TOP = block("funneled_belt/belt"), FUNNELED_BELT_BOTTOM = block("funneled_belt/belt_bottom"),
            SINGLE_BELT_TOP = block("single_belt/top"), SINGLE_BELT_BOTTOM = block("single_belt/bottom")

            ;

    private static PartialModel block(String path) {
        return new PartialModel(new ResourceLocation(MODID,"block/"+path));
    }

    public static void init(){}

}