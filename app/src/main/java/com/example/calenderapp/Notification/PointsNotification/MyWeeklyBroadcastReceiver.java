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
import com.example.calenderapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MyWeeklyBroadcastReceiver extends BroadcastReceiver {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser user = mAuth.getCurrentUser();
    DatabaseReference weekRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("points").child("Week");
    public static final String NOTIFICATION_CHANNEL_ID = "channel_id";
    //User visible Channel Name
    public static final String CHANNEL_NAME = "Notification Channel";
    // number to differentiate Notifications
    public static final int NOTIFICATION_ID = 101;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("WeeklyAlarmTriggered", "Aktionen für Sonntag um 23:59 durchgeführt");
            weekRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String text;
                        int highScoreWeek = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("High_Score_Week").getValue(String.class)));
                        int totalCurrentWeek = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("Total_Current_Week").getValue(String.class)));
                        int totalLastWeek = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("Total_Last_Week").getValue(String.class)));

                        if (totalLastWeek == 0 || totalCurrentWeek == 0){
                            totalLastWeek = 1;
                            totalCurrentWeek = 1;
                        }
                        if (highScoreWeek < totalCurrentWeek) {
                            //Hammer
                            String points = Integer.toString(totalCurrentWeek);
                            text = "Congratulations! You have reached a new weekly high score of " + points + " points. ";
                            weekRef.child("High_Score_Week").setValue(Integer.toString(totalCurrentWeek));
                        }else{
                            if(totalLastWeek > totalCurrentWeek){
                                //Traurig
                                int lowPower = 100 - ((totalCurrentWeek * 100)/totalLastWeek);
                                text = "Time for more power! Your performance has dropped by" + lowPower +"% compared to the previous week.";
                            }else if(totalCurrentWeek > totalLastWeek){
                                //Weiter so
                                int highPower = ((totalCurrentWeek - totalLastWeek) * 100)/totalLastWeek;
                                text = "Bravo, keep up the good work! Your performance has improved by " + highPower +"% compared to the previous week!";
                            }else{
                                text = "Keep improving! Your performance has not changed much from the previous week.";
                            }
                        }
                        weekRef.child("Total_Current_Week").setValue("0");
                        weekRef.child("Total_Last_Week").setValue(Integer.toString(totalCurrentWeek));
                        weekRef.child("object").removeValue();
                        showNotification(context, text);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

    private void showNotification(Context context, String text) {
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_info);
        builder.setContentTitle("Performance Report");
        builder.setContentText(text);

        Intent notificationIntent = new Intent(context, Dashboard.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(contentIntent);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }



}
