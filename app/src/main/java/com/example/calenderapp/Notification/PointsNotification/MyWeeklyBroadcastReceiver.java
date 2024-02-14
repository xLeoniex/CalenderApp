/*
 * *************************************************
 *   Author :           Ehsan Khademi
 *   SubAuthor :        None
 *   Beschreibung :     Dies ist ein BroadcastReceiver,
 *                      der auf einen wöchentlichen Alarmauslöser
*                       wartet. Er holt die Daten über die wöchentlichen
*                       Punkte des Benutzers aus der Datenbank und berechnet
*                       die Leistungsnachrichten auf der Grundlage des
*                       Vergleichs der Punkte der laufenden Woche mit denen
*                       der Vorwoche. Schließlich wird eine Benachrichtigung
*                       mit dem Leistungsbericht angezeigt.
 *   Letzte Änderung :  13/02/2024
 * *************************************************
 */
package com.example.calenderapp.Notification.PointsNotification;

import static android.content.Context.NOTIFICATION_SERVICE;


import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.calenderapp.DashboardBar.Dashboard;
import com.example.calenderapp.Points.PointsView;
import com.example.calenderapp.R;
import com.example.calenderapp.tips.OpenTipView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MyWeeklyBroadcastReceiver extends BroadcastReceiver {
    // Firebase Authentifizierung
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    // Referenz zur Wochen-Punkte-Datenbank
    DatabaseReference weekRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("points").child("Week");

    // Notification Channel Attribute
    public static final String NOTIFICATION_CHANNEL_ID = "channel_id";
    public static final String CHANNEL_NAME = "Notification Channel";
    public static final int NOTIFICATION_ID = 101;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Aktionen, die beim Erhalt des Broadcasts ausgeführt werden sollen
        Log.d("WeeklyAlarmTriggered", "Aktionen für Sonntag um 23:59 durchgeführt");

        // Datenbankabfrage für die wöchentlichen Punkte
        weekRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String text;
                    int highScoreWeek = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("High_Score_Week").getValue(String.class)));
                    int totalCurrentWeek = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("Total_Current_Week").getValue(String.class)));
                    int totalLastWeek = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("Total_Last_Week").getValue(String.class)));

                    // Überprüfung, ob die Punkte existieren und nicht null sind
                    if (totalLastWeek == 0 || totalCurrentWeek == 0){
                        totalLastWeek = 1;
                        totalCurrentWeek = 1;
                    }

                    // Festlegung des Notification-Texts basierend auf den Punkten
                    if (highScoreWeek < totalCurrentWeek) {
                        String points = Integer.toString(totalCurrentWeek);
                        text = "Congratulations! You have reached a new weekly high score of " + points + " points. ";
                        weekRef.child("High_Score_Week").setValue(Integer.toString(totalCurrentWeek));
                    } else {
                        if(totalLastWeek > totalCurrentWeek){
                            int lowPower = 100 - ((totalCurrentWeek * 100)/totalLastWeek);
                            text = "Time for more power! Your performance has dropped by " + lowPower + "% compared to the previous week.";
                        } else if(totalCurrentWeek > totalLastWeek){
                            int highPower = ((totalCurrentWeek - totalLastWeek) * 100)/totalLastWeek;
                            text = "Bravo, keep up the good work! Your performance has improved by " + highPower + "% compared to the previous week!";
                        } else {
                            text = "Keep improving! Your performance has not changed much from the previous week.";
                        }
                    }

                    // Zurücksetzen der aktuellen Woche auf 0 und Aktualisierung der letzten Woche
                    weekRef.child("Total_Current_Week").setValue("0");
                    weekRef.child("Total_Last_Week").setValue(Integer.toString(totalCurrentWeek));
                    weekRef.child("object").removeValue();

                    // Anzeigen der Benachrichtigung
                    showNotification(context, text);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Methode zum Anzeigen der Benachrichtigung
    private void showNotification(Context context, String text) {
        // Erstellen eines Notification Channels
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_info);
        builder.setContentTitle("Performance Report");
        builder.setContentText(text);

        // Intent für die Benachrichtigung
        Intent notificationIntent = new Intent(context, PointsView.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(contentIntent);

        // Erstellen und Anzeigen der Benachrichtigung
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

}
