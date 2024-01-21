package dev.tenacity.module.impl.render;

import dev.tenacity.module.Category;
import dev.tenacity.module.Module;

public class MusicPlayer extends Module {
    public MusicPlayer() {
        super("MusicPlayer",Category.RENDER,"Play music");
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(dev.tenacity.ui.musicplayer.MusicPlayer.INSTANCE);
        setEnabled(false);
    }
}
