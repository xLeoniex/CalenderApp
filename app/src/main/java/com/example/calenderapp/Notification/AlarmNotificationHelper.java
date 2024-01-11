package com.example.calenderapp.Notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;

import androidx.core.app.NotificationCompat;

import com.example.calenderapp.Login.Login;
import com.example.calenderapp.R;

public class AlarmNotificationHelper {
 /*   private static final String ALARM_ACTION = "com.example.calenderapp.ALARM_ACTION";

    public static void scheduleAlarm(Context context, long triggerTimeMillis) {
        Intent intent = getAlarmIntent(context);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, triggerTimeMillis, pendingIntent);
    }

    public static PendingIntent getAlarmIntent(Context context) {
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        // intent.putExtra("KEY", "VALUE");

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }



    public static void showNotification(Context context, String title, String message) {
        // ToDo:  Implementiere hier die Logik zum Anzeigen der Push-Nachricht
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Hier wird ein Intent erstellt, der beim Klicken auf die Benachrichtigung ausgelöst wird.
        Intent intent = new Intent(context, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Hier wird die Benachrichtigung erstellt und konfiguriert.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_info)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true) // Die Benachrichtigung wird automatisch geschlossen, wenn darauf geklickt wird.
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Überprüfen, ob der Android-Version Oreo oder höher ist, da für Oreo und höher ein Benachrichtigungskanal erforderlich ist.
        CharSequence name = "Channel Name";
        String description = "Channel Description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
        channel.setDescription(description);

        // Hier wird der Benachrichtigungskanal erstellt und dem NotificationManager hinzugefügt.
        notificationManager.createNotificationChannel(channel);

        // Die Benachrichtigung wird angezeigt.
        notificationManager.notify(0, builder.build());

    }

  */
}
