package fr.iglee42.createqualityoflife.config;


import net.createmod.catnip.config.ConfigBase;

public class CQOLLogistics extends ConfigBase {

    public final ConfigInt enderLinkRange = i(256, 1, "enderLinkRange", Comments.linkRange);
    public final ConfigInt stockManagerMaxDestroyDistance = i(64,1,"stockManagerMaxDestroyDistance", Comments.stockManagerMaxDestroyDistance);

    @Override
    public String getName() {
        return "logistics";
    }

    private static class Comments {

        static String linkRange = "Define the maximum possible range in blocks of ender packager connections.";
        static String stockManagerMaxDestroyDistance = "Define the maximum distance (relative to the player) where the blaze manager can destroy a network component";
    }
}
