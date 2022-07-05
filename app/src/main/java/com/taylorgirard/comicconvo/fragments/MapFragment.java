package com.taylorgirard.comicconvo.fragments;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.activities.AddPinActivity;
import com.taylorgirard.comicconvo.activities.MainActivity;
import com.taylorgirard.comicconvo.models.Pin;

import java.util.List;

public class MapFragment extends Fragment{

    public static final int DEFAULT_ZOOM = 200;
    public static final String TAG = "MapFragment";

    ImageButton ibAddPin;

    GoogleMap googleMap;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        // Initialize view
        View view=inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize map fragment
        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);

        // Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {

                getDeviceLocation(googleMap);
                googleMap.setMyLocationEnabled(true);

                ParseQuery<Pin> query = ParseQuery.getQuery(Pin.class);
                query.findInBackground(new FindCallback<Pin>() {
                    @Override
                    public void done(List<Pin> pins, ParseException e) {
                        if (e != null){
                            Log.e(TAG, "Issue with getting pins", e);
                            return;
                        }

                        for (Pin pin: pins){
                            ParseGeoPoint location = pin.getParseGeoPoint("Location");
                            LatLng position = new LatLng(location.getLatitude(),location.getLongitude());
                            String title = pin.getString("Title");
                            String description = null;
                            try {
                                description = pin.getString("Description") + "\n Posted by " + pin.getParseUser("Author").fetchIfNeeded().getUsername();
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            googleMap.addMarker(new MarkerOptions().position(position).title(title).snippet(description));

                            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                @Override
                                public View getInfoWindow(Marker arg0) {
                                    return null;
                                }

                                @Override
                                public View getInfoContents(Marker marker) {

                                    LinearLayout info = new LinearLayout(getContext());
                                    info.setOrientation(LinearLayout.VERTICAL);

                                    TextView title = new TextView(getContext());
                                    title.setTextColor(Color.BLACK);
                                    title.setGravity(Gravity.CENTER);
                                    title.setTypeface(null, Typeface.BOLD);
                                    title.setText(marker.getTitle());

                                    TextView snippet = new TextView(getContext());
                                    snippet.setTextColor(Color.GRAY);
                                    snippet.setText(marker.getSnippet());

                                    info.addView(title);
                                    info.addView(snippet);

                                    return info;
                                }
                            });
                        }
                    }
                });

            }
        });
        // Return view
        return view;
    }

    @SuppressLint("MissingPermission")
    public void getDeviceLocation(GoogleMap googleMap){

        if  (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM);
                    googleMap.moveCamera(update);
                }
            });
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ibAddPin = view.findViewById(R.id.ibAddPin);

        ibAddPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddPinActivity.class);
                startActivity(intent);
            }
        });
    }
}