package com.taylorgirard.comicconvo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.models.Message;

public class IndividualMessageActivity extends AppCompatActivity {

    public static final String TAG = "IndividualMessageActivity";

    TextView tvSampleText;
    EditText etMessage;
    ImageButton ibSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_message);

        tvSampleText = findViewById(R.id.tvSampleText);
        etMessage = findViewById(R.id.etMessage);
        ibSend = findViewById(R.id.ibSend);

        ParseUser match = getIntent().getParcelableExtra("Match");

        tvSampleText.setText(match.getUsername());

        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = etMessage.getText().toString();
                Message message = new Message();
                message.put("Body", body);
                message.put("Receiver", match);
                message.put("Sender", ParseUser.getCurrentUser());
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e(TAG, "Error saving message", e);
                        } else{
                            Log.d(TAG, "Successfully saved message");
                        }
                    }
                });
                etMessage.setText("");
            }
        });
    }
}