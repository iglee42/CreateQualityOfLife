package fr.iglee42.createqualityoflife.config;

import net.createmod.catnip.config.ConfigBase;

public class CQOLTools extends ConfigBase {

    public final ConfigBool reach = b(true,"reach",Comments.reach);
    public final ConfigBool digging = b(true,"digging",Comments.digging);
    public final ConfigBool veinMine = b(true,"veinMine",Comments.veinMine);
    public final ConfigInt veinMineMaxBlocks = i(64,1,Integer.MAX_VALUE,"veinMineMaxBlocks",Comments.veinMineMaxBlocks);
    public final ConfigBool treeDecapitation = b(true,"treeDecapitation",Comments.treeDecapitation);
    public final ConfigBool casingifier = b(true,"casingifier",Comments.casingifier);
    public final ConfigInt casingifierMaxBlocks = i(64,1,Integer.MAX_VALUE,"casingifierMaxBlocks",Comments.casingifierMaxBlocks);
    public final ConfigBool smelting = b(true,"smelting",Comments.smelting);
    public final ConfigBool harvesting = b(true,"harvesting",Comments.harvesting);
    public final ConfigBool ploughing = b(true,"ploughing",Comments.ploughing);
    public final ConfigBool swordsAbilities = b(true,"swordsAbilities",Comments.swordsAbilities);
    public final ConfigInt swordsAirConsumption = i(1,0,Integer.MAX_VALUE,"swordsAirConsumption",Comments.swordsAirConsumption);
    public final ConfigFloat swordsRadius = f(4.5f,0.5f,10,"swordsRadius",Comments.swordsRadius);
    public final ConfigFloat swordsStrength = f(1.5f,0.5f,10,"swordsStrength",Comments.swordsStrength);
    public final ConfigInt swordsChargeTime = i(50,0,Integer.MAX_VALUE,"swordsChargeTime",Comments.swordsChargeTime);
    public final ConfigInt swordsCooldowns = i(100,0,Integer.MAX_VALUE,"swordsCooldowns",Comments.swordsCooldowns);

    @Override
    public String getName() {
        return "tools";
    }

    private static class Comments {
        static String reach = "Define if players' reach should be modified by Create QOL Tools";
        static String digging = "Define if players can make a hole of 3x3x3 when mining a block with pickaxes";
        static String veinMine = "Define if all the blocks of the same types should be destroy when mining with pickaxes";
        static String veinMineMaxBlocks = "Define the maximum amount of blocks that can be mined by the vein mine. Warning if this value is too high, lags can be created";
        static String treeDecapitation = "Define if when a wood block is broken, all the tree should be destroyed";
        static String casingifier = "Define if the casingifier function is available";
        static String casingifierMaxBlocks = "Define the maximum amount of blocks that can be modified by the casingifier. Warning if this value is too high, lags can be created";
        static String smelting = "Define if the smelting function is available on shovels";
        static String harvesting = "Define if the harvesting function is available on hoes";
        static String ploughing = "Define if the player can plough dirt in a 3x3 square";
        static String swordsAbilities = "Define if the swords repulsion/attraction effects are enable";
        static String swordsAirConsumption = "Define the amount of air used each time players use swords repulsion/attraction effect. If set to 0, air is not required when using the ability";
        static String swordsRadius = "Define the radius for the swords repulsion/attraction effect";
        static String swordsStrength = "Define the max strength for the swords repulsion/attraction effect. The strength formula is maxStrength * (chargeTime / maxChargeTime)";
        static String swordsChargeTime = "Define the max charge time (in ticks) for the swords repulsion/attraction effect";
        static String swordsCooldowns = "Define the cooldown (in ticks) between two uses of swords repulsion/attraction effect";
    }
}
