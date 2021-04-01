package com.example.locatemychildren;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Nom extends AppCompatActivity {
    Button suivant;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nom);

        suivant = (Button) findViewById(R.id.n_next);
        suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Nom.this, Date_naissance.class);
                startActivity(i);
            }
        });
    }
}
