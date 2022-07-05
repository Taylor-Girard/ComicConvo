package com.taylorgirard.comicconvo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;
import com.taylorgirard.comicconvo.BuildConfig;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.models.Comic;
import com.taylorgirard.comicconvo.models.Pin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Properties;

import okhttp3.Headers;

public class AddPinActivity extends AppCompatActivity {

    public static final String TAG = "AddPinActivity";
    public static final String GOOGLE_GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json?key=" + BuildConfig.MAPS_API_KEY;


    EditText etPinAddress;
    EditText etPinTitle;
    EditText etPinDescription;
    Button btnSubmitPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pin);

        etPinAddress = findViewById(R.id.etPinAddress);
        etPinTitle = findViewById(R.id.etPinTitle);
        etPinDescription = findViewById(R.id.etPinDescription);
        btnSubmitPin = findViewById(R.id.btnSubmitPin);

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
                            JSONObject addressComponents = results.getJSONObject(0);
                            Log.i(TAG, "Address components" + addressComponents.toString());
                            JSONObject geometry = addressComponents.getJSONObject("geometry");
                            Log.i(TAG, "Geometry" + geometry.toString());
                            JSONObject location = geometry.getJSONObject("location");
                            Log.i(TAG, "Location" + location.toString());
                            Double lat = location.getDouble("lat");
                            Log.i(TAG, "Lat" + lat);
                            Double lng = location.getDouble("lng");
                            Log.i(TAG, "Lng" + lng);

                            Pin pin = new Pin();
                            pin.put("Title", etPinTitle.getText().toString());
                            pin.put("Description", etPinDescription.getText().toString());
                            ParseGeoPoint point = new ParseGeoPoint(lat, lng);
                            pin.put("Location", point);
                            pin.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Log.i(TAG, "Successfully saved pin");
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure " + statusCode);
                    }
                });

                Intent intent = new Intent(AddPinActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}