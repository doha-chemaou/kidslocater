package com.example.kidlocater;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Welcome extends AppCompatActivity {
    Button suivant;
    TextView compteExistant;
    ImageButton left_arrow;
    View includedLayout;
    LinearLayout l;
    Dialog dialog;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        includedLayout = findViewById(R.id.welcome);
        TextView title = (TextView )includedLayout.findViewById(R.id.toolbar_title);
        title.setText("Créer un compte");
        title.setTextColor(getResources().getColor(R.color.bluee));


        left_arrow = (ImageButton) findViewById(R.id.arrow);

        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
                outline.setOval(5, 2, size, size);
            }
        };

        compteExistant = (TextView) findViewById(R.id.compteExistant);
        compteExistant.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                l = (LinearLayout)findViewById(R.id.exitdialog);

                dialog = new Dialog(Welcome.this);
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
                arretCreation.setTextColor(getResources().getColor(R.color.light_purple));

                arretCreation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Welcome.this, MainActivity.class);
                        startActivity(i);
                    }
                });
                dialog.show();
            }
        });
        left_arrow.setOutlineProvider(viewOutlineProvider);

        left_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Welcome.this, MainActivity.class);
                startActivity(i);
            }
        });

        suivant = (Button) findViewById(R.id.w_next);
        suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Welcome.this, Nom.class);
                startActivity(i);
                finish();
            }
        });
    }
}