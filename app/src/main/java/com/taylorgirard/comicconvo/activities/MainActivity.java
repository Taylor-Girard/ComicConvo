package com.taylorgirard.comicconvo.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.fragments.MapFragment;
import com.taylorgirard.comicconvo.fragments.MatchesFragment;
import com.taylorgirard.comicconvo.fragments.MessagesFragment;
import com.taylorgirard.comicconvo.fragments.ProfileFragment;
import com.taylorgirard.comicconvo.fragments.SettingsFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

/**Activity that sets up the bottom navigation menu*/

public class MainActivity extends AppCompatActivity {

    ParseUser user = ParseUser.getCurrentUser();
    SmoothBottomBar bottomNavigationView;

    public static final String TAG = "MainActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    final Fragment[] fragment = new Fragment[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Date rightNow = new Date();
        user.put("lastLogin", rightNow);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.i(TAG, "Successfully saved to lastLogin date");
                } else{
                    Log.e(TAG, "Error saving to lastLogin date");
                }
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragment[0] = new MatchesFragment();

        bottomNavigationView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                switch (i) {
                    case 0:
                        fragment[0] = new ProfileFragment();
                        break;
                    case 1:
                        fragment[0] = new MessagesFragment();
                        break;
                    case 2:
                        fragment[0] = new MatchesFragment();
                        break;
                    case 3:
                        fragment[0] = new MapFragment();
                        break;
                    case 4:
                        fragment[0] = new SettingsFragment();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + i);
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment[0]).commit();
                return true;
            }
        });
        //Set default
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment[0]).commit();
    }

}
