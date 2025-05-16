package fr.iglee42.createqualityoflife.statue.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Rotations;
import net.minecraft.network.FriendlyByteBuf;

public class StatuePartTable {

    public static final Codec<StatuePartTable> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("xRot").forGetter(StatuePartTable::getXRot),
                    Codec.FLOAT.fieldOf("yRot").forGetter(StatuePartTable::getYRot),
                    Codec.FLOAT.fieldOf("zRot").forGetter(StatuePartTable::getZRot)
            ).apply(instance, StatuePartTable::new));

    public static void encode(FriendlyByteBuf buf, StatuePartTable table) {
        buf.writeFloat(table.getXRot());
        buf.writeFloat(table.getYRot());
        buf.writeFloat(table.getZRot());
    }

    public static StatuePartTable decode(FriendlyByteBuf buf) {
        float x = buf.readFloat();
        float y = buf.readFloat();
        float z = buf.readFloat();
        return new StatuePartTable(x, y, z);
    }

    private final float xRot;
    private final float yRot;
    private final float zRot;

    public StatuePartTable(float xRot, float yRot, float zRot) {
        this.xRot = xRot;
        this.yRot = yRot;
        this.zRot = zRot;
    }

    public StatuePartTable() {
        this(0,0,0);
    }

    public float getXRot() { return xRot; }

    public float getYRot() {
        return yRot;
    }

    public float getZRot() {
        return zRot;
    }

    public Rotations toRotation(){
        return new Rotations(getXRot(),getYRot(),getZRot());
    }
}
