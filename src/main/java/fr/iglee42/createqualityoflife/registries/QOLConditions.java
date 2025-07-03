package fr.iglee42.createqualityoflife.registries;

import com.mojang.serialization.MapCodec;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.conditions.FeatureLoadedCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class QOLConditions {

    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, CreateQOL.MODID);

    public static final Supplier<MapCodec<FeatureLoadedCondition>> FEATURE = CONDITIONS.register("feature_loaded",()->FeatureLoadedCondition.CODEC);

}
