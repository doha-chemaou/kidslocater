package com.example.localiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.localiser.Models.HistoryWeb;
import com.example.localiser.Models.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryWebActivity extends AppCompatActivity {
    private ListView lv;
    private Button fetch;
    private DatabaseReference mDatabaseRef;
    private ArrayList<HistoryWeb> history;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_web);

        mDatabaseRef = FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com/").getReference("uploads");
        history = new ArrayList<>();

        lv = (ListView) findViewById(R.id.lvHistory);
        fetch=(Button) findViewById(R.id.fetchHistory);
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseRef.child("uploadHistory").setValue("oui");

            }
        });

        mDatabaseRef.child("History").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<HistoryWeb> con = new ArrayList<>();
                if(snapshot!=null && snapshot.getValue()!=null){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        HistoryWeb c= dataSnapshot.getValue(HistoryWeb.class);
                        con.add(c);
                    }
                    if(!con.isEmpty()) history=con;
                    ArrayAdapter<HistoryWeb> arrayAdapter = new ArrayAdapter<HistoryWeb>(HistoryWebActivity.this, android.R.layout.simple_list_item_1,history);
                    lv.setAdapter(arrayAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}