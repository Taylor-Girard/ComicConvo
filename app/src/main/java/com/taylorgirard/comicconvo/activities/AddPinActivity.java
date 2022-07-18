package com.taylorgirard.comicconvo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.BuildConfig;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.models.Pin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

public class AddPinActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "AddPinActivity";
    public static final int MEETUP_TAG = 2;
    public static final String GOOGLE_GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json?key=" + BuildConfig.MAPS_API_KEY;

    EditText etPinAddress;
    EditText etPinTitle;
    EditText etPinDescription;
    Button btnSubmitPin;
    Spinner spTag;
    String pinTag = "Store";
    ParseUser match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pin);

        etPinAddress = findViewById(R.id.etPinAddress);
        etPinTitle = findViewById(R.id.etPinTitle);
        etPinDescription = findViewById(R.id.etPinDescription);
        btnSubmitPin = findViewById(R.id.btnSubmitPin);
        spTag = findViewById(R.id.spTag);

        match = getIntent().getParcelableExtra("Match");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tag_list, android.R.layout.simple_spinner_dropdown_item);
        spTag.setAdapter(adapter);
        spTag.setOnItemSelectedListener(this);

        if (match != null){
            etPinTitle.setText("Meetup with " + match.getUsername());
            etPinDescription.setText("Meeting up with " + match.getUsername());
            spTag.setSelection(MEETUP_TAG);
        }

        btnSubmitPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = etPinAddress.getText().toString();
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("address", query);
                client.get(GOOGLE_GEOCODING_URL, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.d(TAG,"onSuccess");
                        JSONObject jsonObject = json.jsonObject;
                        try {
                            JSONArray results = jsonObject.getJSONArray("results");
                            Log.i(TAG, "Results" + results.toString());
                            if (results.length() == 0){
                                Toast.makeText(AddPinActivity.this, "Invalid address. Make sure to include city and state abbreviation", Toast.LENGTH_SHORT).show();
                            } else if (etPinTitle.getText().toString().length() == 0){
                                Toast.makeText(AddPinActivity.this, "Pin must have a title", Toast.LENGTH_SHORT).show();
                            } else if (etPinDescription.getText().toString().length() == 0){
                                Toast.makeText(AddPinActivity.this, "Pin must have a description", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                JSONObject addressComponents = results.getJSONObject(0);
                                JSONObject geometry = addressComponents.getJSONObject("geometry");
                                JSONObject location = geometry.getJSONObject("location");
                                Double lat = location.getDouble("lat");
                                Double lng = location.getDouble("lng");

                                Pin pin = new Pin();
                                pin.put("Title", etPinTitle.getText().toString());
                                pin.put("Description", etPinDescription.getText().toString());
                                ParseGeoPoint point = new ParseGeoPoint(lat, lng);
                                pin.put("Location", point);
                                pin.put("Tag", pinTag);
                                pin.put("Author", ParseUser.getCurrentUser());
                                pin.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Log.i(TAG, "Successfully saved pin");
                                    }
                                });
                                Intent intent = new Intent(AddPinActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure " + statusCode);
                    }
                });
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        pinTag = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}