package com.taylorgirard.comicconvo.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.tools.TimeUtility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class SettingsFragment extends Fragment {

    public static final String TAG = "SettingsFragment";

    Switch swNotifications;
    Button btnChangeTime;
    EditText etTimeStart;
    EditText etTimeEnd;
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
        btnChangeTime = view.findViewById(R.id.btnChangeTime);
        etTimeStart = view.findViewById(R.id.etTimeStart);
        etTimeEnd = view.findViewById(R.id.etTimeEnd);

        user = ParseUser.getCurrentUser();

        if (user.getNumber("StartDND") != null && user.getNumber("EndDND") != null){
            int timeStart = user.getNumber("StartDND").intValue();
            int timeEnd = user.getNumber("EndDND").intValue();
            etTimeStart.setText(Integer.toString(TimeUtility.UTCtoDevice(timeStart)));
            etTimeEnd.setText(Integer.toString(TimeUtility.UTCtoDevice(timeEnd)));
        }

        btnChangeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int startTime = Integer.parseInt(etTimeStart.getText().toString());
                    int endTime = Integer.parseInt(etTimeEnd.getText().toString());
                    if (startTime < 0 || endTime >= 24 || startTime == endTime){
                        throw new Exception();
                    }

                    int intStart = TimeUtility.deviceToUTC(startTime);
                    int intEnd = TimeUtility.deviceToUTC(endTime);

                    user.put("StartDND", intStart);
                    user.put("EndDND", intEnd);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                Log.i(TAG, "Successfully saved DND times");
                            } else {
                                Log.e(TAG, "Error saving DND times", e);
                            }
                        }
                    });

                } catch(Exception e){
                    Toast.makeText(getContext(), "Enter valid hours (24 hour format)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Boolean notifications = false;
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