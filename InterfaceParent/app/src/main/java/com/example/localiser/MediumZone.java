package com.example.localiser;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MediumZone extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener/*, GeoQueryEventListener */{
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private FirebaseDatabase rootNode;
    private DatabaseReference currentLocalisation;
    private DatabaseReference geoLocation;
    private DatabaseReference zones;
    private ArrayList<Circle> circles = new ArrayList<>();
    //private GeofencingClient geofencingClient;
    private float GEOFENCE_RADIUS = 200;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    double lastLat, lastLong;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private ArrayList<CircleZone> circleZones =  new ArrayList<>();
    private Marker m;
    private Button remove;
    private Button back;
    private Button tuto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        remove = (Button) findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                removeZones();

            }
        });
        tuto = (Button) findViewById(R.id.back);
        tuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Toast t=  Toast.makeText(getApplicationContext(),"Pour ajouter une zone, Vous devez cliquer durent 2 seconds dans la map\n"+
                        "Pour Supprimer une zone il suffit de cliquer dessus !",Toast.LENGTH_LONG);
              t.setGravity(Gravity.CENTER,0,0);
              t.show();
            }
        });
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MediumZone.this, LocaliserMonEnfant.class);
                MediumZone.this.startActivity(myIntent);
            }
        });
        rootNode = FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com");
        currentLocalisation = rootNode.getReference("currentLocalisation");
        zones = rootNode.getReference("circleZones");
        zones.child("medium").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                if(m != null){
                    m.remove();
                    Log.e(TAG, "onDataChange: "+pos.longitude  );
                    m=mMap.addMarker(new MarkerOptions().position(pos));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15.0f));
                }
               /* settingGeoFire(p.getLatitude(), p.getLongtitude());*/
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //geofencingClient = LocationServices.getGeofencingClient(this);
        //geofenceHelper = new GeofenceHelper(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //while (circleZones.isEmpty())

        // Add a marker in Sydney and move the camera
        LatLng pos = new LatLng(lastLat, lastLong);
        m=mMap.addMarker(new MarkerOptions().position(pos));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15.0f));
        enableUserLocation();

        mMap.setOnMapLongClickListener(this);
        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {

                updateZones(circle);
            }
        });
    }
    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           // mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
               // mMap.setMyLocationEnabled(true);
            } else {
                //We do not have the permission..

            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onMapLongClick(LatLng latLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(latLng);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        } else {
            handleMapLongClick(latLng);
        }
    }

    private void handleMapLongClick(LatLng latLng) {
        for(Circle c : circles){
            if(c.getCenter().latitude == latLng.latitude && c.getCenter().longitude == latLng.longitude)
                return;
        }
        // push to dataBase
        addQerry(latLng,GEOFENCE_RADIUS);
    }

    private void addQerry(LatLng latLng, float radius) {
        zones.child("medium").push().setValue(new CircleZone(latLng.latitude,latLng.longitude,radius));
        /*geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude,latLng.longitude),(double) radius/1000);
        geoQuery.addGeoQueryEventListener(MapsActivity.this);*/
        addCircle(latLng, GEOFENCE_RADIUS);
    }

    private void addQerries() {
        //if(circleZones == null || circleZones.isEmpty()) getCircleZones();
        //Log.d(TAG, "addQerries: "+circleZones.size());
        //if(geoQuery!=null) geoQuery.removeAllListeners();
        //else Log.d(TAG, "addQerries: ");
        if(!circles.isEmpty())
        for(Circle circle:circles )  circle.remove();
        for(CircleZone c : circleZones){
          //  Log.d(TAG, "addQerries: "+c.getMetter()+ " "+c.getLatitude()+" "+c.getLongitude());
            //    GeoLocation loc = new GeoLocation(c.getLatitude(),c.getLongitude());
          //  Log.d(TAG, "addQerries: "+loc.toString());
            //    geoQuery = geoFire.queryAtLocation(loc,(double) c.getMetter()/1000);
            //     geoQuery.addGeoQueryEventListener(MapsActivity.this);
            addCircle(new LatLng(c.getLatitude(),c.getLongitude()),c.getMetter());
        }
    }

    private void removeZones() {
        Query removeCircle = zones.child("medium");
        removeCircle.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
                // geoQuery.removeAllListeners();
               /* for(CircleZone c : circleZones){
                    GeoLocation loc = new GeoLocation(c.getLatitude(),c.getLongitude());
                    geoFire.queryAtLocation(loc,(double) c.getMetter()/1000);
                    geoQuery.addGeoQueryEventListener(MapsActivity.this);
                }*/
                /*for(CircleZone cZ : circleZones)
                    circleZones.remove(cZ);*/

                for(Circle c : circles)

                { //circles.remove(c);
                    c.remove();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void updateZones(Circle circle) {
        Query removeCircle = zones.child("medium").orderByChild("latitude").equalTo(circle.getCenter().latitude);
        removeCircle.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
                // geoQuery.removeAllListeners();
               /* for(CircleZone c : circleZones){
                    GeoLocation loc = new GeoLocation(c.getLatitude(),c.getLongitude());
                    geoFire.queryAtLocation(loc,(double) c.getMetter()/1000);
                    geoQuery.addGeoQueryEventListener(MapsActivity.this);
                }*/
                /*for(CircleZone cZ : circleZones) if(cZ.getLongitude() == circle.getCenter().longitude && cZ.getLongitude() == circle.getCenter().latitude)
                    circleZones.remove(cZ);

                for(Circle c : circles)
                    if(c.getCenter().latitude == circle.getCenter().latitude && c.getCenter().longitude == circle.getCenter().longitude)
                        circles.remove(c);*/
                circle.remove();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

    }

   /* private void settingGeoFire(double lat,double lon){
        geoLocation = rootNode.getReference("geoLocation");
        geoFire=new GeoFire(geoLocation);
        geoFire.setLocation("firebase-hq", new GeoLocation(lat, lon));

    }*/

    private void addCircle(LatLng latLng, float radius) {

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

    }
