package fr.iglee42.createqualityoflife.statue;

import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum StatueDefaultRotations {
    DEFAULT(new int[]{0,0,0}, new int[]{0,0,0}, new int[]{-10,0,-10},new int[]{-15,0,10}, new int[]{-1,0,-1},new int[]{1,0,1},"Minecraft"),
    T_POSE(new int[]{0,0,0}, new int[]{0,0,0}, new int[]{0,0,-90},new int[]{0,0,90}, new int[]{0,0,0},new int[]{0,0,0},"Minecraft"),
    A_POSE(new int[]{0,0,0}, new int[]{0,0,0}, new int[]{0,0,-35},new int[]{0,0,35}, new int[]{0,0,0},new int[]{0,0,0},"Minecraft"),
    ATTENTION(new int[]{0, 0, 0}, new int[]{0, 0, 0}, new int[]{0, 0, 0}, new int[]{0, 0, 0}, new int[]{0, 0, 0}, new int[]{0, 0, 0}),
    WALKING(new int[]{0, 0, 0}, new int[]{0, 0, 0}, new int[]{-20, 0, -10}, new int[]{20, 0, 10}, new int[]{20, 0, 0}, new int[]{-20, 0, 0}),
    RUNNING(new int[]{0, 0, 0}, new int[]{0, 0, 0}, new int[]{40, 0, -10}, new int[]{-40, 0, 10}, new int[]{-40, 0, 0}, new int[]{40, 0, 0}),
    POINTING(new int[]{0, 0, 0}, new int[]{0, 20, 0}, new int[]{0, 0, -10}, new int[]{-90, 18, 0}, new int[]{0, 0, 0}, new int[]{0, 0, 0}),
    BLOCKING(new int[]{0, 0, 0}, new int[]{0, 0, 0}, new int[]{-50, 50, 0}, new int[]{-20, -20, 0}, new int[]{20, 0, 0}, new int[]{-20, 0, 0}),
    LUNGENING(new int[]{0, 0, 0}, new int[]{0, 0, 0}, new int[]{10, 0, -10}, new int[]{-60, -10, 0}, new int[]{30, 0, 0}, new int[]{-15, 0, 0}),
    WINNING(new int[]{0, 0, 0}, new int[]{-15, 0, 0}, new int[]{10, 0, -10}, new int[]{-120, -10, 0}, new int[]{15, 0, 0}, new int[]{0, 0, 0}),
    SITTING(new int[]{0, 0, 0}, new int[]{0, 0, 0}, new int[]{-80, -20, 0}, new int[]{-80, 20, 0}, new int[]{-90, -10, 0}, new int[]{-90, 10, 0}),
    ARABESQUE(new int[]{0, 0, 0}, new int[]{-15, 0, 0}, new int[]{70, 0, -10}, new int[]{-140, -10, 0}, new int[]{75, 0, 0}, new int[]{0, 0, 0}),
    CUPID(new int[]{0, 0, 0}, new int[]{0, 0, 0}, new int[]{-75, 0, 10}, new int[]{-90, -10, 0}, new int[]{75, 0, 0}, new int[]{0, 0, 0}),
    CONFIDENT(new int[]{0, 0, 0}, new int[]{-10, 20, 0}, new int[]{5, 0, 0}, new int[]{5, 0, 0}, new int[]{0, -10, -4}, new int[]{16, 2, 10}),
    SALUTE(new int[]{0, 0, 0}, new int[]{0, 0, 0}, new int[]{29, 0, 25}, new int[]{-124, -51, -35}, new int[]{0, 4, 2}, new int[]{0, -4, -2}),
    DEATH(new int[]{0, 0, 0}, new int[]{-85, 0, 0}, new int[]{-90, -10, 0}, new int[]{-90, 10, 0}, new int[]{0, 0, 0}, new int[]{0, 0, 0}),
    FACEPALM(new int[]{0, 0, 0}, new int[]{45, -4, 1}, new int[]{-72, 24, 47}, new int[]{18, -14, 0}, new int[]{-4, -6, -2}, new int[]{25, -2, 0}),
    LAZING(new int[]{0, 0, 0}, new int[]{14, -12, 6}, new int[]{-4, -20, -10}, new int[]{-40, 20, 0}, new int[]{-88, 46, 0}, new int[]{-88, 71, 0}),
    CONFUSED(new int[]{0, 0, 0}, new int[]{0, 30, 0}, new int[]{145, 22, -49}, new int[]{-22, 31, 10}, new int[]{-6, 0, 0}, new int[]{6, -20, 0}),
    FORMAL(new int[]{0, 0, 0}, new int[]{4, 0, 0}, new int[]{30, -20, 21}, new int[]{30, 22, -20}, new int[]{0, 0, -5}, new int[]{0, 0, 5}),
    SAD(new int[]{0, 0, 0}, new int[]{63, 0, 0}, new int[]{-5, 0, -5}, new int[]{-5, 0, 5}, new int[]{-5, 16, -5}, new int[]{-5, -10, 5}),
    JOYOUS(new int[]{0, 0, 0}, new int[]{-11, 0, 0}, new int[]{0, 0, -100}, new int[]{0, 0, 100}, new int[]{-8, 0, -60}, new int[]{-8, 0, 60}),
    STARGAZING(new int[]{0, 0, 0}, new int[]{-22, 25, 0}, new int[]{4, 18, 0}, new int[]{-153, 34, -3}, new int[]{6, 24, 0}, new int[]{-4, 17, 2}),
    BLOCK(new int[]{0, 0, 0}, new int[]{0, 0, 0}, new int[]{0, 0, 0}, new int[]{-15, -45, 0}, new int[]{0, 0, 0}, new int[]{0, 0, 0}),
    ITEM(new int[]{0, 0, 0}, new int[]{0, 0, 0}, new int[]{0, 0, 0}, new int[]{-90, 0, 0}, new int[]{0, 0, 0}, new int[]{0, 0, 0}),


    ;


    private final int[] globalPose;
    private final int[] headPose;
    private final int[] leftArmPose;
    private final int[] rightArmPose;
    private final int[] leftLegPose;
    private final int[] rightLegPose;
    private final String source;

    StatueDefaultRotations(int[] globalPose,int[] headPose, int[] leftArmPose, int[] rightArmPose, int[] leftLegPose, int[] rightLegPose){
        this(globalPose,headPose,leftArmPose,rightArmPose,leftLegPose,rightLegPose,"Vanilla Tweaks");
    }

    StatueDefaultRotations(int[] globalPose,int[] headPose, int[] leftArmPose, int[] rightArmPose, int[] leftLegPose, int[] rightLegPose,String source) {
        this.globalPose = globalPose;
        this.headPose = headPose;
        this.leftArmPose = leftArmPose;
        this.rightArmPose = rightArmPose;
        this.leftLegPose = leftLegPose;
        this.rightLegPose = rightLegPose;
        this.source = source;
    }

    public static Rotations intsToRotation(int[] ints){
        return new Rotations(ints[0],ints[1],ints[2]);
    }

    public MutableComponent getSource() {
        return Component.literal(source);
    }

    public void applyToStatue(Statue statue){
        statue.setEntityRotations(globalPose[0],globalPose[1],globalPose[2]);
        statue.setHeadPose(intsToRotation(headPose));
        statue.setLeftArmPose(intsToRotation(leftArmPose));
        statue.setRightArmPose(intsToRotation(rightArmPose));
        statue.setLeftLegPose(intsToRotation(leftLegPose));
        statue.setRightLegPose(intsToRotation(rightLegPose));
    }

    public MutableComponent getName(){
        return CreateQOLLang.translateDirect("statue.rotations."+name().toLowerCase());
    }
}
