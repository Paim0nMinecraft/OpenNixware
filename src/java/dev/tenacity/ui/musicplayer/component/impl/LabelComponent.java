package dev.tenacity.ui.musicplayer.component.impl;

import dev.tenacity.ui.musicplayer.component.AbstractComponent;
import dev.tenacity.ui.musicplayer.layout.LayOut;
import dev.tenacity.utils.font.AbstractFontRenderer;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TIMER_err
 * Date: 2023.7.23
 */
public class LabelComponent extends AbstractComponent {
    private final AbstractFontRenderer font;
    @Getter
    @Setter
    private String text;
    @Getter
    @Setter
    private int color;

    public LabelComponent(AbstractComponent parent, LayOut layout, String layoutMode, int spacing, float width, float height, AbstractFontRenderer font, String text, int color) {
        super(parent, layout, layoutMode, spacing, width, height);
        this.font = font;
        this.text = text;
        this.color = color;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        super.drawComponent(mouseX, mouseY);
        font.drawString(text, getX(), getY() + (getHeight() - font.getHeight()) / 2f, color);
    }
}
