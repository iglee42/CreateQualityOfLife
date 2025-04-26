package fr.iglee42.createqualityoflife.utils.liquidblazeburners;

import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.Map;

public class LiquidBlazeBurnerManager {

    public static Map<Fluid,LiquidEntry> BLAZE_BURNER_LIQUIDS = new HashMap<>();

    public record LiquidEntry(int burnTime, boolean superHeated, int consumption){};

}
