package com.example.calenderapp.Notification;

import android.icu.util.Calendar;

public class DateTimeHelper {
    public static long getMidnightTimestampForLastDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        resetTimeToMidnight(calendar);
        return calendar.getTimeInMillis();
    }

    public static long getMidnightTimestampForSunday() {
        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysUntilSunday = Calendar.SUNDAY - currentDayOfWeek;

        if (daysUntilSunday <= 0) {
            // If today is Sunday, set it for the next Sunday
            daysUntilSunday += 7;
        }

        calendar.add(Calendar.DAY_OF_MONTH, daysUntilSunday);
        resetTimeToMidnight(calendar);
        return calendar.getTimeInMillis();
    }

    private static void resetTimeToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
