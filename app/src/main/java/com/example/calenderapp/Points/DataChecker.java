package com.example.calenderapp.Points;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class DataChecker {
    Calendar cal = Calendar.getInstance();
    int currentYear = cal.get(Calendar.YEAR);
    int currentMonth = cal.get(Calendar.MONTH) + 1;
    // Datum in ein Date-Objekt umwandeln
    SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Date date;
    public boolean currentMonth(String eventDate){
        try {
            date = dateForm.parse(eventDate);
            if (date != null) {
                cal.setTime(date);
                int eventYear = cal.get(Calendar.YEAR);
                int eventMonth = cal.get(Calendar.MONTH) + 1;

                // Überprüfung, ob das Datum im aktuellen Monat liegt
                if (eventYear == currentYear && eventMonth == currentMonth) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    //Überprüfen ob ein Datum in aktuellen Monat Liegt
    public boolean currentWeek(String eventDate) {
        try {
            date = dateForm.parse(eventDate);
            if (date != null) {
                cal.setTime(date);
                int eventWeek = cal.get(Calendar.WEEK_OF_YEAR);
                int currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
                return eventWeek == currentWeek;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean lastWeek(String eventDate) {
        try {
            date = dateForm.parse(eventDate);
            if (date != null) {
                cal.setTime(date);
                int eventWeek = cal.get(Calendar.WEEK_OF_YEAR);
                int currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
                int lastWeek = (currentWeek - 1) % cal.getActualMaximum(Calendar.WEEK_OF_YEAR);
                return eventWeek == lastWeek;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
