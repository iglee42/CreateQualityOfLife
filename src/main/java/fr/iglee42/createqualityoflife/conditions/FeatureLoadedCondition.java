package fr.iglee42.createqualityoflife.conditions;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.utils.Features;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.common.conditions.ICondition;

public record FeatureLoadedCondition(String feature) implements ICondition {

    public static final MapCodec<FeatureLoadedCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.fieldOf("feature").forGetter(FeatureLoadedCondition::feature)
    ).apply(inst, FeatureLoadedCondition::new));


    @Override
    public boolean test(IContext iContext) {
        return CreateQOL.isActivate(Features.valueOf(feature.toUpperCase()));
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

}
