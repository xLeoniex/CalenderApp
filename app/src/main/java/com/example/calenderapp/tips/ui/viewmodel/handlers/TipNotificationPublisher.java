package com.example.calenderapp.tips.ui.viewmodel.handlers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.calenderapp.R;
import com.example.calenderapp.tips.model.HandlerItems.TipNotification;
import com.example.calenderapp.tips.model.HandlerItems.TipNotificationChannel;

public class TipNotificationPublisher extends BroadcastReceiver {

    public String TipTitle = "TipTitle";
    public String TipText = "TipText";

    public String TipID = "TipId";
    public String AlarmMessageExtra = "AlarmMessage";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TipNotificationPublisher", "Received alarm intent");
        TipNotification tipNotification = new TipNotification(intent.getStringExtra(TipTitle)
                , intent.getStringExtra(TipText), NotificationCompat.PRIORITY_HIGH
                ,Integer.parseInt(intent.getStringExtra(TipID)), R.drawable.baseline_notification_important_24);
        TipNotificationChannel tipNotificationChannel = new TipNotificationChannel("TIP_ID","Tips_Notifications", NotificationManager.IMPORTANCE_HIGH);
        NotificationHelper myHelper = new NotificationHelper(tipNotification,tipNotificationChannel, context);
        myHelper.ShowNotification();
    }
}
