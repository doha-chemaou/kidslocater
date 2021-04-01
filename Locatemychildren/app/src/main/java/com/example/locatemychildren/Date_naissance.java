package com.example.locatemychildren;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
    AlertDialog.Builder builder;
    DatePicker picker;
    String date;

    @SuppressLint("WrongViewCast")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_naissance);
        date = "";
        picker=(DatePicker)findViewById(R.id.datePicker);
        builder = new AlertDialog.Builder(this);

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

                builder.setMessage(R.string.age_confirmation) .setTitle(R.string.age_confirmation);
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
                alert.show();
            }
        });
}

}