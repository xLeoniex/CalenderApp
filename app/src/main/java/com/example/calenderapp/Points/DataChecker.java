/*
 * *************************************************
 *   Author :           Ehsan Khademi
 *   SubAuthor :        None
 *   Beschreibung :     Das Ziel dieser Klasse ist es, das Datum
 *                      von Ereignissen zu überprüfen, so dass beim
 *                      Punkt Sammlung die richtige Woche oder der
 *                      richtige Monat zugeordnet werden kann.
 *                      Oder wenn das Datum in der Zukunft liegt,
 *                      ist dies nicht möglich.
 *   Letzte Änderung :  13/02/2024
 * *************************************************
 */
package com.example.calenderapp.Points;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class DataChecker {
    // Kalenderinstanz für die Datumsmanipulation
    Calendar cal = Calendar.getInstance();
    // Aktuelles Jahr und Monat ermitteln
    int currentYear = cal.get(Calendar.YEAR);
    int currentMonth = cal.get(Calendar.MONTH) + 1;

    // Datum im Format "yyyy-MM-dd" für die Konvertierung
    SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Date date;

    // Methode zur Überprüfung, ob ein Datum im aktuellen Monat liegt
    public boolean currentMonth(String eventDate) {
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

    // Methode zur Überprüfung, ob ein Datum in der aktuellen Woche liegt
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

    // Methode zur Überprüfung, ob ein Datum in der letzten Woche lag
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

    // Methode zur Überprüfung, ob ein Datum in der Zukunft liegt
    public boolean isFutureDate(String eventDate) {
        try {
            date = dateForm.parse(eventDate);
            if (date != null) {
                cal.setTime(date);
                return cal.getTime().after(Calendar.getInstance().getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
