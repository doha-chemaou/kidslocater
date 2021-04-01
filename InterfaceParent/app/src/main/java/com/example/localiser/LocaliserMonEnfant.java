package com.example.localiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.localiser.Models.CircleZone;
import com.example.localiser.Models.Position;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LocaliserMonEnfant extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "Localiser";
    private GoogleMap mMap;
    private FirebaseDatabase rootNode;
    private DatabaseReference currentLocalisation;
    private DatabaseReference geoLocation;
    private DatabaseReference zones;
    private DatabaseReference zonesMedium;
    private ArrayList<Circle> circles = new ArrayList<>();
    private double lastLat, lastLong;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private ArrayList<CircleZone> circleZones =  new ArrayList<>();
    private ArrayList<CircleZone> circleZonesMedium =  new ArrayList<>();
    private Marker m;
    private Button mediumZones;
    private Button dangerZones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localiser_mon_enfant);
        setActivity();
    }
    public void setActivity(){
        rootNode = FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com");
        currentLocalisation = rootNode.getReference("currentLocalisation");
        zones = rootNode.getReference("circleZones");
        zones.child("danger").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    ArrayList<CircleZone> list=new ArrayList<>();
                    for(DataSnapshot cr:task.getResult().getChildren()){
                        CircleZone c = cr.getValue(CircleZone.class);
                        list.add(c);
                    }
                    circleZones=list;
                    addQerries();
                }
            }
        });
        mediumZones = (Button) findViewById(R.id.mediumZones);
        mediumZones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LocaliserMonEnfant.this, MediumZone.class);
                LocaliserMonEnfant.this.startActivity(myIntent);
            }
        });
        dangerZones = (Button) findViewById(R.id.dangerZones);
        dangerZones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LocaliserMonEnfant.this, DangerZone.class);
                LocaliserMonEnfant.this.startActivity(myIntent);
            }
        });
        zonesMedium = rootNode.getReference("circleZones");
        zonesMedium.child("medium").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    ArrayList<CircleZone> list=new ArrayList<>();
                    for(DataSnapshot cr:task.getResult().getChildren()){
                        CircleZone c = cr.getValue(CircleZone.class);
                        list.add(c);
                    }
                    circleZonesMedium=list;
                    addQerriesMedium();
                }
            }
        });
        currentLocalisation.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Position p = task.getResult().getValue(Position.class);
                    lastLat = p.getLatitude();
                    lastLong = p.getLongtitude();
                }
            }
        });

        currentLocalisation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Position p = snapshot.getValue(Position.class);
                LatLng pos = new LatLng(p.getLatitude(), p.getLongtitude());
                if(m!=null)
                m.remove();
                m=mMap.addMarker(new MarkerOptions().position(pos));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15.0f));
                /*settingGeoFire(p.getLatitude(), p.getLongtitude());*/
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    private void addQerriesMedium() {

        for(CircleZone c : circleZonesMedium){
            addCircleMedium(new LatLng(c.getLatitude(),c.getLongitude()),c.getMetter());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void addCircleMedium(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 140,0));
        circleOptions.fillColor(Color.argb(64, 255, 165,0));
        circleOptions.strokeWidth(4);
        circleOptions.clickable(true);

        Circle c= mMap.addCircle(circleOptions);
        circles.add(c);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //while (circleZones.isEmpty())
        // Add a marker in Sydney and move the camera
        LatLng pos = new LatLng(lastLat, lastLong);
        m=mMap.addMarker(new MarkerOptions().position(pos));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15.0f));


    }



    private void addQerries() {

        for(CircleZone c : circleZones){
            addCircle(new LatLng(c.getLatitude(),c.getLongitude()),c.getMetter());
        }
    }

    private void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions()
        .center(latLng)
        .strokeColor(Color.argb(255, 255, 0,0))
        .fillColor(Color.argb(64, 255, 0,0))
        .strokeWidth(4)
        .clickable(true)
        .radius(radius);
        Log.d(TAG, "addCircle: red");
        Circle c= mMap.addCircle(circleOptions);
        circles.add(c);
    }

    /*private void settingGeoFire(double latitude, double longtitude) {
        geoLocation = rootNode.getReference("geoLocation");
        geoFire=new GeoFire(geoLocation);
        geoFire.setLocation("firebase-hq", new GeoLocation(latitude, longtitude));
    }*/
}