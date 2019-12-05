package com.basement.panosx2.playlists.Objects;

/**
 * Created by panosx2 on 16/11/2019
 */
public class Track {
    private String image, name, artist;

    public Track(String image, String name, String artist) {
        this.image = image;
        this.name = name;
        this.artist = artist;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }
}
