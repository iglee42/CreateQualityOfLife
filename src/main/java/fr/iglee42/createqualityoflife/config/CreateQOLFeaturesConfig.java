package fr.iglee42.createqualityoflife.config;

import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

public class CreateQOLFeaturesConfig {

    private static File configFile;

    public static boolean chippedSaw = true;
    public static boolean inventoryLinker = true;
    public static boolean shadowRadiance = true;
    public static boolean proximitySchedule = true;
    public static boolean displayBoardModification = true;
    public static boolean blazeBurnerUseLiquids = true;
    public static boolean statue = false;
    public static boolean trashCan = true;




    public static void load() throws IOException, IllegalAccessException {
        configFile = new File(FMLPaths.CONFIGDIR.get().toFile(),"createqol-features.json");
        if (configFile.exists()){
            JsonObject config = new Gson().fromJson(new FileReader(configFile),JsonObject.class);
            for (Field f : Arrays.stream(CreateQOLFeaturesConfig.class.getDeclaredFields()).filter(f->f.getType().equals(boolean.class)).toList()){
                if (config.has(f.getName())) f.setBoolean(null, config.get(f.getName()).getAsBoolean());
                else {
                    config.addProperty(f.getName(),f.getBoolean(null));
                    FileWriter writer = new FileWriter(configFile);
                    writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(config));
                    writer.close();
                }
            }
        } else {
            JsonObject config = new JsonObject();
            config.addProperty("chippedSaw",chippedSaw);
            config.addProperty("inventoryLinker",inventoryLinker);
            config.addProperty("shadowRadiance",shadowRadiance);
            config.addProperty("proximitySchedule",proximitySchedule);
            config.addProperty("displayBoardModification",displayBoardModification);
            config.addProperty("blazeBurnerUseLiquids",blazeBurnerUseLiquids);
            config.addProperty("statue",statue);
            config.addProperty("trashCan",trashCan);
            FileWriter writer = new FileWriter(configFile);
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(config));
            writer.close();
        }
    }
}
