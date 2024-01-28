package com.example.calenderapp.calender.Helper;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasToString;

import android.os.SystemClock;
import android.util.Log;

import androidx.test.espresso.matcher.ViewMatchers;

import com.example.calenderapp.R;
import com.example.calenderapp.login.Helper.LoginHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalenderHelper {
    private LoginHelper loginHelper;


    public CalenderHelper() {
        loginHelper = new LoginHelper();
    }



    //region Checks
    public void CheckDashboardScreenIsDisplayed()
    {
        onView(withId(R.id.dashboardLayout)).check(matches(isDisplayed()));
    }
    public void CheckCalenderScreenIsDisplayed()
    {
        onView(withId(R.id.MainCalenderLayout)).check(matches(isDisplayed()));
    }

    public void CheckCalenderScreenIsOnWeekly()
    {
        onView(withId(R.id.CalenderWeeklyviewLayout)).check(matches(isDisplayed()));
    }
    public void CheckCalenderScreenIsOnDaily()
    {
        onView(withId(R.id.CalenderDailyViewLayout)).check(matches(isDisplayed()));
    }

    public void CheckCalenderCurrentDateIsCorrect()
    {
        String currentDate = getCurrentDate();
        onView(withId(R.id.monthYearTV))
                .check(matches(withText(currentDate)));
    }
    public void CheckCalenderLastMonthIsCorrect()
    {
        String currentDate = getLastMonthDateTime();
        onView(withId(R.id.monthYearTV))
                .check(matches(withText(currentDate)));
    }
    public void CheckCalenderNextMonthIsCorrect()
    {
        String currentDate = getNextMonthDateTime();
        onView(withId(R.id.monthYearTV))
                .check(matches(withText(currentDate)));
    }

    //endregion


    //region Login-Logout
    public void PerformLogin()
    {
        String email = "tester123@gmail.com";
        String password = "123456789";

        loginHelper.CheckIsLoginScreenDisplayed();
        loginHelper.EditEmail(email);
        loginHelper.EditPassword(password);
        loginHelper.PerformLoginClick();
        SystemClock.sleep(1000);
        loginHelper.CheckDashboardScreenIsDisplayed();
    }

    public void PerformLogoutFromMenuBar()
    {
        onView(withId(R.id.profileMenu)).perform((click()));
        SystemClock.sleep(1000);
        onData(hasToString("Logout")).perform(click());
    }

    //endregion

    //region main Clicks
    public void PerformGoToCalenderClick()
    {
        PerformClick(R.id.btn_calenderView);
    }

    public void PerformGoToWeeklyCalenderViewClick()
    {
        PerformClick(R.id.btn_radio_Week);
    }
    public void PerformGoToNextMonthClick()
    {
        PerformClick(R.id.NextMonthButton);
    }
    public void PerformGoToLastMonthClick()
    {
        PerformClick(R.id.previousMonthButton);
    }


    //endregion

    //region Typing

    private void EditingEditBoxesAndTexts(int id ,String value)
    {
        onView(withId(id)).perform(clearText());
        onView(withId(id)).perform(click());
        onView(withId(id)).perform(typeText(value));
        SystemClock.sleep(500);
        onView(ViewMatchers.isRoot()).perform(closeSoftKeyboard());
    }
    // endregion

    //region clicks


    private void PerformClick(int id)
    {
        onView(withId(id)).perform((click()));
        SystemClock.sleep(500);
    }

    //endregion

    //region Spinners
    private void getSpinnerValue(int id,String value)
    {
        onView(withId(id)).perform(click());
        onData(hasToString(value)).perform(click());
        onView(withId(id)).check(matches(withSpinnerText(containsString(value))));
        SystemClock.sleep(500);
    }

    //endregion

    //region Date
    private String getCurrentDate() {
        // Get the current date in the desired format (e.g., "MMM yyyy" for "Jun 2024")
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getNextMonthDateTime() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1); // Move to the next month

        // Format the date and time with the full month name (e.g., "January 01, 2024 12:34")
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
    private String getLastMonthDateTime() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1); // Move to the last month

        // Format the date and time with the full month name (e.g., "January 01, 2024 12:34")
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
    //endregion
}
