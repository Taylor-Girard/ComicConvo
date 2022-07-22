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
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.models.Comic;
import com.taylorgirard.comicconvo.tools.ComicUtility;
import com.taylorgirard.comicconvo.tools.ListType;

import java.util.ArrayList;
import java.util.List;

/**Activity that allows the user to create a new account on Parse with a username and password*/

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
                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            ArrayList<String> channel = new ArrayList<>();
                            channel.add(user.getObjectId());
                            installation.put("channels", channel);
                            installation.saveInBackground();

                            //make sure the default comic values change their popularity score
                            ParseUser currentUser = null;
                            ParseQuery<ParseUser> newUser = ParseUser.getQuery();
                            newUser.whereEqualTo("username", username);
                            try {
                                currentUser = newUser.getFirst();
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }

                            List<Comic> likes = currentUser.getList("Likes");
                            List<Comic> dislikes = currentUser.getList("Dislikes");

                            for (Comic i: dislikes){
                                incrementPopularity(i);
                            }

                            Comic like = likes.get(0);
                            incrementPopularity(like);

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

    public void incrementPopularity(Comic comic){
        ParseQuery<Comic> query = new ParseQuery<Comic>("Comic");
        try {
            Comic existingComic = query.get(comic.getObjectId());
            int timesAdded = existingComic.getInt("timesAdded");
            timesAdded += 1;
            existingComic.put("timesAdded", timesAdded);
            existingComic.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error incrementing timesAdded", e);
                    } else {
                        Log.i(TAG, "Success incrementing timesAdded");
                    }
                }
            });
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }
}