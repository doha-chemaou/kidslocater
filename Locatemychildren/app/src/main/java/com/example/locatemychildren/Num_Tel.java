package com.example.locatemychildren;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Num_Tel extends AppCompatActivity {
    Button suivant;
    TextView mail;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.num_tel);

        mail = (TextView) findViewById(R.id.email);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Num_Tel.this, Email.class);
                startActivity(i);
            }
        });

        suivant = (Button) findViewById(R.id.p_next);
        suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Num_Tel.this, Mot_de_passe.class);
                startActivity(i);
            }
        });
    }
}
