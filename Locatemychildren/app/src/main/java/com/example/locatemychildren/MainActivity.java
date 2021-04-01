package com.example.locatemychildren;

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

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private ImageView accueil;
    private TextInputLayout textInputLayout;
    private Button connexion,nv_compte ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        // to make the password invisible while using the eye sign that shows and hides the password
        textInputLayout = findViewById(R.id.password);
        textInputLayout.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());

        accueil = (ImageView)findViewById(R.id.accueil);
        Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(),R.drawable.kids1);
        int nh = (int) ( bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
        accueil.setImageBitmap(scaled);

        connexion = (Button) findViewById(R.id.connexion);
        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.connexion), Toast.LENGTH_SHORT).show();
            }
        });

        nv_compte = (Button) findViewById(R.id.nv_cpt);
        nv_compte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Nom.class);
                startActivity(i);
            }
        });


    }

}
