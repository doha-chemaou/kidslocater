package com.example.localiser;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.example.localiser.Models.CircleZone;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class Maisonn extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "ecole";
    private GoogleMap mMap;
    private double lastLat, lastLong;
    private Marker m;
    private float Ecole_Radius = 100;
    private Circle c ;
    private Button ok;
    private FirebaseDatabase rootNode;
    private DatabaseReference zones;
    private LatLng ecole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecole);
        Bundle extras = getIntent().getExtras();
        lastLat= (double) extras.get("lat");
        lastLong= (double) extras.get("long");
        String apiKey = "AIzaSyBsUThhZvSWSEuWbPV4aEcBkW-4MovLPvo";
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        rootNode = FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com");
        zones = rootNode.getReference("circleZones");
        ok = (Button)findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ecole != null){
                    new AlertDialog.Builder(Maisonn.this)
                            .setTitle("La position de la maison")
                            .setMessage("Vous Confirmer la position de la maison ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Log.d(TAG, "onClick: yes");
                                    zones.child("maison").setValue(new CircleZone(ecole.latitude,ecole.longitude,Ecole_Radius));
                                }})
                            .setNegativeButton("Non", null).show();
                }else{
                    new AlertDialog.Builder(Maisonn.this)
                            .setTitle("La position de la maison")
                            .setMessage("Veuillez séléctionner une position en cliquant sur la map")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }})
                            .show();
                }
            }
        });
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS,Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getAddress() + ", " + place.getId());
                Log.i(TAG, "onPlaceSelected: "+place.getLatLng());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mape);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(m!=null){
                    m.remove();
                }
                if(c!=null){
                    c.remove();
                }
                m=mMap.addMarker(new MarkerOptions().position(latLng).title("Maison"));
                ecole= m.getPosition();
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latLng);
                circleOptions.radius(Ecole_Radius);
                circleOptions.strokeColor(Color.argb(255, 127, 255,0));
                circleOptions.fillColor(Color.argb(64, 0, 255,0));
                circleOptions.strokeWidth(4);

                c= mMap.addCircle(circleOptions);
            }
        });
        LatLng emp = new LatLng(lastLat, lastLong);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(emp, 15.0f));
    }
}