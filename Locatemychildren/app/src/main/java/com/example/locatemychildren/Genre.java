package com.example.locatemychildren;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Genre extends Activity {
    Button suivant;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.genre);

        suivant = (Button) findViewById(R.id.next);
        suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Genre.this, Num_Tel.class);
                startActivity(i);
            }
        });
    }
}
