package fr.iglee42.createqualityoflife.config;


import net.createmod.catnip.config.ConfigBase;

public class CQOLServer extends ConfigBase {

    public final CQOLKinetics kinetics = nested(0, CQOLKinetics::new,Comments.kinetics);
    public final CQOLEquipments equipments = nested(0, CQOLEquipments::new,Comments.equipments);


    public ConfigInt statueDistance = i(16,1,"statueMaxDistance",Comments.statueDistance);
    public ConfigBool experimentalWarning = b(true,"experimentalWarning",Comments.experimentalWarning);

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String kinetics = "Modify Create Qol blocks comportment";
        static String equipments = "Modify Create Qol equipments comportment";
        static String statueDistance = "Define the max distance can be set to the statue";
        static String experimentalWarning = "Should the op players be warned about experimental features";
    }
}
