package com.example.kidlocater;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class FinInscription extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth auth;
    Button inscr;
    TextView t_email;
    //private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,})";
    private static final String PASSWORD_PATTERN = "(?=.*[a-z])";
    Matcher matcher;
    Pattern pattern;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {//}, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fin_inscription);
        /**/
        t_email = findViewById(R.id.email);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        inscr = findViewById(R.id.inscr);
        db = FirebaseFirestore.getInstance();
        inscr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** récupération des données à partir des autres activités **/
                Bundle extras = getIntent().getExtras();
                String first_name = null, last_name = null, birthday = null, gender = null, email = null, phone = null, passw = null;
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
                            /*pattern = Pattern.compile(PASSWORD_PATTERN);
                            matcher = pattern.matcher(finalPassw);
                            if (!matcher.matches()){
                                Intent i = new Intent(FinInscription.this, Mot_de_passe.class);
                                i.putExtra("erroraffichage", "affiche");
                                i.putExtra("false_email", finalEmail);
                                i.putExtra("password", finalPassw);
                                i.putExtra("email",finalEmail);
                                i.putExtra("first_name",finalFirst_name);
                                i.putExtra("last_name",finalLast_name);
                                i.putExtra("birthday",finalBirthday);
                                i.putExtra("gender",finalGender);
                                Toast.makeText(FinInscription.this, "mot de passe faible .",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(i);
                                return;
                            }*/
                            if (!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                }
                                // if user enters wrong password.
                                catch (FirebaseAuthWeakPasswordException weakPassword) {
                                    String TAG = "weak password";

                                    Log.d(TAG, "onComplete: weak_password");
                                    // TODO: take your actions!
                                    Intent i = new Intent(FinInscription.this, Mot_de_passe.class);
                                    i.putExtra("erroraffichage", "affiche");
                                    i.putExtra("email", finalEmail);
                                    i.putExtra("password", finalPassw);
                                    i.putExtra("first_name", finalFirst_name);
                                    i.putExtra("last_name", finalLast_name);
                                    i.putExtra("birthday", finalBirthday);
                                    i.putExtra("gender", finalGender);
                                    i.putExtra("password", finalPassw);
                                    i.putExtra("exists_in_firestore", finalEmail);
                                    Toast.makeText(FinInscription.this, "mot de passe faible .",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(i);
                                }
                                // if user enters wrong email.
                                catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                    String TAG = "malfomed email";
                                    Log.d(TAG, "onComplete: malformed_email");

                                } catch (FirebaseAuthUserCollisionException existEmail) {
                                    /*FirebaseUser current_user = auth.getCurrentUser();
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
                                                        Toast.makeText(FinInscription.this, "email already exists in authentification and email added to firestore",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });*/
                                    String TAG = "existing email";
                                    Log.d(TAG, "onComplete: exist_email");
                                    Intent i = new Intent(FinInscription.this, Email.class);
                                    i.putExtra("erroraffichage", "affiche");
                                    i.putExtra("exists", finalEmail);
                                    i.putExtra("password", finalPassw);
                                    i.putExtra("first_name", finalFirst_name);
                                    i.putExtra("last_name", finalLast_name);
                                    i.putExtra("birthday", finalBirthday);
                                    i.putExtra("gender", finalGender);
                                    i.putExtra("password", finalPassw);
                                    i.putExtra("exists_in_firestore", finalEmail);
                                    Toast.makeText(FinInscription.this, "email deja existant .",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(i);
                                    // TODO: Take your action
                                } catch (Exception e) {
                                    String TAG = "onComplete";
                                    Log.d(TAG, "onComplete: " + e.getMessage());
                                    Toast.makeText(FinInscription.this, "unknown exception",
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
                                                                        Intent i = new Intent(FinInscription.this,MainActivity.class);
                                                                        startActivity(i);
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
                                                        Toast.makeText(FinInscription.this, "compte créé",
                                                                Toast.LENGTH_SHORT).show();


                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Intent i = new Intent(FinInscription.this, Email.class);
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
                                                        Toast.makeText(FinInscription.this, "email n'existe pas existant .",
                                                                Toast.LENGTH_SHORT).show();
                                                        startActivity(i);
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(FinInscription.this, "email already in firestore",
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
        });
    }
}
        /*inscr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // récupération des données à partir des autres activités
                Bundle extras = getIntent().getExtras();
                String first_name = null, last_name = null, birthday = null, gender = null, email = null, phone = null, passw = null;
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
                                    Intent i = new Intent(FinInscription.this, Email.class);
                                    i.putExtra("erroraffichage", "affiche");
                                    i.putExtra("false_email", finalEmail);
                                    i.putExtra("password", finalPassw);
                                    Toast.makeText(FinInscription.this, "email mal formé .",
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
                                                        Toast.makeText(FinInscription.this, "email already exists in authentification and email added to firestore",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    String TAG = "existing email";
                                    Log.d(TAG, "onComplete: exist_email");
                                    Intent i = new Intent(FinInscription.this, Email.class);
                                    i.putExtra("erroraffichage", "affiche");
                                    i.putExtra("exists", finalEmail);
                                    i.putExtra("password", finalPassw);
                                    i.putExtra("first_name", finalFirst_name);
                                    i.putExtra("last_name", finalLast_name);
                                    i.putExtra("birthday", finalBirthday);
                                    i.putExtra("gender", finalGender);
                                    i.putExtra("password", finalPassw);
                                    i.putExtra("exists_in_firestore", finalEmail);
                                    Toast.makeText(FinInscription.this, "email deja existant .",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(i);
                                    // TODO: Take your action
                                } catch (Exception e) {
                                    String TAG = "onComplete";
                                    Log.d(TAG, "onComplete: " + e.getMessage());
                                    Toast.makeText(FinInscription.this, "unknown exception",
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
                                                        Toast.makeText(FinInscription.this, "Authentication successful and user added to firebase",
                                                                Toast.LENGTH_SHORT).show();

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Intent i = new Intent(FinInscription.this, Email.class);
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
                                                        Toast.makeText(FinInscription.this, "email n'existe pas existant .",
                                                                Toast.LENGTH_SHORT).show();
                                                        startActivity(i);
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(FinInscription.this, "email already in firestore",
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
        });
    }*/

/*        inscr.setOnClickListener(new View.OnClickListener() {
            private static final String TAG = "hehehe" ;
            @Override
            public void onClick(View v) {
                // récupération des données à partir des autres activités
                Bundle extras = getIntent().getExtras();
                String first_name=null,last_name=null,birthday=null,gender=null,email=null,phone=null,passw=null;
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
                String finalPassw = passw;

                if (finalEmail!=null) {
                    auth.createUserWithEmailAndPassword(finalEmail, finalPassw)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    try {
                                        throw task.getException();
                                    }
                                    // if user enters wrong password.
                                    catch (FirebaseAuthWeakPasswordException weakPassword) {
                                        Log.d(TAG, "onComplete: weak_password");

                                        // TODO: take your actions!
                                    }
                                    // if user enters wrong email.
                                    catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                        Log.d(TAG, "onComplete: malformed_email");
                                        Intent i = new Intent(FinInscription.this, Email.class);
                                        i.putExtra("erroraffichage", "affiche");
                                        i.putExtra("false_email", finalEmail);
                                        i.putExtra("password", finalPassw);
                                        Toast.makeText(FinInscription.this, "email mal formé .",
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(i);

                                    } catch (FirebaseAuthUserCollisionException existEmail) {
                                        Log.d(TAG, "onComplete: exist_email");
                                        Log.d(TAG, "onComplete: malformed_email");
                                        Intent i = new Intent(FinInscription.this, Email.class);
                                        i.putExtra("erroraffichage", "affiche");
                                        i.putExtra("exists", finalEmail);
                                        i.putExtra("password", finalPassw);
                                        i.putExtra("first_name", finalFirst_name);
                                        i.putExtra("last_name", finalLast_name);
                                        i.putExtra("birthday", finalBirthday);
                                        i.putExtra("gender", finalGender);
                                        i.putExtra("password", finalPassw);
                                        i.putExtra("exists_in_firestore", finalEmail);
                                        Toast.makeText(FinInscription.this, "email deja existant .",
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(i);

                                        // TODO: Take your action
                                    } catch (Exception e) {
                                        Log.d(TAG, "onComplete: " + e.getMessage());
                                        Toast.makeText(FinInscription.this, "unknown exception",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                    FirebaseUser current_user = auth.getCurrentUser();
                                } else {
                                    FirebaseUser current_user = auth.getCurrentUser();
                                    //assert current_user != null;
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
                                                    Toast.makeText(FinInscription.this, "Authentication successful and user added to firebase",
                                                            Toast.LENGTH_SHORT).show();
                                                    }}).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Intent i = new Intent(FinInscription.this, Email.class);
                                                            i.putExtra("erroraffichage", "affiche");
                                                            i.putExtra("exists", finalEmail);
                                                            i.putExtra("password", finalPassw);
                                                            i.putExtra("first_name", finalFirst_name);
                                                            i.putExtra("last_name", finalLast_name);
                                                            i.putExtra("birthday", finalBirthday);
                                                            i.putExtra("gender", finalGender);
                                                            i.putExtra("password", finalPassw);
                                                            i.putExtra("exists_in_firestore", finalEmail);
                                                            Toast.makeText(FinInscription.this, "email n'existe pas existant .",
                                                                    Toast.LENGTH_SHORT).show();
                                                            startActivity(i);
                                                        }});
                                                    }
                                                }
                                            });
                                        }
                                    }});}

                final String[] hey = new String[1];

    }});*/

   /* public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        //updateUI(currentUser);
    }*/

/*Query already_existing_email = db.collection("users").whereEqualTo("e-mail", finalEmail);
                        already_existing_email.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Boolean doesnt_exist = task.getResult().isEmpty();
                                    if (doesnt_exist) {
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
                                    }*/
/** si l'e-mail est dans la base de données , redirection vers l'activité qui permet de rentrer un e-mail (marche pas)**/
                                    /*else {
                                        if (finalEmail != null) {
                                            Intent i = new Intent(FinInscription.this, Email.class);
                                            //i.putExtra("erroraffichage", "affiche");
                                            i.putExtra("first_name", finalFirst_name);
                                            i.putExtra("last_name", finalLast_name);
                                            i.putExtra("birthday", finalBirthday);
                                            i.putExtra("gender", finalGender);
                                            i.putExtra("password", finalPassw);
                                            i.putExtra("exists_in_firestore", finalEmail);
                                            startActivity(i);
                                        }
                                    }
                                }}});*/
/**Map<String, Object> user = new HashMap<>();
 user.put("prénom", finalFirst_name);
 user.put("nom de famille", finalLast_name);
 user.put("date de naissance", finalBirthday);
 user.put("genre", finalGender);
 if (finalEmail!=null)
 user.put("e-mail", finalEmail);
 if (finalPhone!=null)
 user.put("téléphone", finalPhone);
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
 });**/

                    /*final FirebaseUser useruser = auth.getCurrentUser();
                    useruser.sendEmailVerification()
                            .addOnCompleteListener(FinInscription.this, new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    // Re-enable button
                                    //findViewById(R.id.verify_email_button).setEnabled(true);

                                    if (task.isSuccessful()) {
                                        Toast.makeText(FinInscription.this,
                                                "Verification email sent to " + useruser.getEmail(),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e(TAG, "sendEmailVerification", task.getException());
                                        Toast.makeText(FinInscription.this,
                                                "Failed to send verification email.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });*/




                /*auth.signInWithCustomToken(finalEmail)
                        .addOnCompleteListener(FinInscription.this, new OnCompleteListener<AuthResult>() {
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
                                    Intent i = new Intent(FinInscription.this, Email.class);
                                    i.putExtra("erroraffichage","affiche");
                                    i.putExtra("false_email",finalEmail);
                                    startActivity(i);
                                    //Toast.makeText(FinInscription.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });*/



/** si l'e-mail n'est pas utilisé par un compte qui est dans la base de données **/
                /*if(finalEmail!=null) {
                    Query already_existing_email = db.collection("users").whereEqualTo("e-mail", finalEmail);
                    already_existing_email.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Boolean doesnt_exist = task.getResult().isEmpty();
                                if (doesnt_exist) {
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
                                }*/
/** si l'e-mail est dans la base de données , redirection vers l'activité qui permet de rentrer un e-mail (marche pas)**/
                                /*else {
                                    if (finalEmail!=null) {
                                        Intent i = new Intent(FinInscription.this, Email.class);
                                        //i.putExtra("erroraffichage", "affiche");
                                        i.putExtra("first_name", finalFirst_name);
                                        i.putExtra("last_name", finalLast_name);
                                        i.putExtra("birthday", finalBirthday);
                                        i.putExtra("gender", finalGender);
                                        i.putExtra("password", finalPassw);
                                        i.putExtra("exists_in_firestore", finalEmail);
                                        startActivity(i);
                                    }
                            }
                            }
                            else{
                                Toast.makeText(FinInscription.this, "couldn't figure out an action",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }*/

                /*if((db.collection("users").whereEqualTo("e-mail",finalEmail))==null && finalEmail!=null) {//;//where('e-mail', '==', finalEmail);
                    Map<String, Object> user = new HashMap<>();
                    user.put("prénom", finalFirst_name);
                    user.put("nom de famille", finalLast_name);
                    user.put("date de naissance", finalBirthday);
                    user.put("genre", finalGender);
                    //if (finalEmail != null)
                    user.put("e-mail", finalEmail);
                    //if (finalPhone != null)
                        //user.put("téléphone", finalPhone);
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
                }*/
/** si l'e-mail est dans la base de données , redirection vers l'activité qui permet de rentrer un e-mail (marche pas)**/
                /*else {
                    if (finalEmail!=null) {
                        Intent i = new Intent(FinInscription.this, Email.class);
                        //i.putExtra("erroraffichage", "affiche");
                        i.putExtra("first_name", finalFirst_name);
                        i.putExtra("last_name", finalLast_name);
                        i.putExtra("birthday", finalBirthday);
                        i.putExtra("gender", finalGender);
                        i.putExtra("password", finalPassw);
                        i.putExtra("exists_in_firestore", finalEmail);
                        startActivity(i);
                    }
                }*/

//t_email.setText(finalEmail+" here's the email");

/*auth.createUserWithEmailAndPassword(finalEmail, finalPassw)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                             @Override
                           Bundle extras = getIntent().getExtras();
                String first_name=null,last_name=null,birthday=null,gender=null,email=null,phone=null,passw=null;
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

                Map<String, Object> user = new HashMap<>();
                user.put("prénom", finalFirst_name);
                user.put("nom de famille", finalLast_name);
                user.put("date de naissance", finalBirthday);
                user.put("genre", finalGender);
                if (finalEmail!=null)
                    user.put("e-mail", finalEmail);
                if (finalPhone!=null)
                    user.put("téléphone", finalPhone);
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
                        });  public void onComplete(@NonNull Task<AuthResult> task) {
                                 if (task.isSuccessful()){
                                     auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             if (task.isSuccessful()){
                                                 hey[0] = "hey";
                                                 Toast.makeText(FinInscription.this, "email sent successfully", Toast.LENGTH_SHORT).show();
                                             }
                                             else{
                                                 Intent i = new Intent(FinInscription.this,Email.class);
                                                 startActivity(i);
                                             }
                                         }
                                     });
                                 }
                                 else{
                                     Intent i = new Intent(FinInscription.this,Email.class);
                                     startActivity(i);
                                 }
                             }
                         }
                );*/
                /*auth.signInWithCustomToken(finalEmail)
                        .addOnCompleteListener(FinInscription.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                            }
                        });*/
                /*if (finalEmail!=null) {
                    auth.createUserWithEmailAndPassword(finalEmail,finalPassw)
                    .addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        try {
                            throw task.getException();
                        }
                        // if user enters wrong password.
                        catch (FirebaseAuthWeakPasswordException weakPassword)
                        {
                        Log.d(TAG, "onComplete: weak_password");

                        // TODO: take your actions!
                        }
                        // if user enters wrong email.
                        catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                            Log.d(TAG, "onComplete: malformed_email");
                            Intent i = new Intent(FinInscription.this, Email.class);
                            i.putExtra("erroraffichage", "affiche");
                            i.putExtra("false_email", finalEmail);
                            i.putExtra("password",finalPassw);
                            Toast.makeText(FinInscription.this, "email mal formé .",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(i);

                        } catch (FirebaseAuthUserCollisionException existEmail) {
                            Log.d(TAG, "onComplete: exist_email");
                            Log.d(TAG, "onComplete: malformed_email");
                            Intent i = new Intent(FinInscription.this, Email.class);
                            i.putExtra("erroraffichage", "affiche");
                            i.putExtra("exists", finalEmail);
                            i.putExtra("password",finalPassw);
                            i.putExtra("first_name", finalFirst_name);
                            i.putExtra("last_name", finalLast_name);
                            i.putExtra("birthday", finalBirthday);
                            i.putExtra("gender", finalGender);
                            i.putExtra("password", finalPassw);
                            i.putExtra("exists_in_firestore", finalEmail);
                            Toast.makeText(FinInscription.this, "email deja existant .",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(i);

                            // TODO: Take your action
                        } catch (Exception e) {
                            Log.d(TAG, "onComplete: " + e.getMessage());
                            Toast.makeText(FinInscription.this, "unknown exception",
                                    Toast.LENGTH_SHORT).show();
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
                        FirebaseUser current_user = auth.getCurrentUser();
                    }
                    else {
                        FirebaseUser current_user = auth.getCurrentUser();
                        assert current_user != null;
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
                                Toast.makeText(FinInscription.this, "Authentication successful and user added to firebase",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Intent i = new Intent(FinInscription.this, Email.class);
                                i.putExtra("erroraffichage", "affiche");
                                i.putExtra("exists", finalEmail);
                                i.putExtra("password",finalPassw);
                                i.putExtra("first_name", finalFirst_name);
                                i.putExtra("last_name", finalLast_name);
                                i.putExtra("birthday", finalBirthday);
                                i.putExtra("gender", finalGender);
                                i.putExtra("password", finalPassw);
                                i.putExtra("exists_in_firestore", finalEmail);
                                Toast.makeText(FinInscription.this, "email n'existe pas existant .",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(i);
                            }
                        });
                    }}});
                }*/


                /*auth.signInWithCustomToken(finalEmail)
                        .addOnCompleteListener(FinInscription.this, new OnCompleteListener<AuthResult>() {
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
                                    Intent i = new Intent(FinInscription.this, Email.class);
                                    i.putExtra("erroraffichage","affiche");
                                    i.putExtra("false_email",finalEmail);
                                    startActivity(i);
                                    //Toast.makeText(FinInscription.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });*/


/*
        Bundle extras = getIntent().getExtras();
        String first_name=null,last_name=null,birthday=null,gender=null,email=null,phone=null,passw=null;
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

        Map<String, Object> user = new HashMap<>();
        user.put("nom", finalLast_name);
        user.put("prénom", finalFirst_name);
        user.put("date de naissance", finalBirthday);
        user.put("genre", finalGender);
        if (finalEmail != null)
            user.put("e-mail", finalEmail);
        if (finalPhone != null)
            user.put("numéro de téléphone", finalPhone);
        user.put("mot de passe", finalPassw
        );

        //user.put("born", 1815);

        // Add a new document with a generated ID
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
                });*/
//DocumentReference docref = db.collection("users").document("1");
/**/


/*
    private void updateUI(FirebaseUser user) {
        if (user!=null)
            startActivity(new Intent(FinInscription.this,Email.class));

    }
*/