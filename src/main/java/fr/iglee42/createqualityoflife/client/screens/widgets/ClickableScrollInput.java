package fr.iglee42.createqualityoflife.client.screens.widgets;

import com.simibubi.create.foundation.gui.widget.ScrollInput;
import net.minecraft.util.Mth;

public class ClickableScrollInput extends ScrollInput {
    public ClickableScrollInput(int xIn, int yIn, int widthIn, int heightIn) {
        super(xIn, yIn, widthIn, heightIn);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        double clickedX = mouseX - getX();
        double progress = clickedX / width;
        int newValue = (int) (progress * (max + Mth.abs(min)));
        setState(newValue - Mth.abs(min));
        onChanged();
    }

}
