package com.example.calenderapp.tips.Helpers;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasToString;

import android.os.SystemClock;

import androidx.test.espresso.matcher.ViewMatchers;

import com.example.calenderapp.R;
import com.example.calenderapp.login.Helper.LoginHelper;

public class TipsHelper {

    private LoginHelper loginHelper;
    public TipsHelper() {
        loginHelper = new LoginHelper();
    }


    //region Checks

    public void CheckDashboardScreenIsDisplayed()
    {
        onView(withId(R.id.dashboardLayout)).check(matches(isDisplayed()));
    }

    public void CheckTipListViewIsDisplayed()
    {
        onView(withId(R.id.tipsListLayout)).check(matches(isDisplayed()));
    }
    public void CheckTipInformationIsDisplayed()
    {
        onView(withId(R.id.open_tip_Layout)).check(matches(isDisplayed()));
    }

    public void CheckCreateTipIsDisplayed()
    {
        onView(withId(R.id.CreateTipsLayout)).check(matches(isDisplayed()));
    }

    public void CheckTipIsDisplayedOnTipList(String name)
    {
        onView(withText(name)).check(matches(isDisplayed()));
    }

    public void CheckTipAtPositionIsInTipList(String TipOnList,int position)
    {
        onData(anything())
                .inAdapterView(withId(R.id.list_allTips))
                .atPosition(position)
                .check(matches(withText(TipOnList)));
    }
    //endregion

    //region MainActions

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

    public void PerformGoToTipList()
    {
        PerformClick(R.id.btn_tipsView);
    }

    public void PerformGoToCreateTip()
    {
        PerformClick(R.id.btn_addTip);
    }
    public void PerformAddTipClick()
    {
        PerformClick(R.id.AddTipButton);
    }

    public void PerfromOnImageClick()
    {
        PerformClick(R.id.TipImageView);
    }

    public void EditTipTitle(String value)
    {
        EditingEditBoxesAndTexts(R.id.TipTitle,value);
    }

    public void EditTipDescription(String value)
    {
        EditingEditBoxesAndTexts(R.id.TipDescription,value);
    }

    public void ChoseTipType(String value)
    {
        getSpinnerValue(R.id.TipTypeSpinner,value);
    }

    public void AddTipWithoutImage(String Title,String Description,String Type)
    {
        EditTipTitle(Title);
        ChoseTipType(Type);
        EditTipDescription(Description);
        PerformAddTipClick();
    }

    public void PerformTipInformationClickAtPosition(int position)
    {
        onData(anything())
                .inAdapterView(withId(R.id.list_allTips))
                .atPosition(position)
                .perform(longClick());
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
