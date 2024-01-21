package dev.tenacity.ui.musicplayer.component;

/**
 * @author TIMER_err
 * Date: 2023.7.23
 */
public interface Component {
    void init();

    void drawComponent(int mouseX, int mouseY);

    void mouseClicked(int mouseX, int mouseY, int mouseButton);

    void mouseReleased(int mouseX, int mouseY, int mouseButton);

    void keyTyped(char typedChar, int keyCode);
}
