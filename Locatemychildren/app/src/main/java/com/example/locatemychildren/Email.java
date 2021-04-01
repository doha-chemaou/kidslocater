package com.example.locatemychildren;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Email extends AppCompatActivity {
    Button suivant;
    TextView phone;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email);

        phone = (TextView) findViewById(R.id.phone);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Email.this, Num_Tel.class);
                startActivity(i);
            }
        });

        suivant = (Button) findViewById(R.id.e_next);
        suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Email.this, Mot_de_passe.class);
                startActivity(i);
            }
        });
    }
}
