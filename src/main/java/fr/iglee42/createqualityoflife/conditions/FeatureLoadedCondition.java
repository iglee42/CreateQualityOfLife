package fr.iglee42.createqualityoflife.conditions;

import com.google.gson.JsonObject;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.utils.Features;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class FeatureLoadedCondition implements ICondition {

    private final String feature;

    public FeatureLoadedCondition(String feature) {
        this.feature = feature;
    }

    @Override
    public ResourceLocation getID() {
        return new ResourceLocation(CreateQOL.MODID,"feature_loaded");
    }

    @Override
    public boolean test(IContext iContext) {
        return CreateQOL.isActivate(Features.valueOf(feature.toUpperCase()));
    }

    public static class Serializer implements IConditionSerializer<FeatureLoadedCondition> {
        public static final FeatureLoadedCondition.Serializer INSTANCE = new FeatureLoadedCondition.Serializer();

        public Serializer() {
        }

        public void write(JsonObject json, FeatureLoadedCondition value) {
            json.addProperty("feature", value.feature.toString());
        }

        public FeatureLoadedCondition read(JsonObject json) {
            return new FeatureLoadedCondition(GsonHelper.getAsString(json,"feature"));
        }

        public ResourceLocation getID() {
            return new ResourceLocation(CreateQOL.MODID,"feature_loaded");
        }
    }
}
