package com.example.testlocalisation;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testlocalisation.Models.CircleZone;
import com.example.testlocalisation.Models.NotificationBody;
import com.example.testlocalisation.Models.NotificationRoot;
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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Maison implements GeoQueryEventListener {
    private static final String TAG = "Zones";
    FirebaseDatabase   rootNode=FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com");;
    DatabaseReference geoLocation;
    DatabaseReference zones;
    DatabaseReference Token;
    DatabaseReference currentLocalisation;
    private boolean dansLaMaison=true;

    private boolean sended=false;
 /*   private boolean inMaison = false;*/
   /* private boolean notificationSended = false;*/
    /*double lati,log;*/
/*
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private RequestQueue requestQueue;

*/
String token;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private String url = "https://fcm.googleapis.com/";
    private CircleZone circleZones;

    private void addQerries() {

        //if(circleZones == null || circleZones.isEmpty()) getCircleZones();
        if(geoQuery!=null) geoQuery.removeAllListeners();
        /*if(geoQuery==null) settingGeoFire();*/

            if(circleZones!=null){
                GeoLocation loc = new GeoLocation(circleZones.getLatitude(),circleZones.getLongitude());
                geoQuery = geoFire.queryAtLocation(loc,(double) circleZones.getMetter()/1000);
                geoQuery.addGeoQueryEventListener(Maison.this);
            }




    }

    public boolean isSended() {
        return sended;
    }

    public boolean isDansLaMaison() {
        return dansLaMaison;
    }

    public void setSended(boolean sended) {
        this.sended = sended;
    }

    public void setDansLaMaison(boolean dansLaMaison) {
        this.dansLaMaison = dansLaMaison;
    }

    /* public boolean isInMaison() {
                return inMaison;
            }

            public boolean getStatus(){
                return sended;
            }
            public void setStatus(boolean s){
                sended=s;
            }*/
    public void settingGeoFire(double lati, double log){
        geoLocation = rootNode.getReference("geoLocation");
        geoFire=new GeoFire(geoLocation);
        geoFire.setLocation("firebase-hq", new GeoLocation(lati, log));
    }
    public void initGeoZones(){
        zones = rootNode.getReference("circleZones");
        zones.child("maison").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                circleZones=snapshot.getValue(CircleZone.class);
                Log.d(TAG, "onDataChange: maison");
                zones.child("dansLaMaison").setValue("non");
                if(geoQuery!=null) geoQuery.removeAllListeners();
                GeoLocation loc = new GeoLocation(circleZones.getLatitude(),circleZones.getLongitude());
                geoQuery = geoFire.queryAtLocation(loc,(double) circleZones.getMetter()/1000);
                geoQuery.addGeoQueryEventListener(Maison.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

     /*   */
        /*zones.child("maison").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildAdded: ");
                circleZones = snapshot.getValue(CircleZone.class);
                if(geoQuery!=null) geoQuery.removeAllListeners();
                    GeoLocation loc = new GeoLocation(circleZones.getLatitude(),circleZones.getLongitude());
                    geoQuery = geoFire.queryAtLocation(loc,(double) circleZones.getMetter()/1000);
                    geoQuery.addGeoQueryEventListener(Maison.this);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }
   
    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        zones.child("dansLaMaison").setValue("oui");
        Log.d(TAG, "notifonKeyEntered: maison entrer "+key);
        NotifyParent("Votre fils a rentrer à la maison");

        Log.d(TAG, "notifonKeyEntered: "+this.isDansLaMaison());
    }
    public void notifyNotInMaison(){
        this.setSended(true);
        Log.d(TAG, "notifyNotInMaison: ");
        NotifyParent("Votre enfant n'a pas encore rentrer a la maison !");

    }
    private void NotifyParent(String msg) {
       Token = rootNode.getReference("NotificationToken");

        Token.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                token =  snapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: "+token);
                NotificationBody data = new NotificationBody("Information",msg);
                NotificationRoot root = new NotificationRoot(data,token);
                Gson json = new Gson();
                String requestBody = json.toJson(root);
                Log.d(TAG, "NotifyParent: root "+ requestBody);
                Retrofit r = new Retrofit.Builder().baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                /*Log.d(TAG, "NotifyParent: "+r.toString());*/
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
        zones.child("dansLaMaison").setValue("non");
        this.setSended(true);
        Log.d(TAG, "notifyNotInMaison: ");
        NotifyParent("Votre enfant n'a pas encore rentrer a la maison !");
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Log.d(TAG, "onKeyMoved: ");
        /*this.setDansLaMaison(true);*/
       /* handler = true;*/
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
