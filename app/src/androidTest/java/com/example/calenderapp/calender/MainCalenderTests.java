package com.example.calenderapp.calender;


import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;

import android.Manifest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.GrantPermissionRule;

import com.example.calenderapp.DashboardBar.Dashboard;
import com.example.calenderapp.calender.Helper.CalenderHelper;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainCalenderTests extends TestCase {

    private CalenderHelper myHelper = new CalenderHelper();
    @Rule
    public ActivityScenarioRule<Dashboard> rule = new ActivityScenarioRule(Dashboard.class);
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);
    @Before
    public void setup()
    {
        init();
        ActivityScenario<Dashboard> scenario = ActivityScenario.launch(Dashboard.class);
        myHelper.PerformLogin();

    }
    @After
    public void tearDown()
    {
        myHelper.PerformLogoutFromMenuBar();
        release();
        rule.getScenario().close();
    }


    @Test
    public void GivenLoggedInUser_WhenUserClickCalenderButton_ThenExpectCalenderViewIsDisplayed()
    {
        myHelper.CheckDashboardScreenIsDisplayed();
        myHelper.PerformGoToCalenderClick();
        myHelper.CheckCalenderScreenIsDisplayed();
    }
    @Test
    public void GivenUserInCalenderView_WhenUserClickWeekButton_ThenExpectCalenderWeeklyViewIsDisplayed()
    {
        myHelper.CheckDashboardScreenIsDisplayed();
        myHelper.PerformGoToCalenderClick();
        myHelper.CheckCalenderScreenIsDisplayed();
        myHelper.PerformGoToWeeklyCalenderViewClick();
        myHelper.CheckCalenderScreenIsOnWeekly();
    }

    // verify Date

    @Test
    public void GivenLoggedInUser_WhenUserClickOnCalenderButton_ThenExpectToDisplayCalenderWithRightMonthAndYear()
    {
        myHelper.CheckDashboardScreenIsDisplayed();
        myHelper.PerformGoToCalenderClick();
        myHelper.CheckCalenderScreenIsDisplayed();

        myHelper.CheckCalenderCurrentDateIsCorrect();

        myHelper.PerformGoToLastMonthClick();
        myHelper.CheckCalenderLastMonthIsCorrect();

        myHelper.PerformGoToNextMonthClick();
        myHelper.CheckCalenderCurrentDateIsCorrect();

        myHelper.PerformGoToNextMonthClick();
        myHelper.CheckCalenderNextMonthIsCorrect();
    }

}
