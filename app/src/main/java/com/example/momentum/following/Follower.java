package com.example.momentum.following;

import static android.content.ContentValues.TAG;

import android.util.Log;

public class Follower {
    private String id;
    private String name;

    public Follower (String id, String name){
        this.id = id;
        this.name = name;
        Log.d(TAG, "created "+ id + " "+ name);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
