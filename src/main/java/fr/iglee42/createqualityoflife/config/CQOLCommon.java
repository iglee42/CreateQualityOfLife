package fr.iglee42.createqualityoflife.config;


import net.createmod.catnip.config.ConfigBase;

public class CQOLCommon extends ConfigBase {

    public final CQOLKinetics kinetics = nested(0, CQOLKinetics::new,Comments.kinetics);
    public ConfigBool helmetHaveGoggles = b(true,"helmetHaveGoggles",Comments.goggles);
    public ConfigBool propellersAllowed = b(true,"propellersAllowed",Comments.propellers);
    public ConfigBool hoverAllowed = b(true,"hoverAllowed",Comments.hover);
    public ConfigBool armorEffects = b(true,"armorEffectsEnable",Comments.effects);
    public ConfigBool bootsDiving = b(true,"bootsDiving",Comments.diving,Comments.explainDiving);
    public ConfigBool bootsLavaWalking = b(true,"bootsLavaWalking",Comments.lavaWalking,Comments.explainDiving);

    @Override
    public String getName() {
        return "common";
    }

    private static class Comments {
        static String kinetics = "Modify Create Qol blocks comportements";
        static String goggles = "Define if the shadow radiance helmet has goggles";
        static String propellers = "Define if players can add propellers to their shadow radiance chestplate";
        static String hover = "Define if players can activate the hover mode with their shadow radiance chestplate";
        static String effects = "Define if the shadow radiance armor pieces give potion effects";
        static String diving = "Define if the diving effect is applied on the shadow radiance boots";
        static String explainDiving = "Diving makes players descend quicker in liquids ";
        static String lavaWalking = "Define if players can walk normally under lava with shadow radiance boots ";

    }
}
