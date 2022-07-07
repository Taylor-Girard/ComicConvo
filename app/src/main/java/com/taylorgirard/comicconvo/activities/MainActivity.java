package com.taylorgirard.comicconvo.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.fragments.MapFragment;
import com.taylorgirard.comicconvo.fragments.MatchesFragment;
import com.taylorgirard.comicconvo.fragments.ProfileFragment;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {

    SmoothBottomBar bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Fragment[] fragment = new Fragment[1];
        fragment[0] = new MapFragment();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                switch (i) {
                    case 0:
                        fragment[0] = new ProfileFragment();
                        break;
                    case 1:
                        fragment[0] = new MatchesFragment();
                        break;
                    case 2:
                        fragment[0] = new MapFragment();
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
