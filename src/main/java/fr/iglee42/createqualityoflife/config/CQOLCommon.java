package fr.iglee42.createqualityoflife.config;


import net.createmod.catnip.config.ConfigBase;

public class CQOLServer extends ConfigBase {

    public final CQOLKinetics kinetics = nested(0, CQOLKinetics::new,Comments.kinetics);
    public ConfigBool helmetHaveGoggles = b(true,"helmetHaveGoggles",Comments.goggles);
    public ConfigBool propellersAllowed = b(true,"propellersAllowed",Comments.propellers);
    public ConfigBool hoverAllowed = b(true,"hoverAllowed",Comments.hover);
    public ConfigBool armorEffects = b(true,"armorEffectsEnable",Comments.effects);
    public ConfigBool bootsDiving = b(true,"bootsDiving",Comments.diving,Comments.explainDiving);
    public ConfigBool bootsLavaWalking = b(true,"bootsLavaWalking",Comments.lavaWalking);

    public ConfigInt statueDistance = i(16,"statueMaxDistance",Comments.statueDistance);
    public ConfigBool experimentalWarning = b(true,"experimentalWarning",Comments.experimentalWarning);

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String kinetics = "Modify Create Qol blocks comportment";
        static String goggles = "Define if the shadow radiance helmet has goggles";
        static String propellers = "Define if players can add propellers to their shadow radiance chestplate";
        static String hover = "Define if players can activate the hover mode with their shadow radiance chestplate";
        static String effects = "Define if the shadow radiance armor pieces give potion effects";
        static String diving = "Define if the diving effect is applied on the shadow radiance boots";
        static String explainDiving = "Diving makes players descend quicker in liquids ";
        static String lavaWalking = "Define if players can walk normally under lava with shadow radiance boots";
        static String statueDistance = "Define the max distance can be set to the statue";
        static String experimentalWarning = "Should the op players be warning about experimental features";
    }
}
