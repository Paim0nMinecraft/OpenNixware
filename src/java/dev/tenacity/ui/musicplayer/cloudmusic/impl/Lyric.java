package dev.tenacity.ui.musicplayer.cloudmusic.impl;


public class Lyric {
    public long time;
    public String text;
    public float y, cacheY;

    public Lyric(String text, long time) {
        this.text = text;
        this.time = time;
    }
}
