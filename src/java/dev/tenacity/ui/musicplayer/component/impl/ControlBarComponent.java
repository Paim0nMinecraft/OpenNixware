package dev.tenacity.ui.musicplayer.component.impl;

import dev.tenacity.ui.musicplayer.MusicPlayer;
import dev.tenacity.ui.musicplayer.component.AbstractComponent;
import dev.tenacity.ui.musicplayer.layout.LayOut;
import dev.tenacity.ui.musicplayer.utils.Blur;
import dev.tenacity.ui.musicplayer.cloudmusic.MusicManager;
import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.font.hanabi.FontUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;

public class ControlBarComponent extends AbstractComponent {
    public ControlBarComponent(AbstractComponent parent, LayOut layout, String layoutMode, int spacing, float width, float height) {
        super(parent, layout, layoutMode, spacing, width, height);
    }

    public ArrayList<Button> buttons = new ArrayList<>();

    @Override
    public void init() {
        super.init();
        AbstractFontRenderer icon = FontUtil.icon30;
        float y = getY() + 20;
        buttons.clear();
        buttons.add(new Button("L", getX() + (getWidth() - getSpacing() * 2 - (20 + 4) * 3 - 4) / 2f, y, 20, 20) {
            @Override
            void action() {
                MusicManager.INSTANCE.prev();
            }
        });
        buttons.add(new Button(MusicManager.INSTANCE.getMediaPlayer() != null && MusicManager.INSTANCE.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING ? "K" : "J",
                getX() + (getWidth() - getSpacing() * 2 - (20 + 4) * 3 - 4) / 2f + 20 + 4, y, 20, 20) {
            @Override
            void action() {
                if (!MusicManager.INSTANCE.playlist.isEmpty()) {
                    if (MusicManager.INSTANCE.getCurrentTrack() == null) {
                        try {
                            MusicManager.INSTANCE.play(MusicManager.INSTANCE.playlist.get(0));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (MusicManager.INSTANCE.getMediaPlayer() != null) {
                            if (MusicManager.INSTANCE.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
                                MusicManager.INSTANCE.getMediaPlayer().pause();
                            } else {
                                MusicManager.INSTANCE.getMediaPlayer().play();
                            }
                        }
                    }
                }
            }
        });
        buttons.add(new Button("M", getX() + (getWidth() - getSpacing() * 2 - (20 + 4) * 3 - 4) / 2f + (20 + 4) * 2, y, 20, 20) {
            @Override
            void action() {
                MusicManager.INSTANCE.next();
            }
        });

        buttons.add(new Button(MusicManager.INSTANCE.repeat ? "O" : "N", getX() + getWidth() - getSpacing() - 2 - icon.getStringWidth("N"), y, 20, 20) {
            @Override
            void action() {
                MusicManager.INSTANCE.repeat = !MusicManager.INSTANCE.repeat;
            }
        });
    }

    @Override
    public void drawComponent(int mouseX, int mouseY) {
        super.drawComponent(mouseX, mouseY);
        AbstractFontRenderer icon = FontUtil.icon30;
        float y = getY() + 20;
        buttons.forEach(b -> {
            switch (buttons.indexOf(b)) {
                case 0:
                    b.setX(getX() + (getWidth() - getSpacing() * 2 - (20 + 4) * 3 - 4) / 2f);
                    b.setY(y);
                    break;
                case 1:
                    b.setX(getX() + (getWidth() - getSpacing() * 2 - (20 + 4) * 3 - 4) / 2f + 20 + 4);
                    b.setY(y);
                    b.setText(MusicManager.INSTANCE.getMediaPlayer() != null && MusicManager.INSTANCE.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING ? "K" : "J");
                    break;
                case 2:
                    b.setX(getX() + (getWidth() - getSpacing() * 2 - (20 + 4) * 3 - 4) / 2f + (20 + 4) * 2);
                    b.setY(y);
                    break;
                case 3:
                    b.setX(getX() + getWidth() - getSpacing() * 2 - 10 - icon.getStringWidth("N"));
                    b.setY(y);
                    b.setText(MusicManager.INSTANCE.repeat ? "O" : "N");
                    break;
            }
        });
        RoundedUtil.drawRound(getX(), getY(), getWidth() - 2 * getSpacing(), getHeight() - 2 * getSpacing(), 2, MusicPlayer.INSTANCE.getTheme()[1]);

        float imgH = getHeight() - getSpacing() * 2 - 15;
        if (MusicManager.INSTANCE.getCurrentTrack() != null && MusicManager.INSTANCE.getArt(MusicManager.INSTANCE.getCurrentTrack().id) != null) {
            if (!System.getProperty("os.name").startsWith("Mac OS"))
                Blur.bloom(() -> RoundedUtil.drawRound(getX() + 5 + 1, getY() + 5 + 1, imgH - 2, imgH - 2, 1, new Color(0)), 2, 1);
            RenderUtil.drawImage(MusicManager.INSTANCE.getArt(MusicManager.INSTANCE.getCurrentTrack().id), getX() + 5, getY() + 5, imgH, imgH);
            dev.tenacity.utils.font.FontUtil.tenacityFont24.drawString(MusicManager.INSTANCE.getCurrentTrack().name, getX() + 5 + imgH + 2, getY() + 12, -1);
            dev.tenacity.utils.font.FontUtil.tenacityFont14.drawString(MusicManager.INSTANCE.getCurrentTrack().artists, getX() + 5 + imgH + 2, getY() + 12 + 10, -1);
        }
        buttons.forEach(b -> b.draw(mouseX, mouseY));

        float progress = 0;
        float Py = getHeight() - 10;
        float width = getWidth() - getSpacing() * 2 - 10;
        if (MusicManager.INSTANCE.getMediaPlayer() != null) {
            progress = (float) MusicManager.INSTANCE.getMediaPlayer().getCurrentTime().toSeconds() / (float) MusicManager.INSTANCE.getMediaPlayer().getStopTime().toSeconds();
        }
        RoundedUtil.drawRound(getX() + 5, getY() + Py, width, 4, 1.4f, new Color(0, 0, 0, 137));
        if (MusicManager.INSTANCE.loadingThread != null) {
            RoundedUtil.drawRound(getX() + 5, getY() + Py, (MusicManager.INSTANCE.downloadProgress / 100 * width), 4, 1.4f, new Color(-1));
            RenderUtil.circle(getX() + 5 + (MusicManager.INSTANCE.downloadProgress / 100 * width), getY() + Py + 2, 3, new Color(255, 255, 255,128).getRGB());
            RenderUtil.circle(getX() + 5 + (MusicManager.INSTANCE.downloadProgress / 100 * width), getY() + Py + 2, 2, new Color(255, 50, 50,128).getRGB());
        } else {
            RoundedUtil.drawRound(getX() + 5, getY() + Py, progress * width, 4, 1.4f, new Color(-1));
            RenderUtil.circle(getX() + progress * width + 5, getY() + Py + 2, 3, new Color(255, 255, 255,128).getRGB());
            RenderUtil.circle(getX() + progress * width + 5, getY() + Py + 2, 2, new Color(255, 255, 255).getRGB());
        }


    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        buttons.forEach(b -> b.mouseClicked(mouseX, mouseY, mouseButton));
    }

    abstract static class Button {
        @Getter
        @Setter
        private float x, y, width, height;
        @Getter
        @Setter
        private String text;

        public Button(String text, float x, float y, float width, float height) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void draw(float mouseX, float mouseY) {
            if (RenderUtil.isHovered(x, y, width, height, mouseX, mouseY))
                RoundedUtil.drawRound(x, y, width, height, 4, MusicPlayer.INSTANCE.getTheme()[1].brighter());
            AbstractFontRenderer font = FontUtil.icon30;
            font.drawString(text, x + (width - font.getStringWidth(text)) / 2f,
                    y + (height - font.getHeight()) / 2f + 8, -1);
        }

        public void mouseClicked(float mouseX, float mouseY, int button) {
            if (RenderUtil.isHovered(x, y, width, height, mouseX, mouseY) && button == 0) action();
        }

        abstract void action();
    }
}
