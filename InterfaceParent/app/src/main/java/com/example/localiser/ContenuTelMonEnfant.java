package com.example.localiser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.example.localiser.Models.Upload;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class ContenuTelMonEnfant extends AppCompatActivity {
    private static final String TAG = "Contenu";
    private Button images;
private Button videos;
private Button contact;
private Button appel ;
private Button message;
private Button appelEnf;
/*private Button browser;*/
/*private StorageReference mStorageRef;*/
    private DatabaseReference mDatabaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* mStorageRef = FirebaseStorage.getInstance("gs://localisation-2b7ab.appspot.com").getReference("uploads");*/
        setContentView(R.layout.activity_tel_mon_enfant);
        message=(Button) findViewById(R.id.msg);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ContenuTelMonEnfant.this,MessageActivity.class);
                startActivity(i);
            }
        });
       /* browser = (Button ) findViewById(R.id.bhistory);
        browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ContenuTelMonEnfant.this,HistoryWebActivity.class);
                startActivity(i);
            }
        });*/
        appel = (Button) findViewById(R.id.appelP);
        appel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ContenuTelMonEnfant.this,AppelActivity.class);
                startActivity(i);
            }
        });
        mDatabaseRef = FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com").getReference("uploads");
        images=(Button) findViewById(R.id.photo);
        videos = (Button) findViewById(R.id.video);
        contact =(Button) findViewById(R.id.contact);
        images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ContenuTelMonEnfant.this,ImagesActivity.class);
                startActivity(i);
                }
        });

        videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ContenuTelMonEnfant.this,VideoActivity.class);
                startActivity(i);
            }
        });
        appelEnf = (Button) findViewById(R.id.appel);
        appelEnf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ContenuTelMonEnfant.this,CallActivity.class);
                startActivity(i);
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ContenuTelMonEnfant.this,ContactActivity.class);
                startActivity(i);
            }
        });
    }

}