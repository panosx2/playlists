package com.basement.panosx2.playlists.Objects;

import java.io.Serializable;

/**
 * Created by panosx2 on 13/11/2019
 */
public class Playlist implements Serializable {
    private String id, image, name;
    private int count;

    public Playlist(String id, String image, String name, int count) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
}
