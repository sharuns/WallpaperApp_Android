package com.example.wallpapernamo.models;

import com.google.firebase.database.Exclude;

public class Wallpaper {

    @Exclude
    public String id;

    @Exclude
    public String category;

    @Exclude
    public boolean isFavourite = false;

    public String title, desc , url;

    public Wallpaper(String id, String title, String desc, String url, String category) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.url = url;
        this.category = category;
    }
}


