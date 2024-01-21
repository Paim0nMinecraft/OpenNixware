package dev.tenacity.ui.musicplayer.layout.impl;

import dev.tenacity.ui.musicplayer.MusicPlayer;
import dev.tenacity.ui.musicplayer.component.AbstractComponent;
import dev.tenacity.ui.musicplayer.layout.LayOut;

public class CommonLayOut extends LayOut {
    @Override
    public void apply(String mode, AbstractComponent component) {
        switch (mode) {
            case "Vertical": {
                component.setRelativeX(component.getParent().getRelativeX());
                component.setRelativeY(component.getParent().getRelativeY() + component.getParent().getHeight() + component.getSpacing());
                component.setX(MusicPlayer.INSTANCE.getX() + component.getRelativeX());
                component.setY(MusicPlayer.INSTANCE.getY() + component.getRelativeY());
                break;
            }
            case "Horizontal": {
                component.setRelativeX(component.getParent().getRelativeX() + component.getParent().getWidth() + component.getSpacing());
                component.setRelativeY(component.getParent().getRelativeY());
                component.setX(MusicPlayer.INSTANCE.getX() + component.getRelativeX());
                component.setY(MusicPlayer.INSTANCE.getY() + component.getRelativeY());
                break;
            }
        }
    }
}
