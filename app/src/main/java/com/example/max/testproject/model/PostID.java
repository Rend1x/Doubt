package com.example.max.testproject.model;

import android.support.annotation.NonNull;

public class PostID {

    public String postId;

    public <T extends PostID> T withId(@NonNull final String id){
        this.postId = id;
        return (T) this;
    }
}
