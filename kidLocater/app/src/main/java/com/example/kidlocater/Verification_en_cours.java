package com.example.kidlocater;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Verification_en_cours extends AppCompatActivity {
    FirebaseAuth auth;
    Bundle extras;
    String final_false_email, final_passw;
    String finalFirst_name, finalLast_name, finalBirthday, finalGender;
    TextView f_em;
    FirebaseFirestore db;
    String email=null;
    Matcher matcher;
    Pattern pattern;
    //private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,})";
    private static final String PASSWORD_PATTERN = "(?=.*[a-z])";
    private static final String TAG = "hehehe";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verification_en_cours);
        db = FirebaseFirestore.getInstance();

        f_em = findViewById(R.id.false_email);
        extras = getIntent().getExtras();
        String error_affichage = null, exist_email = null, first_name = null, last_name = null, birthday = null, gender = null, passw = null, false_email = null;

        if (extras != null) {
            first_name = extras.getString("first_name");
            last_name = extras.getString("last_name");
            birthday = extras.getString("birthday");
            gender = extras.getString("gender");
            error_affichage = extras.getString("error");
            exist_email = extras.getString("exists_in_firestore");
            email = extras.getString("email");
            false_email = extras.getString("false_email");

            passw = extras.getString("password");
        }
        final_false_email = false_email;
        final_passw = passw;
        String final_Exists_email = exist_email;
        finalFirst_name = first_name;
        finalLast_name = last_name;
        finalBirthday = birthday;
        finalGender = gender;
        String finalExists_in_firestore = exist_email;//exists_in_firestore;
        //f_em.setText(final_false_email);

        auth = FirebaseAuth.getInstance();
        Handler h = new Handler();
        h.postDelayed(post, 2000);
        }
