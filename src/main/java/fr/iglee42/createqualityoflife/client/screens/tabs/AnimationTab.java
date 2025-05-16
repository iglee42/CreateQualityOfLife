package fr.iglee42.createqualityoflife.client.screens.tabs;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.client.screens.ConfigureStatueScreen;
import fr.iglee42.createqualityoflife.client.screens.widgets.ScrollableEditBox;
import fr.iglee42.createqualityoflife.packets.PublishAnimationPacket;
import fr.iglee42.createqualityoflife.registries.ModGuiTextures;
import fr.iglee42.createqualityoflife.registries.ModIcons;
import fr.iglee42.createqualityoflife.registries.ModPackets;
import fr.iglee42.createqualityoflife.statue.animation.StatueAnimation;
import fr.iglee42.createqualityoflife.statue.animation.StatueAnimationFrame;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class AnimationTab extends StatueTab {

    public static final int LOOP_X = 0;
    public static final int REVERT_X = 22;
    public static final int COPY_ALL_X = 44;

    private static final List<Component> LOOP_TOOLTIPS = List.of(CreateLang.translateDirect("schedule.loop"),CreateLang.translateDirect("gui.schematicannon.optionDisabled").withStyle(ChatFormatting.RED));
    private static final List<Component> ON_LOOP_TOOLTIPS = List.of(CreateLang.translateDirect("schedule.loop"),CreateLang.translateDirect("gui.schematicannon.optionEnabled").withStyle(ChatFormatting.GREEN));
    private static final List<Component> REVERSE_TOOLTIPS = List.of(CreateQOLLang.translateDirect("statue.animation.revert"),CreateLang.translateDirect("gui.schematicannon.optionDisabled").withStyle(ChatFormatting.RED),CreateQOLLang.translateDirect("statue.animation.revert_1"),CreateQOLLang.translateDirect("statue.animation.revert_2"));
    private static final List<Component> ON_REVERSE_TOOLTIPS = List.of(CreateQOLLang.translateDirect("statue.animation.revert"),CreateLang.translateDirect("gui.schematicannon.optionEnabled").withStyle(ChatFormatting.GREEN),CreateQOLLang.translateDirect("statue.animation.revert_1"),CreateQOLLang.translateDirect("statue.animation.revert_2"));
    private static final List<Component> COPY_ALL_TOOLTIPS = List.of(CreateQOLLang.translateDirect("statue.animation.copy_all"),CreateQOLLang.translateDirect("statue.animation.copy_all_1"),CreateQOLLang.translateDirect("statue.animation.copy_all_2"));

    private StatueAnimation animation;
    private IconButton loopButton;
    private IconButton revertButton;
    private IconButton createButton;
    private IconButton copyAllButton;
    private EditBox frameSelector;
    private IconButton previousFrameButton;
    private IconButton nextFrameButton;
    private IconButton deleteFrameButton;
    private IconButton publishButton;
    private EditBox nameEdit;

    private ScrollableEditBox globalX, globalY, globalZ;
    private IconButton globalButton;

    private ScrollableEditBox headX, headY, headZ;
    private IconButton headButton;

    private ScrollableEditBox leftArmX, leftArmY, leftArmZ;
    private IconButton leftArmButton;

    private ScrollableEditBox rightArmX, rightArmY, rightArmZ;
    private IconButton rightArmButton;

    private ScrollableEditBox leftLegX, leftLegY, leftLegZ;
    private IconButton leftLegButton;

    private ScrollableEditBox rightLegX, rightLegY, rightLegZ;
    private IconButton rightLegButton;

    List<ScrollableEditBox> boxes;

    private int currentFrame;

    public AnimationTab(int index, ConfigureStatueScreen parent) {
        super(index, Items.NETHER_STAR, parent, "statue.animationTab");
        animation = parent.getExampleStatue().getAnimation().orElse(null);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial, int x, int y) {
        y-=14;
        //LOGIC
        createButton.visible = animation == null || getCurrentFrame() == null;
        loopButton.visible = animation != null;
        revertButton.visible = animation != null;
        boxes.forEach(b->b.visible = animation != null && getCurrentFrame() != null);
        globalButton.visible = animation != null && getCurrentFrame() != null;
        headButton.visible = animation != null && getCurrentFrame() != null;
        leftArmButton.visible = animation != null && getCurrentFrame() != null;
        rightArmButton.visible = animation != null && getCurrentFrame() != null;
        leftLegButton.visible = animation != null && getCurrentFrame() != null;
        rightLegButton.visible = animation != null && getCurrentFrame() != null;
        copyAllButton.visible = animation != null && getCurrentFrame() != null;
        deleteFrameButton.visible = animation != null && getCurrentFrame() != null;
        frameSelector.visible = animation != null;
        previousFrameButton.visible = animation != null;
        previousFrameButton.active = previousFrameButton.visible && getCurrentFrame() != null && animation.getPreviousFrame(getCurrentFrame()) != null;
        nextFrameButton.visible = animation != null;
        nextFrameButton.active = nextFrameButton.visible && getCurrentFrame() != null && animation.getNextFrame(getCurrentFrame()) != null;
        publishButton.visible = animation != null;
        nameEdit.visible = animation != null;

        if (animation != null){
            loopButton.getToolTip().clear();
            revertButton.getToolTip().clear();
            loopButton.getToolTip().addAll(loopButton.green ? ON_LOOP_TOOLTIPS : LOOP_TOOLTIPS);
            revertButton.getToolTip().addAll(revertButton.green ? ON_REVERSE_TOOLTIPS : REVERSE_TOOLTIPS);
            animation.setLoop(loopButton.green);
            animation.setRevert(revertButton.green);
        }

        //RENDER
        if (animation != null && getCurrentFrame() != null) {
            ModGuiTextures.ROTATIONS.render(graphics, x + 20 + 2 * BASE_OFFSET, y + 2 * BASE_OFFSET + 20);
            ModGuiTextures.ROTATIONS.render(graphics, x + 20 + 2 * BASE_OFFSET, y + 3 * BASE_OFFSET + 40);
            ModGuiTextures.ROTATIONS.render(graphics, x + 20 + 2 * BASE_OFFSET, y + 4 * BASE_OFFSET + 60);
            ModGuiTextures.ROTATIONS.render(graphics, x + 20 + 2 * BASE_OFFSET, y + 5 * BASE_OFFSET + 80);
            ModGuiTextures.ROTATIONS.render(graphics, x + 20 + 2 * BASE_OFFSET, y + 6 * BASE_OFFSET + 100);
            ModGuiTextures.ROTATIONS.render(graphics, x + 20 + 2 * BASE_OFFSET, y + 7 * BASE_OFFSET + 120);
        }
        if (animation != null){
            ModGuiTextures.NAME_EDIT_BOX.render(graphics,x + BASE_OFFSET + LOOP_X + 19, y + 173 - 19);
            ModGuiTextures.SIMPLE_EDIT_BOX.render(graphics,getParent().getGuiLeft() + getParent().imageWidth /2 - 25+ Minecraft.getInstance().font.width(CreateQOLLang.translateDirect("statue.animation.frame")),getParent().getGuiTop() + getParent().imageHeight - 24);
            graphics.drawString(Minecraft.getInstance().font, CreateQOLLang.translateDirect("statue.animation.frame"),getParent().getGuiLeft() + getParent().imageWidth /2 - 25, getParent().getGuiTop() + getParent().imageHeight - 24+ TEXT_Y_OFFSET - 1, 0xffffff);
        }
        if (animation != null && getCurrentFrame() == null){
            graphics.drawWordWrap(Minecraft.getInstance().font, CreateQOLLang.translateDirect("statue.animation.frame.create"), x + 3*BASE_OFFSET,  y +173/ 2 - 64, 131-6*BASE_OFFSET, 0xffffff);
        } else if (animation == null){
            graphics.drawWordWrap(Minecraft.getInstance().font, CreateQOLLang.translateDirect("statue.animation.create"), x + 3*BASE_OFFSET,  y + 173 / 2 - 64, 131-6*BASE_OFFSET, 0xffffff);
        }
    }

    @Override
    public void forEachWidgets(Consumer<AbstractWidget> function) {
        function.accept(createButton);
        function.accept(loopButton);
        function.accept(revertButton);
        function.accept(copyAllButton);
        function.accept(frameSelector);
        function.accept(previousFrameButton);
        function.accept(nextFrameButton);
        function.accept(deleteFrameButton);
        function.accept(publishButton);
        function.accept(nameEdit);

        function.accept(globalX);
        function.accept(globalY);
        function.accept(globalZ);
        function.accept(globalButton);

        function.accept(headX);
        function.accept(headY);
        function.accept(headZ);
        function.accept(headButton);

        function.accept(leftArmX);
        function.accept(leftArmY);
        function.accept(leftArmZ);
        function.accept(leftArmButton);

        function.accept(rightArmX);
        function.accept(rightArmY);
        function.accept(rightArmZ);
        function.accept(rightArmButton);

        function.accept(leftLegX);
        function.accept(leftLegY);
        function.accept(leftLegZ);
        function.accept(leftLegButton);

        function.accept(rightLegX);
        function.accept(rightLegY);
        function.accept(rightLegZ);
        function.accept(rightLegButton);

    }

    @Override
    public void initWidgets(int x, int y) {

        y -= 14;

        //MAIN FUNCTIONS
        createButton = new IconButton(x + (141 - 18) / 2, y + (173 - 18) / 2, AllIcons.I_ADD);
        createButton.withCallback(this::createButtonCallback);

        loopButton = new IconButton(x + BASE_OFFSET + LOOP_X, y + BASE_OFFSET, AllIcons.I_REFRESH);
        loopButton.withCallback(() -> {
            loopButton.green = !loopButton.green;
        });

        revertButton = new IconButton(x + BASE_OFFSET + REVERT_X, y + BASE_OFFSET, AllIcons.I_CONFIG_BACK);
        revertButton.withCallback(() -> {
            revertButton.green = !revertButton.green;
        });

        copyAllButton = new IconButton(x + BASE_OFFSET + COPY_ALL_X, y + BASE_OFFSET, AllIcons.I_TARGET);
        copyAllButton.withCallback(() -> {
            updateRotation(f->f
                    .withGlobalRotation(getExampleStatue().getEntityXRotation(),getExampleStatue().getYRot(),getExampleStatue().getEntityZRotation())
                    .withHeadRotation(getExampleStatue().getHeadPose().getX(),getExampleStatue().getHeadPose().getY(),getExampleStatue().getHeadPose().getZ())
                    .withLeftArmRotation(getExampleStatue().getLeftArmPose().getX(), getExampleStatue().getLeftArmPose().getY(), getExampleStatue().getLeftArmPose().getZ())
                    .withRightArmRotation(getExampleStatue().getRightArmPose().getX(), getExampleStatue().getRightArmPose().getY(), getExampleStatue().getRightArmPose().getZ())
                    .withLeftLegRotation(getExampleStatue().getLeftLegPose().getX(), getExampleStatue().getLeftLegPose().getY(), getExampleStatue().getLeftLegPose().getZ())
                    .withRightLegRotation(getExampleStatue().getRightLegPose().getX(), getExampleStatue().getRightLegPose().getY(), getExampleStatue().getRightLegPose().getZ())
            );
            globalX.setValue("" + (int) getExampleStatue().getEntityXRotation());
            globalY.setValue("" + (int) getExampleStatue().getYRot());
            globalZ.setValue("" + (int) getExampleStatue().getEntityZRotation());

            headX.setValue("" + (int) getExampleStatue().getHeadPose().getX());
            headY.setValue("" + (int) getExampleStatue().getHeadPose().getY());
            headZ.setValue("" + (int) getExampleStatue().getHeadPose().getZ());

            leftArmX.setValue("" + (int) getExampleStatue().getLeftArmPose().getX());
            leftArmY.setValue("" + (int) getExampleStatue().getLeftArmPose().getY());
            leftArmZ.setValue("" + (int) getExampleStatue().getLeftArmPose().getZ());

            rightArmX.setValue("" + (int) getExampleStatue().getRightArmPose().getX());
            rightArmY.setValue("" + (int) getExampleStatue().getRightArmPose().getY());
            rightArmZ.setValue("" + (int) getExampleStatue().getRightArmPose().getZ());

            leftLegX.setValue("" + (int) getExampleStatue().getLeftLegPose().getX());
            leftLegY.setValue("" + (int) getExampleStatue().getLeftLegPose().getY());
            leftLegZ.setValue("" + (int) getExampleStatue().getLeftLegPose().getZ());

            rightLegX.setValue("" + (int) getExampleStatue().getRightLegPose().getX());
            rightLegY.setValue("" + (int) getExampleStatue().getRightLegPose().getY());
            rightLegZ.setValue("" + (int) getExampleStatue().getRightLegPose().getZ());
        });

        copyAllButton.getToolTip().addAll(COPY_ALL_TOOLTIPS);

        deleteFrameButton = new IconButton(x + BASE_OFFSET + COPY_ALL_X + 22, y + BASE_OFFSET, AllIcons.I_CONFIG_DISCARD);
        deleteFrameButton.withCallback(() -> {
            animation.deleteFrame(getCurrentFrame());
        });
        deleteFrameButton.setToolTip(CreateQOLLang.translateDirect("statue.animation.delete_frame"));

        frameSelector = new EditBox(Minecraft.getInstance().font, getParent().getGuiLeft() + getParent().imageWidth /2 - 23 + Minecraft.getInstance().font.width(CreateQOLLang.translateDirect("statue.animation.frame")), getParent().getGuiTop() + getParent().imageHeight - 24 + TEXT_Y_OFFSET,32,18,CommonComponents.EMPTY);
        frameSelector.setBordered(false);
        frameSelector.setValue(""+currentFrame);
        frameSelector.setMaxLength(4);
        frameSelector.setTextColor(0xFFFFFF);
        frameSelector.setFocused(false);
        frameSelector.mouseClicked(0,0, 0);
        frameSelector.setResponder(s->{if (!s.isEmpty()){
            try {
                currentFrame = Integer.parseInt(s);
                if (getCurrentFrame() != null) {
                    globalX.setValue("" + (int) getCurrentFrame().getGlobal().getXRot());
                    globalY.setValue("" + (int) getCurrentFrame().getGlobal().getYRot());
                    globalZ.setValue("" + (int) getCurrentFrame().getGlobal().getZRot());

                    headX.setValue("" + (int) getCurrentFrame().getHead().getXRot());
                    headY.setValue("" + (int) getCurrentFrame().getHead().getYRot());
                    headZ.setValue("" + (int) getCurrentFrame().getHead().getZRot());

                    leftArmX.setValue("" + (int) getCurrentFrame().getLeftArm().getXRot());
                    leftArmY.setValue("" + (int) getCurrentFrame().getLeftArm().getYRot());
                    leftArmZ.setValue("" + (int) getCurrentFrame().getLeftArm().getZRot());

                    rightArmX.setValue("" + (int) getCurrentFrame().getRightArm().getXRot());
                    rightArmY.setValue("" + (int) getCurrentFrame().getRightArm().getYRot());
                    rightArmZ.setValue("" + (int) getCurrentFrame().getRightArm().getZRot());

                    leftLegX.setValue("" + (int) getCurrentFrame().getLeftLeg().getXRot());
                    leftLegY.setValue("" + (int) getCurrentFrame().getLeftLeg().getYRot());
                    leftLegZ.setValue("" + (int) getCurrentFrame().getLeftLeg().getZRot());

                    rightLegX.setValue("" + (int) getCurrentFrame().getRightLeg().getXRot());
                    rightLegY.setValue("" + (int) getCurrentFrame().getRightLeg().getYRot());
                    rightLegZ.setValue("" + (int) getCurrentFrame().getRightLeg().getZRot());
                }
            } catch (NumberFormatException ignored){}
        }});
        frameSelector.setFilter(s -> {
            if (s.isEmpty())
                return true;
            try {
                int i = Integer.parseInt(s);
                return i >=0;
            } catch (NumberFormatException e) {
                return false;
            }
        });

        previousFrameButton = new IconButton(getParent().getGuiLeft() + getParent().imageWidth /2 - 23 - 22, getParent().getGuiTop() + getParent().imageHeight - 24, AllIcons.I_CONFIG_BACK);
        previousFrameButton.withCallback(() -> {
            if (animation != null){
                if (animation.getPreviousFrame(getCurrentFrame()) != null){
                    frameSelector.setValue(""+animation.getPreviousFrame(getCurrentFrame()).getTick());
                }
            }
        });
        previousFrameButton.setToolTip(CreateQOLLang.translateDirect("statue.animation.previous_frame"));

        nextFrameButton = new IconButton(getParent().getGuiLeft() + getParent().imageWidth /2 - 23 + Minecraft.getInstance().font.width(CreateQOLLang.translateDirect("statue.animation.frame")) + 26, getParent().getGuiTop() + getParent().imageHeight - 24, AllIcons.I_CONFIG_OPEN);
        nextFrameButton.withCallback(() -> {
            if (animation != null){
                if (animation.getNextFrame(getCurrentFrame()) != null){
                    frameSelector.setValue(""+animation.getNextFrame(getCurrentFrame()).getTick());
                }
            }
        });
        nextFrameButton.setToolTip(CreateQOLLang.translateDirect("statue.animation.next_frame"));

        publishButton = new IconButton(x + BASE_OFFSET + LOOP_X, y + 173 - 19, AllIcons.I_SEND_ONLY);
        publishButton.withCallback(() -> {
            if (!nameEdit.getValue().isEmpty()) ModPackets.getChannel().sendToServer(new PublishAnimationPacket(Minecraft.getInstance().player.getUUID(), nameEdit.getValue(),animation));
        });
        publishButton.setToolTip(CreateQOLLang.translateDirect("statue.animation.publish"));

        nameEdit = new EditBox(Minecraft.getInstance().font, x + BASE_OFFSET + LOOP_X + 21, y + 173 - 19 + TEXT_Y_OFFSET,TEXT_BOX_WIDTH - 20,18,CommonComponents.EMPTY);
        nameEdit.setBordered(false);
        nameEdit.setFocused(false);
        nameEdit.setHint(Component.literal("Animation Name"));

        if (animation != null) {
            loopButton.green = animation.isLooping();
            revertButton.green = animation.canBeRevert();
        }

        //ROTATIONS

        int offsetY = 20;

        boxes = new ArrayList<>();

        globalX = new ScrollableEditBox(Minecraft.getInstance().font,x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18, CommonComponents.EMPTY);
        globalY = new ScrollableEditBox(Minecraft.getInstance().font,x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET + 30, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18,CommonComponents.EMPTY);
        globalZ = new ScrollableEditBox(Minecraft.getInstance().font,x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET + 60, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18,CommonComponents.EMPTY);
        setupEditBoxes(i->updateGlobal(), globalX, globalY, globalZ);
        if (animation != null && getCurrentFrame() != null) updateEditBoxes((int) getCurrentFrame().getGlobal().getXRot(), (int) getCurrentFrame().getGlobal().getYRot(), (int) getCurrentFrame().getGlobal().getZRot(), globalX, globalY, globalZ);
        globalButton = new IconButton(x + BASE_OFFSET,y + 2*BASE_OFFSET + 20, ModIcons.I_STATUE);
        globalButton.withCallback(()->{
            Rotations rotation = getExampleStatue().getEntityRotations();
            updateRotation(frame -> frame.withGlobalRotation(rotation.getX(),rotation.getY(),rotation.getZ()));
            globalX.setValue(""+(int)rotation.getX());
            globalY.setValue(""+(int)rotation.getY());
            globalZ.setValue(""+(int)rotation.getZ());
        });
        globalButton.getToolTip().addAll(createTooltipFor(PlayerModelPart.CAPE));

        offsetY += 20;

        headX = new ScrollableEditBox(Minecraft.getInstance().font,x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18, CommonComponents.EMPTY);
        headY = new ScrollableEditBox(Minecraft.getInstance().font,x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET + 30, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18,CommonComponents.EMPTY);
        headZ = new ScrollableEditBox(Minecraft.getInstance().font,x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET + 60, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18,CommonComponents.EMPTY);
        setupEditBoxes(i -> updateHead(), headX, headY, headZ);
        if (animation != null && getCurrentFrame() != null) {
            updateEditBoxes((int) getCurrentFrame().getHead().getXRot(), (int) getCurrentFrame().getHead().getYRot(), (int) getCurrentFrame().getHead().getZRot(), headX, headY, headZ);
        }
        headButton = new IconButton(x + BASE_OFFSET,y + (offsetY / 20 + 1) * BASE_OFFSET + offsetY, ModIcons.I_HAT);
        headButton.withCallback(()->{
            Rotations rotation = getExampleStatue().getHeadPose();
            updateRotation(frame -> frame.withHeadRotation(rotation.getX(),rotation.getY(),rotation.getZ()));
            headX.setValue(""+(int)rotation.getX());
            headY.setValue(""+(int)rotation.getY());
            headZ.setValue(""+(int)rotation.getZ());
        });
        headButton.getToolTip().addAll(createTooltipFor(PlayerModelPart.HAT));

        offsetY += 20;

        leftArmX = new ScrollableEditBox(Minecraft.getInstance().font, x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18, CommonComponents.EMPTY);
        leftArmY = new ScrollableEditBox(Minecraft.getInstance().font, x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET + 30, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18, CommonComponents.EMPTY);
        leftArmZ = new ScrollableEditBox(Minecraft.getInstance().font, x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET + 60, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18, CommonComponents.EMPTY);
        setupEditBoxes(i -> updateLeftArm(), leftArmX, leftArmY, leftArmZ);
        if (animation != null && getCurrentFrame() != null) {
            updateEditBoxes((int) getCurrentFrame().getLeftArm().getXRot(), (int) getCurrentFrame().getLeftArm().getYRot(), (int) getCurrentFrame().getLeftArm().getZRot(), leftArmX, leftArmY, leftArmZ);
        }
        leftArmButton = new IconButton(x + BASE_OFFSET, y + (offsetY / 20 + 1) * BASE_OFFSET + offsetY, ModIcons.I_LEFT_SLEEVE);
        leftArmButton.withCallback(() -> {
            Rotations rot = getExampleStatue().getLeftArmPose();
            updateRotation(f -> f.withLeftArmRotation(rot.getX(), rot.getY(), rot.getZ()));
            leftArmX.setValue("" + (int) rot.getX());
            leftArmY.setValue("" + (int) rot.getY());
            leftArmZ.setValue("" + (int) rot.getZ());
        });
        leftArmButton.getToolTip().addAll(createTooltipFor(PlayerModelPart.LEFT_SLEEVE));

        offsetY += 20;

        rightArmX = new ScrollableEditBox(Minecraft.getInstance().font, x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18, CommonComponents.EMPTY);
        rightArmY = new ScrollableEditBox(Minecraft.getInstance().font, x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET + 30, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18, CommonComponents.EMPTY);
        rightArmZ = new ScrollableEditBox(Minecraft.getInstance().font, x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET + 60, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18, CommonComponents.EMPTY);
        setupEditBoxes(i -> updateRightArm(), rightArmX, rightArmY, rightArmZ);
        if (animation != null && getCurrentFrame() != null) {
            updateEditBoxes((int) getCurrentFrame().getRightArm().getXRot(), (int) getCurrentFrame().getRightArm().getYRot(), (int) getCurrentFrame().getRightArm().getZRot(), rightArmX, rightArmY, rightArmZ);
        }
        rightArmButton = new IconButton(x + BASE_OFFSET, y + (offsetY / 20 + 1) * BASE_OFFSET + offsetY, ModIcons.I_RIGHT_SLEEVE);
        rightArmButton.withCallback(() -> {
            Rotations rot = getExampleStatue().getRightArmPose();
            updateRotation(f -> f.withRightArmRotation(rot.getX(), rot.getY(), rot.getZ()));
            rightArmX.setValue("" + (int) rot.getX());
            rightArmY.setValue("" + (int) rot.getY());
            rightArmZ.setValue("" + (int) rot.getZ());
        });
        rightArmButton.getToolTip().addAll(createTooltipFor(PlayerModelPart.RIGHT_SLEEVE));

        offsetY += 20;

        leftLegX = new ScrollableEditBox(Minecraft.getInstance().font, x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18, CommonComponents.EMPTY);
        leftLegY = new ScrollableEditBox(Minecraft.getInstance().font, x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET + 30, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18, CommonComponents.EMPTY);
        leftLegZ = new ScrollableEditBox(Minecraft.getInstance().font, x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET + 60, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18, CommonComponents.EMPTY);
        setupEditBoxes(i -> updateLeftLeg(), leftLegX, leftLegY, leftLegZ);
        if (animation != null && getCurrentFrame() != null) {
            updateEditBoxes((int) getCurrentFrame().getLeftLeg().getXRot(), (int) getCurrentFrame().getLeftLeg().getYRot(), (int) getCurrentFrame().getLeftLeg().getZRot(), leftLegX, leftLegY, leftLegZ);
        }
        leftLegButton = new IconButton(x + BASE_OFFSET, y + (offsetY / 20 + 1) * BASE_OFFSET + offsetY, ModIcons.I_LEFT_PANTS);
        leftLegButton.withCallback(() -> {
            Rotations rot = getExampleStatue().getLeftLegPose();
            updateRotation(f -> f.withLeftLegRotation(rot.getX(), rot.getY(), rot.getZ()));
            leftLegX.setValue("" + (int) rot.getX());
            leftLegY.setValue("" + (int) rot.getY());
            leftLegZ.setValue("" + (int) rot.getZ());
        });
        leftLegButton.getToolTip().addAll(createTooltipFor(PlayerModelPart.LEFT_PANTS_LEG));

        offsetY += 20;

        rightLegX = new ScrollableEditBox(Minecraft.getInstance().font, x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18, CommonComponents.EMPTY);
        rightLegY = new ScrollableEditBox(Minecraft.getInstance().font, x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET + 30, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18, CommonComponents.EMPTY);
        rightLegZ = new ScrollableEditBox(Minecraft.getInstance().font, x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET + 60, y + (offsetY / 20 + 1) * BASE_OFFSET +TEXT_Y_OFFSET + offsetY, 32, 18, CommonComponents.EMPTY);
        setupEditBoxes(i -> updateRightLeg(), rightLegX, rightLegY, rightLegZ);
        if (animation != null && getCurrentFrame() != null) {
            updateEditBoxes((int) getCurrentFrame().getRightLeg().getXRot(), (int) getCurrentFrame().getRightLeg().getYRot(), (int) getCurrentFrame().getRightLeg().getZRot(), rightLegX, rightLegY, rightLegZ);
        }
        rightLegButton = new IconButton(x + BASE_OFFSET, y + (offsetY / 20 + 1) * BASE_OFFSET + offsetY, ModIcons.I_RIGHT_PANTS);
        rightLegButton.withCallback(() -> {
            Rotations rot = getExampleStatue().getRightLegPose();
            updateRotation(f -> f.withRightLegRotation(rot.getX(), rot.getY(), rot.getZ()));
            rightLegX.setValue("" + (int) rot.getX());
            rightLegY.setValue("" + (int) rot.getY());
            rightLegZ.setValue("" + (int) rot.getZ());
        });
        rightLegButton.getToolTip().addAll(createTooltipFor(PlayerModelPart.RIGHT_PANTS_LEG));


    }

    private List<Component> createTooltipFor(PlayerModelPart part){
        List<Component> components = new ArrayList<>();
        components.add(CreateQOLLang.translateDirect("statue.animation.copy_from",CreateQOLLang.translateDirect("statue.rotation."+part.name().toLowerCase())));
        return components;
    }


    private void setupEditBoxes(Consumer<Integer> onChanged,ScrollableEditBox... inputs){
        for (ScrollableEditBox widget : inputs) {
            widget.setMaxLength(4);
            widget.setBordered(false);
            widget.setTextColor(0xFFFFFF);
            widget.setFocused(false);
            widget.mouseClicked(0,0, 0);
            widget.setResponder(s->{if (!s.isEmpty()){
                try {
                    int i = Integer.parseInt(s);
                    onChanged.accept(i);
                } catch (NumberFormatException ignored){}
            }});
            widget.setFilter(s -> {
                if (s.isEmpty() || s.equals("-"))
                    return true;
                try {
                    int i = Integer.parseInt(s);
                    return i >= -180 && i < 180;
                } catch (NumberFormatException e) {
                    return false;
                }
            });
            boxes.add(widget);
        }
    }

    private void updateEditBoxes(int value1,int value2,int value3,EditBox... inputs){
        if (inputs.length != 3) return;
        inputs[0].setValue(""+value1);
        inputs[1].setValue(""+value2);
        inputs[2].setValue(""+value3);
    }

    @Override
    public void onQuit() {
        super.onQuit();
        getExampleStatue().setAnimation(animation);
    }

    private StatueAnimationFrame getCurrentFrame(){
        if (animation == null) return null;
        return animation.getPreciseFrame(currentFrame);
    }

    //UPDATE METHODS

    private void updateRotation(Function<StatueAnimationFrame,StatueAnimationFrame> function){
        if (animation == null) return;
        if (!animation.doesFrameExist(currentFrame)) return;
        StatueAnimationFrame frame = getCurrentFrame();
        frame = function.apply(frame);
        animation.updateFrame(currentFrame,frame);
    }

    private void updateGlobal(){
        updateRotation(f->f.withGlobalRotation(Integer.parseInt(globalX.getValue()),Integer.parseInt(globalY.getValue()),Integer.parseInt(globalZ.getValue())));
    }

    private void updateHead(){
        updateRotation(f->f.withHeadRotation(Integer.parseInt(headX.getValue()),Integer.parseInt(headY.getValue()),Integer.parseInt(headZ.getValue())));
    }

    private void updateLeftArm() {
        updateRotation(f -> f.withLeftArmRotation(Integer.parseInt(leftArmX.getValue()), Integer.parseInt(leftArmY.getValue()), Integer.parseInt(leftArmZ.getValue())));
    }

    private void updateRightArm() {
        updateRotation(f -> f.withRightArmRotation(Integer.parseInt(rightArmX.getValue()), Integer.parseInt(rightArmY.getValue()), Integer.parseInt(rightArmZ.getValue())));
    }

    private void updateLeftLeg() {
        updateRotation(f -> f.withLeftLegRotation(Integer.parseInt(leftLegX.getValue()), Integer.parseInt(leftLegY.getValue()), Integer.parseInt(leftLegZ.getValue())));
    }

    private void updateRightLeg() {
        updateRotation(f -> f.withRightLegRotation(Integer.parseInt(rightLegX.getValue()), Integer.parseInt(rightLegY.getValue()), Integer.parseInt(rightLegZ.getValue())));
    }


    private void createButtonCallback() {
        if (animation == null) {
            animation = new StatueAnimation();
        } else if (getCurrentFrame() == null) {
            animation.addFrame(new StatueAnimationFrame(currentFrame));
        } else {
            throw new RuntimeException(new IllegalAccessException("Create Button shouldn't be accessible when animation and frame aren't null, animation: " + animation +", frame: " + getCurrentFrame()));
        }
    }
}
