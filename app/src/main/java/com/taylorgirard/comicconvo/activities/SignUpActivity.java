package com.taylorgirard.comicconvo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.taylorgirard.comicconvo.R;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = "SignUpActivity";

    EditText etUsername;
    EditText etPassword;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = new ParseUser();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                user.setUsername(username);
                user.setPassword(password);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            //Toast.makeText(SignUpActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            ArrayList<String> channel = new ArrayList<>();
                            channel.add(user.getObjectId());
                            installation.put("channels", channel);
                            installation.saveInBackground();
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else{
                            Log.e(TAG, "Issues with login", e);
                            Toast.makeText(SignUpActivity.this, "Issue with login!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}