package com.example.calenderapp.Notification.PointsNotification;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class myJobIntentService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Intent broadcastIntent = new Intent(this, MyBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
