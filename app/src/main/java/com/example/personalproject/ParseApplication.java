package com.example.personalproject;

import android.app.Application;

import com.example.personalproject.models.Item;
import com.example.personalproject.models.Transaction;
import com.example.personalproject.models.UserMeasurement;
import com.parse.Parse;
import com.parse.ParseObject;


public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Item.class);
        ParseObject.registerSubclass(UserMeasurement.class);
        ParseObject.registerSubclass(Transaction.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.parse_application_id))
                .clientKey(getString(R.string.parse_client_key))
                .server("https://parseapi.back4app.com")
                .build()
        );

    }
}

