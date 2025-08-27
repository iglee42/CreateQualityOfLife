package fr.iglee42.createqualityoflife.client.screens.tabs;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.Label;
import fr.iglee42.createqualityoflife.CreateQOLLang;
import fr.iglee42.createqualityoflife.client.screens.ConfigureStatueScreen;
import fr.iglee42.createqualityoflife.client.screens.widgets.ClickableScrollInput;
import fr.iglee42.createqualityoflife.client.screens.widgets.FloatScrollInput;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLGuiTextures;
import fr.iglee42.createqualityoflife.registries.QOLItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

import static net.minecraft.client.gui.screens.Screen.isPaste;

public class StatueTransformTab extends StatueTab {

    private Label positionLabel;
    private EditBox posX;
    private EditBox posY;
    private EditBox posZ;

    private Label scaleLabel;
    private FloatScrollInput scale;

    private Label rotationLabel;
    private ClickableScrollInput rotationX;
    private ClickableScrollInput rotationY;
    private ClickableScrollInput rotationZ;

    public StatueTransformTab(int index, ConfigureStatueScreen parent) {
        super(index, QOLItems.STATUE.asItem(), parent, "statue.transformTab");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial, int x, int y) {
        for (ClickableScrollInput i : new ClickableScrollInput[]{rotationX, rotationY, rotationZ}) {
            int inputX = i.getX();
            AllGuiTextures.TRAIN_PROMPT_L.render(graphics, inputX - 3,i.getY());
            AllGuiTextures.TRAIN_PROMPT_R.render(graphics, inputX +i.getWidth(),i.getY());
            QOLGuiTextures.SLIDER.render(graphics,inputX,i.getY());

            float progress = (float) (i.getState() + 180) / 360;
            AllGuiTextures.TRAIN_PROMPT_L.render(graphics, (int) (inputX + progress * i.getWidth() - 3),i.getY());
            AllGuiTextures.TRAIN_PROMPT_R.render(graphics, (int) (inputX + progress * i.getWidth()),i.getY());
        }

        int inputX = scale.getX();
        AllGuiTextures.TRAIN_PROMPT_L.render(graphics, inputX - 3,scale.getY());
        AllGuiTextures.TRAIN_PROMPT_R.render(graphics, inputX +scale.getWidth(),scale.getY());
        QOLGuiTextures.SLIDER.render(graphics,inputX,scale.getY());

        float progress = scale.getState() / 10;
        AllGuiTextures.TRAIN_PROMPT_L.render(graphics, (int) (inputX + progress * scale.getWidth() - 3),scale.getY());
        AllGuiTextures.TRAIN_PROMPT_R.render(graphics, (int) (inputX + progress * scale.getWidth()),scale.getY());

        QOLGuiTextures.COORDINATES.render(graphics,x + BASE_OFFSET, y);

    }

    @Override
    public void forEachWidgets(Consumer<AbstractWidget> function) {
        function.accept(posX);
        function.accept(posY);
        function.accept(posZ);
        function.accept(positionLabel);
        function.accept(scaleLabel);
        function.accept(scale);
        function.accept(rotationX);
        function.accept(rotationY);
        function.accept(rotationZ);
        function.accept(rotationLabel);
    }

    private void setRotation(char axis, int rotation){
        switch (axis){
            case 'x'-> getExampleStatue().setEntityXRotation(rotation);
            case 'y'-> getExampleStatue().setYRot(rotation);
            case 'z'-> getExampleStatue().setEntityZRotation(rotation);
            default -> throw new IllegalStateException("Unexpected value: " + axis);
        }
    }
    public boolean keyPressed(int code, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (isPaste(code)) {
            String coords = Minecraft.getInstance().keyboardHandler.getClipboard();
            if (coords != null && !coords.isEmpty()) {
                coords = coords.replaceAll(" ", ",");
                String[] split = coords.split(",");
                if (split.length == 3) {
                    boolean valid = true;
                    for (String s : split) {
                        try {
                            Double.parseDouble(s);
                        } catch (NumberFormatException e) {
                            valid = false;
                        }
                    }
                    if (calculateCoords(Double.parseDouble(split[0]),Double.parseDouble(split[1]),Double.parseDouble(split[2])) == null) return false;
                    if (valid) {
                        posX.setValue(split[0]);
                        posY.setValue(split[1]);
                        posZ.setValue(split[2]);
                    }
                }
            }
        }

        return false;
    }

