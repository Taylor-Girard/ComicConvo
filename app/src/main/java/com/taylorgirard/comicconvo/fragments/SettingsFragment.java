package com.taylorgirard.comicconvo.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.tools.TimeUtility;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;

/**Fragment where user can set notifications on or off and change do not disturb times*/

public class SettingsFragment extends Fragment {

    public static final String TAG = "SettingsFragment";

    Switch swNotifications;
    ImageButton btnChangeStart;
    ImageButton btnChangeEnd;
    Button btnClear;
    TextView tvTimeStart;
    TextView tvTimeEnd;
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
        btnChangeStart = view.findViewById(R.id.btnChangeStart);
        btnChangeEnd = view.findViewById(R.id.btnChangeEnd);
        tvTimeStart = view.findViewById(R.id.tvTimeStart);
        tvTimeEnd = view.findViewById(R.id.tvTimeEnd);
        btnClear = view.findViewById(R.id.btnClear);

        user = ParseUser.getCurrentUser();

        if (user.getNumber("StartDND") != null && !user.getNumber("StartDND").equals("")){
            int timeStart = user.getNumber("StartDND").intValue();
            tvTimeStart.setText(Integer.toString(TimeUtility.UTCtoDevice(timeStart)));
        } else {
            tvTimeStart.setText("Set a start time!");
        }
        if (user.getNumber("EndDND") != null){
            int timeEnd = user.getNumber("EndDND").intValue();
            tvTimeEnd.setText(Integer.toString(TimeUtility.UTCtoDevice(timeEnd)));
        } else {
            tvTimeEnd.setText("Set an end time!");
        }

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.put("StartDND", "");
                user.put("EndDND", "");
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            Log.i(TAG, "Successfully cleared DND times");
                        } else{
                            Log.e(TAG, "Error clearing DND times", e);
                        }
                    }
                });
                tvTimeStart.setText("Set a start time!");
                tvTimeEnd.setText("Set an end time!");
            }
        });

        btnChangeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        tvTimeStart.setText(hourOfDay + "");
                        if (tvTimeEnd.getText().equals("Set an end time!")){
                            Toast.makeText(getContext(), "Set end time to set up Do Not Disturb!", Toast.LENGTH_SHORT).show();
                        }
                        int intStart = TimeUtility.deviceToUTC(hourOfDay);
                        user.put("StartDND", intStart);
                        user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                Log.i(TAG, "Successfully saved start time");
                            } else {
                                Log.e(TAG, "Error saving start time", e);
                            }
                        }
                    });
                    }
                }, new Date().getHours(), new Date().getMinutes(), true);
                timePickerDialog.enableMinutes(false);
                timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
            }
        });

        btnChangeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        tvTimeEnd.setText(hourOfDay + "");
                        if (tvTimeStart.getText().equals("Set a start time!")){
                            Toast.makeText(getContext(), "Set start time to set up Do Not Disturb!", Toast.LENGTH_SHORT).show();
                        }
                        int intEnd = TimeUtility.deviceToUTC(hourOfDay);
                        user.put("EndDND", intEnd);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null){
                                    Log.i(TAG, "Successfully saved end time");
                                } else {
                                    Log.e(TAG, "Error saving end time", e);
                                }
                            }
                        });
                    }
                }, new Date().getHours(), new Date().getMinutes(), true);
                timePickerDialog.enableMinutes(false);
                timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
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