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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.BitmapResource;
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
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.taylorgirard.comicconvo.R;
import com.taylorgirard.comicconvo.activities.AddPinActivity;
import com.taylorgirard.comicconvo.activities.MainActivity;
import com.taylorgirard.comicconvo.models.Pin;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment{

    public static final int REQUEST_GETMYLOCATION = 0;
    public static final String[] PERMISSION_GETMYLOCATION = new String[]{"android.permission.ACCESS_FINE_LOCATION","android.permission.ACCESS_COARSE_LOCATION"};
    public static final int DEFAULT_ZOOM = 200;
    public static final String TAG = "MapFragment";

    ImageButton ibAddPin;
    EditText etPinRadius;
    ImageButton ibRadiusSubmit;
    CheckBox cbStore;
    CheckBox cbConvention;
    CheckBox cbMeetup;

    LatLng userPos;
    int radius = 0;

    GoogleMap map;

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
                map = googleMap;
                if  (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(PERMISSION_GETMYLOCATION, REQUEST_GETMYLOCATION);
                } else{
                    getPositionAndSetUp();
                }

            }
        });
        // Return view
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ibAddPin = view.findViewById(R.id.ibAddPin);
        ibRadiusSubmit = view.findViewById(R.id.ibRadiusSubmit);
        etPinRadius = view.findViewById(R.id.etPinRadius);
        cbStore = view.findViewById(R.id.cbStore);
        cbConvention = view.findViewById(R.id.cbConvention);
        cbMeetup = view.findViewById(R.id.cbMeetup);

        cbStore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                addPins(radius, userPos);
            }
        });

        cbConvention.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                addPins(radius, userPos);
            }
        });

        cbMeetup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                addPins(radius, userPos);
            }
        });

        ibRadiusSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    radius = Integer.parseInt(etPinRadius.getText().toString());
                    addPins(radius, userPos);
                } catch(Exception e){
                    Toast.makeText(getContext(), "Enter a valid radius", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ibAddPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddPinActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getPositionAndSetUp();
        }
    }

    @SuppressLint("MissingPermission")
    public void getPositionAndSetUp(){
        Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                userPos = new LatLng(location.getLatitude(), location.getLongitude());
                setUpMap();
            }
        });

    }

    @SuppressLint("MissingPermission")
    public void setUpMap(){

        map.setMyLocationEnabled(true);
        addPins(radius, userPos);

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userPos, DEFAULT_ZOOM);
        map.moveCamera(update);

    }

    public void updateFilterList (CheckBox check, String tagName, List<ParseQuery<Pin>> filters){

        if (check.isChecked()){
            ParseQuery<Pin> filterStore = ParseQuery.getQuery(Pin.class);
            filterStore.whereEqualTo("Tag", tagName);
            filters.add(filterStore);
        }

    }


    public void addPins(int radius, LatLng userPos){
        map.clear();
        ParseQuery<Pin> query;
        List<ParseQuery<Pin>> filterList = new ArrayList<ParseQuery<Pin>>();
        updateFilterList(cbStore, "Store", filterList);
        updateFilterList(cbConvention, "Convention", filterList);
        updateFilterList(cbMeetup, "Meetup", filterList);

        if (filterList.size() > 0){
            query = ParseQuery.or(filterList);
        } else {
            query = new ParseQuery<Pin>(Pin.class);
        }

        if (radius != 0){
            ParseGeoPoint userLocation = new ParseGeoPoint(userPos.latitude, userPos.longitude);
            query.whereWithinMiles("Location", userLocation, radius);
        }

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

                    String tag = pin.getString("Tag");
                    BitmapDescriptor icon;

                    switch (tag){
                        case "Store":
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.storeicon);
                            break;
                        case "Convention":
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.conventionicon);
                            break;
                        case "Meetup":
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.meetupicon);
                            break;
                        default:
                            Log.i(TAG, "Issue with setting icon");
                            icon = BitmapDescriptorFactory.fromResource(R.drawable.defaulticon);
                            break;

                    }

                    map.addMarker(new MarkerOptions().position(position).title(title).snippet(description).icon(icon));

                    map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

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

}