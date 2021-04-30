package com.example.localiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.localiser.Models.Appel;
import com.example.localiser.Models.Contact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AppelActivity extends AppCompatActivity {
    private ListView lv;
    private Button fetch;
    private DatabaseReference mDatabaseRef;
    private ArrayList<Appel> appels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseRef = FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com/").getReference("uploads");
        appels = new ArrayList<>();

        setContentView(R.layout.activity_appel);
        lv = (ListView) findViewById(R.id.lvAppel);
        fetch=(Button) findViewById(R.id.fetchAppel);
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseRef.child("uploadAppel").setValue("oui");

            }
        });

        mDatabaseRef.child("Appel").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Appel> con = new ArrayList<>();
                if(snapshot!=null && snapshot.getValue()!=null){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Appel c= dataSnapshot.getValue(Appel.class);
                        con.add(c);
                    }
                    if(!con.isEmpty()) appels=con;
                    ArrayAdapter<Appel> arrayAdapter = new ArrayAdapter<Appel>(AppelActivity.this, android.R.layout.simple_list_item_1,appels);
                    lv.setAdapter(arrayAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}