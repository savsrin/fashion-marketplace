package com.example.personalproject;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;


public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Register your parse models
        //ParseObject.registerSubclass(Post.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("kKvnZRoo4Gl7mkMAvY2uuwruAMsktVhU8BfygVQ7")
                .clientKey("JEbNJW3AEX7wyaP1bDsNdTtyUpQX4dKwqqftX5KB")
                .server("https://parseapi.back4app.com")
                .build()
        );

    }
}

