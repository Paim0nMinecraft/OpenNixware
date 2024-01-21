package dev.tenacity.ui.musicplayer.component.impl;

import dev.tenacity.ui.musicplayer.MusicPlayer;
import dev.tenacity.ui.musicplayer.component.AbstractComponent;
import dev.tenacity.ui.musicplayer.layout.LayOut;
import dev.tenacity.ui.musicplayer.cloudmusic.MusicManager;
import dev.tenacity.ui.musicplayer.cloudmusic.impl.Track;
import dev.tenacity.ui.musicplayer.cloudmusic.util.Stencil;
import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author TIMER_err
 * Date: 2023.7.23
 */
public class SearchResultComponent extends AbstractComponent {
    @Getter
    private final CopyOnWriteArrayList<Track> list = new CopyOnWriteArrayList<>();
    private float scrollY = 0, scrollCacheY;

    public SearchResultComponent(AbstractComponent parent, LayOut layout, String layoutMode, int spacing, float width, float height) {
        super(parent, layout, layoutMode, spacing, width, height);
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        super.drawComponent(mouseX, mouseY);

        AbstractFontRenderer font = dev.tenacity.utils.font.hanabi.FontUtil.getFromCustomFont(FontUtil.tenacityFont24);

        Stencil.write(false);
        Gui.drawRect(getX(), getY(), getX() + getWidth() - getSpacing() * 2, getY() + getHeight() - getSpacing() * 2, -1);
        Stencil.erase(true);
        list.forEach(track -> {
            float y = getY() + list.indexOf(track) * (50 + 4) + scrollCacheY;
            if (y + 50 < getY() || y > getY() + getHeight() - getSpacing() * 2) return;
            track.fade += ((RenderUtil.isHovered(
                    getX(), getY(), getWidth() - getSpacing() * 2,
                    getHeight() - getSpacing() * 2, mouseX, mouseY
            ) && RenderUtil.isHovered(getX(), y,
                    getWidth() - getSpacing() * 2,
                    50, mouseX, mouseY
            ) ? 1 : 0) - track.fade) / 10f;
            RoundedUtil.drawRoundOutline(getX(), y,
                    getWidth() - getSpacing() * 2, 50, 4,
                    0.1f, MusicPlayer.INSTANCE.getTheme()[1],
                    new Color(255, 255, 255, (int) (255 * track.fade))
            );
            if (MusicManager.INSTANCE.getArt(track.id) != null)
                RenderUtil.drawImage(MusicManager.INSTANCE.getArt(track.id), getX() + 2, y + 2, 46, 46);
            else if (track.picThread == null) {
                File artFile = new File(MusicManager.INSTANCE.artPicFolder, String.valueOf(track.id));
                track.picThread = new Thread(() -> {
                    MusicManager.INSTANCE.downloadFile(track.picUrl, artFile.getAbsolutePath());
                    MusicManager.INSTANCE.loadFromCache(track.id);
                    track.picThread = null;
                });
                track.picThread.start();
            }
            font.drawString(track.name + " | " + track.artists,
                    getX() + 2 + 46 + 2, y + (50 - font.getHeight()) / 2f, -1
            );
        });
        Stencil.dispose();
        if (RenderUtil.isHovered(
                getX(), getY(), getWidth() - getSpacing() * 2,
                getHeight() - getSpacing() * 2, mouseX, mouseY
        )) {
            int dWheel = Mouse.getDWheel();
            scrollCacheY += (scrollY - scrollCacheY) / 20f;
            if (dWheel > 0) scrollY += 35;
            if (dWheel < 0) scrollY -= 35;
            if (getHeight() - getSpacing() * 2 - scrollY > list.size() * (50 + 4))
                scrollY = getHeight() - getSpacing() * 2 - list.size() * (50 + 4);
            if (scrollY > 0)
                scrollY = 0;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        list.forEach(track -> {
            float Ty = getY() + list.indexOf(track) * (50 + 4) + scrollCacheY;
            if (RenderUtil.isHovered(getX(), getY(),
                    getWidth() - getSpacing() * 2, getHeight() - getSpacing() * 2,
                    mouseX, mouseY
            ) && RenderUtil.isHovered(getX(), Ty,
                    getWidth() - getSpacing() * 2, 50,
                    mouseX, mouseY
            )) {
                for (Track t : MusicManager.INSTANCE.playlist)
                    if (t.id == track.id)
                        return;
                MusicManager.INSTANCE.playlist.add(track);
            }
        });
    }
}
