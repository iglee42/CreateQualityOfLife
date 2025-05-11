package fr.iglee42.createqualityoflife.client.screens.tabs;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.client.screens.ConfigureStatueScreen;
import fr.iglee42.createqualityoflife.registries.ModGuiTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class PartsRotationTab extends StatueTab {

    private final PlayerModelPart part1;
    private final @Nullable PlayerModelPart part2;

    private Label part1Label;

    private ScrollInput part1X;
    private ScrollInput part1Y;
    private ScrollInput part1Z;

    private Label part2Label;

    private ScrollInput part2X;
    private ScrollInput part2Y;
    private ScrollInput part2Z;

    Map<ScrollInput,Integer> inputs;
    public PartsRotationTab(int index, Item item, ConfigureStatueScreen parent, PlayerModelPart part1) {
        this(index, item, parent, part1,null);
    }
    public PartsRotationTab(int index, Item item, ConfigureStatueScreen parent, PlayerModelPart part1, @Nullable PlayerModelPart part2) {
        super(index, item, parent,"statue." + part1.name().toLowerCase() + "Tab");
        this.part1 = part1;
        this.part2 = part2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial, int x, int y) {
        inputs.forEach((i,value)->{
            int inputX = i.getX();
            AllGuiTextures.TRAIN_PROMPT_L.render(graphics, inputX - 3,i.getY());
            AllGuiTextures.TRAIN_PROMPT_R.render(graphics, inputX +i.getWidth(),i.getY());
            ModGuiTextures.SLIDER.render(graphics,inputX,i.getY());

            float progress = (float) (value + 180) / 360;
            AllGuiTextures.TRAIN_PROMPT_L.render(graphics, (int) (inputX + progress * i.getWidth() - 3),i.getY());
            AllGuiTextures.TRAIN_PROMPT_R.render(graphics, (int) (inputX + progress * i.getWidth()),i.getY());
        });
    }

    @Override
    public void forEachWidgets(Consumer<AbstractWidget> function) {
        if (part1 != null){
            function.accept(part1X);
            function.accept(part1Y);
            function.accept(part1Z);
            function.accept(part1Label);
        }
        if (part2 != null){
            function.accept(part2X);
            function.accept(part2Y);
            function.accept(part2Z);
            function.accept(part2Label);
        }
    }

    private Rotations getRotationsForPart(PlayerModelPart part){
        return switch (part){
            case CAPE -> getExampleStatue().getEntityRotations();
            case JACKET -> getExampleStatue().getBodyPose();
            case LEFT_SLEEVE -> getExampleStatue().getLeftArmPose();
            case RIGHT_SLEEVE -> getExampleStatue().getRightArmPose();
            case LEFT_PANTS_LEG -> getExampleStatue().getLeftLegPose();
            case RIGHT_PANTS_LEG -> getExampleStatue().getRightLegPose();
            case HAT -> getExampleStatue().getHeadPose();
        };
    }
    private void setRotation(PlayerModelPart part,Rotations rotations){
        switch (part){
            case CAPE -> getExampleStatue().setEntityRotations(rotations.getX(),rotations.getY(),rotations.getZ());
            case JACKET -> getExampleStatue().setBodyPose(rotations);
            case LEFT_SLEEVE -> getExampleStatue().setLeftArmPose(rotations);
            case RIGHT_SLEEVE -> getExampleStatue().setRightArmPose(rotations);
            case LEFT_PANTS_LEG -> getExampleStatue().setLeftLegPose(rotations);
            case RIGHT_PANTS_LEG -> getExampleStatue().setRightLegPose(rotations);
            case HAT -> getExampleStatue().setHeadPose(rotations);
        };
    }

    private void setRotation(PlayerModelPart part, char axis, int rotation){
        Rotations rotations = getRotationsForPart(part);
        switch (axis){
            case 'x'-> rotations = new Rotations(rotation,rotations.getY(),rotations.getZ());
            case 'y'-> rotations = new Rotations(rotations.getX(),rotation,rotations.getZ());
            case 'z'-> rotations = new Rotations(rotations.getX(),rotations.getY(),rotation);
            default -> throw new IllegalStateException("Unexpected value: " + axis);
        }
        setRotation(part,rotations);
    }

    @Override
    public void initWidgets(int x, int y) {
        if (part1 != null && part2 == null) y =  y + (158-y) / 2;
        inputs = new HashMap<>();
        if (part1 != null){
            part1Label = new Label(x + 2* BASE_OFFSET, y-LABEL_Y_OFFSET*2,CreateQOLLang.translateDirect("statue.rotation." + part1.name().toLowerCase()));
            part1Label.text = CreateQOLLang.translateDirect("statue.rotation." + part1.name().toLowerCase());
            part1X = new ScrollInput(x + 2* BASE_OFFSET, y,TEXT_BOX_WIDTH,18);
            part1X.calling(i->{
                setRotation(part1,'x',i);
                inputs.put(part1X,i);
                part1X.titled(Component.literal("X: " + i + "°"));
                getParent().sendUpdatePacket();
            });
            part1Y = new ScrollInput(x + 2* BASE_OFFSET, y + 22,TEXT_BOX_WIDTH,18);
            part1Y.calling(i->{
                setRotation(part1,'y',i);
                inputs.put(part1Y,i);
                part1Y.titled(Component.literal("Y: " + i + "°"));
                getParent().sendUpdatePacket();
            });
            part1Z = new ScrollInput(x + 2* BASE_OFFSET, y + 44,TEXT_BOX_WIDTH,18);
            part1Z.calling(i->{
                setRotation(part1,'z',i);
                inputs.put(part1Z,i);
                part1Z.titled(Component.literal("Z: " + i + "°"));
                getParent().sendUpdatePacket();
            });
            setupConfigForInputs(getRotationsForPart(part1),part1X,part1Y,part1Z);
        }
        if (part2 != null){
            part2Label = new Label(x + 2* BASE_OFFSET, y+88 - LABEL_Y_OFFSET*2,CreateQOLLang.translateDirect("statue.rotation." + part2.name().toLowerCase()));
            part2Label.text = CreateQOLLang.translateDirect("statue.rotation." + part2.name().toLowerCase());
            part2X = new ScrollInput(x + 2* BASE_OFFSET, y + 88,TEXT_BOX_WIDTH,18);
            part2X.calling(i->{
                setRotation(part2,'x',i);
                inputs.put(part2X,i);
                part2X.titled(Component.literal("X: " + i + "°"));
                getParent().sendUpdatePacket();
            });
            part2Y = new ScrollInput(x + 2* BASE_OFFSET, y + 110,TEXT_BOX_WIDTH,18);
            part2Y.calling(i->{
                setRotation(part2,'y',i);
                inputs.put(part2Y,i);
                part2Y.titled(Component.literal("Y: " + i + "°"));
                getParent().sendUpdatePacket();

            });
            part2Z = new ScrollInput(x + 2* BASE_OFFSET, y + 132,TEXT_BOX_WIDTH,18);
            part2Z.calling(i->{
                setRotation(part2,'z',i);
                inputs.put(part2Z,i);
                part2Z.titled(Component.literal("Z: " + i + "°"));
                getParent().sendUpdatePacket();

            });
            setupConfigForInputs(getRotationsForPart(part2),part2X,part2Y,part2Z);
        }
    }

    private void setupConfigForInputs(Rotations rotations, ScrollInput... inputs){
        for (int index = 0; index < inputs.length; index++) {
            ScrollInput input = inputs[index];
            input.withRange(-180,180);
            input.withStepFunction(ctx->ctx.control ? 90 : ctx.shift ? 45 : 1);
            input.setState((int) switch (index){
                case 0 -> rotations.getX();
                case 1 -> rotations.getY();
                case 2 -> rotations.getZ();
                default -> throw new IllegalStateException("Unexpected value: " + index);
            });
            input.titled(Component.literal((index == 0? "X" : index == 1 ?"Y" : "Z") + ": " + input.getState() + "°"));
            this.inputs.put(input,input.getState());
        }
    }
}
