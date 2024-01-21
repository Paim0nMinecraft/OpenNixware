package dev.tenacity.ui.musicplayer.component.impl;

import dev.tenacity.ui.musicplayer.MusicPlayer;
import dev.tenacity.ui.musicplayer.cloudmusic.impl.Track;
import dev.tenacity.ui.musicplayer.component.AbstractComponent;
import dev.tenacity.ui.musicplayer.layout.LayOut;
import dev.tenacity.ui.musicplayer.cloudmusic.MusicManager;
import dev.tenacity.ui.musicplayer.cloudmusic.util.Stencil;
import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;

import java.awt.*;

/**
 * @author TIMER_err
 * Date: 2023.7.23
 */
public class PlayListComponent extends AbstractComponent {
    public PlayListComponent(AbstractComponent parent, LayOut layout, String layoutMode, int spacing, float width, float height) {
        super(parent, layout, layoutMode, spacing, width, height);
    }

    private float scrollY = 0, scrollCacheY;

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        super.drawComponent(mouseX, mouseY);
        AbstractFontRenderer font = dev.tenacity.utils.font.hanabi.FontUtil.getFromCustomFont(FontUtil.csgoFont18);
        AbstractFontRenderer font2 =dev.tenacity.utils.font.hanabi.FontUtil.getFromCustomFont(FontUtil.tenacityFont14);
        RoundedUtil.drawRound(getX(), getY(), getWidth() - 2 * getSpacing(), getHeight() - 2 * getSpacing(), 2, MusicPlayer.INSTANCE.getTheme()[1]);
        font.drawString("Play Lists", getX() + (getWidth() - getSpacing() * 2 - font.getStringWidth("Play Lists")) / 2f,
                getY() + 4, -1);
        Gui.drawRect(getX() + 3, getY() + font.getHeight() + 4, getX() + 3 + getWidth() - getSpacing() * 2 - 6, getY() + font.getHeight() + 4 + 0.5f, -1);

        Stencil.write(false);
        Gui.drawRect(getX(), getY(), getX() + getWidth() - getSpacing() * 2, getY() + getHeight() - getSpacing() * 2, -1);
        Stencil.erase(true);
        MusicManager.INSTANCE.playlist.forEach(track -> {
            float y = getY() + font.getHeight() + 4 + 2 + MusicManager.INSTANCE.playlist.indexOf(track) * (15 + 2) + scrollCacheY;
            track.fade += ((RenderUtil.isHovered(
                    getX(), getY(), getWidth() - getSpacing() * 2,
                    getHeight() - getSpacing() * 2, mouseX, mouseY
            ) && RenderUtil.isHovered(getX(), y,
                    getWidth() - getSpacing() * 2,
                    15, mouseX, mouseY
            ) ? 1 : 0) - track.fade) / 10f;
            RoundedUtil.drawRoundOutline(getX(), y, getWidth() - getSpacing() * 2, 15, 3, 0.1f, MusicPlayer.INSTANCE.getTheme()[1], new Color(255, 255, 255, (int) (track.fade * 255)));
            font2.drawString(track.name, getX() + 2, y + (15 - font2.getHeight()) / 2f, -1);
        });
        Stencil.dispose();

        if (RenderUtil.isHovered(
                getX(), getY(), getWidth() - getSpacing() * 2,
                getHeight() - getSpacing() * 2, mouseX, mouseY
        )) {
            int dWheel = Mouse.getDWheel();
            scrollCacheY += (scrollY - scrollCacheY) / 20f;
            if (dWheel > 0) scrollY += 25;
            if (dWheel < 0) scrollY -= 25;
            if (getHeight() - getSpacing() * 2 - scrollY > MusicManager.INSTANCE.playlist.size() * (15 + 2) - font.getHeight() - 2)
                scrollY = getHeight() - getSpacing() * 2 - MusicManager.INSTANCE.playlist.size() * (15 + 2) - font.getHeight() - 2;
            if (scrollY > 0)
                scrollY = 0;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        AbstractFontRenderer font = dev.tenacity.utils.font.hanabi.FontUtil.getFromCustomFont(FontUtil.tenacityFont18);
        for (Track track : MusicManager.INSTANCE.playlist) {
            {
                float y = getY() + font.getHeight() + 4 + 2 + MusicManager.INSTANCE.playlist.indexOf(track) * (15 + 2) + scrollCacheY;
                if (RenderUtil.isHovered(
                        getX(), getY(), getWidth() - getSpacing() * 2,
                        getHeight() - getSpacing() * 2, mouseX, mouseY
                ) && RenderUtil.isHovered(getX(), y,
                        getWidth() - getSpacing() * 2,
                        15, mouseX, mouseY
                )) {
                    if (mouseButton == 0)
                        try {
                            MusicManager.INSTANCE.play(track);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    else {
                        MusicManager.INSTANCE.playlist.remove(track);
                        break;
                    }
                }
            }
        }
    }
}
