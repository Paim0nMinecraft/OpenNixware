package dev.tenacity.ui.musicplayer;

import dev.tenacity.ui.musicplayer.cloudmusic.MusicManager;
import dev.tenacity.ui.musicplayer.cloudmusic.api.CloudMusicAPI;
import dev.tenacity.ui.musicplayer.cloudmusic.impl.Track;
import dev.tenacity.ui.musicplayer.component.AbstractComponent;
import dev.tenacity.ui.musicplayer.component.impl.*;
import dev.tenacity.ui.musicplayer.layout.impl.OrientationLayOut;
import dev.tenacity.ui.musicplayer.utils.Blur;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;


/**
 * @author TIMER_err
 * Date: 2023.7.23
 */
public class MusicPlayer extends GuiScreen {
    public static MusicPlayer INSTANCE = new MusicPlayer();
    @Getter
    private final ArrayList<AbstractComponent> components = new ArrayList<>();
    @Getter
    private final AbstractComponent title;
    @Getter
    private final PlayListComponent playListComponent;
    @Getter
    private final TextFieldComponent textField;
    @Getter
    private final TextFieldComponent volumeField;
    private final Pattern pattern = Pattern.compile("-?[0-9]+(\\\\.[0-9]+)?");
    @Getter
    private final ControlBarComponent controlBar;
    @Getter
    private final Color[] theme = {new Color(255, 255, 255, 18), new Color(0, 0, 0, 128)};
    @Getter
    private float width, height;
    @Getter
    private float x, y;
    private boolean dragging;
    private float dragX, dragY;
    @Getter
    private SearchResultComponent searchResultComponent;
    private Thread searchThread;

    public MusicPlayer() {
        ScaledResolution sc = new ScaledResolution(Minecraft.getMinecraft());
        width = Math.min(sc.getScaledWidth() - 60, 600);
        height = Math.min(sc.getScaledHeight() - 70, 370.8f);
        x = (sc.getScaledWidth() - width) / 2f;
        y = (sc.getScaledHeight() - height) / 2f;

        title = new LabelComponent(null, null, null, 2,
                dev.tenacity.utils.font.hanabi.FontUtil.getFromCustomFont(FontUtil.tenacityFont36).getStringWidth("nixware") + 2 * 2, 20, FontUtil.csgoFont35,
                "nixware", -1);
        components.add(title);

        this.playListComponent = new PlayListComponent(title, new OrientationLayOut(), "SOUTH", 2, width / 6.5f, height - 4 - title.getHeight());
        components.add(playListComponent);

        textField = new TextFieldComponent(title, new OrientationLayOut(), "EAST", 2, 100, 15, content -> {
            if (searchThread == null) {
                searchThread = new Thread(() -> {
                    try {
                        searchResultComponent.getList().clear();
                        if (!pattern.matcher(content).matches())
                            for (Object[] strings : CloudMusicAPI.INSTANCE.requestSearch(CloudMusicAPI.INSTANCE.getSearchJson(content)))
                                searchResultComponent.getList().add(new Track(Long.parseLong(strings[1].toString()), strings[0].toString(), strings[3].toString(), strings[2].toString()));
                        else //noinspection unchecked
                            for (Track track : ((ArrayList<Track>) CloudMusicAPI.INSTANCE.getPlaylistDetail(content)[1]))
                                searchResultComponent.getList().add(track);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        searchThread = null;
                    }
                });
                searchThread.start();
            }
        });
        textField.setBackText("Search");
        components.add(textField);

        volumeField = new TextFieldComponent(textField, new OrientationLayOut(), "EAST", 2, 100, 15, content -> {
            if (MusicManager.INSTANCE.getMediaPlayer() != null)
                MusicManager.INSTANCE.getMediaPlayer().setVolume(Integer.parseInt(content) / 100d);
        });
        volumeField.setBackText("Volume (0-100)");
        components.add(volumeField);

        float controlBarHeight = height / 4;

        searchResultComponent = new SearchResultComponent(playListComponent, new OrientationLayOut(), "EAST", 2, width - 2 * 2 - playListComponent.getWidth(), height - textField.getHeight() - 2 * 2 - 4 - controlBarHeight - 4);

        components.add(searchResultComponent);

        controlBar = new ControlBarComponent(searchResultComponent, new OrientationLayOut(), "SOUTH", 2, width - 2 * 2 - playListComponent.getWidth(), controlBarHeight);
        components.add(controlBar);
    }

    @Override
    public void initGui() {
        ScaledResolution sc = new ScaledResolution(Minecraft.getMinecraft());
        width = Math.min(sc.getScaledWidth() - 60, 600);
        height = Math.min(sc.getScaledHeight() - 70, 370.8f);
        x = (sc.getScaledWidth() - width) / 2f;
        y = (sc.getScaledHeight() - height) / 2f;

        playListComponent.setWidth(width / 6.5f);
        playListComponent.setHeight(height - 4 - title.getHeight());
        float controlBarHeight = height / 4;
        searchResultComponent.setWidth(width - 2 * 2 - playListComponent.getWidth());
        searchResultComponent.setHeight(height - textField.getHeight() - 2 * 2 - 4 - controlBarHeight - 4);
        controlBar.setWidth(width - 2 * 2 - playListComponent.getWidth());
        controlBar.setHeight(controlBarHeight);

        components.forEach(AbstractComponent::init);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!System.getProperty("os.name").startsWith("Mac OS"))
            Blur.bloom(() -> RoundedUtil.drawRound(x, y, width, height, 0, new Color(0, 0, 0, 128)), 2, 1);

        // Music screen
        RoundedUtil.drawRound(x - 1, y - 1, width + 1, height + 1, 0, new Color(0, 0, 0,150));
        components.forEach(c -> c.drawComponent(mouseX, mouseY));
        if (dragging) {
            x = mouseX + dragX;
            y = mouseY + dragY;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (RenderUtil.isHovered(x, y, width, 20, mouseX, mouseY)) {
            dragging = true;
            dragX = x - mouseX;
            dragY = y - mouseY;
        }
        components.forEach(c -> c.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        components.forEach(c -> c.mouseReleased(mouseX, mouseY, state));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        components.forEach(c -> c.keyTyped(typedChar, keyCode));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
