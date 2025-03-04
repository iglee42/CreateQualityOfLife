package fr.iglee42.createqualityoflife.registries;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import fr.iglee42.createqualityoflife.CreateQOL;
import net.minecraft.resources.ResourceLocation;

import static fr.iglee42.createqualityoflife.CreateQOL.MODID;

public class ModPartialModels {

    public static final PartialModel

            INVENTORY_LINKER = block("inventory_linker/inner_off"), INVENTORY_LINKER_ON = block("inventory_linker/inner_on"),

            SHADOW_RADIANCE_TANK_COGS = block("shadow_radiance_chestplate/cogs"), SHADOW_RADIANCE_TANK_SHAFT = block("shadow_radiance_chestplate/block_shaft_input"),
            SHADOW_RADIANCE_CHESTPLATE_PROPELLERS = block("shadow_radiance_chestplate/propellers"),SHADOW_RADIANCE_CHESTPLATE_PROPELLERS_ALT = block("shadow_radiance_chestplate/propellers_alt")
            ;

    private static PartialModel block(String path) {
        return PartialModel.of(CreateQOL.asResource("block/"+path));
    }

    public static void init(){}

}