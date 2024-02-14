/*
 * *************************************************
 *   Author :           Ehsan Khademi
 *   SubAuthor :        Ahmed Ibrahim Almohamed
 *   Beschreibung :     Das Dashboard bietet eine zentrale Anlaufstelle
 *                      mit einer Begrüßungsnachricht und Schaltflächen
 *                      für die wichtigsten Funktionen. Außerdem eine
 *                      Schaltfläche für die Informationsansicht. Die
 *                      Alarmkonfiguration ist integriert, um Benachrichtigungen
 *                      für tägliche, wöchentliche und monatliche Ereignisse zu planen.
 *                      Letzte Änderung :  13/02/2024
 * *************************************************
 */
package com.example.calenderapp.DashboardBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
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

import com.example.calenderapp.Notification.PointsNotification.MyDailyBroadcastReceiver;
import com.example.calenderapp.tips.AllTipsView;
import com.example.calenderapp.Notification.PointsNotification.MyMonthlyBroadcastReceiver;
import com.example.calenderapp.Notification.PointsNotification.MyWeeklyBroadcastReceiver;
import com.example.calenderapp.calenderView.MainActivity;
import com.example.calenderapp.Login.Login;
import com.example.calenderapp.Points.PointsView;
import com.example.calenderapp.R;
import com.example.calenderapp.tips.model.TipModel;
import com.example.calenderapp.tips.ui.viewmodel.TipViewModel;
import com.example.calenderapp.tips.ui.viewmodel.handlers.TipNotificationPublisher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Random;

public class Dashboard extends AppCompatActivity {

    //Zwei-Variabeln, Einmal für User und einmal für Firebase Authentification
    FirebaseAuth auth;
    private TipViewModel tipViewModel;
    CardView btn_calender, btn_points, btn_tips;
    FirebaseUser user;
    TextView  information;
    TextView textWelcome;
    private static final int PERMISSION_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Die MainActivity ist die Hauptaktivität der Anwendung, die nach dem Start angezeigt wird.
        textWelcome = findViewById(R.id.totalPoints);
        information = findViewById(R.id.information);
        btn_calender = findViewById(R.id.btn_calenderView);
        btn_points = findViewById(R.id.btn_pointsView);
        btn_tips = findViewById(R.id.btn_tipsView);
        tipViewModel = new ViewModelProvider(this).get(TipViewModel.class);

        // Berechtigung für Benachrichtigungen wird angefordert.
        requestRunTimePermission();
        // Alarm für jeden Sonntag um 23:59 Uhr wird konfiguriert.
        startWeeklyAlert();
        // Alarm für den letzten Tag des Monats um 23:59 Uhr wird konfiguriert.
        startMonthlyAlert();
        // Alarm für jeden Tag wird gestartet.
        startDailyAlert();

        // Ein Observer wird verwendet, um Tipps aus dem ViewModel zu erhalten und einen wiederkehrenden Alarm mit einem zufälligen Tipp zu setzen.
        tipViewModel.getDataFromRepository().observe(this, new Observer<List<TipModel>>() {
            @Override
            public void onChanged(List<TipModel> tipModels) {
                if(!tipModels.isEmpty()){
                    Random random = new Random();
                    int idx = random.nextInt(tipModels.size());
                    Log.d("AlarmStarted","Alarm is starting...." );
                    setRepeatingAlarm(tipModels.get(idx));
                }
            }
        });

        // Initialisierung der Firebase-Variablen
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Überprüfung, ob ein Benutzer angemeldet ist und eine Begrüßungsnachricht wird angezeigt.
        if(user == null || user.getDisplayName() == null){
            // Wenn kein Benutzer angemeldet ist, wird zur Login-Aktivität navigiert.
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }else{
            String welcome = textWelcome.getText().toString();
            welcome = welcome + " "+ user.getDisplayName() + "!";
            textWelcome.setText(welcome);
        }

        // OnClickListener für den Kalender-Button, der zur MainActivity führt.
        btn_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // OnClickListener für den Punkte-Button, der zur PointsView führt.
        btn_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PointsView.class);
                startActivity(intent);
                finish();
            }
        });

        // OnClickListener für den Tipps-Button, der zur AllTipsView führt.
        btn_tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllTipsView.class);
                startActivity(intent);
                finish();
            }
        });

        // OnClickListener für die Informationsansicht, die zur ApplicationInformation führt.
        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ApplicationInformation.class);
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

    // Methode zum Starten des täglichen Alarms für neue Events
    public void startDailyAlert() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, MyDailyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Alarm wird jeden Tag um 00:00 Uhr gestartet
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    24 * 60 * 60 * 1000,  // Wiederhole jeden Tag
                    pendingIntent
            );

            Log.d("DailyAlarmStarted","Alarm wird jeden Tag um 00:00 gestartet" );
        }
    }

    // Methode zum Setzen eines wiederkehrenden Alarms mit einem zufälligen Tipp
    private void setRepeatingAlarm(TipModel tipModel) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, TipNotificationPublisher.class);
        alarmIntent.putExtra("TipTitle",tipModel.getTipTitle());
        alarmIntent.putExtra("TipText",tipModel.getTipDescription());
        alarmIntent.putExtra("TipId","10");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        long intervalMillis = 24*60*60*1000; // Jeden Tag nach 24 Stunden

        if (alarmManager != null) {
            alarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + intervalMillis,
                    intervalMillis,
                    pendingIntent
            );
            Log.d("AlarmStarted","Alarm wird gestartet...." );
        }
    }

    // Methode zum Starten des wöchentlichen Alarms
    public void startWeeklyAlert() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, MyWeeklyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Alarm wird jeden Sonntag um 23:59 Uhr gestartet
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    7 * 24 * 60 * 60 * 1000,  // Wiederhole jede Woche
                    pendingIntent
            );
            Log.d("WeeklyAlarmStarted","Alarm wird jeden Sonntag um 23:59 gestartet" );
        }
    }

    // Methode zum Starten des monatlichen Alarms
    public void startMonthlyAlert() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, MyMonthlyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Alarm wird am ersten Tag des nächsten Monats um 00:00 Uhr gestartet
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Erster Tag des aktuellen Monats
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MONTH, 1); // Nächsten Monat einstellen

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    24 * 60 * 60 * 1000,  // Wiederhole jeden Tag
                    pendingIntent
            );

            Log.d("MonthlyAlarmStarted","Alarm wird jeden letzten Monatstag um 23:59 gestartet" );
        }

    }

}