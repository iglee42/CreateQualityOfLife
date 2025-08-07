package fr.iglee42.createqualityoflife.ponder;

import fr.iglee42.createqualityoflife.CreateQOL;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class QOLPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return CreateQOL.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        QOLPonderScenes.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        QOLPonderTags.register(helper);
    }
}