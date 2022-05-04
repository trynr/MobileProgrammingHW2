package com.ynr.keypsd.mobileprogramminghw2.Models;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Song implements Serializable {

    private String path;
    private String name;
    private String artist;
    private String album;
    private int duration;
    private String albumImageEncoded;

    public Song(String path, String name, String artist, String album, int duration, String albumImageEncoded) {
        this.path = path;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.albumImageEncoded = albumImageEncoded;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAlbumImageEncoded() {
        return albumImageEncoded;
    }

    public void setAlbumImageEncoded(String albumImageEncoded) {
        this.albumImageEncoded = albumImageEncoded;
    }
}