/*
    private Runnable post = new Runnable() {
        @Override
        public void run() {
            String first_name = null, last_name = null, birthday = null, gender = null, email = null, phone = null, passw = null;

            Bundle extras = getIntent().getExtras();
            //String first_name = null, last_name = null, birthday = null, gender = null, email = null, phone = null, passw = null;
            //String email = null, phone = null;
            if (extras != null) {
                first_name = extras.getString("first_name");
                last_name = extras.getString("last_name");
                birthday = extras.getString("birthday");
                gender = extras.getString("gender");
                email = extras.getString("email");
                phone = extras.getString("phone_number");
                passw = extras.getString("password");
            }
            String finalFirst_name = first_name;
            String finalLast_name = last_name;
            String finalBirthday = birthday;
            String finalGender = gender;
            String finalEmail = email;
            String finalPhone = phone;
            String finalPassw = passw;
            if (finalEmail != null) {
                auth.createUserWithEmailAndPassword(finalEmail, finalPassw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // if email is already existing in the authentification database or if password is weak or if email is malformed
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            }
                            // if user enters wrong password.
                            catch (FirebaseAuthWeakPasswordException weakPassword) {
                                String TAG = "weak password";
                                Log.d(TAG, "onComplete: weak_password");
                                // TODO: take your actions!
                            }
                            // if user enters wrong email.
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                String TAG = "malfomed email";
                                Log.d(TAG, "onComplete: malformed_email");
                                Intent i = new Intent(Verification_en_cours.this, Email.class);
                                i.putExtra("erroraffichage", "affiche");
                                i.putExtra("false_email", finalEmail);
                                i.putExtra("password", finalPassw);
                                Toast.makeText(Verification_en_cours.this, "email mal formé .",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(i);
                            } catch (FirebaseAuthUserCollisionException existEmail) {
                                FirebaseUser current_user = auth.getCurrentUser();
                                Query already_existing_email = db.collection("users").whereEqualTo("e-mail", finalEmail);
                                already_existing_email.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            current_user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Map<String, Object> user = new HashMap<>();
                                                    user.put("prénom", finalFirst_name);
                                                    user.put("nom de famille", finalLast_name);
                                                    user.put("date de naissance", finalBirthday);
                                                    user.put("genre", finalGender);
                                                    //if (finalEmail != null)
                                                    user.put("e-mail", finalEmail);
                                                    user.put("mot de passe", finalPassw);

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
                                                    Toast.makeText(Verification_en_cours.this, "email already exists in authentification and email added to firestore",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                });
                                String TAG = "existing email";
                                Log.d(TAG, "onComplete: exist_email");
                                Intent i = new Intent(Verification_en_cours.this, Email.class);
                                i.putExtra("erroraffichage", "affiche");
                                i.putExtra("exists", finalEmail);
                                i.putExtra("password", finalPassw);
                                i.putExtra("first_name", finalFirst_name);
                                i.putExtra("last_name", finalLast_name);
                                i.putExtra("birthday", finalBirthday);
                                i.putExtra("gender", finalGender);
                                i.putExtra("password", finalPassw);
                                i.putExtra("exists_in_firestore", finalEmail);
                                Toast.makeText(Verification_en_cours.this, "email deja existant .",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(i);
                                // TODO: Take your action
                            } catch (Exception e) {
                                String TAG = "onComplete";
                                Log.d(TAG, "onComplete: " + e.getMessage());
                                Toast.makeText(Verification_en_cours.this, "unknown exception",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        // add email to authentification database
                        else {
                            FirebaseUser current_user = auth.getCurrentUser();
                            Query already_existing_email = db.collection("users").whereEqualTo("e-mail", finalEmail);
                            already_existing_email.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Boolean doesnt_exist = task.getResult().isEmpty();
                                        if (doesnt_exist) {//if email is not in forestore , send email to see if it's a real email adress
                                            current_user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Map<String, Object> user = new HashMap<>();
                                                    user.put("prénom", finalFirst_name);
                                                    user.put("nom de famille", finalLast_name);
                                                    user.put("date de naissance", finalBirthday);
                                                    user.put("genre", finalGender);
                                                    //if (finalEmail != null)
                                                    user.put("e-mail", finalEmail);
                                                    user.put("mot de passe", finalPassw);

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
                                                    Toast.makeText(Verification_en_cours.this, "Authentication successful and user added to firebase",
                                                            Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(Verification_en_cours.this,FinInscription.class);
                                                    startActivity(i);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Intent i = new Intent(Verification_en_cours.this, Email.class);
                                                    i.putExtra("erroraffichage", "affiche");
                                                    i.putExtra("exists", finalEmail);
                                                    i.putExtra("password", finalPassw);
                                                    i.putExtra("first_name", finalFirst_name);
                                                    i.putExtra("last_name", finalLast_name);
                                                    i.putExtra("birthday", finalBirthday);
                                                    Boolean doesnt_exist = task.getResult().isEmpty();
                                                    i.putExtra("gender", finalGender);
                                                    i.putExtra("password", finalPassw);
                                                    i.putExtra("exists_in_firestore", finalEmail);
                                                    Toast.makeText(Verification_en_cours.this, "email n'existe pas existant .",
                                                            Toast.LENGTH_SHORT).show();
                                                    startActivity(i);
                                                }
                                            });
                                        } else {
                                            Toast.makeText(Verification_en_cours.this, "email already in firestore",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
    };*/


    private Runnable post = new Runnable() {
        @Override
        public void run() {
            /*if (db.collection("users").whereEqualTo("e-mail",final_false_email)!=null) {
                Intent i = new Intent(Verification_en_cours.this, Email.class);
                i.putExtra("first_name", finalFirst_name);
                i.putExtra("last_name", finalLast_name);
                i.putExtra("birthday", finalBirthday);
                i.putExtra("gender", finalGender);
                i.putExtra("erroraffichage","affiche");
                i.putExtra("exists_in_firestore",final_false_email);
                i.putExtra("password",final_passw);
                startActivity(i);
                finish();
                return;
            }*/
            //else {
                Map<String, Object> user = new HashMap<>();
                user.put("prénom", finalFirst_name);
                user.put("nom de famille", finalLast_name);
                user.put("date de naissance", finalBirthday);
                user.put("genre", finalGender);
                if (final_false_email != null)
                    user.put("e-mail", final_false_email);
                user.put("mot de passe", final_passw);


            //}
            String e = (final_false_email==null? email:final_false_email);
            auth.createUserWithEmailAndPassword(e,final_passw)
                    .addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    /*pattern = Pattern.compile(PASSWORD_PATTERN);
                                    matcher = pattern.matcher(final_passw);
                                    if (!matcher.matches()){
                                        Intent i = new Intent(Verification_en_cours.this, Mot_de_passe.class);
                                        i.putExtra("erroraffichage", "affiche");
                                        i.putExtra("false_email", final_false_email);
                                        i.putExtra("password", final_passw);
                                        i.putExtra("email",final_false_email);
                                        i.putExtra("first_name",finalFirst_name);
                                        i.putExtra("last_name",finalLast_name);
                                        i.putExtra("birthday",finalBirthday);
                                        i.putExtra("gender",finalGender);
                                        Toast.makeText(Verification_en_cours.this, "mot de passe faible .",
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(i);
                                        return;
                                    }*/
                                    if (!task.isSuccessful()) {
                                        try {
                                            throw task.getException();
                                        }
                                        // if user enters wrong email.
                                        catch (FirebaseAuthWeakPasswordException weakPassword) {
                                            Log.d(TAG, "onComplete: weak_password");
                                            Intent i = new Intent(Verification_en_cours.this, Mot_de_passe.class);
                                            i.putExtra("erroraffichage", "affiche");
                                            i.putExtra("false_email", final_false_email);
                                            i.putExtra("password", final_passw);
                                            i.putExtra("email",final_false_email);
                                            i.putExtra("first_name",finalFirst_name);
                                            i.putExtra("last_name",finalLast_name);
                                            i.putExtra("birthday",finalBirthday);
                                            i.putExtra("gender",finalGender);
                                            Toast.makeText(Verification_en_cours.this, "mot de passe faible .",
                                                    Toast.LENGTH_SHORT).show();
                                            startActivity(i);

                                            // TODO: take your actions!
                                        }
                                        // if user enters wrong password.
                                        catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                            Log.d(TAG, "onComplete: malformed_email");
                                            Toast.makeText(Verification_en_cours.this, "email doesn't exist.", Toast.LENGTH_SHORT).show();

                                            Log.d(TAG, "onComplete: malformed_email");
                                            Intent i = new Intent(Verification_en_cours.this, Email.class);
                                            i.putExtra("erroraffichage", "affiche");
                                            i.putExtra("false_email", final_false_email);
                                            i.putExtra("password", final_passw);
                                            i.putExtra("email",final_false_email);
                                            i.putExtra("first_name",finalFirst_name);
                                            i.putExtra("last_name",finalLast_name);
                                            i.putExtra("birthday",finalBirthday);
                                            i.putExtra("gender",finalGender);

                                            startActivity(i);
                                            finish();
                                        } catch (FirebaseAuthUserCollisionException existEmail) {
                                            Toast.makeText(Verification_en_cours.this, "don't know what that is", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onComplete: exist_email");
                                            Intent i = new Intent(Verification_en_cours.this, Email.class);
                                            i.putExtra("erroraffichage", "affiche");
                                            i.putExtra("false_email", final_false_email);
                                            i.putExtra("password", final_passw);
                                            i.putExtra("email",final_false_email);
                                            i.putExtra("first_name",finalFirst_name);
                                            i.putExtra("last_name",finalLast_name);
                                            i.putExtra("birthday",finalBirthday);
                                            i.putExtra("gender",finalGender);
                                            startActivity(i);
                                            finish();

                                            // TODO: Take your action
                                        } catch (Exception e) {
                                            Toast.makeText(Verification_en_cours.this, "last resort.", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onComplete: " + e.getMessage());

                                        }
                                    } else {

                                        FirebaseUser current_user = auth.getCurrentUser();
                                        Query already_existing_email = db.collection("users").whereEqualTo("e-mail", final_false_email);
                                        already_existing_email.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                Boolean doesnt_exist = task.getResult().isEmpty();
                                                if (doesnt_exist) {//if email is not in forestore , send email to see if it's a real email adress
                                                    current_user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            db.collection("users")
                                                                    .add(user)
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                        private static final String TAG = "oui :)";

                                                                        @Override
                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                                        }
                                                                    });
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
                                                Toast.makeText(Verification_en_cours.this, "compte créé", Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(Verification_en_cours.this, MainActivity.class);
                                                startActivity(i);
                                        }
                                        }
                                        }
                                        });
                                        
                                    }
                                }
                            }
                    );

            /*auth.signInWithCustomToken(final_false_email)
                    .addOnCompleteListener(Verification_en_cours.this, new OnCompleteListener<AuthResult>() {
                        //private static final String TAG ="hehe" ;
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "signInWithCustomToken:success");
                                FirebaseUser user = auth.getCurrentUser();
                                //updateUI(user);
                                if(user.isEmailVerified())
                                    //redirect to user profile if email is verified
                                    Toast.makeText(Verification_en_cours.this, "email verified.", Toast.LENGTH_SHORT).show();
                                else {
                                    // sending an email
                                    user.sendEmailVerification();
                                    Toast.makeText(Verification_en_cours.this, "verification email is sent", Toast.LENGTH_SHORT).show();
                                    // If sign in fails, display a message to the user.
                                    //Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                                    Intent i = new Intent(Verification_en_cours.this, Email.class);
                                    i.putExtra("false_email",final_false_email);
                                    startActivity(i);
                                    //Toast.makeText(FinInscription.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                //Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                                Intent i = new Intent(Verification_en_cours.this, Email.class);
                                i.putExtra("false_email",final_false_email);
                                startActivity(i);
                                Toast.makeText(Verification_en_cours.this, "make sure you have the right adress", Toast.LENGTH_SHORT).show();
                                //Toast.makeText(FinInscription.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });*/
        }
    };

}