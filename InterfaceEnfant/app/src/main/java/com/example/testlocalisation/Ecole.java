package com.example.testlocalisation;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.testlocalisation.Models.CircleZone;
import com.example.testlocalisation.Models.NotificationBody;
import com.example.testlocalisation.Models.NotificationRoot;
import com.example.testlocalisation.Models.RetrofitCaller;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Ecole implements GeoQueryEventListener {
    private static final String TAG = "Zones";
    FirebaseDatabase   rootNode=FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com");;
    DatabaseReference geoLocation;
    DatabaseReference zones;
    DatabaseReference Token;
    DatabaseReference currentLocalisation;
    private boolean dansEcole=false;
    private String dateLundi="";
    private String dateVendredi="";
    private String dateJeudi="";
    private String dateMercredi="";
    private String dateMardi="";
    private boolean sended=false;
    DatabaseReference ecoleEmplois ;
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
                geoQuery.addGeoQueryEventListener(Ecole.this);
            }




    }

    public boolean isSended() {
        return sended;
    }

    public boolean isDansEcole() {
        return dansEcole;
    }

    public void setSended(boolean sended) {
        this.sended = sended;
    }

    public void setDansEcole(boolean dansLaMaison) {
        this.dansEcole = dansLaMaison;
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
        ecoleEmplois = rootNode.getReference("ecoleEmplois");
        zones = rootNode.getReference("circleZones");
        zones.child("ecole").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                circleZones=snapshot.getValue(CircleZone.class);
                /*Log.d(TAG, "onDataChange: ");*/
                if(geoQuery!=null) geoQuery.removeAllListeners();
                GeoLocation loc = new GeoLocation(circleZones.getLatitude(),circleZones.getLongitude());
                geoQuery = geoFire.queryAtLocation(loc,(double) circleZones.getMetter()/1000);
                geoQuery.addGeoQueryEventListener(Ecole.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ecoleEmplois.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dateLundi=snapshot.child("lundi").getValue(String.class);
                dateMardi=snapshot.child("mardi").getValue(String.class);
                dateMercredi=snapshot.child("mercredi").getValue(String.class);
                dateJeudi=snapshot.child("jeudi").getValue(String.class);
                dateVendredi=snapshot.child("vendredi").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
   
    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        zones.child("dansEcole").setValue("oui");
        /*Log.d(TAG, "notifonKeyEntered: maison entrer "+key);*/
        NotifyParent("Votre fils a rentrer à l'ecole");

        /*Log.d(TAG, "notifonKeyEntered: "+this.isDansEcole());*/
    }
    public void notifyNotInEcole(){
        this.setSended(true);
        NotifyParent("Votre enfant a raté une séance !");

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
    private boolean rater(){
        boolean seance = false;
        Date now = new Date();
        Calendar calNow = Calendar.getInstance();
        calNow.setTime(now);
        switch (calNow.get(calNow.DAY_OF_WEEK)){

            case 1 : //lundi

            //9h22 - 22h30 / ...
            if(dateLundi!=null && !dateLundi.equals("")){
                for(String s : dateLundi.split("/")){
                    Log.d(TAG, "onClick: "+s);
                    if(s.split("-").length == 2){
                        String heureD = s.split("-")[0].split("h")[0];
                        String minuteD ="0";
                        if(s.split("-")[0].split("h").length > 1)
                            minuteD = s.split("-")[0].split("h")[1];
                        /* Log.d(TAG, "onClick: h/m D "+heureD+"/"+minuteD);*/
                        String heureF = s.split("-")[1].split("h")[0];
                        String minuteF ="0";
                        if(s.split("-")[1].split("h").length > 1)
                            minuteF= s.split("-")[1].split("h")[1];           //   13 - 15 =2  >  14-13 =1
                        if((Double.parseDouble(heureF)+(Double.parseDouble(minuteF)/60.0)) //heureDebut - heureFin > now- heureDeut
                        - (Double.parseDouble(heureD)+(Double.parseDouble(minuteD)/60.0))>
                                (calNow.get(calNow.HOUR_OF_DAY)+((double)calNow.get(calNow.MINUTE)/60.0))
                                -(Double.parseDouble(heureD)+(Double.parseDouble(minuteD)/60.0))
                        )
                       {
                            seance = true;return seance;
                        }else {
                            seance=false;
                        }
                    }

                }
            }
            break;
            case 2 : //mardi
                if(dateMardi!=null && !dateMardi.equals("")){
                    for(String s : dateLundi.split("/")){
                        Log.d(TAG, "onClick: "+s);
                        if(s.split("-").length == 2){
                            String heureD = s.split("-")[0].split("h")[0];
                            String minuteD ="0";
                            if(s.split("-")[0].split("h").length > 1)
                                minuteD = s.split("-")[0].split("h")[1];
                            /* Log.d(TAG, "onClick: h/m D "+heureD+"/"+minuteD);*/
                            String heureF = s.split("-")[1].split("h")[0];
                            String minuteF ="0";
                            if(s.split("-")[1].split("h").length > 1)
                                minuteF= s.split("-")[1].split("h")[1];           //   13 - 15 =2  >  14-13 =1
                            if((Double.parseDouble(heureF)+(Double.parseDouble(minuteF)/60.0)) //heureDebut - heureFin > now- heureDeut
                                    - (Double.parseDouble(heureD)+(Double.parseDouble(minuteD)/60.0))>
                                    (calNow.get(calNow.HOUR_OF_DAY)+((double)calNow.get(calNow.MINUTE)/60.0))
                                            -(Double.parseDouble(heureD)+(Double.parseDouble(minuteD)/60.0))
                            )
                            {
                                seance = true;return seance;
                            }else {
                                seance=false;
                            }
                        }

                    }
                }
                break;
            case 3 : //mercredi
                if(dateMercredi!=null && !dateMercredi.equals("")){
                    for(String s : dateLundi.split("/")){
                        Log.d(TAG, "onClick: "+s);
                        if(s.split("-").length == 2){
                            String heureD = s.split("-")[0].split("h")[0];
                            String minuteD ="0";
                            if(s.split("-")[0].split("h").length > 1)
                                minuteD = s.split("-")[0].split("h")[1];
                            /* Log.d(TAG, "onClick: h/m D "+heureD+"/"+minuteD);*/
                            String heureF = s.split("-")[1].split("h")[0];
                            String minuteF ="0";
                            if(s.split("-")[1].split("h").length > 1)
                                minuteF= s.split("-")[1].split("h")[1];           //   15 -13=2  >  14-13 =1
                            if((Double.parseDouble(heureF)+(Double.parseDouble(minuteF)/60.0)) //heureDebut - heureFin > now- heureDeut
                                    - (Double.parseDouble(heureD)+(Double.parseDouble(minuteD)/60.0))>
                                    (calNow.get(calNow.HOUR_OF_DAY)+((double)calNow.get(calNow.MINUTE)/60.0))
                                            -(Double.parseDouble(heureD)+(Double.parseDouble(minuteD)/60.0))
                            )
                            {
                                seance = true;return seance;
                            }else {
                                seance=false;
                            }
                        }

                    }
                }
                break;
            case 4:  //jeudi
                if(dateJeudi!=null && !dateJeudi.equals("")){
                    for(String s : dateLundi.split("/")){
                        Log.d(TAG, "onClick: "+s);
                        if(s.split("-").length == 2){
                            String heureD = s.split("-")[0].split("h")[0];
                            String minuteD ="0";
                            if(s.split("-")[0].split("h").length > 1)
                                minuteD = s.split("-")[0].split("h")[1];
                            /* Log.d(TAG, "onClick: h/m D "+heureD+"/"+minuteD);*/
                            String heureF = s.split("-")[1].split("h")[0];
                            String minuteF ="0";
                            if(s.split("-")[1].split("h").length > 1)
                                minuteF= s.split("-")[1].split("h")[1];           //   15 -13=2  >  14-13 =1
                            if((Double.parseDouble(heureF)+(Double.parseDouble(minuteF)/60.0)) //heureDebut - heureFin > now- heureDeut
                                    - (Double.parseDouble(heureD)+(Double.parseDouble(minuteD)/60.0))>
                                    (calNow.get(calNow.HOUR_OF_DAY)+((double)calNow.get(calNow.MINUTE)/60.0))
                                            -(Double.parseDouble(heureD)+(Double.parseDouble(minuteD)/60.0))
                            )
                            {
                                seance = true;return seance;
                            }else {
                                seance=false;
                            }
                        }

                    }
                }
                break;
            case 5://vendredi
                if(dateVendredi!=null && !dateVendredi.equals("")){
                    for(String s : dateLundi.split("/")){
                        Log.d(TAG, "onClick: "+s);
                        if(s.split("-").length == 2){
                            String heureD = s.split("-")[0].split("h")[0];
                            String minuteD ="0";
                            if(s.split("-")[0].split("h").length > 1)
                                minuteD = s.split("-")[0].split("h")[1];
                            /* Log.d(TAG, "onClick: h/m D "+heureD+"/"+minuteD);*/
                            String heureF = s.split("-")[1].split("h")[0];
                            String minuteF ="0";
                            if(s.split("-")[1].split("h").length > 1)
                                minuteF= s.split("-")[1].split("h")[1];           //   15 -13=2  >  14-13 =1
                            if((Double.parseDouble(heureF)+(Double.parseDouble(minuteF)/60.0)) //heureDebut - heureFin > now- heureDeut
                                    - (Double.parseDouble(heureD)+(Double.parseDouble(minuteD)/60.0))>
                                    (calNow.get(calNow.HOUR_OF_DAY)+((double)calNow.get(calNow.MINUTE)/60.0))
                                            -(Double.parseDouble(heureD)+(Double.parseDouble(minuteD)/60.0))
                            )
                            {
                                seance = true;
                                return seance;
                            }else {
                                seance=false;
                            }
                        }

                    }
                }
                break;

        }
    return seance;
    }
    @Override
    public void onKeyExited(String key) {
        zones.child("dansEcole").setValue("non");
        Token = rootNode.getReference("NotificationToken");
        Log.d(TAG, "onKeyExited: ");
        Token.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                token =  snapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: "+token);
                NotificationBody data;
                if(rater()==true)
                    data = new NotificationBody("Information","Votre fils a rater l'ecole");
                else
                    data = new NotificationBody("Information","Votre fils a sortie de l'ecole");
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
