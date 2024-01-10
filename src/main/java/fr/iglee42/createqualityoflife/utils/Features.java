package fr.iglee42.createqualityoflife.utils;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.config.ConfigBase;
import fr.iglee42.createqualityoflife.config.CreateQOLCommonConfig;

import java.lang.reflect.Field;

public enum Features {

    CHIPPED_SAW("chipped_saw", CreateQOLCommonConfig.chippedSaw),
    SHADOW_RADIANCE("shadow_radiance", CreateQOLCommonConfig.shadowRadiance),
    INVENTORY_LINKER("inventory_linker", CreateQOLCommonConfig.inventoryLinker),
    PROXIMITY_SCHEDULE("proximity_schedule", CreateQOLCommonConfig.proximitySchedule),

    ;


    private final Boolean config;
    private final String name;

    Features(String name, Boolean config) {
        this.name = name;
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public Boolean getConfig() {
        return config;
    }
}
