package com.example.personalproject;

import android.app.Application;

import com.example.personalproject.models.UserMeasurement;
import com.parse.Parse;
import com.parse.ParseObject;


public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Register your parse models
        ParseObject.registerSubclass(UserMeasurement.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("DouOn8b9GOFYkKiRsxIo8keR39badQgCQgm6BFhi")
                .clientKey("1ucpGCPkprDB2Z5W3Yv8Ec9HYHHFQyBfVmEcU27F")
                .server("https://parseapi.back4app.com")
                .build()
        );

    }
}

