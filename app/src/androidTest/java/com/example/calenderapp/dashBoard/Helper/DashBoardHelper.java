package com.example.calenderapp.dashBoard.Helper;

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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasToString;

import android.os.SystemClock;

import androidx.test.espresso.matcher.ViewMatchers;

import com.example.calenderapp.R;
import com.example.calenderapp.login.Helper.LoginHelper;

public class DashBoardHelper {

    private LoginHelper loginHelper;

    public DashBoardHelper() {
        this.loginHelper = new LoginHelper();
    }

    public void CheckDashboardScreenIsDisplayed()
    {
        onView(withId(R.id.dashboardLayout)).check(matches(isDisplayed()));
    }
    public void CheckCalenderScreenIsDisplayed()
    {
        onView(withId(R.id.MainCalenderLayout)).check(matches(isDisplayed()));
    }
    public void CheckPointsScreenIsDisplayed()
    {
        onView(withId(R.id.PointsViewLayout)).check(matches(isDisplayed()));
    }

    public void CheckEventsScreenIsDisplayed()
    {
        onView(withId(R.id.AllEventsViewLayout)).check(matches(isDisplayed()));
    }


    public void CheckTipListViewIsDisplayed()
    {
        onView(withId(R.id.tipsListLayout)).check(matches(isDisplayed()));
    }

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

    public void PerformGoToCalenderClick()
    {
        PerformClick(R.id.btn_calenderView);
    }
    public void PerformGoToPointsClick()
    {
        PerformClick(R.id.btn_pointsView);
    }
    public void PerformGoToTipListClick()
    {
        PerformClick(R.id.btn_tipsView);
    }
    public void PerformGoToAllEventsClick()
    {
        PerformClick(R.id.btn_eventsView);
    }

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

}
