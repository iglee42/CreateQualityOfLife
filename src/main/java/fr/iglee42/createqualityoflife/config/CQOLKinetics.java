package fr.iglee42.createqualityoflife.config;


import net.createmod.catnip.config.ConfigBase;

public class CQOLKinetics extends ConfigBase {

    public final CQOLStress stressValues = nested(1, CQOLStress::new, Comments.stress);

    @Override
    public String getName() {
        return "kinetics";
    }

    private static class Comments {
        static String stress = "Fine tune the kinetic stats of individual components";

    }
}
