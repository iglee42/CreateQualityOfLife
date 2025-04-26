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
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Map;
import java.util.Optional;

public class LiquidBlazeBurnerReloadListener extends SimpleJsonResourceReloadListener {

    public static final String FLUID_KEY = "fluid";
    public static final String BURN_TIME_KEY = "burn_time";
    public static final int DEFAULT_BURN_TIME = 20;
    public static final String SUPER_HEATED_KEY = "super_heat";
    public static final String CONSUMPTION_KEY = "consumptionPerTick";
    public static final int DEFAULT_CONSUMPTION = 1;

    public static final LiquidBlazeBurnerReloadListener INSTANCE = new LiquidBlazeBurnerReloadListener();

    public LiquidBlazeBurnerReloadListener() {
        super(new Gson(), "blaze_burner_liquids");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager manager, ProfilerFiller filler) {
        LiquidBlazeBurnerManager.BLAZE_BURNER_LIQUIDS.clear();
        CreateQOL.LOGGER.info("Reloading Liquids for Blaze Burner...");
        RegistryOps<JsonElement> registryops = this.makeConditionalOps();

        jsons.forEach((rs,el)->{
            try  {
                if (!el.isJsonObject()) throw new RuntimeException(rs.getPath() + " doesn't contain a json object as root");
                JsonObject json = el.getAsJsonObject();
                Optional<WithConditions<Entry>> decoded = Entry.CONDITIONAL_CODEC.parse(registryops, json).getOrThrow(JsonParseException::new);
                decoded.ifPresentOrElse(e->{
                    Entry entry = e.carrier();
                    if (LiquidBlazeBurnerManager.BLAZE_BURNER_LIQUIDS.containsKey(entry.fluid)) throw new RuntimeException("Fluid " + entry.fluid + " is registered for blaze burners in two different files");
                    entry.registerToMap();
                },() -> CreateQOL.LOGGER.debug("Skipping loading blaze burner liquid {} as its conditions were not met", rs));
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

        public static final Codec<Optional<WithConditions<Entry>>> CONDITIONAL_CODEC = ConditionalOps.createConditionalCodecWithConditions(CODEC);


        public void registerToMap(){
            LiquidBlazeBurnerManager.BLAZE_BURNER_LIQUIDS.put(fluid,new LiquidBlazeBurnerManager.LiquidEntry(burnTime,superHeat,consumption));
        }
    }
}
