package fr.iglee42.createqualityoflife.utils;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface CopyComponentsExtension {
    List<ResourceLocation> createQOL$copiedComponents();
    void createQOL$setCopiedComponents(List<ResourceLocation> list);

    default int createQOL$copiedSlot(){return 0;};
    default void createQOL$setCopiedSlot(int slot){}
}
