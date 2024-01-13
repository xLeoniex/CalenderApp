package com.example.calenderapp.Notification.PointsNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //ToDo: was soll Ende der Woche und den letzten Tag des Monats passieren
        Log.d("AlarmHier", " haloooooooooooooooo");

        //ToDo: Push nachricht definieren
       // AlarmNotificationHelper.showNotification(context, "Deine Push-Nachricht", "Nachrichtentext");
    }

}
