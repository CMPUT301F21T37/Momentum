package com.example.momentum.sharing;

import java.io.Serializable;

public class FollowingEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    //following username
    private String name;

    //following uid
    private String uid;

    public FollowingEntity(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
