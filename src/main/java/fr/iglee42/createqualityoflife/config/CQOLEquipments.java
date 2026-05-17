package fr.iglee42.createqualityoflife.config;

import net.createmod.catnip.config.ConfigBase;

public class CQOLEquipments extends ConfigBase {

    public final ConfigBool useAir = b(true, "useAir", "Use backtank air before item durability for Refined Radiance, Shadow Steel, and Shadow Radiance equipment.");

    public final CQOLArmors armors = nested(1,CQOLArmors::new,Comments.armors);
    public final CQOLTools tools = nested(1,CQOLTools::new,Comments.tools);

    @Override
    public String getName() {
        return "equipments";
    }

    private static class Comments{
        static String armors = "Modify Create Qol armors comportment";
        static String tools = "Modify Create Qol tools comportment";

    }
}
