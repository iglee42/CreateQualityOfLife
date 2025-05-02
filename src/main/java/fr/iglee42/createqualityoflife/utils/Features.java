package fr.iglee42.createqualityoflife.utils;

import fr.iglee42.createqualityoflife.config.CreateQOLFeaturesConfig;

public enum Features {

    CHIPPED_SAW("chipped_saw", CreateQOLFeaturesConfig.chippedSaw),
    SHADOW_RADIANCE("shadow_radiance", CreateQOLFeaturesConfig.shadowRadiance),
    INVENTORY_LINKER("inventory_linker", CreateQOLFeaturesConfig.inventoryLinker),
    PROXIMITY_SCHEDULE("proximity_schedule", CreateQOLFeaturesConfig.proximitySchedule),
    DISPLAY_BOARD_MODIFICATION("display_board_modification", CreateQOLFeaturesConfig.displayBoardModification),
    LIQUID_BLAZE_BURNER("liquid_blaze_burner", CreateQOLFeaturesConfig.blazeBurnerUseLiquids),
    STATUE("statue", CreateQOLFeaturesConfig.statue),
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
