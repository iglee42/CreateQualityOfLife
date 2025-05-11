package fr.iglee42.createqualityoflife.statue.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class StatueAnimationFrame{

    public static final Codec<StatueAnimationFrame> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("tick").forGetter(frame -> frame.tick),
            StatuePartTable.CODEC.fieldOf("global").forGetter(frame -> frame.global),
            StatuePartTable.CODEC.fieldOf("head").forGetter(frame -> frame.head),
            StatuePartTable.CODEC.fieldOf("leftArm").forGetter(frame -> frame.leftArm),
            StatuePartTable.CODEC.fieldOf("rightArm").forGetter(frame -> frame.rightArm),
            StatuePartTable.CODEC.fieldOf("leftLeg").forGetter(frame -> frame.leftLeg),
            StatuePartTable.CODEC.fieldOf("rightLeg").forGetter(frame -> frame.rightLeg)
    ).apply(instance, StatueAnimationFrame::new));

    public static final StreamCodec<FriendlyByteBuf,StatueAnimationFrame> STREAM_CODEC = new StreamCodec<FriendlyByteBuf, StatueAnimationFrame>() {
        @Override
        public @NotNull StatueAnimationFrame decode(FriendlyByteBuf buf) {
            int ticks = buf.readInt();
            StatuePartTable global = StatuePartTable.STREAM_CODEC.decode(buf);
            StatuePartTable head = StatuePartTable.STREAM_CODEC.decode(buf);
            StatuePartTable leftArm = StatuePartTable.STREAM_CODEC.decode(buf);
            StatuePartTable rightArm = StatuePartTable.STREAM_CODEC.decode(buf);
            StatuePartTable leftLeg = StatuePartTable.STREAM_CODEC.decode(buf);
            StatuePartTable rightLeg = StatuePartTable.STREAM_CODEC.decode(buf);
            return new StatueAnimationFrame(ticks,global,head,leftArm,rightArm,leftLeg,rightLeg);
        }

        @Override
        public void encode(@NotNull FriendlyByteBuf buf, @NotNull StatueAnimationFrame frame) {
            buf.writeInt(frame.tick);
            StatuePartTable.STREAM_CODEC.encode(buf,frame.global);
            StatuePartTable.STREAM_CODEC.encode(buf,frame.head);
            StatuePartTable.STREAM_CODEC.encode(buf,frame.leftArm);
            StatuePartTable.STREAM_CODEC.encode(buf,frame.rightArm);
            StatuePartTable.STREAM_CODEC.encode(buf,frame.leftLeg);
            StatuePartTable.STREAM_CODEC.encode(buf,frame.rightLeg);
        }
    };
    private final int tick;
    private StatuePartTable global = new StatuePartTable();
    private StatuePartTable head = new StatuePartTable();
    private StatuePartTable leftArm = new StatuePartTable();
    private StatuePartTable rightArm = new StatuePartTable();
    private StatuePartTable leftLeg = new StatuePartTable();
    private StatuePartTable rightLeg = new StatuePartTable();

    protected StatueAnimationFrame(int tick, StatuePartTable global, StatuePartTable head, StatuePartTable leftArm, StatuePartTable rightArm, StatuePartTable leftLeg, StatuePartTable rightLeg) {
        this.tick = tick;
        this.global = global;
        this.head = head;
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
    }

    public StatueAnimationFrame(int tick) {
        this.tick = tick;
    }

    public StatueAnimationFrame withGlobalRotation(float x,float y, float z){
        this.global = new StatuePartTable(x,y,z);
        return this;
    }

    public StatueAnimationFrame withHeadRotation(float x,float y, float z){
        this.head = new StatuePartTable(x,y,z);
        return this;
    }

    public StatueAnimationFrame withLeftArmRotation(float x,float y, float z){
        this.leftArm = new StatuePartTable(x,y,z);
        return this;
    }

    public StatueAnimationFrame withRightArmRotation(float x,float y, float z){
        this.rightArm = new StatuePartTable(x,y,z);
        return this;
    }

    public StatueAnimationFrame withLeftLegRotation(float x,float y, float z){
        this.leftLeg = new StatuePartTable(x,y,z);
        return this;
    }

    public StatueAnimationFrame withRightLegRotation(float x,float y, float z){
        this.rightLeg = new StatuePartTable(x,y,z);
        return this;
    }

    public StatuePartTable getGlobal() {
        return global;
    }

    public StatuePartTable getHead() {
        return head;
    }

    public StatuePartTable getLeftArm() {
        return leftArm;
    }

    public StatuePartTable getRightArm() {
        return rightArm;
    }

    public StatuePartTable getLeftLeg() {
        return leftLeg;
    }

    public StatuePartTable getRightLeg() {
        return rightLeg;
    }

    public int getTick() {
        return tick;
    }
}
