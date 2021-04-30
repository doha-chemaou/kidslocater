package com.example.localiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.localiser.Models.CircleZone;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EcoleEmploiActivity extends AppCompatActivity {
    private static final String TAG = "E";
    private Button save;
    private EditText lundi;
    private EditText mardi;
    private EditText mercredi;
    private EditText jeudi;
    private EditText vendredi;
    private FirebaseDatabase rootNode;
    private DatabaseReference emplois;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ecole_emploi);
        rootNode = FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com");
        emplois = rootNode.getReference("ecoleEmplois");
        lundi = (EditText) findViewById(R.id.lundi);
        mardi = (EditText) findViewById(R.id.mardi);
        mercredi = (EditText) findViewById(R.id.mercredi);
        jeudi = (EditText) findViewById(R.id.jeudi);
        vendredi = (EditText) findViewById(R.id.vendredi);
        emplois.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult()!=null){
                    lundi.setText(task.getResult().child("lundi").getValue(String.class));
                    mardi.setText(task.getResult().child("mardi").getValue(String.class));
                    mercredi.setText(task.getResult().child("mercredi").getValue(String.class));
                    jeudi.setText(task.getResult().child("jeudi").getValue(String.class));
                    vendredi.setText(task.getResult().child("vendredi").getValue(String.class));
                }


            }
        });
        save  = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*zones.child("medium").push().setValue(new CircleZone(latLng.latitude,latLng.longitude,radius));*/
                //9h22 - 22h30 / ...
                boolean exactFormat = true;
                String failure = "";
                try {

                String lund = lundi.getText().toString();
                Log.d(TAG, "onClick: "+lund);
                if(lund!=null && !lund.equals(""))
                for(String s : lund.split("/")){
                    Log.d(TAG, "onClick: "+s);
                    if(s.split("-").length == 2){


                    String heureD = s.split("-")[0].split("h")[0];
                    String minuteD ="0";
                    if(s.split("-")[0].split("h").length > 1)
                    minuteD = s.split("-")[0].split("h")[1];
                        Log.d(TAG, "onClick: h/m D "+heureD+"/"+minuteD);
                    String heureF = s.split("-")[1].split("h")[0];
                    String minuteF ="0";
                    if(s.split("-")[1].split("h").length > 1)
                    minuteF= s.split("-")[1].split("h")[1];
                        Log.d(TAG, "onClick: f"+heureF+"§"+minuteF);
                    if(heureD.length() >2 || minuteD.length() >2 || heureF.length() >2 || minuteF.length() >2){
                        exactFormat = false;
                        failure+="lundi ";
                    }else if(Integer.parseInt(heureD) >23 || Integer.parseInt(minuteD)<0 || Integer.parseInt(heureD) > Integer.parseInt(heureF) ||
                    Integer.parseInt(heureF) >23 || Integer.parseInt(minuteF)<0){
                        exactFormat = false;
                        failure+="lundi ";
                    }
                    }else{
                        exactFormat = false;
                        failure+="lundi ";
                    }

                }
                String mard = mardi.getText().toString();
                if(mard!=null && !mard.equals(""))
                    for(String s : mard.split("/")){
                        if(s.split("-").length == 2) {
                            String heureD = s.split("-")[0].split("h")[0];
                            String minuteD = "0";
                            if (s.split("-")[0].split("h").length > 1)
                                minuteD = s.split("-")[0].split("h")[1];
                            String heureF = s.split("-")[1].split("h")[0];
                            String minuteF = "0";
                            if (s.split("-")[1].split("h").length > 1)
                                minuteF = s.split("-")[1].split("h")[1];

                            if (heureD.length() > 2 || minuteD.length() > 2 || heureF.length() > 2 || minuteF.length() > 2) {
                                exactFormat = false;
                                failure += "mardi ";
                            } else if (Integer.parseInt(heureD) > 23 || Integer.parseInt(minuteD) < 0 || Integer.parseInt(heureD) > Integer.parseInt(heureF) ||
                                    Integer.parseInt(heureF) > 23 || Integer.parseInt(minuteF) < 0) {
                                exactFormat = false;
                                failure += "mardi ";
                            }
                        }else{
                            exactFormat = false;
                            failure += "mardi ";
                        }
                    }

                String mercred = mercredi.getText().toString();
                if(mercred!=null && !mercred.equals(""))
                    for(String s : mercred.split("/")){
                        if(s.split("-").length == 2) {
                            String heureD = s.split("-")[0].split("h")[0];
                            String minuteD = "0";
                            if (s.split("-")[0].split("h").length > 1)
                                minuteD = s.split("-")[0].split("h")[1];
                            String heureF = s.split("-")[1].split("h")[0];
                            String minuteF = "0";
                            if (s.split("-")[1].split("h").length > 1)
                                minuteF = s.split("-")[1].split("h")[1];

                            if (heureD.length() > 2 || minuteD.length() > 2 || heureF.length() > 2 || minuteF.length() > 2) {
                                exactFormat = false;
                                failure += "mercredi ";
                            } else if (Integer.parseInt(heureD) > 23 || Integer.parseInt(minuteD) < 0 || Integer.parseInt(heureD) > Integer.parseInt(heureF) ||
                                    Integer.parseInt(heureF) > 23 || Integer.parseInt(minuteF) < 0) {
                                exactFormat = false;
                                failure += "mercredi ";
                            }
                        }else{
                            exactFormat = false;
                            failure += "mercredi ";
                        }
                    }
                String jeud = jeudi.getText().toString();
                if(jeud!=null && !jeud.equals(""))
                    for(String s : jeud.split("/")){
                        if(s.split("-").length == 2) {
                            String heureD = s.split("-")[0].split("h")[0];
                            String minuteD = "0";
                            if (s.split("-")[0].split("h").length > 1)
                                minuteD = s.split("-")[0].split("h")[1];
                            String heureF = s.split("-")[1].split("h")[0];
                            String minuteF = "0";
                            if (s.split("-")[1].split("h").length > 1)
                                minuteF = s.split("-")[1].split("h")[1];

                            if (heureD.length() > 2 || minuteD.length() > 2 || heureF.length() > 2 || minuteF.length() > 2) {
                                exactFormat = false;
                                failure += "jeudi ";
                            } else if (Integer.parseInt(heureD) > 23 || Integer.parseInt(minuteD) < 0 || Integer.parseInt(heureD) > Integer.parseInt(heureF) ||
                                    Integer.parseInt(heureF) > 23 || Integer.parseInt(minuteF) < 0) {
                                exactFormat = false;
                                failure += "jeudi ";
                            }

                        }else{
                            exactFormat = false;
                            failure += "jeudi ";
                        }
                    }
                String vendred = vendredi.getText().toString();
                if(vendred!=null && !vendred.equals(""))
                    for(String s : vendred.split("/")){
                        if(s.split("-").length == 2) {
                            String heureD = s.split("-")[0].split("h")[0];
                            String minuteD = "0";
                            if (s.split("-")[0].split("h").length > 1)
                                minuteD = s.split("-")[0].split("h")[1];
                            String heureF = s.split("-")[1].split("h")[0];
                            String minuteF = "0";
                            if (s.split("-")[1].split("h").length > 1)
                                minuteF = s.split("-")[1].split("h")[1];

                            if (heureD.length() > 2 || minuteD.length() > 2 || heureF.length() > 2 || minuteF.length() > 2) {
                                exactFormat = false;
                                failure += "vendredi ";
                            } else if (Integer.parseInt(heureD) > 23 || Integer.parseInt(minuteD) < 0 || Integer.parseInt(heureD) > Integer.parseInt(heureF) ||
                                    Integer.parseInt(heureF) > 23 || Integer.parseInt(minuteF) < 0) {
                                exactFormat = false;
                                failure += "vendredi ";
                            }
                        }else{
                            exactFormat = false;
                            failure+="vendredi ";
                        }
                    }
                if(exactFormat==true){


                    new AlertDialog.Builder(EcoleEmploiActivity.this)
                            .setTitle("Sauvegarder l'emplois")
                            .setMessage("Voullez vous sauvegarder l'emplois ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    emplois.child("jeudi").setValue(jeud==null?"null":jeud);
                                    emplois.child("vendredi").setValue(vendred==null?"null":vendred);
                                    emplois.child("mardi").setValue(mard==null?"null":mard);
                                    emplois.child("mercredi").setValue(mercred==null?"null":mercred);
                                    emplois.child("lundi").setValue(lund==null?"null":lund);
                                }})
                            .setNegativeButton("Non", null).show();
                }else{

                    new AlertDialog.Builder(EcoleEmploiActivity.this)
                            .setTitle("Erreur Format")
                            .setMessage("Erreur format au champs : "+failure)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }})
                            .show();
                }

                }catch (Exception e){
                    new AlertDialog.Builder(EcoleEmploiActivity.this)
                            .setTitle("Erreur Format")
                            .setMessage("Erreur format au champs, vérifier que vous avez bien saisie les crénaux !")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }})
                            .show();
                }
            }
        });
    }
}