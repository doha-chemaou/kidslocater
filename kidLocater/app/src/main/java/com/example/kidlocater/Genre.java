package com.example.kidlocater;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Genre extends Activity {
    Button suivant;
    ImageButton left_arrow;
    View includedLayout;
    LinearLayout l;
    Dialog dialog;
    RadioGroup radioGroup;
    RadioButton radioButton;
    TextView error,test;
    private static int SPLASH_TIME_OUT = 50 ;
    private Handler h1;
    //Button display;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.genre);

        h1 = new Handler();
        test = findViewById(R.id.test);
        includedLayout = findViewById(R.id.gender);
        error = findViewById(R.id.error);
        TextView title = (TextView )includedLayout.findViewById(R.id.toolbar_title);
        title.setText("Genre");
        title.setTextColor(getResources().getColor(R.color.yellow));


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

                dialog = new Dialog(Genre.this);
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
                arretCreation.setTextColor(getResources().getColor(R.color.yellow));

                arretCreation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Genre.this, MainActivity.class);
                        startActivity(i);
                    }
                });
                dialog.show();
            }
        });

        Bundle extras = getIntent().getExtras();
        String first_name = null,last_name = null,birthday=null;
        if (extras != null) {
            first_name = extras.getString("first_name");
            last_name = extras.getString("last_name");
            birthday = extras.getString("birthday");
        }
        String finalFirst_name = first_name;
        String finalLast_name = last_name;
        String finalBirthday = birthday;
        radioGroup = findViewById(R.id.genre);
        final String[] gender = {null};
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ViewGroup.LayoutParams params = error.getLayoutParams();
                params.height = 0;
                error.setLayoutParams(params);
                switch(checkedId){
                    case R.id.femme:
                        gender[0] = "Femme";
                        break;
                    case R.id.homme:
                        gender[0] = "Homme";
                }
            }
        });
        suivant = (Button) findViewById(R.id.next);
        final int[] number_of_clicks = {0};
        suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId;
                selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId!=-1){
                    radioButton = (RadioButton) findViewById(selectedId);
                    //String gender = radioButton.getText().toString();
                    Intent i = new Intent(Genre.this, Num_Tel.class);
                    i.putExtra("first_name", finalFirst_name);
                    i.putExtra("last_name", finalLast_name);
                    i.putExtra("birthday", finalBirthday);
                    i.putExtra("gender", gender[0]);
                    //test.setText("prénom: "+finalFirst_name+"/ nom:"+finalLast_name+ "/ date naissance "+finalBirthday+ "/ genre : "+ gender[0]);
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
                    error.setText("Veuillez sélectionner votre genre");
                    ViewGroup.LayoutParams params = error.getLayoutParams();
                    params.height = 60;
                    error.setLayoutParams(params);
                    if (number_of_clicks[0] == 0)
                        number_of_clicks[0]++;
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
