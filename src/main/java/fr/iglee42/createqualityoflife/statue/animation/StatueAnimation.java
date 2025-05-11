package fr.iglee42.createqualityoflife.statue.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StatueAnimation {

    public static final Codec<StatueAnimation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StatueAnimationFrame.CODEC.listOf().fieldOf("frames").forGetter(StatueAnimation::getFrames),
            Codec.BOOL.fieldOf("loop").forGetter(StatueAnimation::isLooping),
            Codec.BOOL.fieldOf("revert").forGetter(StatueAnimation::canBeRevert)
    ).apply(instance, (frames, loop,revert) -> new StatueAnimation(calculateDuration(frames), loop,revert, frames)));


    public static final StreamCodec<FriendlyByteBuf,StatueAnimation> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public StatueAnimation decode(FriendlyByteBuf buf) {
            int size = buf.readInt();
            List<StatueAnimationFrame> frames = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                frames.add(StatueAnimationFrame.STREAM_CODEC.decode(buf));
            }
            boolean loop = buf.readBoolean();
            boolean revert = buf.readBoolean();
            return new StatueAnimation(calculateDuration(frames), loop,revert, frames);
        }

        @Override
        public void encode(FriendlyByteBuf buf, StatueAnimation animation) {
            buf.writeInt(animation.getFrames().size());
            animation.getFrames().forEach(f -> StatueAnimationFrame.STREAM_CODEC.encode(buf, f));
            buf.writeBoolean(animation.isLooping());
            buf.writeBoolean(animation.canBeRevert());
        }
    };

    private int duration;
    private boolean loop;
    private boolean revert;
    private List<StatueAnimationFrame> frames;

    public StatueAnimation() {
        this.frames = new ArrayList<>();
    }

    public StatueAnimation(int duration, boolean loop, boolean revert, List<StatueAnimationFrame> frames) {
        this.duration = duration;
        this.loop = loop;
        this.frames = new ArrayList<>(frames);
        this.revert = revert;
    }

    public StatueAnimation addFrame(StatueAnimationFrame frame){
        frames.add(frame);
        frames.sort(Comparator.comparingInt(StatueAnimationFrame::getTick));
        duration = calculateDuration(frames);
        return this;
    }

    public StatueAnimation loop(){
        this.loop = !loop;
        return this;
    }

    public StatueAnimation revert(){
        this.revert = !revert;
        return this;
    }

    public boolean canBeRevert(){
        return revert;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setRevert(boolean revert) {
        this.revert = revert;
    }

    private static int calculateDuration(List<StatueAnimationFrame> frames){
        if (frames.isEmpty()) return 0;
        List<StatueAnimationFrame> sortedFrames = new ArrayList<>(frames);
        sortedFrames.sort(Comparator.comparingInt(StatueAnimationFrame::getTick));
        return sortedFrames.getLast().getTick();
    }

    public boolean isLooping() {
        return loop;
    }

    public List<StatueAnimationFrame> getFrames() {
        return frames;
    }

    public int getDuration() {
        return duration;
    }

    public StatueAnimationFrame getNextFrame(StatueAnimationFrame frame){
        if (!frames.contains(frame)) throw new IllegalArgumentException("The frame isn't a part of this animation");
        if (frames.indexOf(frame) >= frames.size() - 1) return null;
        return frames.get(frames.indexOf(frame) + 1);
    }

    public StatueAnimationFrame getPreviousFrame(StatueAnimationFrame frame){
        if (!frames.contains(frame)) throw new IllegalArgumentException("The frame isn't a part of this animation");
        if (frames.indexOf(frame) <= 0) return null;
        return frames.get(frames.indexOf(frame) - 1);
    }


    public StatueAnimationFrame getFrameForProgress(int progress){
        for (StatueAnimationFrame frame : frames) {
            if (getNextFrame(frame) == null) return frame;
            if (progress >= frame.getTick() && progress < getNextFrame(frame).getTick()){
                return frame;
            }
        }
        return null;
    }

    public StatueAnimationFrame getPreciseFrame(int tick){
        return frames.stream().filter(f->f.getTick() == tick).findAny().orElse(null);
    }

    public boolean doesFrameExist(int tick){
        return getPreciseFrame(tick) != null;
    }

    public void updateFrame(int tick,StatueAnimationFrame frame){
        int index = frames.indexOf(getPreciseFrame(tick));
        if (index == -1) return;
        frames.set(index,frame);
    }

    public void deleteFrame(StatueAnimationFrame frame){
        frames.remove(frame);
    }
}