    private Vec3 calculateCoords(){
        try {
            return calculateCoords(Double.parseDouble(posX.getValue()), Double.parseDouble(posY.getValue()),
                    Double.parseDouble(posZ.getValue()));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Vec3 calculateCoords(double x,double y, double z){
        try {
            Vec3 newLocation = new Vec3(x,y,z);
            if (newLocation.distanceTo(getExampleStatue().position()) > CreateQOLConfigs.server().statueDistance.get()) return null;
            return newLocation;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void updateCoords(){
        Vec3 newLocation = calculateCoords();

        if (newLocation != null) {
            getExampleStatue().setPos(newLocation.x,newLocation.y,newLocation.z);
            getParent().sendUpdatePacket();
        }
    }

    @Override
    public void initWidgets(int x, int y) {
        posX = new EditBox(Minecraft.getInstance().font,x + TEXT_BOX_X_OFFSET, y+TEXT_Y_OFFSET, 34, 18, CommonComponents.EMPTY);
        posY = new EditBox(Minecraft.getInstance().font,x + TEXT_BOX_X_OFFSET + 40, y+TEXT_Y_OFFSET, 34, 18,CommonComponents.EMPTY);
        posZ = new EditBox(Minecraft.getInstance().font,x + TEXT_BOX_X_OFFSET + 80, y+TEXT_Y_OFFSET, 34, 18,CommonComponents.EMPTY);
        positionLabel = new Label(x + 2* BASE_OFFSET, y-2*LABEL_Y_OFFSET,CreateQOLLang.translateDirect("statue.position"));
        positionLabel.text = CreateQOLLang.translateDirect("statue.position");


        for (EditBox widget : new EditBox[]{posX, posY, posZ}) {
            widget.setMaxLength(6);
            widget.setBordered(false);
            widget.setTextColor(0xFFFFFF);
            widget.setFocused(false);
            widget.mouseClicked(0, 0, 0);
            widget.setResponder(s->{if (!s.isEmpty())updateCoords();});
            widget.setFilter(s -> {
                if (s.isEmpty() || s.equals("-"))
                    return true;
                try {
                    Double.parseDouble(s);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            });
        }

        posX.setValue(""+getParent().getMenu().contentHolder.position().x);
        posY.setValue(""+getParent().getMenu().contentHolder.position().y);
        posZ.setValue(""+getParent().getMenu().contentHolder.position().z);

        scaleLabel = new Label(x + 2* BASE_OFFSET, y+44 - 3*LABEL_Y_OFFSET,CreateQOLLang.translateDirect("statue.scale"));
        scaleLabel.text = CreateQOLLang.translateDirect("statue.scale");

        scale = new FloatScrollInput(x + 2* BASE_OFFSET, y + 44,TEXT_BOX_WIDTH,18);
        scale.withRange(0.1f,10);
        scale.withStepFunction(ctx->ctx.control ? 1 : ctx.shift ? 0.5f : 0.1f);
        scale.calling(i->{
            getExampleStatue().setEntityScale(i);
            scale.titled(Component.literal("Scale: " + String.format("%.2f",scale.getState()) ));
            getParent().sendUpdatePacket();
        });
        scale.setState(getExampleStatue().getEntityScale());
        scale.titled(Component.literal("Scale: " + String.format("%.2f",scale.getState())));

        rotationLabel = new Label(x + 2* BASE_OFFSET, y+88 - LABEL_Y_OFFSET*3,CreateQOLLang.translateDirect("statue.rotation.cape"));
        rotationLabel.text = CreateQOLLang.translateDirect("statue.rotation.cape");
        rotationX = new ClickableScrollInput(x + 2* BASE_OFFSET, y + 88,TEXT_BOX_WIDTH,18);
        rotationX.withRange(-180,180);
        rotationX.withStepFunction(ctx->ctx.control ? 90 : ctx.shift ? 45 : 1);
        rotationX.calling(i->{
            setRotation('x',i);
            rotationX.titled(Component.literal("X: " + i + "°"));
            getParent().sendUpdatePacket();
        });
        rotationX.setState((int) getExampleStatue().getEntityRotations().getX());
        rotationX.titled(Component.literal("X: " + rotationX.getState() + "°"));

        rotationY = new ClickableScrollInput(x + 2* BASE_OFFSET, y + 110,TEXT_BOX_WIDTH,18);
        rotationY.withRange(-180,180);
        rotationY.withStepFunction(ctx->ctx.control ? 90 : ctx.shift ? 45 : 1);
        rotationY.calling(i->{
            setRotation('y',i);
            rotationY.titled(Component.literal("Y: " + i + "°"));
            getParent().sendUpdatePacket();
        });
        rotationY.setState((int) getExampleStatue().getEntityRotations().getY());
        rotationY.titled(Component.literal("Y: " + rotationY.getState() + "°"));

        rotationZ = new ClickableScrollInput(x + 2* BASE_OFFSET, y + 132,TEXT_BOX_WIDTH,18);
        rotationZ.withRange(-180,180);
        rotationZ.withStepFunction(ctx->ctx.control ? 90 : ctx.shift ? 45 : 1);
        rotationZ.calling(i->{
            setRotation('z',i);
            rotationZ.titled(Component.literal("Z: " + i + "°"));
            getParent().sendUpdatePacket();
        });
        rotationZ.setState((int) getExampleStatue().getEntityRotations().getZ());
        rotationZ.titled(Component.literal("Z: " + rotationZ.getState() + "°"));
    }

}
