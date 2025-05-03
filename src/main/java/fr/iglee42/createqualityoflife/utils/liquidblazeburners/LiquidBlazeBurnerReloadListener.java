package fr.iglee42.createqualityoflife.utils.liquidblazeburners;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.iglee42.createqualityoflife.CreateQOL;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Optional;

public class LiquidBlazeBurnerReloadListener extends SimpleJsonResourceReloadListener {

    public static final String FLUID_KEY = "fluid";
    public static final String BURN_TIME_KEY = "burnTime";
    public static final int DEFAULT_BURN_TIME = 20;
    public static final String SUPER_HEATED_KEY = "superHeat";
    public static final String CONSUMPTION_KEY = "consumptionPerTick";
    public static final int DEFAULT_CONSUMPTION = 1;

    ICondition.IContext context;

    public static final LiquidBlazeBurnerReloadListener INSTANCE = new LiquidBlazeBurnerReloadListener();

    public LiquidBlazeBurnerReloadListener() {
        this(ICondition.IContext.EMPTY);
    }
    public LiquidBlazeBurnerReloadListener(ICondition.IContext context) {
        super(new Gson(), "blaze_burner_liquids");
        this.context = context;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager manager, ProfilerFiller filler) {
        LiquidBlazeBurnerManager.BLAZE_BURNER_LIQUIDS.clear();
        CreateQOL.LOGGER.info("Reloading Liquids for Blaze Burner...");

        jsons.forEach((rs,el)->{
            try  {
                if (!el.isJsonObject()) throw new RuntimeException(rs.getPath() + " doesn't contain a json object as root");
                JsonObject json = el.getAsJsonObject();
                if (!CraftingHelper.processConditions(json,"conditions",context)){
                    CreateQOL.LOGGER.debug("Skipping loading blaze burner liquid {} as its conditions were not met", rs);
                    return;
                }
                if (!json.has(FLUID_KEY)) throw new JsonParseException("Missing fluid key");
                String fluid = json.get(FLUID_KEY).getAsString();
                if (!ForgeRegistries.FLUIDS.containsKey(ResourceLocation.tryParse(fluid))) throw new JsonParseException(fluid + " is invalid in blaze burner liquid " + rs.toString());
                int burnTime = json.has(BURN_TIME_KEY) ? json.get(BURN_TIME_KEY).getAsInt() : DEFAULT_BURN_TIME;
                if (burnTime < 1) throw new JsonParseException("Burn time must be at least 1 in " + rs.toString());
                boolean superHeat = json.has(SUPER_HEATED_KEY) && json.get(SUPER_HEATED_KEY).getAsBoolean();
                int consumption = json.has(CONSUMPTION_KEY) ? json.get(CONSUMPTION_KEY).getAsInt() : DEFAULT_CONSUMPTION;
                if (consumption < 0) throw new JsonParseException("Consumption can't be negative in " + rs.toString());
                new Entry(ForgeRegistries.FLUIDS.getValue(ResourceLocation.tryParse(fluid)),burnTime,superHeat,consumption).registerToMap();
            } catch (JsonParseException | IllegalArgumentException exception) {
                CreateQOL.LOGGER.error("Parsing error loading blaze burner liquid {}", rs, exception);
            }
        });
        CreateQOL.LOGGER.info("Liquids for Blaze Burner Reloaded, found {} liquids.", LiquidBlazeBurnerManager.BLAZE_BURNER_LIQUIDS.size());
    }

    private record Entry(Fluid fluid, int burnTime, boolean superHeat,int consumption){

        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BuiltInRegistries.FLUID.byNameCodec().fieldOf(FLUID_KEY).forGetter(Entry::fluid),
                        Codec.INT.optionalFieldOf(BURN_TIME_KEY,DEFAULT_BURN_TIME).forGetter(Entry::burnTime),
                        Codec.BOOL.optionalFieldOf(SUPER_HEATED_KEY,false).forGetter(Entry::superHeat),
                        Codec.INT.optionalFieldOf(CONSUMPTION_KEY,DEFAULT_CONSUMPTION).forGetter(Entry::consumption)
                ).apply(instance,Entry::new));


        public void registerToMap() throws IllegalArgumentException{
            if (LiquidBlazeBurnerManager.BLAZE_BURNER_LIQUIDS.containsKey(fluid)) throw new IllegalArgumentException("Fluid " + fluid + " is registered for blaze burners in two different files");
            LiquidBlazeBurnerManager.BLAZE_BURNER_LIQUIDS.put(fluid,new LiquidBlazeBurnerManager.LiquidEntry(burnTime,superHeat,consumption));
        }
    }
}
