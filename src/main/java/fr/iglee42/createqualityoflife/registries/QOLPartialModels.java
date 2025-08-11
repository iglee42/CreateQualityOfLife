package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.box.PackageStyles;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import fr.iglee42.createqualityoflife.CreateQOL;
import net.minecraft.resources.ResourceLocation;

public class QOLPartialModels {

    public static final PartialModel

            INVENTORY_LINKER = block("inventory_linker/inner_off"), INVENTORY_LINKER_ON = block("inventory_linker/inner_on"),

            SHADOW_RADIANCE_TANK_COGS = block("shadow_radiance_chestplate/cogs"), SHADOW_RADIANCE_TANK_SHAFT = block("shadow_radiance_chestplate/block_shaft_input"),
            SHADOW_RADIANCE_CHESTPLATE_PROPELLERS = block("shadow_radiance_chestplate/propellers"),SHADOW_RADIANCE_CHESTPLATE_PROPELLERS_ALT = block("shadow_radiance_chestplate/propellers_alt"),

            SHADOW_STEEL_TANK_COGS = block("shadow_steel_chestplate/cogs"), SHADOW_STEEL_TANK_SHAFT = block("shadow_steel_chestplate/block_shaft_input"),
            REFINED_RADIANCE_TANK_COGS = block("refined_radiance_chestplate/cogs"), REFINED_RADIANCE_TANK_SHAFT = block("refined_radiance_chestplate/block_shaft_input"),

            ENDER_PACKAGER_HATCH_CLOSED = block("ender_packager/hatch_closed"),
            ENDER_PACKAGER_HATCH_OPEN = block("ender_packager/hatch_open"),

            STOCK_MANAGER_RODS_1 = block("stock_manager/rods_small"),
            STOCK_MANAGER_RODS_2 = block("stock_manager/rods_large")
    ;

    private static PartialModel block(String path) {
        return PartialModel.of(CreateQOL.asResource("block/"+path));
    }

    public static void init(){
        registerPackageStyle(QOLItems.FURTI);
        registerPackageStyle(QOLItems.DELTA);
        registerPackageStyle(QOLItems.IGLEE);
    }

    private static void registerPackageStyle(PackageStyles.PackageStyle style){
        ResourceLocation key = CreateQOL.asResource(style.getItemId().getPath());
        PartialModel model = PartialModel.of(CreateQOL.asResource("item/" + key.getPath()));
        AllPartialModels.PACKAGES.put(key, model);
        if (!style.rare())
            AllPartialModels.PACKAGES_TO_HIDE_AS.add(model);
        AllPartialModels.PACKAGE_RIGGING.put(key, PartialModel.of(style.getRiggingModel()));
    }

}