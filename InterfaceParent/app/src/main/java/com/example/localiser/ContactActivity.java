package com.example.localiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.localiser.Models.Contact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {
    private Button fetch;
    private DatabaseReference mDatabaseRef;
    private ArrayList<Contact> contacts;
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        mDatabaseRef = FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com/").getReference("uploads");
        fetch= (Button) findViewById(R.id.fetchContact);
        contacts = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listContact);

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseRef.child("uploadContact").setValue("oui");

            }
        });
        mDatabaseRef.child("Contact").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Contact> con = new ArrayList<>();
                if(snapshot!=null && snapshot.getValue()!=null){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Contact c= dataSnapshot.getValue(Contact.class);
                        con.add(c);
                    }
                    if(!con.isEmpty()) contacts=con;
                    ArrayAdapter<Contact> arrayAdapter = new ArrayAdapter<Contact>(ContactActivity.this, android.R.layout.simple_list_item_1,contacts);
                    lv.setAdapter(arrayAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}