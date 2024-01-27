package com.example.calenderapp.dashBoard;


import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;

import android.Manifest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.GrantPermissionRule;

import com.example.calenderapp.DashboardBar.Dashboard;
import com.example.calenderapp.Login.Login;
import com.example.calenderapp.dashBoard.Helper.DashBoardHelper;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class DashBoardTests extends TestCase {

    @Rule
    public ActivityScenarioRule<Dashboard> rule = new ActivityScenarioRule(Dashboard.class);
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);
    @Before
    public void setup()
    {
        init();
        ActivityScenario<Dashboard> scenario = ActivityScenario.launch(Dashboard.class);
        // if you want to run all the test at once , just comment the following line
        myHelper.PerformLogin();

    }
    @After
    public void tearDown()
    {
        myHelper.PerformLogoutFromMenuBar();
        release();
        rule.getScenario().close();
    }
    private DashBoardHelper myHelper = new DashBoardHelper();
    @Test
    public void GivenLoggedInUser_WhenUserIsLoggedIn_ThenExpectToStartOnDashBoard()
    {
        //myHelper.PerformLogin();
        myHelper.CheckDashboardScreenIsDisplayed();
    }

    @Test
    public void GivenLoggedInUser_WhenUserClickCalenderButton_ThenExpectCalenderViewIsDisplayed()
    {
        myHelper.CheckDashboardScreenIsDisplayed();
        myHelper.PerformGoToCalenderClick();
        myHelper.CheckCalenderScreenIsDisplayed();
    }
    @Test
    public void GivenLoggedInUser_WhenUserClickPointsButton_ThenExpectPointsViewIsDisplayed()
    {
        myHelper.CheckDashboardScreenIsDisplayed();
        myHelper.PerformGoToPointsClick();
        myHelper.CheckPointsScreenIsDisplayed();
    }
    @Test
    public void GivenLoggedInUser_WhenUserClickTipsButton_ThenExpectTipsViewIsDisplayed()
    {
        myHelper.CheckDashboardScreenIsDisplayed();
        myHelper.PerformGoToTipListClick();
        myHelper.CheckTipListViewIsDisplayed();
    }

    @Test
    public void GivenLoggedInUser_WhenUserClickEventsButton_ThenExpectEventsViewIsDisplayed()
    {
        myHelper.CheckDashboardScreenIsDisplayed();
        myHelper.PerformGoToAllEventsClick();
        myHelper.CheckEventsScreenIsDisplayed();
    }

}
