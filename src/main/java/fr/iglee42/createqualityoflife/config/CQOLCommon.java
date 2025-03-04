package fr.iglee42.createqualityoflife.config;


import net.createmod.catnip.config.ConfigBase;

public class CQOLCommon extends ConfigBase {

    public final CQOLKinetics kinetics = nested(0, CQOLKinetics::new,Comments.kinetics);


    @Override
    public String getName() {
        return "common";
    }

    private static class Comments {
        static String kinetics = "Modify Create Qol blocks comportements";

    }
}
