package dev.tenacity.ui.musicplayer.component;

import dev.tenacity.ui.musicplayer.MusicPlayer;
import dev.tenacity.ui.musicplayer.layout.LayOut;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TIMER_err
 * Date: 2023.7.23
 */
public abstract class AbstractComponent implements Component {
    @Getter
    @Setter
    private float relativeX, relativeY;
    @Getter
    @Setter
    private float x, y;
    @Getter
    private final int spacing;
    @Getter
    @Setter
    private float width, height;
    @Getter
    private final AbstractComponent parent;
    @Getter
    private final LayOut layout;
    @Getter
    private final String layoutMode;

    public AbstractComponent(AbstractComponent parent, LayOut layout, String layoutMode, int spacing, float width, float height) {
        this.parent = parent;
        this.layout = layout;
        this.layoutMode = layoutMode;
        this.spacing = spacing;
        this.width = width;
        this.height = height;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        if (parent != null && layout != null && layoutMode != null)
            layout.apply(layoutMode, this);
        else {
            relativeX = 0;
            relativeY = 0;
            x = MusicPlayer.INSTANCE.getX() + spacing + relativeX;
            y = MusicPlayer.INSTANCE.getY() + spacing + relativeY;
        }
    }

    @Override
    public void init() {
        if (parent != null && layout != null && layoutMode != null)
            this.layout.apply(layoutMode, this);
        else {
            relativeX = 0;
            relativeY = 0;
            x = MusicPlayer.INSTANCE.getX() + spacing + relativeX;
            y = MusicPlayer.INSTANCE.getY() + spacing + relativeY;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }
}
