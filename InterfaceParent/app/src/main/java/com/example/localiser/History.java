package com.example.localiser;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.example.localiser.Models.Position;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class History extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "history";
    private GoogleMap mMap;
    private FirebaseDatabase rootNode;
    private DatabaseReference currentLocalisation;
    private double lastLat, lastLong;
    private PolylineOptions options;
    private Polyline line;
    private DatabaseReference localisationTracker;
    private ArrayList<Position> tracked = new ArrayList<>();
    private Marker m;
    private ArrayList<LatLng> roadLine = new ArrayList<>();
    private boolean selected = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        rootNode = FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com");

        Bundle extras = getIntent().getExtras();
        lastLat= (double) extras.get("lat");
        lastLong= (double) extras.get("long");
        Spinner sp = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.history, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: ");
                String history = parent.getItemAtPosition(position).toString();
                parent.setActivated(false);
                switch (history){
                    case "Last Hour":
                        selected = true;
                        lastHour();
                        break;
                    case "Today" :
                        today();
                        break;
                    case "Last Day":
                        lastDay();
                        break;
                    case "Last Week":
                        lastWeek();
                        break;
                    case "Last Month":
                        lastMonth();
                        break;
                    case "Custom":
                        Custom();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                lastHour();
            }
        });
        getTracked();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maph);
        mapFragment.getMapAsync(History.this);
    }
    private void Custom() {
        roadLine = new ArrayList<>();
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                for(Position p : tracked){
                    Calendar c = toCalendar(p.getDate());
                    /* Log.d(TAG, "today: c"+c);*/
                    if(c.get(c.DAY_OF_MONTH) == myCalendar.get(myCalendar.DAY_OF_MONTH) && c.get(c.MONTH) == myCalendar.get(myCalendar.MONTH)
                            && c.get(c.YEAR) == myCalendar.get(myCalendar.YEAR)
                    ){
                        roadLine.add(new LatLng(p.getLatitude(),p.getLongtitude()));
                    }
                }
                  Log.d(TAG, "today: mush"+roadLine.size());
                drawLine(roadLine);
            }
        };
        new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    private void lastWeek() {
        ArrayList<LatLng> roadLine = new ArrayList<>();
        Date d = new Date();
        Calendar hour = Calendar.getInstance();
        hour.setTime(d);
        hour.add(Calendar.DAY_OF_MONTH, -7);
        for(Position p : tracked){
            Calendar c = toCalendar(p.getDate());
            if(c.compareTo(hour)>0){
                roadLine.add(new LatLng(p.getLatitude(),p.getLongtitude()));
            }
        }
        drawLine(roadLine);
    }
    private synchronized   void setLastPos() {

        currentLocalisation = rootNode.getReference("currentLocalisation");
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
    }
    private void lastHour() {
        ArrayList<LatLng> roadLine = new ArrayList<>();
        Date d = new Date();
        Calendar hour = Calendar.getInstance();
        hour.setTime(d);
        hour.add(Calendar.HOUR, -1);
      /*  Log.d(TAG, "today: "+hour);*/

        Log.d(TAG, "today: "+hour.get(hour.HOUR_OF_DAY));
        for(Position p : tracked){
            Calendar c = toCalendar(p.getDate());
            /*Log.d(TAG, "today: c"+c);*/
            if(c.compareTo(hour)>0){
                roadLine.add(new LatLng(p.getLatitude(),p.getLongtitude()));
            }
        }
        Log.d(TAG, "lastHour: "+hour);


        Log.d(TAG, "today: mush"+roadLine.size());
        drawLine(roadLine);
    }
    private void getTracked() {
        localisationTracker=rootNode.getReference("localisationTracker");
        localisationTracker.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Position> positions = new ArrayList<>();
                for (DataSnapshot pos: snapshot.getChildren()) {
                    Position p = pos.getValue(Position.class);
                    positions.add(p);
                }
                Log.d(TAG, "onDataChange: "+positions.size());
                tracked=positions;
                if(selected) lastHour();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void today(){
        selected = false;
        ArrayList<LatLng> roadLine = new ArrayList<>();
        Date d = new Date();

        Calendar now = Calendar.getInstance();
       /* Log.d(TAG, "today: "+now);
        Log.d(TAG, "today: "+now.get(now.DAY_OF_MONTH));
        Log.d(TAG, "today: "+now.get(now.MONTH));
        Log.d(TAG, "today: "+now.get(now.YEAR));
        Log.d(TAG, "today: "+now.get(now.HOUR_OF_DAY));
        Log.d(TAG, "today: "+now.get(now.MINUTE));
        Log.d(TAG, "today: "+now.get(now.SECOND));*/
        now.setTime(d);
        for(Position p : tracked){
            Calendar c = toCalendar(p.getDate());
           /* Log.d(TAG, "today: c"+c);*/
            if(c.get(c.DAY_OF_MONTH) == now.get(now.DAY_OF_MONTH) && c.get(c.MONTH) == now.get(now.MONTH)
            && c.get(c.YEAR) == now.get(now.YEAR)
            ){
            roadLine.add(new LatLng(p.getLatitude(),p.getLongtitude()));
            }
        }
      /*  Log.d(TAG, "today: mush"+roadLine.size());*/
        drawLine(roadLine);
    }
    private void lastMonth(){
        ArrayList<LatLng> roadLine = new ArrayList<>();
        Date d = new Date();
        Calendar hour = Calendar.getInstance();
        hour.setTime(d);
        hour.add(Calendar.DAY_OF_MONTH, -30);
        for(Position p : tracked){
            Calendar c = toCalendar(p.getDate());
            Log.d(TAG, "today: c"+c);
            if(c.compareTo(hour)>0){
                roadLine.add(new LatLng(p.getLatitude(),p.getLongtitude()));
            }
        }
        drawLine(roadLine);
    }
    private void lastDay(){
        ArrayList<LatLng> roadLine = new ArrayList<>();
        Date d = new Date();
        Calendar hour = Calendar.getInstance();
       /* long timer = 60*60;
        d.setTime(d.getTime()-timer);*/
        hour.setTime(d);
        hour.add(Calendar.DAY_OF_MONTH, -1);
        /*Log.d(TAG, "today: "+hour);

        Log.d(TAG, "today: "+hour.get(hour.HOUR_OF_DAY));*/
        for(Position p : tracked){
            Calendar c = toCalendar(p.getDate());
            /*Log.d(TAG, "today: c"+c);*/
            if(c.compareTo(hour)>0){
                roadLine.add(new LatLng(p.getLatitude(),p.getLongtitude()));
            }
        }
        /*Log.d(TAG, "lastHour: "+hour);


        Log.d(TAG, "today: mush"+roadLine.size());*/
        drawLine(roadLine);
    }
    public  Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //while (circleZones.isEmpty())
        // Add a marker in Sydney and move the camera
        LatLng pos = new LatLng(lastLat, lastLong);
        if(m!=null)
            m.remove();
        /*drawLine();*/
        m=mMap.addMarker(new MarkerOptions().position(pos));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15.0f));
    }

    private void drawLine(ArrayList<LatLng> roadLine) {
        options = new PolylineOptions().width(15).color(Color.BLUE).geodesic(true);
        Log.d(TAG, "drawLine: "+roadLine.size());
        for (int i = 0; i < roadLine.size(); i++) {

            options.add(roadLine.get(i));

        }
        if(line != null) line.remove();
        line = mMap.addPolyline(options);
    }
}