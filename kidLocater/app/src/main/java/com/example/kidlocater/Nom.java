package com.example.kidlocater;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.kidlocater.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

//import com.example.kidlocater.TextChangedListener;
//sjjjjjjjjj
public class Nom extends AppCompatActivity{
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    private static int SPLASH_TIME_OUT = 50 ;
    private Handler h1;
    TextView test ;

    //final FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference ref = database.getReference("server/saving-data/firelog");

    Button suivant;
    //ImageView left_arrow;
    View includedLayout;
    //ShapeableImageView left_arrow;
    ImageButton left_arrow;
    Drawable arrow;
    //LinearLayout l;
    Dialog dialog;
    TextInputLayout prenom, nom,error_space;
    TextInputEditText edit_prenom,edit_nom;
    TextView error,prenom_before,prenom_after,current_prenom,current_nom;
    String s_prenom,s_nom,s_error, s_prenom_before,s_nom_before,s_prenom_after,s_nom_after;
    private DatabaseReference mDatabase;


    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        openKeyboard();
        /*mDatabase = FirebaseDatabase.getInstance().getReference("user");
        mDatabase.setValue("hehehe");*/

        setContentView(R.layout.nom);

        test = findViewById(R.id.test);

        h1 = new Handler();

        /*adding info to firebase*/
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseDatabase.getInstance().getReference().child("user").child("u1").push().setValue("abcd");

        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);


        error_space=findViewById(R.id.error_space);
        edit_prenom = findViewById(R.id.edit_prenom);

        edit_nom = findViewById(R.id.edit_nom);
        s_error = "";
        s_nom = s_nom_before = s_nom_after = nom.getEditText().getText().toString();
        s_prenom = prenom.getEditText().getText().toString();

        /*prenom_before = findViewById(R.id.prenom_before);
        prenom_after = findViewById(R.id.prenom_after);
        current_prenom = findViewById(R.id.current_prenom);
        current_nom = findViewById(R.id.current_nom);*/

        s_prenom_before = prenom.getEditText().getText().toString();;
        s_prenom_after = prenom.getEditText().getText().toString();



        // version 1 of error
        error = findViewById(R.id.error);


        /*prenom.addTextChangedListener(new TextChangedListener<TextInputLayout>(prenom) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                //Do stuff
            }
        });*/

        /*prenom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });*/

        /*arrow = ContextCompat.getDrawable(this,R.drawable.ic_baseline_arrow_back_24);
        arrow= DrawableCompat.wrap(arrow);
        DrawableCompat.setTint(arrow,Color.BLACK);*/

        includedLayout = findViewById(R.id.name);
        TextView title = (TextView) includedLayout.findViewById(R.id.toolbar_title);
        title.setText("Nom");
        title.setTextColor(getResources().getColor(R.color.light_orange));
        //title.setTextColor(R.color.light_orange);


        /*edit_prenom.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if ((s_prenom_before.isEmpty() & !s_prenom_after.isEmpty()) | (!s_prenom_before.isEmpty() & !s_prenom_after.isEmpty()))
                    error_space.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                s_prenom_before = prenom.getEditText().getText().toString();
                prenom_before.setText(s_prenom_before);
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                s_prenom_after = prenom.getEditText().getText().toString();
                prenom_after.setText(s_prenom_after);
            }
        });

        edit_nom.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if ((s_nom_before.isEmpty() & !s_nom_after.isEmpty() | (!s_nom_before.isEmpty() & !s_nom_after.isEmpty())))
                    error_space.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                s_nom_before = nom.getEditText().getText().toString();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                s_nom_after = nom.getEditText().getText().toString();
            }
        });*/
        


        //Outline
        /*int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
        Outline outline = new Outline();
        outline.setOval(0, 0, size, size);
        findViewById(R.id.arrow).setOutline(outline);*/
        //Button fab = (Button) findViewById(R.id.fab);
        //Outline outline = new Outline();
        //outline.setOval(0, 0, size, size);
        //fab.setOutline(outline);
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


        Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_arrow_back_24);

        left_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //l = (LinearLayout) findViewById(R.id.exitdialog);

                dialog = new Dialog(Nom.this);
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
                //arretCreation.setTextColor(R.color.light_orange);
                //arretCreation.setTextColor(Color.RED);
                arretCreation.setTextColor(getResources().getColor(R.color.light_orange));
                arretCreation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Nom.this, MainActivity.class);
                        startActivity(i);
                    }
                });
                dialog.show();
            }
        });
        reference = FirebaseDatabase.getInstance().getReference().child("users");

        suivant = (Button) findViewById(R.id.n_next);
        final int[] number_of_clicks = {0};
        suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reference=rootNode.getReference("users");
                //reference.setValue("first data ");
                //s_error = ((s_prenom.isEmpty() & s_nom.isEmpty())? "Veuillez entrer votre pr&nom et votre nom":)

                //String pre = edit_prenom.getText().toString().trim();
                //String name = edit_nom.getText().toString().trim();

                String pre = edit_prenom.getText().toString();
                String name = edit_nom.getText().toString();


                s_prenom = prenom.getEditText().getText().toString();
                s_nom = nom.getEditText().getText().toString();
                if (s_prenom.isEmpty() & s_nom.isEmpty()) {
                    s_error = "Veuillez entrer votre prénom et votre nom";
                    /*ViewGroup.LayoutParams params = error_space.getLayoutParams();
                    params.height = 60;
                    error_space.setLayoutParams(params);*/
                    //error_space.setError(s_error);
                    error.setText(s_error);
                    if (number_of_clicks[0] !=0) {
                        error.setVisibility(View.INVISIBLE);
                        //error.setVisibility(View.VISIBLE);
                        h1.postDelayed(error_msg, SPLASH_TIME_OUT);
                    }

                    ViewGroup.LayoutParams params = error.getLayoutParams();
                    params.height = 60;
                    //params.width = 100;
                    error.setLayoutParams(params);
                    if (number_of_clicks[0] == 0)
                        number_of_clicks[0]++;
                    //error.setVisibility(View.VISIBLE);

                    //----------------------- version 1 of error
                    /*error.setText(s_error);
                    ViewGroup.LayoutParams params = error.getLayoutParams();
                    params.height = 60;
                    //params.width = 100;
                    error.setLayoutParams(params);
                    error.setVisibility(View.VISIBLE);*/

                    /*Drawable dr = Nom.this.getResources().getDrawable(R.drawable.ic_baseline_error_24);
                    dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                    error.setError(error.getText(), dr);*/
                } else {
                    if (s_prenom.isEmpty()) {
                        s_error = "Entrez votre prénom";
                        /*ViewGroup.LayoutParams params = error_space.getLayoutParams();
                        params.height = 60;
                        error_space.setLayoutParams(params);*/
                        //error_space.setError(s_error);
                        if (number_of_clicks[0] !=0) {
                            error.setVisibility(View.INVISIBLE);
                            //error.setVisibility(View.VISIBLE);
                            h1.postDelayed(error_msg, SPLASH_TIME_OUT);
                        }
                        error.setText(s_error);
                        ViewGroup.LayoutParams params = error.getLayoutParams();
                        params.height = 60;
                        //params.width = 100;
                        error.setLayoutParams(params);
                        if (number_of_clicks[0] == 0)
                            number_of_clicks[0]++;
                        //error.setVisibility(View.VISIBLE);
                        //------------------------------- version 1 of error
                        /*error.setText(s_error);
                        ViewGroup.LayoutParams params = error.getLayoutParams();
                        params.height = 60;
                        //params.width = 100;
                        error.setLayoutParams(params);
                        error.setVisibility(View.VISIBLE);*/

                        /*Drawable dr = Nom.this.getResources().getDrawable(R.drawable.ic_baseline_error_24);
                        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                        error.setError(error.getText(), dr);*/
                    } else {
                        if (s_nom.isEmpty()) {

                            s_error = "Veuillez entrer votre nom de famille";
                            /*ViewGroup.LayoutParams params = error_space.getLayoutParams();
                            params.height = 60;
                            error_space.setLayoutParams(params);*/
                            //error_space.setError(s_error);
                            error.setText(s_error);
                            if (number_of_clicks[0] !=0) {
                                error.setVisibility(View.INVISIBLE);
                                //error.setVisibility(View.VISIBLE);
                                h1.postDelayed(error_msg, SPLASH_TIME_OUT);
                            }
                            ViewGroup.LayoutParams params = error.getLayoutParams();
                            params.height = 60;
                            //params.width = 100;
                            error.setLayoutParams(params);
                            if (number_of_clicks[0] == 0)
                                number_of_clicks[0]++;
                            //error.setVisibility(View.VISIBLE);
                            //-------------------------------version 1 of error
                            /*error.setText(s_error);
                            ViewGroup.LayoutParams params = error.getLayoutParams();
                            params.height = 60;
                            //params.width = 100;
                            error.setLayoutParams(params);
                            error.setVisibility(View.VISIBLE);*/

                            /*Drawable dr = Nom.this.getResources().getDrawable(R.drawable.ic_baseline_error_24);
                            dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                            error.setError(error.getText(), dr);*/
                        } else {
                            //-------------------------------- version 1 of error
                            /*ViewGroup.LayoutParams params = error.getLayoutParams();
                            params.height = 0;
                            //params.width = 100;
                            error.setLayoutParams(params);
                            error.setVisibility(View.GONE);*/
                            closeKeyboard();
                            //error_space.setError(null);
                            ViewGroup.LayoutParams params = error.getLayoutParams();
                            params.height = 0;
                            //params.width = 100;
                            error.setLayoutParams(params);
                            //error.setVisibility(View.VISIBLE);
                            //reference = FirebaseDatabase.getInstance().getReference().child("users");

                            //rootNode = FirebaseDatabase.getInstance();
                            //reference=rootNode.getReference().child("users");
                            //reference=rootNode.getReference("users");
                            //reference.setValue("hey there");
                            // get all the values
                            /*String first_name = prenom.getEditText().getText().toString();
                            String last_name = nom.getEditText().getText().toString();*/

                            //UserHelper helper = new UserHelper("hey","hey","","","","","");
                            //reference.child(first_name).setValue("first data ");
                            //reference.push().setValue(helper);
                            //reference.child("users").push().setValue(helper);

                            /*DatabaseReference userRef = ref.child("users");
                            DatabaseReference newuserRef = userRef.push();
                            newuserRef.setValueAsync(new UserHelper("hey","hey","hey","hey","hey","hey","hey"));*/
                            //userRed.ppush().setValueAsync()
                            /*Map<String,UserHelper> users = new HashMap<>();
                            users.put("aladoizej",new UserHelper("hey","heu","hey","hey","hey","hey","hey"));

                            userRef.setValueAsync(users);*/
                            //userRef.child("dezjioj").setValueAsync(new UserHelper("hey","hey","hey","hey","hey","hey","hey"));



                            /*Map<String, Object> user = new HashMap<>();
                            user.put("first", "Ada");
                            user.put("last", "Lovelace");
                            user.put("born", 1815);

                            // Add a new document with a generated ID
                            FirebaseFirestore db;
                            db = FirebaseFirestore.getInstance();
                            db.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        private static final String TAG = "oui :)";
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        private static final String TAG = "non";
                                        @SuppressLint("LongLogTag")
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });
                            //DocumentReference docref = db.collection("users").document("1");*/
                            /*error.setVisibility(View.INVISIBLE);
                            //error.setVisibility(View.VISIBLE);
                            h1.postDelayed(error_msg,SPLASH_TIME_OUT);*/
                            //test.setText("prénom: "+edit_prenom.getText().toString()+"/ nom:"+edit_nom.getText().toString());
                            Intent i = new Intent(Nom.this, Date_naissance.class);
                            i.putExtra("first_name",edit_prenom.getText().toString());
                            i.putExtra("last_name", edit_nom.getText().toString());
                            /*if (number_of_clicks[0] == 0)
                                number_of_clicks[0]++;*/
                            startActivity(i);
                        }

                    }
                }

                /*else {
                    if (s_prenom.isEmpty()) {
                        s_error = "Entrez votre prénom";
                        error.setText(s_error);
                        error.setVisibility(View.VISIBLE);
                    }
                    else {
                        if (s_nom.isEmpty()) {
                            s_error = "Veuillez entrer votre nom de famille";
                            error.setText(s_error);
                            error.setVisibility(View.VISIBLE);
                        }
                        else {
                            Intent i = new Intent(Nom.this, Date_naissance.class);
                            startActivity(i);
                        }
                    }
                }

                }*/
            }
        });
