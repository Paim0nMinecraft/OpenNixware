package dev.tenacity.ui.musicplayer.layout.impl;

import dev.tenacity.ui.musicplayer.MusicPlayer;
import dev.tenacity.ui.musicplayer.component.AbstractComponent;
import dev.tenacity.ui.musicplayer.layout.LayOut;

public class OrientationLayOut extends LayOut {
    public void apply(String mode, AbstractComponent component) {
        switch (mode) {
            case "EAST": {
                component.setRelativeX(component.getParent().getRelativeX() + component.getParent().getWidth() + component.getSpacing());
                component.setRelativeY(component.getParent().getRelativeY());
                break;
            }
            case "WEST": {
                component.setRelativeX(component.getParent().getRelativeX() - component.getSpacing() - component.getWidth());
                component.setRelativeY(component.getParent().getRelativeY());
                break;
            }
            case "SOUTH": {
                component.setRelativeX(component.getParent().getRelativeX());
                component.setRelativeY(component.getParent().getRelativeY() + component.getParent().getHeight() + component.getSpacing());
                break;
            }
            case "NORTH": {
                component.setRelativeX(component.getParent().getRelativeX());
                component.setRelativeY(component.getParent().getRelativeY() - component.getSpacing() - component.getHeight());
                break;
            }
        }
        component.setX(MusicPlayer.INSTANCE.getX() + component.getSpacing() + component.getRelativeX());
        component.setY(MusicPlayer.INSTANCE.getY() + component.getSpacing() + component.getRelativeY());
    }
}
