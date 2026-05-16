package fr.iglee42.createqualityoflife.config;


import net.createmod.catnip.config.ConfigBase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class CQOLLogistics extends ConfigBase {

    public final ConfigInt enderLinkRange = i(256, 1, "enderLinkRange", Comments.linkRange);
    public final ConfigInt stockManagerMaxDestroyDistance = i(64,1,"stockManagerMaxDestroyDistance", Comments.stockManagerMaxDestroyDistance);
    public CValue<List<? extends String>, ModConfigSpec.ConfigValue<List<? extends String>>> trashCanItemBlacklist = new CValue<>(
        "trashcan_item_blacklist",
            builder->builder.defineList("trashcan_item_blacklist",new ArrayList<>(),()->"minecraft:stone",obj->{
                    if (!(obj instanceof String str))return false;
                    ResourceLocation id = ResourceLocation.tryParse(str);
                    if (id == null) return false;
                    return BuiltInRegistries.ITEM.containsKey(id);
                }
            ),
            Comments.trashCanItemBlacklist
    );
    public CValue<List<? extends String>, ModConfigSpec.ConfigValue<List<? extends String>>> trashCanFluidBlacklist = new CValue<>(
            "trashcan_fluid_blacklist",
            builder->builder.defineList("trashcan_fluid_blacklist",new ArrayList<>(),()->"minecraft:water",obj->{
                        if (!(obj instanceof String str))return false;
                        ResourceLocation id = ResourceLocation.tryParse(str);
                        if (id == null) return false;
                        return BuiltInRegistries.FLUID.containsKey(id);
                    }
            ),
            Comments.trashCanFluidBlacklist
    );


    @Override
    public String getName() {
        return "logistics";
    }

    private static class Comments {

        static String linkRange = "Define the maximum possible range in blocks of ender packager connections.";
        static String stockManagerMaxDestroyDistance = "Define the maximum distance (relative to the player) where the blaze manager can destroy a network component";
        static String trashCanItemBlacklist = "The items that the trash can won't accept at all";
        static String trashCanFluidBlacklist = "The fluids that the trash can won't accept at all";
    }
}
