package dev.tenacity.ui.musicplayer.cloudmusic.impl;


public class Track {
    public long id;
    public String name;
    public String artists;
    public String picUrl;
    public float fade;
    public Thread picThread = null;

    public Track(long id, String name, String artists, String picUrl) {
        this.id = id;
        this.name = name;
        this.artists = artists;
        this.picUrl = picUrl;
    }
}
