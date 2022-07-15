package com.taylorgirard.comicconvo.tools;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.taylorgirard.comicconvo.models.Comic;
import com.taylorgirard.comicconvo.models.Message;
import com.taylorgirard.comicconvo.models.Pin;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Comic.class);
        ParseObject.registerSubclass(Pin.class);
        ParseObject.registerSubclass(Message.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("ImIcGutoZYOh1JNfJpE8PtDkWDOST0hH7Hgw6W0f")
                .clientKey("dBslTewLkr3AzdXkVtG0bgmzY2hj73M9Z5qkGJhy")
                .server("https://parseapi.back4app.com")
                .build()
        );

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "389146853598");
        installation.saveInBackground();
    }
}
