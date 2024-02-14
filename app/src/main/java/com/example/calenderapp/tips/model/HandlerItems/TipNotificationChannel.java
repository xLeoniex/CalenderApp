/*
 * *************************************************
 *   Author :           Ahmed Ibrahim Almohamed
 *   SubAuthor :        None
 *   Beschreibung :     A Model Class to hold information about the TipNotification Channel
 *   Letzte Änderung :  12/01/2024, 18:11
 * *************************************************
 */

package com.example.calenderapp.tips.model.HandlerItems;

import android.app.NotificationManager;

public class TipNotificationChannel {
    private String channelId;
    private String channelName;
    private int channelImportance;

    public TipNotificationChannel(String channelId, String channelName, int channelImportance) {
        this.channelId = channelId;
        this.channelName = channelName;

        if (channelImportance >= NotificationManager.IMPORTANCE_UNSPECIFIED &&
                channelImportance <= NotificationManager.IMPORTANCE_HIGH) {
            this.channelImportance = channelImportance;
        } else {
            // Handle an invalid importance value (perhaps set it to a default value)
            this.channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
        }
    }


    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getChannelImportance() {
        if (channelImportance >= NotificationManager.IMPORTANCE_UNSPECIFIED &&
                channelImportance <= NotificationManager.IMPORTANCE_HIGH) {
            return  this.channelImportance ;
        } else {
            return NotificationManager.IMPORTANCE_DEFAULT;
        }
    }

    public void setChannelImportance(int channelImportance) {
        this.channelImportance = channelImportance;
    }
}
