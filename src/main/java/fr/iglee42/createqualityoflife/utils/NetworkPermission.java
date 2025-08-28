package fr.iglee42.createqualityoflife.utils;

import net.minecraft.util.StringRepresentable;

public enum NetworkPermission implements StringRepresentable{

    NONE(),
    MEMBER(),
    ADMIN(),
    OWNER();


    public NetworkPermission next(){
        int nextOrdinal = (this.ordinal() + 1) % values().length;
        return values()[nextOrdinal];
    }

    public NetworkPermission previous() {
        int prevOrdinal = (this.ordinal() - 1 + values().length) % values().length;
        return values()[prevOrdinal];
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
