/*
 * *************************************************
 *   Author :           Ahmed Ibrahim Almohamed
 *   SubAuthor :        None
 *   Beschreibung :     A TipNotification Model class to hold a Tip-Notification
 *   Letzte Änderung :  12/01/2024, 18:10
 * *************************************************
 */

package com.example.calenderapp.tips.model.HandlerItems;

public class TipNotification {
    private String NotificationTitle;
    private String NotificationText;
    private int TipNotificationPriority;
    private int TipNotificationID;

    private int TipNotificationIcon;


    public TipNotification(String notificationTitle, String notificationText, int tipNotificationPriority, int tipNotificationID, int tipNotificationIcon) {
        NotificationTitle = notificationTitle;
        NotificationText = notificationText;
        TipNotificationPriority = tipNotificationPriority;
        TipNotificationID = tipNotificationID;
        TipNotificationIcon = tipNotificationIcon;
    }

    public String getNotificationTitle() {
        return NotificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        NotificationTitle = notificationTitle;
    }

    public String getNotificationText() {
        return NotificationText;
    }

    public void setNotificationText(String notificationText) {
        NotificationText = notificationText;
    }

    public int getTipNotificationPriority() {
        return TipNotificationPriority;
    }

    public void setTipNotificationPriority(int tipNotificationPriority) {
        TipNotificationPriority = tipNotificationPriority;
    }

    public int getTipNotificationID() {
        return TipNotificationID;
    }

    public void setTipNotificationID(int tipNotificationID) {
        TipNotificationID = tipNotificationID;
    }

    public int getTipNotificationIcon() {
        return TipNotificationIcon;
    }

    public void setTipNotificationIcon(int tipNotificationIcon) {
        TipNotificationIcon = tipNotificationIcon;
    }
}
