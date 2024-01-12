package com.example.calenderapp.tips.ui.viewmodel.handlers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.example.calenderapp.tips.model.HandlerItems.TipNotification;
import com.example.calenderapp.tips.model.HandlerItems.TipNotificationChannel;

public class NotificationHelper {
    private TipNotification tipNotification;

    private TipNotificationChannel tipNotificationChannel;

    private Context tipcontext;

    public NotificationHelper(TipNotification tipNotification,
                              TipNotificationChannel tipNotificationChannel,
                              Context tipcontext) {
        this.tipNotification = tipNotification;
        this.tipNotificationChannel = tipNotificationChannel;
        this.tipcontext = tipcontext;
    }

    private Notification CreateNotification()
    {
        Notification notification = new NotificationCompat.Builder(tipcontext,tipNotificationChannel.getChannelId())
                .setSmallIcon(tipNotification.getTipNotificationIcon())
                .setContentTitle(tipNotification.getNotificationTitle())
                .setContentText(tipNotification.getNotificationText())
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
