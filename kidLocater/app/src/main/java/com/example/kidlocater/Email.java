package com.example.kidlocater;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class Email extends AppCompatActivity {
    Button suivant;
    TextView phone,error,test;
    ImageButton left_arrow;
    View includedLayout;
    LinearLayout l;
    Dialog dialog;
    TextInputLayout email;
    TextInputEditText edit_email;
    FirebaseAuth auth;
    private static int SPLASH_TIME_OUT = 50 ;
    private Handler h1;
    private static final String TAG = "hehehe" ;
    static int[] number_of_clicks = {0};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email);

        h1 = new Handler();

        error = findViewById(R.id.error);
        email = findViewById(R.id.email);
        edit_email = findViewById(R.id.edit_email);
        includedLayout = findViewById(R.id.e_mail);
        test = findViewById(R.id.test);

        auth = FirebaseAuth.getInstance();



        TextView title = (TextView) includedLayout.findViewById(R.id.toolbar_title);
        title.setText("Adresse e-mail");
        title.setTextColor(getResources().getColor(R.color.light_blue));

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
        left_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l = (LinearLayout) findViewById(R.id.exitdialog);

                dialog = new Dialog(Email.this);
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
                arretCreation.setTextColor(getResources().getColor(R.color.light_blue));

                arretCreation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Email.this, MainActivity.class);
                        startActivity(i);
                    }
                });
                dialog.show();
            }
        });

        Bundle extras = getIntent().getExtras();
        String false_email=null,error_affichage=null,passw=null,exist_email=null,first_name=null,last_name=null,birthday=null,gender=null;

        if (extras != null) {
            false_email = extras.getString("false_email");
            first_name = extras.getString("first_name");
            last_name = extras.getString("last_name");
            birthday = extras.getString("birthday");
            gender = extras.getString("gender");
            error_affichage = extras.getString("error");
            passw = extras.getString("password");
            exist_email = extras.getString("exists");
        }
        String final_false_email = false_email;
        String final_passw = passw;
        String final_Exists_email=exist_email;
        String finalFirst_name = first_name;
        String finalLast_name = last_name;
        String finalBirthday = birthday;
        String finalGender = gender;
        String finalExists_in_firestore = exist_email;//exists_in_firestore;

        if (final_false_email!=null || final_Exists_email!=null){
            if (number_of_clicks[0] != 0) {
                error.setVisibility(View.INVISIBLE);
                //error.setVisibility(View.VISIBLE);
                h1.postDelayed(error_msg, SPLASH_TIME_OUT);
            }


            if (final_false_email!=null) {
                edit_email.setText(final_false_email);
                error.setText("Veillez saisir une adresse e-mail valide");
            }
            if(final_Exists_email!=null){
                edit_email.setText(final_Exists_email);
                error.setText("Un compte existant est déjà associé à cet e-mail");
            }
            ViewGroup.LayoutParams params = error.getLayoutParams();
            params.height = 60;
            error.setLayoutParams(params);
            if (number_of_clicks[0] == 0)
                number_of_clicks[0]++;
        }

        String s_num_of_clicks="0",exist=null;
        if (extras != null) {
            exist = extras.getString("exists");
        }

        String finalExist = exist;

        suivant = (Button) findViewById(R.id.e_next);

        phone = (TextView) findViewById(R.id.phone);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Email.this, Num_Tel.class);
                i.putExtra("first_name", finalFirst_name);
                i.putExtra("last_name", finalLast_name);
                i.putExtra("birthday", finalBirthday);
                i.putExtra("gender", finalGender);
                startActivity(i);
            }
        });
        suivant.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                /*String s_email  = email.getEditText().getText().toString();
                if (!s_email.contains(" ")&& !s_email.isEmpty()) {
                    long number_of_at = s_email.chars().filter(num -> num == '@').count();
                    int index_of_at = s_email.indexOf('@');
                    String sub_starting_from_at = s_email.substring(index_of_at);
                    long number_of_dots = sub_starting_from_at.chars().filter(num -> num == '.').count();
                    int index_of_dot = sub_starting_from_at.indexOf('.');
                    //String between_at_and_dot = sub_starting_from_at.substring(1,index_of_dot);
                    String between_at_and_end_of_email = sub_starting_from_at.substring(1,sub_starting_from_at.length());
                    String[] split_according_to_dot = between_at_and_end_of_email.split("\\.");
                    long number_of_dots_starting_from_at = between_at_and_end_of_email.chars().filter(num -> num == '.').count();
                    int browser = 0;
                    String s = sub_starting_from_at + " / " + between_at_and_end_of_email + " / "+Integer.toString(index_of_dot)+ " / "+ Arrays.toString(between_at_and_end_of_email.split("\\."))+" / " ;

                    while (browser < split_according_to_dot.length && !split_according_to_dot[browser].isEmpty() && number_of_dots_starting_from_at+1 == split_according_to_dot.length){
                        s+= split_according_to_dot[browser]+" // "+Boolean.toString(split_according_to_dot[browser].isEmpty())+" // ";
                        browser++;
                    }
                    test.setText(s);
                    //String[] split_according
                    if (!s_email.isEmpty() && number_of_at == 1 && number_of_dots >= 1 && !between_at_and_end_of_email.isEmpty() && browser==split_according_to_dot.length && browser!=0) {
                        Intent i = new Intent(Email.this, Mot_de_passe.class);
                        i.putExtra("first_name", finalFirst_name);
                        i.putExtra("last_name", finalLast_name);
                        i.putExtra("birthday", finalBirthday);
                        if (number_of_clicks[0] == 0)
                            number_of_clicks[0]++;
                        startActivity(i);
                    }
                    else {
                        if (number_of_clicks[0] !=0) {
                            error.setVisibility(View.INVISIBLE);
                            //error.setVisibility(View.VISIBLE);
                            h1.postDelayed(error_msg, SPLASH_TIME_OUT);
                        }
                        error.setText("Veillez saisir une adresse e-mail valide");
                        ViewGroup.LayoutParams params = error.getLayoutParams();
                        params.height = 60;
                        error.setLayoutParams(params);
                        if (number_of_clicks[0] == 0)
                            number_of_clicks[0]++;
                    }
                }
                else{
                    if (number_of_clicks[0] !=0) {
                        error.setVisibility(View.INVISIBLE);
                        //error.setVisibility(View.VISIBLE);
                        h1.postDelayed(error_msg, SPLASH_TIME_OUT);
                    }
                    error.setText("Veillez saisir une adresse e-mail valide");

                    ViewGroup.LayoutParams params = error.getLayoutParams();
                    params.height = 60;
                    error.setLayoutParams(params);
                    if (number_of_clicks[0] == 0)
                        number_of_clicks[0]++;
                }*/
                //test.setText("prénom: "+finalFirst_name+"/ nom:"+finalLast_name+ "/ date naissance "+finalBirthday+ "/ genre : "+ finalGender+ " / email : " + final_false_email+ "/email existant "+finalExists_in_firestore);

                String s_email = email.getEditText().getText().toString();
                if(final_false_email!=null || finalExist!=null){
                    /*auth.signInWithCustomToken(final_false_email)
                            .addOnCompleteListener(Email.this, new OnCompleteListener<AuthResult>() {
                                //private static final String TAG ="hehe" ;
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithCustomToken:success");
                                        FirebaseUser user = auth.getCurrentUser();
                                        //updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                                        Intent i = new Intent(Email.this, Email.class);
                                        i.putExtra("erroraffichage","affiche");
                                        i.putExtra("false_email",final_false_email);
                                        startActivity(i);
                                        //Toast.makeText(FinInscription.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                        //updateUI(null);
                                    }
                                }
                            });*/
                    /*String final_false_email = false_email;
                    String final_passw = passw;
                    String final_Exists_email=exist_email;
                    String finalFirst_name = first_name;
                    String finalLast_name = last_name;
                    String finalBirthday = birthday;
                    String finalGender = gender;
                    String finalExists_in_firestore = exist_email;//exists_in_firestore;*/
                    if (!s_email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(s_email).matches()) {

                        Intent i = new Intent(Email.this, Verification_en_cours.class);
                        i.putExtra("false_email", s_email);
                        i.putExtra("password", final_passw);
                        i.putExtra("first_name", finalFirst_name);
                        i.putExtra("last_name", finalLast_name);
                        i.putExtra("birthday", finalBirthday);
                        i.putExtra("gender", finalGender);
                        startActivity(i);
                        finish();
                    }
                    else {
                        if (number_of_clicks[0] != 0) {
                            error.setVisibility(View.INVISIBLE);
                            //error.setVisibility(View.VISIBLE);
                            h1.postDelayed(error_msg, SPLASH_TIME_OUT);
                        }
                        if (finalExist == null || finalExists_in_firestore==null )
                            error.setText("Veuillez saisir une adresse e-mail valide");
                        else
                            error.setText("Un compte existant est déjà associé à cet e-mail");
                        ViewGroup.LayoutParams params = error.getLayoutParams();
                        params.height = 60;
                        error.setLayoutParams(params);
                        if (number_of_clicks[0] == 0)
                            number_of_clicks[0]++;
                    }
                }
                else {
                    if (!s_email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(s_email).matches()) {
                        Intent i = new Intent(Email.this, Mot_de_passe.class);
                        i.putExtra("first_name", finalFirst_name);
                        i.putExtra("last_name", finalLast_name);
                        i.putExtra("birthday", finalBirthday);
                        i.putExtra("gender", finalGender);
                        i.putExtra("email", s_email);
                        //test.setText("prénom: "+finalFirst_name+"/ nom:"+finalLast_name+ "/ date naissance "+finalBirthday+ "/ genre : "+ finalGender+ " / email : " + s_email);
                        if (number_of_clicks[0] == 0)
                            number_of_clicks[0]++;
                        startActivity(i);
                    } else {
                        if (number_of_clicks[0] != 0) {
                            error.setVisibility(View.INVISIBLE);
                            //error.setVisibility(View.VISIBLE);
                            h1.postDelayed(error_msg, SPLASH_TIME_OUT);
                        }
                        if (finalExist == null || finalExists_in_firestore==null )
                            error.setText("Veuillez saisir une adresse e-mail valide");
                        else
                            error.setText("Un compte existant est déjà associé à cet e-mail");
                        ViewGroup.LayoutParams params = error.getLayoutParams();
                        params.height = 60;
                        error.setLayoutParams(params);
                        if (number_of_clicks[0] == 0)
                            number_of_clicks[0]++;
                    }
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