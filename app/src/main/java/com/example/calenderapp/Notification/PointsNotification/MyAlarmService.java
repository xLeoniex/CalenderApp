package com.example.calenderapp.Notification.PointsNotification;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class MyAlarmService extends IntentService {
    public MyAlarmService() {
        super("MyAlarmService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Hier kannst du den BroadcastReceiver auslösen
        Intent broadcastIntent = new Intent(this, MyBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
    }
}
