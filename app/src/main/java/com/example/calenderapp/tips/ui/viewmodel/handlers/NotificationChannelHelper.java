package com.example.calenderapp.tips.ui.viewmodel.handlers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.example.calenderapp.tips.model.HandlerItems.TipNotificationChannel;

public class NotificationChannelHelper {
    private Context channelContext;
    private TipNotificationChannel tipNotificationChannel;


    public NotificationChannelHelper(Context channelContext, TipNotificationChannel tipNotificationChannel) {
        this.channelContext = channelContext;
        this.tipNotificationChannel = tipNotificationChannel;
    }
    public void DeleteNotificationChannel()
    {
        NotificationManager notificationManager = (NotificationManager) channelContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.deleteNotificationChannel(tipNotificationChannel.getChannelId());
    }
    public void SetNotificationChannel()
    {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {

            NotificationChannel channel = new NotificationChannel(tipNotificationChannel.getChannelId(),
                    tipNotificationChannel.getChannelName(),
                    tipNotificationChannel.getChannelImportance());
            NotificationManager notificationManager = (NotificationManager) channelContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
