package com.example.locatemychildren;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Mot_de_passe extends AppCompatActivity {
    Button suivant;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mot_de_passe);

        suivant = (Button) findViewById(R.id.pass_next);
        suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Mot_de_passe.this, Mot_de_passe.class);
                startActivity(i);
            }
        });
    }
}

