package fr.iglee42.createqualityoflife.utils;

import java.util.List;

public interface CopyNBTsExtension {
    List<String> createQOL$copiedNBTs();
    void createQOL$setCopiedNBTs(List<String> list);

    default int createQOL$copiedSlot(){return 0;};
    default void createQOL$setCopiedSlot(int slot){}
}
