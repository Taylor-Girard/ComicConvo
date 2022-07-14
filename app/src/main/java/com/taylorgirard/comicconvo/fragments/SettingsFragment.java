package com.taylorgirard.comicconvo.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.R;

public class SettingsFragment extends Fragment {

    public static final String TAG = "SettingsFragment";

    Switch swNotifications;
    ParseUser user;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swNotifications = view.findViewById(R.id.swNotifications);

        user = ParseUser.getCurrentUser();
        Boolean notifications = null;
        try {
            notifications = user.fetchIfNeeded().getBoolean("Notifications");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        swNotifications.setChecked(notifications);
        swNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.put("Notifications", swNotifications.isChecked());
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            Log.i(TAG, "Successfully saved user notifications");
                        } else{
                            Log.e(TAG, "Error saving user notifications", e);
                        }
                    }
                });
            }
        });
    }
}