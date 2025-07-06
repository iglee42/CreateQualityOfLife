package fr.iglee42.createqualityoflife.config;


import net.createmod.catnip.config.ConfigBase;

public class CQOLServer extends ConfigBase {

    public final CQOLKinetics kinetics = nested(0, CQOLKinetics::new,Comments.kinetics);
    public ConfigBool helmetHaveGoggles = b(true,"helmetHaveGoggles",Comments.goggles);
    public ConfigBool propellerAllowed = b(true,"propellerAllowed",Comments.propeller);
    public ConfigBool hoverAllowed = b(true,"hoverAllowed",Comments.hover);
    public ConfigBool elytraAllowed = b(true,"elytraAllowed",Comments.elytra);
    public ConfigBool elytraBoostAllowed = b(true,"elytraBoostAllowed",Comments.elytraBoost);
    public ConfigBool useFireworksForBoost = b(false,"useFireworksForBoost",Comments.useFireworksForBoost);
    public ConfigInt fireworkDuration = i(40,1,"fireworkDuration",Comments.fireworkDuration);
    public ConfigBool armorEffects = b(true,"armorEffectsEnable",Comments.effects);
    public ConfigBool bootsDiving = b(true,"bootsDiving",Comments.diving,Comments.explainDiving);
    public ConfigBool bootsLavaWalking = b(true,"bootsLavaWalking",Comments.lavaWalking);
    public ConfigBool dashAllowed = b(true,"dashAllowed",Comments.dash);

    public ConfigInt statueDistance = i(16,1,"statueMaxDistance",Comments.statueDistance);
    public ConfigBool experimentalWarning = b(true,"experimentalWarning",Comments.experimentalWarning);

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String kinetics = "Modify Create Qol blocks comportment";
        static String goggles = "Define if helmets have goggles";
        static String propeller = "Define if players can add a propeller to their shadow radiance chestplate";
        static String hover = "Define if players can activate the hover mode with their shadow radiance chestplate";
        static String elytra = "Define if players can add elytra to their shadow radiance chestplate";
        static String elytraBoost = "Define if players can use air in the chestplate to boost the elytra";
        static String useFireworksForBoost = "Define if the elytra boost must use fireworks instead of air (the elytra boost must be enabled)";
        static String fireworkDuration = "Define the time (in ticks) between the consumption of fireworks for the boost (the usage of fireworks for the boost must be enabled)";
        static String effects = "Define if the items give potion effects";
        static String diving = "Define if the diving effect is applied on the shadow radiance boots";
        static String explainDiving = "Diving makes players descend quicker in liquids ";
        static String lavaWalking = "Define if players can walk normally under lava with shadow radiance/shadow steel boots";
        static String statueDistance = "Define the max distance can be set to the statue";
        static String experimentalWarning = "Should the op players be warned about experimental features";
        static String dash = "Define if players can dash with a shadow radiance/shadow steel chestplate";
    }
}
