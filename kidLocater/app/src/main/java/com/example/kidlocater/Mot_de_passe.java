package com.example.kidlocater;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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

public class Mot_de_passe extends AppCompatActivity {
    Button suivant;
    ImageButton left_arrow;
    View includedLayout;
    LinearLayout l;
    Dialog dialog;
    TextView error;
    TextInputLayout passw;
    TextInputEditText edit_passw;
    private static int SPLASH_TIME_OUT = 50 ;
    private Handler h1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mot_de_passe);

        h1 = new Handler();
        error = findViewById(R.id.error);
        passw = findViewById(R.id.passw);
        edit_passw = findViewById(R.id.edit_passw);

        passw.getEditText().setTransformationMethod(HideReturnsTransformationMethod.getInstance());


        includedLayout = findViewById(R.id.motdepasse);
        TextView title = (TextView) includedLayout.findViewById(R.id.toolbar_title);
        title.setText("Mot de passe");
        title.setTextColor(getResources().getColor(R.color.brown));


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

                dialog = new Dialog(Mot_de_passe.this);
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
                arretCreation.setTextColor(getResources().getColor(R.color.brown));
                arretCreation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Mot_de_passe.this, MainActivity.class);
                        startActivity(i);
                    }
                });
                dialog.show();
            }
        });
        Bundle extras = getIntent().getExtras();
        String first_name = null,last_name = null,birthday=null,gender=null,email=null,phone=null,error_affiche=null,false_email=null;
        if (extras != null) {
            first_name = extras.getString("first_name");
            last_name = extras.getString("last_name");
            birthday = extras.getString("birthday");
            gender = extras.getString("gender");
            email = extras.getString("email");
            false_email = extras.getString("false_email");
            phone = extras.getString("phone_number");
            error_affiche = extras.getString("erroraffichage");
        }

        String finalFirst_name = first_name;
        String finalLast_name = last_name;
        String finalBirthday = birthday;
        String finalGender = gender;
        String finalEmail = (email==null? false_email:email);
        String final_phone_number = phone;
        String finalErrorAffiche = error_affiche;
        //String finalFalseEmail = false_email;
        suivant = (Button) findViewById(R.id.pass_next);
        final int[] number_of_clicks = {0};
        if (finalErrorAffiche!=null){
            if (number_of_clicks[0] !=0) {
                error.setVisibility(View.INVISIBLE);
                //error.setVisibility(View.VISIBLE);
                h1.postDelayed(error_msg, SPLASH_TIME_OUT);
            }
            error.setText("Votre mot de passe doit contenir au moins 6 lettres, chiffres et symboles (comme ! et % %)");
            ViewGroup.LayoutParams params = error.getLayoutParams();
            params.height = 90;
            error.setLayoutParams(params);
            if (number_of_clicks[0] == 0)
                number_of_clicks[0]++;
        }
        suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s_passw  = passw.getEditText().getText().toString();
                if(finalErrorAffiche==null){
                    if (!s_passw.isEmpty() && s_passw.length()>=6) {
                        Intent i = new Intent(Mot_de_passe.this, FinInscription.class);
                        i.putExtra("first_name", finalFirst_name);
                        i.putExtra("last_name", finalLast_name);
                        i.putExtra("birthday", finalBirthday);
                        i.putExtra("gender", finalGender);
                        if (finalEmail != null)
                            i.putExtra("email", finalEmail);
                        if (final_phone_number != null)
                            i.putExtra("phone_number", final_phone_number);
                        i.putExtra("password", s_passw);

                    /*if (number_of_clicks[0] == 0)
                        number_of_clicks[0]++;*/
                        startActivity(i);
                    } else {
                        if (number_of_clicks[0] != 0) {
                            error.setVisibility(View.INVISIBLE);
                            //error.setVisibility(View.VISIBLE);
                            h1.postDelayed(error_msg, SPLASH_TIME_OUT);
                        }
                        error.setText("Votre mot de passe doit contenir au moins 6 lettres, chiffres et symboles (comme ! et % %)");
                        ViewGroup.LayoutParams params = error.getLayoutParams();
                        params.height = 90;
                        error.setLayoutParams(params);
                        if (number_of_clicks[0] == 0)
                            number_of_clicks[0]++;
                    }
                }
                else{
                    Intent i = new Intent(Mot_de_passe.this, Verification_en_cours.class);
                    i.putExtra("first_name", finalFirst_name);
                    i.putExtra("last_name", finalLast_name);
                    i.putExtra("birthday", finalBirthday);
                    i.putExtra("gender", finalGender);
                    i.putExtra("false_email", finalEmail);
                    if (final_phone_number != null)
                        i.putExtra("phone_number", final_phone_number);
                    i.putExtra("password", s_passw);
                    startActivity(i);

                }
            }
        });
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
