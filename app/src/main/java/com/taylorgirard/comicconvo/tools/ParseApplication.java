package com.taylorgirard.comicconvo.tools;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.taylorgirard.comicconvo.models.Comic;

import okhttp3.OkHttpClient;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Comic.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("ImIcGutoZYOh1JNfJpE8PtDkWDOST0hH7Hgw6W0f")
                .clientKey("dBslTewLkr3AzdXkVtG0bgmzY2hj73M9Z5qkGJhy")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
