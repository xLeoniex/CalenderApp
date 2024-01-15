package com.example.calenderapp.DashboardBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calenderapp.Notification.AllTipsView;
import com.example.calenderapp.Notification.PointsNotification.MyMonthlyBroadcastReceiver;
import com.example.calenderapp.Notification.PointsNotification.MyWeeklyBroadcastReceiver;
import com.example.calenderapp.calenderView.MainActivity;
import com.example.calenderapp.Login.Login;
import com.example.calenderapp.Points.AllEventsView;
import com.example.calenderapp.Points.PointsView;
import com.example.calenderapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class Dashboard extends AppCompatActivity {

    //ToDo: wir brauchen überall in der App Profil und HomeButton (Leonie,Ehsan,Ibrahim)
    //Zwei-Variabeln, Einmal für User und einmal für Firebase Authentification
    FirebaseAuth auth;

    Button btn_calender, btn_points, btn_tips, btn_events;
    FirebaseUser user;
    TextView textView;
    TextView textWelcome;
    private static final int PERMISSION_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        textWelcome = findViewById(R.id.totalPoints);
        btn_calender = findViewById(R.id.btn_calenderView);
        btn_points = findViewById(R.id.btn_pointsView);
        btn_tips = findViewById(R.id.btn_tipsView);
        btn_events = findViewById(R.id.btn_eventsView);

        //Premission für Notifications
        requestRunTimePermission();
        //Alarm für jeden Sonntag 23:59 konfiguriert
        startWeeklyAlert();
        //Alarm für letzten Tag des Monats 23:59
        startMonthlyAlert();

        //Firebase-Variablen
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //nun schauen ob user login ist bzw. exisitiert
        if(user == null || user.getDisplayName() == null){
            //Dann muss sich Loggen --> zu login aktivity
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }else{
            String welcome = textWelcome.getText().toString();
            welcome = welcome + " "+ user.getDisplayName() + "!";
            textWelcome.setText(welcome);
        }
        btn_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PointsView.class);
                startActivity(intent);
                finish();
            }
        });
        btn_tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllTipsView.class);
                startActivity(intent);
                finish();
            }
        });

        btn_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllEventsView.class);
                startActivity(intent);
                finish();
            }
        });

    }

    //Bar-Menus --> strg + O drucken
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(MenuHelper.handleMenuItem(item, user, this)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void requestRunTimePermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this,"Permission is Granted",Toast.LENGTH_SHORT).show();
        }else if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.POST_NOTIFICATIONS))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This app requiers Notification Permission!")
                    .setTitle("Permission Requierd")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Dashboard.this,
                                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                    PERMISSION_CODE);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());
            builder.show();
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_CODE);
        }
    }

    //Wochen Alarm konfigurieren
    public void startWeeklyAlert() {
       AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, MyWeeklyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Alarm auf jeden Sonntag um 23:59 Uhr
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7 ,  // Wiederhole jede Woche
                    pendingIntent
            );
            Log.d("WeeklyAlarmStarted","Alarm wird jeden Sonntag um 23:59 gestartet" );
        }
    }
    //Alarm am letzten Tag des Monats ausführen
    public void startMonthlyAlert() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, MyMonthlyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Alarm am erster Tag des nächsten Monats um 00:00 Uhr
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Erster Tag des aktuellen Monats
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MONTH, 1); // Nächste Monat einstellen

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,  // Wiederhole jeden Tag
                    pendingIntent
            );

            Log.d("MonthlyAlarmStarted","Alarm wird jeden letzten Monatstag um 23:59 gestartet" );
        }

    }
}