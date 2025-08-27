package fr.iglee42.createqualityoflife.client.screens.tabs;

import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.client.screens.ConfigureStatueScreen;
import fr.iglee42.createqualityoflife.client.screens.widgets.NotUpdatableEditBox;
import fr.iglee42.createqualityoflife.client.screens.widgets.ScrollableEditBox;
import fr.iglee42.createqualityoflife.packets.PublishAnimationPacket;
import fr.iglee42.createqualityoflife.registries.QOLGuiTextures;
import fr.iglee42.createqualityoflife.registries.QOLIcons;
import fr.iglee42.createqualityoflife.registries.QOLPackets;
import fr.iglee42.createqualityoflife.statue.animation.StatueAnimation;
import fr.iglee42.createqualityoflife.statue.animation.StatueAnimationFrame;
import fr.iglee42.createqualityoflife.statue.animation.StatuePartTable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
    private NotUpdatableEditBox frameSelector;
    private IconButton previousFrameButton;
    private IconButton nextFrameButton;
    private IconButton deleteFrameButton;
    private IconButton publishButton;
    private EditBox nameEdit;

    List<ScrollableEditBox> editWidgets;
    List<IconButton> iconButtons;

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
        editWidgets.forEach(b->b.visible = animation != null && getCurrentFrame() != null);
        iconButtons.forEach(b->b.visible = animation != null && getCurrentFrame() != null);
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
            QOLGuiTextures.ROTATIONS.render(graphics, x + 20 + 2 * BASE_OFFSET, y + 2 * BASE_OFFSET + 20);
            QOLGuiTextures.ROTATIONS.render(graphics, x + 20 + 2 * BASE_OFFSET, y + 3 * BASE_OFFSET + 40);
            QOLGuiTextures.ROTATIONS.render(graphics, x + 20 + 2 * BASE_OFFSET, y + 4 * BASE_OFFSET + 60);
            QOLGuiTextures.ROTATIONS.render(graphics, x + 20 + 2 * BASE_OFFSET, y + 5 * BASE_OFFSET + 80);
            QOLGuiTextures.ROTATIONS.render(graphics, x + 20 + 2 * BASE_OFFSET, y + 6 * BASE_OFFSET + 100);
            QOLGuiTextures.ROTATIONS.render(graphics, x + 20 + 2 * BASE_OFFSET, y + 7 * BASE_OFFSET + 120);
        }
        if (animation != null){
            QOLGuiTextures.NAME_EDIT_BOX.render(graphics,x + BASE_OFFSET + LOOP_X + 19, y + 173 - 19);
            QOLGuiTextures.SIMPLE_EDIT_BOX.render(graphics,getParent().getGuiLeft() + getParent().imageWidth /2 - 25+ Minecraft.getInstance().font.width(CreateQOLLang.translateDirect("statue.animation.frame")),getParent().getGuiTop() + getParent().imageHeight - 24);
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

        editWidgets.forEach(function);
        iconButtons.forEach(function);
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
            updateBoxesFromFrame(currentFrame);
        });

        copyAllButton.getToolTip().addAll(COPY_ALL_TOOLTIPS);

        deleteFrameButton = new IconButton(x + BASE_OFFSET + COPY_ALL_X + 22, y + BASE_OFFSET, AllIcons.I_CONFIG_DISCARD);
        deleteFrameButton.withCallback(() -> {
            animation.deleteFrame(getCurrentFrame());
        });
        deleteFrameButton.setToolTip(CreateQOLLang.translateDirect("statue.animation.delete_frame"));

        frameSelector = new NotUpdatableEditBox(Minecraft.getInstance().font, getParent().getGuiLeft() + getParent().imageWidth /2 - 23 + Minecraft.getInstance().font.width(CreateQOLLang.translateDirect("statue.animation.frame")), getParent().getGuiTop() + getParent().imageHeight - 24 + TEXT_Y_OFFSET,32,18,CommonComponents.EMPTY);
        frameSelector.setBordered(false);
        frameSelector.setValue(""+currentFrame);
        frameSelector.setMaxLength(4);
        frameSelector.setTextColor(0xFFFFFF);
        frameSelector.setFocused(false);
        frameSelector.mouseClicked(0,0, 0);
        frameSelector.setResponder(s->{if (!s.isEmpty()){
            try {
                changeFrame(Integer.parseInt(s),true);
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
                    changeFrame(animation.getPreviousFrame(getCurrentFrame()).getTick(),false);
                }
            }
        });
        previousFrameButton.setToolTip(CreateQOLLang.translateDirect("statue.animation.previous_frame"));

        nextFrameButton = new IconButton(getParent().getGuiLeft() + getParent().imageWidth /2 - 23 + Minecraft.getInstance().font.width(CreateQOLLang.translateDirect("statue.animation.frame")) + 26, getParent().getGuiTop() + getParent().imageHeight - 24, AllIcons.I_CONFIG_OPEN);
        nextFrameButton.withCallback(() -> {
            if (animation != null){
                if (animation.getNextFrame(getCurrentFrame()) != null){
                    changeFrame(animation.getNextFrame(getCurrentFrame()).getTick(),false);
                }
            }
        });
        nextFrameButton.setToolTip(CreateQOLLang.translateDirect("statue.animation.next_frame"));

        publishButton = new IconButton(x + BASE_OFFSET + LOOP_X, y + 173 - 19, AllIcons.I_SEND_ONLY);
        publishButton.withCallback(() -> {
            if (!nameEdit.getValue().isEmpty()) QOLPackets.getChannel().sendToServer(new PublishAnimationPacket(Minecraft.getInstance().player.getUUID(), nameEdit.getValue(),animation));
        });
        publishButton.setToolTip(CreateQOLLang.translateDirect("statue.animation.publish"));

        nameEdit = new EditBox(Minecraft.getInstance().font, x + BASE_OFFSET + LOOP_X + 21, y + 173 - 19 + TEXT_Y_OFFSET,TEXT_BOX_WIDTH - 20,18,CommonComponents.EMPTY);
        nameEdit.setBordered(false);
        nameEdit.setFocused(false);
        nameEdit.setHint(Component.literal("Animation Name"));
        nameEdit.setTooltip(Tooltip.create(CreateQOLLang.translateDirect("statue.name_utility")));

        if (animation != null) {
            loopButton.green = animation.isLooping();
            revertButton.green = animation.canBeRevert();
        }

        //ROTATIONS

        int offsetY = 22;

        editWidgets = new ArrayList<>();
        iconButtons = new ArrayList<>();


        setupRotationControls(
                x, y + offsetY, QOLIcons.I_STATUE,
                () -> getCurrentFrame().getGlobal(),
                () -> getExampleStatue().getEntityRotations(),
                this::updateGlobal,
                rot -> frame -> frame.withGlobalRotation(rot.getX(), rot.getY(), rot.getZ()),
                PlayerModelPart.CAPE,
                editWidgets,
                iconButtons
        );
        offsetY += 22;

        setupRotationControls(
                x, y + offsetY, QOLIcons.I_HAT,
                () -> getCurrentFrame().getHead(),
                () -> getExampleStatue().getHeadPose(),
                this::updateHead,
                rot -> frame -> frame.withHeadRotation(rot.getX(), rot.getY(), rot.getZ()),
                PlayerModelPart.HAT,
                editWidgets,
                iconButtons
        );
        offsetY += 22;

        setupRotationControls(
                x, y + offsetY, QOLIcons.I_LEFT_SLEEVE,
                () -> getCurrentFrame().getLeftArm(),
                () -> getExampleStatue().getLeftArmPose(),
                this::updateLeftArm,
                rot -> frame -> frame.withLeftArmRotation(rot.getX(), rot.getY(), rot.getZ()),
                PlayerModelPart.LEFT_SLEEVE,
                editWidgets,
                iconButtons
        );
        offsetY += 22;

        setupRotationControls(
                x, y + offsetY, QOLIcons.I_RIGHT_SLEEVE,
                () -> getCurrentFrame().getRightArm(),
                () -> getExampleStatue().getRightArmPose(),
                this::updateRightArm,
                rot -> frame -> frame.withRightArmRotation(rot.getX(), rot.getY(), rot.getZ()),
                PlayerModelPart.RIGHT_SLEEVE,
                editWidgets,
                iconButtons
        );
        offsetY += 22;

        setupRotationControls(
                x, y + offsetY, QOLIcons.I_LEFT_PANTS,
                () -> getCurrentFrame().getLeftLeg(),
                () -> getExampleStatue().getLeftLegPose(),
                this::updateLeftLeg,
                rot -> frame -> frame.withLeftLegRotation(rot.getX(), rot.getY(), rot.getZ()),
                PlayerModelPart.LEFT_PANTS_LEG,
                editWidgets,
                iconButtons
        );
        offsetY += 22;

        setupRotationControls(
                x, y + offsetY, QOLIcons.I_RIGHT_PANTS,
                () -> getCurrentFrame().getRightLeg(),
                () -> getExampleStatue().getRightLegPose(),
                this::updateRightLeg,
                rot -> frame -> frame.withRightLegRotation(rot.getX(), rot.getY(), rot.getZ()),
                PlayerModelPart.RIGHT_PANTS_LEG,
                editWidgets,
                iconButtons
        );


    }
    private void setupRotationControls(
            int x, int yOffset, QOLIcons icon,
            Supplier<StatuePartTable> currentFrameSupplier,
            Supplier<Rotations> defaultPoseSupplier,
            BiConsumer<Character, Integer> updater,
            Function<Rotations, Function<StatueAnimationFrame, StatueAnimationFrame>> rotationSetter,
            PlayerModelPart tooltipPart,
            List<ScrollableEditBox> collector,
            List<IconButton> buttons) {

        ScrollableEditBox boxX = new ScrollableEditBox(Minecraft.getInstance().font, x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET, yOffset + TEXT_Y_OFFSET + BASE_OFFSET, 32, 18, CommonComponents.EMPTY);
        ScrollableEditBox boxY = new ScrollableEditBox(Minecraft.getInstance().font, x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET + 30, yOffset + TEXT_Y_OFFSET + BASE_OFFSET, 32, 18, CommonComponents.EMPTY);
        ScrollableEditBox boxZ = new ScrollableEditBox(Minecraft.getInstance().font, x + 20 + BASE_OFFSET + TEXT_BOX_X_OFFSET + 60, yOffset + TEXT_Y_OFFSET + BASE_OFFSET, 32, 18, CommonComponents.EMPTY);

        boxX.setPrefix(Component.literal("X: "));
        boxY.setPrefix(Component.literal("Y: "));
        boxZ.setPrefix(Component.literal("Z: "));
        setupEditBoxes((value, widget) -> {
            char axis = widget == boxX ? 'x' : widget == boxY ? 'y' : 'z';
            updater.accept(axis, value);
        }, boxX, boxY, boxZ);

        if (animation != null && getCurrentFrame() != null) {
            StatuePartTable part = currentFrameSupplier.get();
            updateEditBoxes((int) part.getXRot(), (int) part.getYRot(), (int) part.getZRot(), boxX, boxY, boxZ);
        }

        IconButton button = new IconButton(x + BASE_OFFSET, yOffset + 2, icon);
        button.withCallback(() -> {
            Rotations rot = defaultPoseSupplier.get();
            updateRotation(rotationSetter.apply(rot));
            boxX.setValue("" + (int) rot.getX());
            boxY.setValue("" + (int) rot.getY());
            boxZ.setValue("" + (int) rot.getZ());
        });
        button.getToolTip().addAll(createTooltipFor(tooltipPart));

        buttons.add(button);
        collector.add(boxX);
        collector.add(boxY);
        collector.add(boxZ);
    }


    private void changeFrame(int newFrame, boolean fromSelector) {
        this.currentFrame = newFrame;
        if (!fromSelector) frameSelector.setValueNoUpdate(newFrame + "");
        updateBoxesFromFrame(newFrame);
    }

    private void updateBoxesFromFrame(int currentFrame){
        StatueAnimationFrame frame = animation.getPreciseFrame(currentFrame);
        if (frame != null) {
            List<StatuePartTable> rotations = List.of(
                    frame.getGlobal(),
                    frame.getHead(),
                    frame.getLeftArm(),
                    frame.getRightArm(),
                    frame.getLeftLeg(),
                    frame.getRightLeg()
            );

            int widgetIndex = 0;
            for (StatuePartTable rot : rotations) {
                editWidgets.get(widgetIndex++).setValue("" + (int) rot.getXRot());
                editWidgets.get(widgetIndex++).setValue("" + (int) rot.getYRot());
                editWidgets.get(widgetIndex++).setValue("" + (int) rot.getZRot());
            }
        }
    }


    private List<Component> createTooltipFor(PlayerModelPart part){
        List<Component> components = new ArrayList<>();
        components.add(CreateQOLLang.translateDirect("statue.animation.copy_from",CreateQOLLang.translateDirect("statue.rotation."+part.name().toLowerCase())));
        return components;
    }


    private void setupEditBoxes(BiConsumer<Integer,ScrollableEditBox> onChanged, ScrollableEditBox... inputs){
        for (ScrollableEditBox widget : inputs) {
            widget.setMaxLength(4);
            widget.setBordered(false);
            widget.setTextColor(0xFFFFFF);
            widget.setFocused(false);
            widget.mouseClicked(0,0, 0);
            widget.setResponder(s->{if (!s.isEmpty()){
                try {
                    int i = Integer.parseInt(s);
                    onChanged.accept(i,widget);
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

    private void updateGlobal(char axis, int value) {
        updateRotation(f -> {
            StatuePartTable current = f.getGlobal();
            float x = current.getXRot();
            float y = current.getYRot();
            float z = current.getZRot();

            switch (axis) {
                case 'x' -> x = value;
                case 'y' -> y = value;
                case 'z' -> z = value;
            }

            return f.withGlobalRotation(x, y, z);
        });
    }

    private void updateHead(char axis, int value) {
        updateRotation(f -> {
            StatuePartTable current = f.getHead();
            float x = current.getXRot();
            float y = current.getYRot();
            float z = current.getZRot();

            switch (axis) {
                case 'x' -> x = value;
                case 'y' -> y = value;
                case 'z' -> z = value;
            }

            return f.withHeadRotation(x, y, z);
        });
    }

    private void updateLeftArm(char axis, int value) {
        updateRotation(f -> {
            StatuePartTable current = f.getLeftArm();
            float x = current.getXRot();
            float y = current.getYRot();
            float z = current.getZRot();

            switch (axis) {
                case 'x' -> x = value;
                case 'y' -> y = value;
                case 'z' -> z = value;
            }

            return f.withLeftArmRotation(x, y, z);
        });
    }

    private void updateRightArm(char axis, int value) {
        updateRotation(f -> {
            StatuePartTable current = f.getRightArm();
            float x = current.getXRot();
            float y = current.getYRot();
            float z = current.getZRot();

            switch (axis) {
                case 'x' -> x = value;
                case 'y' -> y = value;
                case 'z' -> z = value;
            }

            return f.withRightArmRotation(x, y, z);
        });
    }

    private void updateLeftLeg(char axis, int value) {
        updateRotation(f -> {
            StatuePartTable current = f.getLeftLeg();
            float x = current.getXRot();
            float y = current.getYRot();
            float z = current.getZRot();

            switch (axis) {
                case 'x' -> x = value;
                case 'y' -> y = value;
                case 'z' -> z = value;
            }

            return f.withLeftLegRotation(x, y, z);
        });
    }

    private void updateRightLeg(char axis, int value) {
        updateRotation(f -> {
            StatuePartTable current = f.getRightLeg();
            float x = current.getXRot();
            float y = current.getYRot();
            float z = current.getZRot();

            switch (axis) {
                case 'x' -> x = value;
                case 'y' -> y = value;
                case 'z' -> z = value;
            }

            return f.withRightLegRotation(x, y, z);
        });
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