/*
        edit_prenom.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (s_error == "Entrez votre prénom"){
                    if ((s_prenom_before.isEmpty() & !s_prenom_after.isEmpty()) | (!s_prenom_before.isEmpty() & !s_prenom_after.isEmpty()))
                        error_space.setError(null);
                }
                if (s_error == "Veuillez entrer votre prénom et votre nom")
                    error_space.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                s_prenom_before = prenom.getEditText().getText().toString();
                prenom_before.setText("prenom_before: "+s_prenom_before);
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                s_prenom_after = prenom.getEditText().getText().toString();
                prenom_after.setText("prenom_after : "+s_prenom_after);
            }
        });

        edit_nom.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (s_error == "Veuillez entrer votre nom de famille"){
                    if ((s_nom_before.isEmpty() & !s_nom_after.isEmpty()) | (!s_nom_before.isEmpty() & !s_nom_after.isEmpty()))
                        error_space.setError(null);
                }
                if (s_error == "Veuillez entrer votre prénom et votre nom")
                    error_space.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                s_nom_before = nom.getEditText().getText().toString();
                current_prenom.setText("nom_before : "+s_nom_before);

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                s_nom_after = nom.getEditText().getText().toString();
                current_nom.setText("nom_after : "+s_nom_after);
            }
        });*/

        edit_prenom.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (s_error == "Veuillez entrer votre prénom et votre nom") {
                    //error_space.setError(null);
                    ViewGroup.LayoutParams params = error.getLayoutParams();
                    params.height = 0;
                    //params.width = 100;


                    //error.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                s_prenom_before = prenom.getEditText().getText().toString();
                //prenom_before.setText("prenom_before: "+s_prenom_before);
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                s_prenom_after = prenom.getEditText().getText().toString();
                if (s_error == "Entrez votre prénom"){
                    if ((s_prenom_before.isEmpty() & !s_prenom_after.isEmpty()) | (!s_prenom_before.isEmpty() & s_prenom_after.isEmpty())) {
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
        });

        edit_nom.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

                if (s_error == "Veuillez entrer votre prénom et votre nom") {
                    //error_space.setError(null);
                    ViewGroup.LayoutParams params = error.getLayoutParams();
                    params.height = 0;
                    //params.width = 100;
                    error.setLayoutParams(params);
                    //error.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                s_nom_before = nom.getEditText().getText().toString();
                //current_prenom.setText("nom_before : "+s_nom_before);

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                s_nom_after = nom.getEditText().getText().toString();
                if (s_error == "Veuillez entrer votre nom de famille"){
                    if ((s_nom_before.isEmpty() & !s_nom_after.isEmpty()) | (!s_nom_before.isEmpty() & s_nom_after.isEmpty())){
                        //error_space.setError(null);
                        ViewGroup.LayoutParams params = error.getLayoutParams();
                        params.height = 0;
                        //params.width = 100;
                        error.setLayoutParams(params);
                        //error.setVisibility(View.VISIBLE);
                    }
                }
                //current_nom.setText("nom_after : "+s_nom_after);
            }
        });


    }
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Nom.this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void openKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Nom.this.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edit_prenom,InputMethodManager.SHOW_FORCED);        }
    }

   /* public void basicReadWrite(){
        FirebaseDatabase databse = FirebaseDatabase.getInstance();
        DatabaseReference ref = databse.getReference("message");
        ref.setValue("hehehe");
    }*/
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

/*
    ImageButton left_arrow;
    View includedLayout;
includedLayout = findViewById(R.id.name);
        TextView title = (TextView )includedLayout.findViewById(R.id.toolbar_title);
        title.setText("Nom");
        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
@Override
public void getOutline(View view, Outline outline) {
        // Or read size directly from the view's width/height
        int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
        outline.setOval(5, 2, size, size);
        }
        };
        left_arrow.setOutlineProvider(viewOutlineProvider);
*/