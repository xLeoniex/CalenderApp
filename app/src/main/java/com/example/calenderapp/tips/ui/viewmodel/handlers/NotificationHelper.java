package com.example.calenderapp.tips.ui.viewmodel.handlers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.calenderapp.Notification.AllTipsView;
import com.example.calenderapp.tips.model.HandlerItems.TipNotification;
import com.example.calenderapp.tips.model.HandlerItems.TipNotificationChannel;

public class NotificationHelper {
    private TipNotification tipNotification;
    private Intent NotificationIntent;
    private TipNotificationChannel tipNotificationChannel;

    private Context tipcontext;

    public NotificationHelper(TipNotification tipNotification,
                              TipNotificationChannel tipNotificationChannel,
                              Context tipcontext) {
        this.tipNotification = tipNotification;
        this.tipNotificationChannel = tipNotificationChannel;
        this.tipcontext = tipcontext;
        NotificationIntent = new Intent(tipcontext, AllTipsView.class);
    }

    private Notification CreateNotification()
    {
        PendingIntent notificationClickIntent = PendingIntent.getActivity(tipcontext,1,NotificationIntent,PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(tipcontext,tipNotificationChannel.getChannelId())
                .setSmallIcon(tipNotification.getTipNotificationIcon())
                .setContentTitle(tipNotification.getNotificationTitle())
                .setContentText(tipNotification.getNotificationText())
                .setContentIntent(notificationClickIntent)
                .setChannelId(tipNotificationChannel.getChannelId())
                .setPriority(tipNotification.getTipNotificationPriority())
                .build();

        return notification;
    }
    public void ShowNotification()
    {
        Notification notification = CreateNotification();
        NotificationManager notificationManager = (NotificationManager) tipcontext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(tipNotification.getTipNotificationID(),notification);
    }
}
