package com.example.calenderapp.tips;

import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import static org.hamcrest.core.AllOf.allOf;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.GrantPermissionRule;

import com.example.calenderapp.DashboardBar.Dashboard;
import com.example.calenderapp.Login.Login;
import com.example.calenderapp.R;
import com.example.calenderapp.tips.Helpers.TipsHelper;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class TipOverViewTests extends TestCase {

    @Rule
    public ActivityScenarioRule<Login> rule = new ActivityScenarioRule(Login.class);
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);
    private TipsHelper myHelper = new TipsHelper();

    @Before
    public void StartDashBoard()
    {

        ActivityScenario<Login> scenario = ActivityScenario.launch(Login.class);
        init();
        // if you want to run all the test at once , just comment the following line
        myHelper.PerformLogin();

    }
    @After
    public void tearDown()
    {
        release();
        rule.getScenario().close();
    }

    @Test
    public void GivenDashboard_WhenUserClicksOnTips_ThenExpectTipsListIsDisplayed()
    {
        myHelper.PerformLogin();
        myHelper.CheckDashboardScreenIsDisplayed();
        myHelper.PerformGoToTipList();
        myHelper.CheckTipListViewIsDisplayed();
    }

    @Test
    public void GivenNewTip_WhenUserClicksOnTheTipInTipList_ThenExpectTipInformationIsDisplayed()
    {
        String tipTitle ="Generic Tip";
        String tipType = "Exercise";
        String TipOnList = tipTitle + " (" +tipType + ") " + "Not completed";

        GotoCreateTipsScreen();
        CreateSomeTip();

        myHelper.CheckTipAtPositionIsInTipList(TipOnList,0);
        SystemClock.sleep(1000);
        myHelper.PerformTipInformationClickAtPosition(1);
        SystemClock.sleep(8000);
        intended(hasComponent(OpenTipView.class.getName()));
    }

    private void GotoCreateTipsScreen()
    {
        myHelper.CheckDashboardScreenIsDisplayed();
        myHelper.PerformGoToTipList();
        myHelper.CheckTipListViewIsDisplayed();
        myHelper.PerformGoToCreateTip();
        myHelper.CheckCreateTipIsDisplayed();
    }

    private void CreateSomeTip()
    {
        String tipTitle ="Generic Tip";
        String tipType = "Exercise";
        String tipDescription = "Some Generic Tip with image";
        String TipOnList = tipTitle + " (" +tipType + ") " + "Not completed";

        CreateMockImage();
        myHelper.PerfromOnImageClick();
        myHelper.AddTipWithoutImage(tipTitle,tipDescription,tipType);
        SystemClock.sleep(3000);
        myHelper.CheckTipListViewIsDisplayed();
    }

    private void CreateMockImage()
    {
        Uri uri1 = getURLForResource(R.drawable.test);
        Intent resultData = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        resultData.setData(uri1);
        intending(allOf()).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));
    }
    public Uri getURLForResource (int resourceId) {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("android.resource://" +R.class.getPackage().getName()+"/" +resourceId);
    }
}
