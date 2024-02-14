/*
 * *************************************************
 *   Author :           Ehsan Khademi
 *   SubAuthor :        None
 *   Beschreibung :     Dies ist ein BroadcastReceiver, der auf einen
 *                      wöchentlichen Alarmauslöser wartet. Er holt die
 *                      Daten über die monatlichen Punkte des Benutzers
 *                      aus der Datenbank und berechnet die Leistungsnachrichten
 *                      auf der Grundlage des Vergleichs der Punkte des aktuellen
 *                      Monats mit denen des Vormonats. Schließlich zeigt
 *                      es eine Benachrichtigung mit dem Leistungsbericht
 *                      an und schaltet alle Tipps für den nächsten Monat frei.

 *   Letzte Änderung :  13/02/2024
 * *************************************************
 */


package com.example.calenderapp.Notification.PointsNotification;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.calenderapp.DashboardBar.Dashboard;
import com.example.calenderapp.Points.PointsView;
import com.example.calenderapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MyMonthlyBroadcastReceiver extends BroadcastReceiver {
    // Firebase Authentifizierung
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    // Referenz zur Monats-Punkte-Datenbank
    DatabaseReference monthRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("points").child("Month");

    // Notification Channel Attribute
    public static final String NOTIFICATION_CHANNEL_ID = "channel_id";
    public static final String CHANNEL_NAME = "Notification Channel";
    public static final int NOTIFICATION_ID = 102;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Erhalten des aktuellen Datums und Überprüfung, ob es der erste Tag des Monats ist
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            Log.d("MonthlyAlarmTriggered", "Aktionen für den ersten Tag des Monats um 00:00 durchgeführt");

            // Datenbankabfrage für die monatlichen Punkte
            monthRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String text;
                        int highScoreMonth = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("High_Score_Month").getValue(String.class)));
                        int totalCurrentMonth = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("Total_Current_Month").getValue(String.class)));
                        int totalLastMonth = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("Total_Last_Month").getValue(String.class)));

                        // Überprüfung der Punkte und Festlegung des Notification-Texts
                        if (highScoreMonth < totalCurrentMonth) {
                            String points = Integer.toString(totalCurrentMonth);
                            text = "Congratulations! You have reached a new monthly high score of " + points + " points. ";
                            monthRef.child("High_Score_Month").setValue(Integer.toString(totalCurrentMonth));
                        } else {
                            if(totalLastMonth > totalCurrentMonth){
                                int lowPower = 100 - ((totalCurrentMonth * 100)/totalLastMonth);
                                text = "Time for more power! Your performance has dropped by " + lowPower + "% compared to the previous month.";
                            } else if(totalCurrentMonth > totalLastMonth){
                                int highPower = ((totalCurrentMonth - totalLastMonth) * 100)/totalLastMonth;
                                text = "Bravo, keep up the good work! Your performance has improved by " + highPower + "% compared to the previous month!";
                            } else {
                                text = "Keep improving! Your performance has not changed much from the previous month.";
                            }
                        }

                        // Zurücksetzen der aktuellen Monatspunkte auf 0 und Aktualisierung der letzten Monatspunkte
                        monthRef.child("Total_Current_Month").setValue("0");
                        monthRef.child("Total_Last_Month").setValue(Integer.toString(totalCurrentMonth));
                        monthRef.child("object").removeValue();

                        // Anzeigen der Benachrichtigung
                        showNotification(context, text);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            // Freischalten aller Tipps am Ende des Monats
            refreshTips();
        }
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

    // Methode zum Freischalten aller Tipps am Ende des Monats
    public void refreshTips(){
        DatabaseReference tipsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("Tips");
        tipsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String ID = (String) dataSnapshot.getKey();
                    if (ID != null) {
                        DatabaseReference tipRef = tipsRef.child(ID);
                        tipRef.child("tipState").setValue("inProgress");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

