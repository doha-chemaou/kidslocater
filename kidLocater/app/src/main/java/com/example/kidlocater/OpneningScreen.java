package com.example.kidlocater;
//jhglugukefsf
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class  OpneningScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000 ;
    private static int SPLASH_TIME_OUT_1 = 9500 ;

    private Handler h1;
    private Handler h2;

    TextView title;
    ImageView i1,i2,i3,i4,i5,i6,i7,i8,locimg,map;
    AnimationDrawable locAnimation;
    Animation  top,bot;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.openingscreen);
        h1 = new Handler();
        h2 = new Handler();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);



        /*locimg=findViewById(R.id.loc);
        locimg.setBackgroundResource(R.drawable.d_fade_in);
        locAnimation=(AnimationDrawable)locimg.getBackground();*/



        /*Drawable myIcon = getResources().getDrawable( R.drawable.ic_circle );
        ColorFilter filter = new LightingColorFilter(getResources().getColor(R.color.light_yellow),getResources().getColor(R.color.light_yellow));
        myIcon.setColorFilter(filter);*/
/*
        i1 = findViewById(R.id.y1);
        i2 = findViewById(R.id.y2);
        i3 = findViewById(R.id.y3);
        i4 = findViewById(R.id.y4);
        i5 = findViewById(R.id.y5);
        i6 = findViewById(R.id.y6);
        i7 = findViewById(R.id.y7);
        i8 = findViewById(R.id.y8);*/

        title = findViewById(R.id.title);
        SpannableString redSpannable= new SpannableString("KidsLocater");
        redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, 1, 2);//K
        redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 1, 2, 3);//i
        redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 2, 3, 4);//d
        //redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 3, 4, 5);//s
        redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.purple_500)), 4, 5, 6);//L
        /*redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.purple_500)), 5, 6, 7);//o
        redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.grey)), 6, 7, 8);//c
        redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.middle_pink)), 7, 8, 9);//a
        redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.light_purple)), 8, 9, 10);//t
        redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 9, 10, 11);//e*/
        redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pink)), 10, 11, 12);//r
        title.setText(redSpannable);
        bot = AnimationUtils.loadAnimation(this,R.animator.bot_animation);
        title.setAnimation(bot);

        top = AnimationUtils.loadAnimation(this,R.animator.top_animation);
        map=findViewById(R.id.map);
        map.setAnimation(top);

        @SuppressLint("ResourceType")
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.animator.fade_out);
        /*i1.setAnimation(animation);
        i2.setAnimation(animation);
        i3.setAnimation(animation);
        i4.setAnimation(animation);
        i5.setAnimation(animation);
        i6.setAnimation(animation);
        i7.setAnimation(animation);
        i8.setAnimation(animation);*/

        h1.postDelayed(locater,SPLASH_TIME_OUT);
        h2.postDelayed(launchActivity,SPLASH_TIME_OUT_1);


        /*new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent homeIntent = new Intent (OpneningScreen.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);*/

    }

    private Runnable launchActivity = new Runnable() {
        @Override
        public void run() {
            Intent homeIntent = new Intent (OpneningScreen.this, MainActivity.class);
            /*startActivity(homeIntent);
            finish();*/
            Pair[] pairs = new Pair[2];

            pairs[0]=new Pair<View,String>(map,"logo_image");
            pairs[1]=new Pair<View,String>(title,"logo_text");
            ActivityOptions options =  ActivityOptions.makeSceneTransitionAnimation(OpneningScreen.this,pairs);
            startActivity(homeIntent, options.toBundle());
            finish();
        }
    };

    private Runnable locater = new Runnable() {
        @Override
        public void run() {
            locimg=findViewById(R.id.loc);
            locimg.setBackgroundResource(R.drawable.d_fade_in);
            locAnimation=(AnimationDrawable)locimg.getBackground();
            locAnimation.start();
        }
    };
/*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //locAnimation.start();
    }*/
}















