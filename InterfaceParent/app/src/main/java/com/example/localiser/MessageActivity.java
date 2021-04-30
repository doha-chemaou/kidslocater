package com.example.localiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.localiser.Models.Appel;
import com.example.localiser.Models.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    private ListView lv;
    private Button fetch;
    private DatabaseReference mDatabaseRef;
    private ArrayList<Message> messages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mDatabaseRef = FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com/").getReference("uploads");
        messages = new ArrayList<>();

        lv = (ListView) findViewById(R.id.lvMessage);
        fetch=(Button) findViewById(R.id.fetchMessge);
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseRef.child("uploadMessage").setValue("oui");

            }
        });

        mDatabaseRef.child("Message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Message> con = new ArrayList<>();
                if(snapshot!=null && snapshot.getValue()!=null){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Message c= dataSnapshot.getValue(Message.class);
                        con.add(c);
                    }
                    if(!con.isEmpty()) messages=con;
                    ArrayAdapter<Message> arrayAdapter = new ArrayAdapter<Message>(MessageActivity.this, android.R.layout.simple_list_item_1,messages);
                    lv.setAdapter(arrayAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}