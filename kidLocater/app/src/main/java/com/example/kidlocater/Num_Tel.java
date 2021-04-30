package com.example.kidlocater;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Num_Tel extends AppCompatActivity {
    Button suivant;
    TextView mail,error;
    ImageButton left_arrow;
    View includedLayout;
    LinearLayout l;
    Dialog dialog;
    TextInputLayout num;
    TextInputEditText edit_num;
    private static int SPLASH_TIME_OUT = 50 ;
    private Handler h1;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.num_tel);

        h1 = new Handler();

        num = findViewById(R.id.tel);
        error = findViewById(R.id.error);
        edit_num = findViewById(R.id.edit_tel);

        includedLayout = findViewById(R.id.numtel);
        TextView title = (TextView )includedLayout.findViewById(R.id.toolbar_title);
        title.setText("Numéro de téléphone");
        title.setTextColor(getResources().getColor(R.color.pink));


        left_arrow = (ImageButton) findViewById(R.id.arrow);

        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
                outline.setOval(5, 2, size, size);
            }
        };
        left_arrow.setOutlineProvider(viewOutlineProvider);

        left_arrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                l = (LinearLayout)findViewById(R.id.exitdialog);

                dialog = new Dialog(Num_Tel.this);
                dialog.setContentView(R.layout.exit_dialog);
                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.exit_background));
                //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                TextView pourCreation = (TextView) dialog.findViewById(R.id.pourCreation);
                pourCreation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                TextView arretCreation = (TextView) dialog.findViewById(R.id.arretCreation);
                arretCreation.setTextColor(getResources().getColor(R.color.pink));

                arretCreation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Num_Tel.this, MainActivity.class);
                        startActivity(i);
                    }
                });
                dialog.show();
            }
        });
        Bundle extras = getIntent().getExtras();
        String first_name = null,last_name = null,birthday=null, gender=null;
        if (extras != null) {
            first_name = extras.getString("first_name");
            last_name = extras.getString("last_name");
            birthday = extras.getString("birthday");
            gender = extras.getString("gender");
        }
        String finalFirst_name = first_name;
        String finalLast_name = last_name;
        String finalBirthday = birthday;
        String finalGender = gender;
        suivant = (Button) findViewById(R.id.p_next);
        final int[] number_of_clicks = {0};

        mail = (TextView) findViewById(R.id.email);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Num_Tel.this, Email.class);
                i.putExtra("first_name", finalFirst_name);
                i.putExtra("last_name", finalLast_name);
                i.putExtra("birthday", finalBirthday);
                i.putExtra("gender", finalGender);
                startActivity(i);
            }
        });

        suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num_tel  = num.getEditText().getText().toString();
                if (num_tel.length()>=3) {
                    Intent i = new Intent(Num_Tel.this, Mot_de_passe.class);
                    i.putExtra("first_name", finalFirst_name);
                    i.putExtra("last_name", finalLast_name);
                    i.putExtra("birthday", finalBirthday);
                    i.putExtra("gender", finalGender);
                    i.putExtra("phone_number", num_tel);
                    /*if (number_of_clicks[0] == 0)
                        number_of_clicks[0]++;*/
                    startActivity(i);
                }
                else {
                    if (number_of_clicks[0] !=0) {
                        error.setVisibility(View.INVISIBLE);
                        //error.setVisibility(View.VISIBLE);
                        h1.postDelayed(error_msg, SPLASH_TIME_OUT);
                    }
                    error.setText("Veuillez entrer un numéro de téléphone valide ou utiliser votre adresse e-mail");
                    ViewGroup.LayoutParams params = error.getLayoutParams();
                    params.height = 90;
                    error.setLayoutParams(params);
                    if (number_of_clicks[0] == 0)
                        number_of_clicks[0]++;
                }
            }
        });
/*
        edit_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    //error_space.setError(null);
                    ViewGroup.LayoutParams params = error.getLayoutParams();
                    params.height = 0;
                    //params.width = 100;
                    error.setLayoutParams(params);
                    //error.setVisibility(View.VISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //prenom_before.setText("prenom_before: "+s_prenom_before);
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                s_prenom_after = prenom.getEditText().getText().toString();
                    if (!num_tel.isEmpty() ){
                        //error_space.setError(null);
                        ViewGroup.LayoutParams params = error.getLayoutParams();
                        params.height = 0;
                        //params.width = 100;
                        error.setLayoutParams(params);
                        //error.setVisibility(View.VISIBLE);
                    }
                }
                //prenom_after.setText("prenom_after : "+s_prenom_after);
            }
        });*/
    }
    private Runnable error_msg = new Runnable() {
        @Override
        public void run() {
            /*locimg=findViewById(R.id.loc);
            locimg.setBackgroundResource(R.drawable.d_fade_in);
            locAnimation=(AnimationDrawable)locimg.getBackground();
            locAnimation.start();*/
            error.setVisibility(View.VISIBLE);

        }
    };

}