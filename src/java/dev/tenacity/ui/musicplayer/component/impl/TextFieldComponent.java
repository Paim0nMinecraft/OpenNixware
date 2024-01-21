package dev.tenacity.ui.musicplayer.component.impl;

import dev.tenacity.ui.musicplayer.cloudmusic.util.Stencil;
import dev.tenacity.ui.musicplayer.component.AbstractComponent;
import dev.tenacity.ui.musicplayer.component.callback.TextFieldCallback;
import dev.tenacity.ui.musicplayer.layout.LayOut;
import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @author TIMER_err
 * Date: 2023.7.23
 */
public class TextFieldComponent extends AbstractComponent {
    private final TextFieldCallback callback;
    @Getter
    @Setter
    private boolean focus = false;
    @Getter
    private String text = "";
    @Getter
    @Setter
    private String backText = "";
    private int cursor = 0;
    private float cursorDis = 0, cursorCacheDis = 0, cursorFade = 1;
    private boolean fadePlus = false;
    public TextFieldComponent(AbstractComponent parent, LayOut layout, String layoutMode, int spacing, float width, float height, TextFieldCallback callback) {
        super(parent, layout, layoutMode, spacing, width, height);
        this.callback = callback;
    }

    @Override
    public void init() {
        super.init();
        cursorDis = getDistanceToCursor(dev.tenacity.utils.font.hanabi.FontUtil.getFromCustomFont(FontUtil.tenacityFont14));
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        super.drawComponent(mouseX, mouseY);
        AbstractFontRenderer font = dev.tenacity.utils.font.hanabi.FontUtil.getFromCustomFont(FontUtil.tenacityFont18);
        float fix = font.getStringWidth(text) > getWidth() - getSpacing() * 2 ? font.getStringWidth(text + "|") - (getWidth() - getSpacing() * 2) : 0;
        Stencil.write(false);
        Gui.drawRect(getX() + getSpacing(), getY() + getSpacing(), getX() + getWidth() - getSpacing(), getY() + getHeight() - getSpacing(), -1);
        Stencil.erase(true);
        if (text.isEmpty())
            font.drawString(backText, getX() + 1 + getSpacing() + 3, getY()  + 2 + (getHeight() - font.getHeight()) / 2f, new Color(122, 122, 122).getRGB());
        font.drawString(text, getX() + 1 + getSpacing() - fix, getY() + 2+ (getHeight() - font.getHeight()) / 2f, -1);
        cursorCacheDis += (cursorDis - cursorCacheDis) / 10f;
        if (Math.round(cursorFade * 10) == 10)
            fadePlus = false;
        else if (Math.round(cursorFade * 10) == 0)
            fadePlus = true;
        if (fadePlus) cursorFade += 0.015f;
        else cursorFade -= 0.015f;
        if (cursorFade > 1) cursorFade = 1;
        else if (cursorFade < 0) cursorFade = 0;
        if (focus)
            font.drawString("|", getX() + 1 + getSpacing() + cursorCacheDis - fix, getY() + 2 + (getHeight() - font.getHeight()) / 2f, new Color(255, 255, 255, (int) (cursorFade * 255)).getRGB());
        Stencil.dispose();
        RoundedUtil.drawRoundOutline(getX(), getY(), getWidth(), getHeight(), 3, 0.1f, new Color(0, 0, 0, 0), focus ? new Color(-1) : new Color(122, 122, 122));
    }

    private float getDistanceToCursor(AbstractFontRenderer font) {
        if (this.text.isEmpty()) {
            return 0;
        }
        char[] chars = this.text.toCharArray();
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < chars.length + 1; i++) {
            if (i == cursor)
                break;
            if (i < chars.length)
                string.append(chars[i]);
        }
        return font.getStringWidth(string.toString());
    }

    public void setText(String text) {
        this.text = text;
        cursor = text.length();
    }

    private void delete() {
        if (this.text.isEmpty()) return;
        StringBuilder result = new StringBuilder();
        char[] chars = this.text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i == cursor - 1)
                continue;
            result.append(chars[i]);
        }
        cursor--;
        this.text = result.toString();
    }

    private void write(String text) {
        if (this.text.isEmpty()) {
            this.text = text;
            cursor += text.length();
            return;
        }
        StringBuilder result = new StringBuilder();
        char[] chars = this.text.toCharArray();
        for (int i = 0; i < chars.length + 1; i++) {
            if (i == cursor)
                result.append(text);
            if (i < chars.length)
                result.append(chars[i]);
        }
        cursor += text.length();
        this.text = result.toString();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        focus = RenderUtil.isHovered(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (focus) {
            if (GuiScreen.isKeyComboCtrlV(keyCode))
                write(GuiScreen.getClipboardString());
            else if (keyCode == Keyboard.KEY_BACK && !text.isEmpty() && cursor != 0)
                delete();
            else if (keyCode == Keyboard.KEY_LEFT && cursor != 0)
                cursor--;
            else if (keyCode == Keyboard.KEY_RIGHT && cursor <= text.length())
                cursor++;
            else if (keyCode == Keyboard.KEY_RETURN)
                callback.run(text);
            else if (typedChar != 167 && typedChar >= 32 && typedChar != 127) write(String.valueOf(typedChar));
        }
        if (cursor > this.text.length()) cursor = this.text.length();
        cursorDis = getDistanceToCursor(dev.tenacity.utils.font.hanabi.FontUtil.getFromCustomFont(FontUtil.tenacityFont18));
    }
}
