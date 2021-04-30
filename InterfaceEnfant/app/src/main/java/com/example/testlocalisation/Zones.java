package com.example.testlocalisation;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testlocalisation.Models.CircleZone;
import com.example.testlocalisation.Models.NotificationBody;
import com.example.testlocalisation.Models.NotificationRoot;
import com.example.testlocalisation.Models.Position;
import com.example.testlocalisation.Models.RetrofitCaller;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Zones implements GeoQueryEventListener {
    private static final String TAG = "Zones";
    FirebaseDatabase   rootNode=FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com");;
    DatabaseReference geoLocation;
    DatabaseReference zones;
    DatabaseReference Token;
    DatabaseReference currentLocalisation;
    /*double lati,log;*/
/*
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private RequestQueue requestQueue;

*/
String token;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private String url = "https://fcm.googleapis.com/";
    private ArrayList<CircleZone> circleZones =  new ArrayList<>();
    private void addQerries() {

        //if(circleZones == null || circleZones.isEmpty()) getCircleZones();
        if(geoQuery!=null) geoQuery.removeAllListeners();
        /*if(geoQuery==null) settingGeoFire();*/
        for(CircleZone c : circleZones){

            GeoLocation loc = new GeoLocation(c.getLatitude(),c.getLongitude());
            geoQuery = geoFire.queryAtLocation(loc,(double) c.getMetter()/1000);
            geoQuery.addGeoQueryEventListener(Zones.this);

        }

    }
    public void settingGeoFire(double lati, double log){
       /* currentLocalisation = rootNode.getReference("currentLocalisation");
        currentLocalisation.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Position p = task.getResult().getValue(Position.class);
                    log = p.getLongtitude();
                    lati=p.getLongtitude();
                }
            }
        });*/
        geoLocation = rootNode.getReference("geoLocation");
        geoFire=new GeoFire(geoLocation);
        geoFire.setLocation("firebase-hq", new GeoLocation(lati, log));

    }
    public void initGeoZones(){
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
        zones.child("danger").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildAdded: ");
                CircleZone z = snapshot.getValue(CircleZone.class);
                circleZones.add(z);
                if(geoQuery!=null) geoQuery.removeAllListeners();
                for(CircleZone c : circleZones){
                    GeoLocation loc = new GeoLocation(c.getLatitude(),c.getLongitude());
                    geoQuery = geoFire.queryAtLocation(loc,(double) c.getMetter()/1000);
                    geoQuery.addGeoQueryEventListener(Zones.this);
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                /*Log.d(TAG, "onChildRemoved: "+circleZones.size());*/
                CircleZone z = snapshot.getValue(CircleZone.class);
                try{

                for(CircleZone e: circleZones){
                    if(e.getLatitude()==z.getLatitude() && e.getLongitude()==z.getLongitude()) circleZones.remove(e);
                }
                /*Log.d(TAG, "onChildRemoved: "+circleZones.size());*/
                if(geoQuery!=null) geoQuery.removeAllListeners();
                for(CircleZone c : circleZones){
                    GeoLocation loc = new GeoLocation(c.getLatitude(),c.getLongitude());
                    geoQuery = geoFire.queryAtLocation(loc,(double) c.getMetter()/1000);
                    geoQuery.addGeoQueryEventListener(Zones.this);
                }

                }catch(Exception e){

                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
   
    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        Log.d(TAG, "onKeyEntered: entrer "+key);
        NotifyParent();
    }

    private void NotifyParent() {
       Token = rootNode.getReference("NotificationToken");

        Token.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                token =  snapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: "+token);
                NotificationBody data = new NotificationBody("Danger","Votre fils a entrer dans une zone qui est trés dangereuse");
                NotificationRoot root = new NotificationRoot(data,token);
                Gson json = new Gson();
                String requestBody = json.toJson(root);
                Log.d(TAG, "NotifyParent: root "+ requestBody);
                Retrofit r = new Retrofit.Builder().baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                Log.d(TAG, "NotifyParent: "+r.toString());
                RetrofitCaller retrofitCaller =r.create(RetrofitCaller.class);
                Call<NotificationRoot> call = retrofitCaller.PostData(root);
                call.enqueue(new Callback<NotificationRoot>() {
                    @Override
                    public void onResponse(Call<NotificationRoot> call, retrofit2.Response<NotificationRoot> response) {
                        Log.d(TAG, "onResponse: "+response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<NotificationRoot> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onKeyExited(String key) {

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {

    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {

    }

  /*  String post(String json) throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + fcm_token)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }*/
}
