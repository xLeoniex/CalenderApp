package com.example.calenderapp.Notification.PointsNotification;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.calenderapp.Points.ToDoneEventView;
import com.example.calenderapp.R;
import com.example.calenderapp.tips.OpenTipView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MyDailyBroadcastReceiver extends BroadcastReceiver {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser user = mAuth.getCurrentUser();
    DatabaseReference defaultEventsRef = FirebaseDatabase.getInstance().getReference("DefaultEvents");
    DatabaseReference userEventsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("Events");

    int numberOfdefaultEvents = 5;

    String eventDate;

    public static final String NOTIFICATION_CHANNEL_ID = "channel_id";
    //User visible Channel Name
    public static final String CHANNEL_NAME = "Notification Channel";
    // number to differentiate Notifications
    public static final int NOTIFICATION_ID = 123;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DailyAlarmTriggerd", "Aktionen für jeden Tag um 00:00 durchgeführt");
        //ein zufälligen event wählen
        Random random = new Random();
        int randomNumber = random.nextInt(numberOfdefaultEvents) + 1;
        String defaultID = String.valueOf(randomNumber);
        DatabaseReference defaultEvent = defaultEventsRef.child(defaultID);

        //Datum von Morggigen Tag ablesen
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tommorrow = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        eventDate = dateFormat.format(tommorrow);

        //Die Daten aus defaulEvent raus lesen
        defaultEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Daten für das Event gefunden
                    String ImageUrl = dataSnapshot.child("eventImageUrl").getValue(String.class);
                    String endingTime = dataSnapshot.child("endingTime").getValue(String.class);
                    String startingTime = dataSnapshot.child("startingTime").getValue(String.class);
                    String eventDescription = dataSnapshot.child("eventDescription").getValue(String.class);
                    String eventName = dataSnapshot.child("eventName").getValue(String.class);
                    String eventType = dataSnapshot.child("eventType").getValue(String.class);
                    String eventState = dataSnapshot.child("eventState").getValue(String.class);
                    String eventWeight = dataSnapshot.child("eventWeight").getValue(String.class);
                    String recurring = dataSnapshot.child("recurringEventType").getValue(String.class);

                    String sKey = userEventsRef.push().getKey();
                    if (sKey != null && ImageUrl != null && endingTime != null && startingTime != null && eventDescription != null && eventName != null && eventType != null && eventState != null && eventWeight != null && recurring != null && eventDate != null ) {
                        userEventsRef.child(sKey).child("eventImageUrl").setValue(ImageUrl);
                        userEventsRef.child(sKey).child("endingTime").setValue(endingTime);
                        userEventsRef.child(sKey).child("startingTime").setValue(startingTime);
                        userEventsRef.child(sKey).child("eventDate").setValue(eventDate);
                        userEventsRef.child(sKey).child("eventDescription").setValue(eventDescription);
                        userEventsRef.child(sKey).child("eventName").setValue(eventName);
                        userEventsRef.child(sKey).child("eventType").setValue(eventType);
                        userEventsRef.child(sKey).child("eventState").setValue(eventState);
                        userEventsRef.child(sKey).child("eventWeight").setValue(eventWeight);
                        userEventsRef.child(sKey).child("recurringEventType").setValue(recurring);
                        userEventsRef.child(sKey).child("eventId").setValue(sKey);

                        showNotification(context, "There is a new event for tomorrow, complete it to feel better and earn more points.", sKey);
                    }
                } else {
                    Log.d("Error defaultEvent", "no defaultEvent exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void showNotification(Context context, String text,String id) {
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_info);
        builder.setContentTitle("New daily event");
        builder.setContentText(text);

        Intent notificationIntent = new Intent(context, ToDoneEventView.class);
        notificationIntent.putExtra("event-ID",id);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(contentIntent);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

}
