package com.example.kidlocater;
//kjk
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

public class MainActivity extends AppCompatActivity {

    private ImageView accueil;
    private TextInputLayout password,tel_email;
    private Button connexion,nv_compte ;
    FirebaseUser user;
    DocumentReference ref;
    String userId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);


        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseFirestore.getInstance().collection("users").document();
        userId = user.getUid();
        // to make the password invisible while using the eye sign that shows and hides the password
        password = findViewById(R.id.password);
        //textInputLayout.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
        tel_email = findViewById(R.id.tel_email);

        accueil = (ImageView)findViewById(R.id.logo_image);
        /*Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(),R.drawable.kids1);
        int nh = (int) ( bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
        accueil.setImageBitmap(scaled);*/


        connexion = (Button) findViewById(R.id.connexion);
        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s_tel_email = tel_email.getEditText().getText().toString();
                String s_password = password.getEditText().getText().toString();
                if (s_tel_email.isEmpty() | s_password.isEmpty()) {
                    return;}
                else{
                    FirebaseFirestore.getInstance().collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    String email = doc.getString("e-mail");
                                    String password = doc.getString("mot de passe");
                                    if (email.equalsIgnoreCase(s_tel_email) && password.equalsIgnoreCase(s_password))
                                        Toast.makeText(MainActivity.this, "connexion réussie", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(MainActivity.this, "assurez vous que votre adresse mail et votre mot de passe soient correctes", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(MainActivity.this, "something's wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    /*
                    ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserHelper userp= snapshot.getValue(UserHelper.class);
                            if(userp!=null){
                                String passw = userp.password;
                                String email = userp.email;
                                if (passw == s_password && email == s_tel_email){
                                    Toast.makeText(MainActivity.this, "connexion réussie", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(MainActivity.this, "les coordonénes rentrées sont fausses", Toast.LENGTH_SHORT).show();
                                }
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MainActivity.this, "something's wrong with the user ", Toast.LENGTH_SHORT).show();
                        }
                    });*/
                }
                //textInputEmail.setError("Field can't be empty");
                //Toast.makeText(getApplicationContext(), getResources().getText(R.string.connexion), Toast.LENGTH_SHORT).show();
            }
        });

        nv_compte = (Button) findViewById(R.id.nv_cpt);
        nv_compte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Welcome.class);
                startActivity(i);
            }
        });


    }

}
