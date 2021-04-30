 package com.example.kidlocater;
//    implementation 'com.google.firebase:firebase-analytics'
 //    apply plugin: 'com.google.gms.google-services'
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Date_naissance extends AppCompatActivity {
    Button suivant;
    //AlertDialog.Builder builder;
    DatePicker picker;
    String date;
    ImageButton left_arrow;
    View includedLayout;
    LinearLayout l;
    Dialog dialog;
    TextView test;

    LinearLayout exit_l;
    Dialog exit_dialog;

    @SuppressLint({"WrongViewCast", "ResourceAsColor"})
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_naissance);

        String first_name="",last_name="";

        test = findViewById(R.id.test);

        includedLayout = findViewById(R.id.datenaissance);
        TextView title = (TextView )includedLayout.findViewById(R.id.toolbar_title);
        title.setText("Date de naissance");
        title.setTextColor(getResources().getColor(R.color.light_green));

        left_arrow = (ImageButton) findViewById(R.id.arrow);

        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
                outline.setOval(5, 2, size, size);
            }
        };
        left_arrow.setOutlineProvider(viewOutlineProvider);

        left_arrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                exit_l = (LinearLayout)findViewById(R.id.exitdialog);

                exit_dialog = new Dialog(Date_naissance.this);
                exit_dialog.setContentView(R.layout.exit_dialog);
                exit_dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.exit_background));
                //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                exit_dialog.setCancelable(true);
                exit_dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                TextView pourCreation = (TextView) exit_dialog.findViewById(R.id.pourCreation);
                pourCreation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exit_dialog.cancel();
                    }
                });
                TextView arretCreation = (TextView) exit_dialog.findViewById(R.id.arretCreation);
                arretCreation.setTextColor(getResources().getColor(R.color.light_green));
                arretCreation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Date_naissance.this, MainActivity.class);
                        startActivity(i);
                    }
                });
                exit_dialog.show();
            }
        });

        date = "";
        picker=(DatePicker)findViewById(R.id.datePicker);
        //builder = new AlertDialog.Builder(this,R.style.MaterialAlertDialog_OUI_color);//R.style.AlertDialogCustom);


        suivant = (Button) findViewById(R.id.next);
        suivant.setOnClickListener(new View.OnClickListener() {
            //@RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                /* to retrieve the date that is selected in the datpicker*/
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                GregorianCalendar calendarBeg=new GregorianCalendar(picker.getYear(),
                        picker.getMonth(),picker.getDayOfMonth());
                dateFormatter.setCalendar(calendarBeg);
                String strDate = dateFormatter.format(calendarBeg.getTime());
                /* to retrieve the date that is selected in the datpicker*/

                String[] strDatearray = strDate.split("-");
                int month = Integer.parseInt(strDatearray[1]);
                String mon = "";
                switch (month) {
                    case 1:
                        mon = "janv.";
                        break;
                    case 2:
                        mon = "févr.";
                        break;
                    case 3:
                        mon = "mars";
                        break;
                    case 4:
                        mon = "avr.";
                        break;
                    case 5:
                        mon = "mai";
                        break;
                    case 6:
                        mon = "juin";
                        break;
                    case 7:
                        mon = "juil.";
                        break;
                    case 8:
                        mon = "août";
                        break;
                    case 9:
                        mon = "sept.";
                        break;
                    case 10:
                        mon = "oct.";
                        break;
                    case 11:
                        mon = "nov.";
                        break;
                    case 12:
                        mon = "déc.";
                        break;
                }
                //TextView qu = (TextView)includedLayout.findViewById(R.id.qu);
                //TextView qu = (TextView)findViewById(R.id.qu);
                l = (LinearLayout)findViewById(R.id.birthdaydialog);

                dialog = new Dialog(Date_naissance.this);
                dialog.setContentView(R.layout.custom_dialog);
                TextView qu = (TextView )dialog.findViewById(R.id.qu);
                String txt = "Êtes vous né(e) le "+ strDatearray[0] + " "+mon+ " "+ strDatearray[2] + " ?";
                qu.setText(txt);
                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));
                //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                Bundle extras = getIntent().getExtras();
                String first_name = null,last_name = null,birthday=null;
                if (extras != null) {
                    first_name = extras.getString("first_name");
                    last_name = extras.getString("last_name");
                    birthday = strDatearray[0] + " / "+month+ " / "+ strDatearray[2];
                }
                String finalFirst_name = first_name;
                String finalLast_name = last_name;
                String finalBirthday = birthday;
                TextView oui = (TextView) dialog.findViewById(R.id.oui);
                oui.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Date_naissance.this, Genre.class);
                        i.putExtra("first_name", finalFirst_name);
                        i.putExtra("last_name", finalLast_name);
                        i.putExtra("birthday", finalBirthday);
                        //test.setText("prénom: "+finalFirst_name+"/ nom:"+finalLast_name+ "/ date naissance "+finalBirthday);
                        startActivity(i);
                    }
                });
                TextView non = (TextView) dialog.findViewById(R.id.non);
                non.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();

                //---------------------------------------------------------------------------------

                //uncomment from here to the end if it's not working
                /*builder.setMessage(R.string.age_confirmation) .setTitle(R.string.age_confirmation);
                builder.setMessage("Êtes vous né(e) le "+ strDatearray[0] + " "+mon+ " "+ strDatearray[2] + " ?")
                        .setCancelable(false)
                        .setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                Intent i = new Intent(Date_naissance.this, Genre.class);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("NON", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();

                            }
                        });

                AlertDialog alert = builder.create();
                alert.setTitle("Confirmez votre anniversaire");
                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onShow(DialogInterface arg0) {
                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.black);
                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.purple_700);
                    }
                });
                alert.show();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                lp.copyFrom(alert.getWindow().getAttributes());
                lp.gravity = Gravity.CENTER;
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = 390;

                //lp.width = 600;
                //lp.height = 400;
                //lp.x=-130;
                //lp.y=50;
                alert.getWindow().setAttributes(lp);
*/

            }
        });
    }

}

/*
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(R.style.RightJustifyTextView);
                builder.setTitle("Title");
                alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getWindow().setLayout(600, 400); //Controlling width and height.
*/
                /*
                alertDialog.show();
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                lp.copyFrom(alertDialog.getWindow().getAttributes());
                lp.width = 150;
                lp.height = 500;
                lp.x=-170;
                lp.y=100;
                alertDialog.getWindow().setAttributes(lp);*/

//AlertDialog.Builder(Context context, int themeResId)

                /*
                new MaterialAlertDialogBuilder(context,
                R.style.MaterialAlertDialog_OK_color)
                .setMessage("Message......")
                .setPositiveButton("ok", null)
                .setNegativeButton("Cancel", null)
                .show();

                * */