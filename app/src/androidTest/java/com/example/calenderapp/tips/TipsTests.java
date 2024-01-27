package com.example.calenderapp.tips;


import static android.app.Instrumentation.*;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.intent.Intents.*;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.intent.IntentCallback;

import com.bumptech.glide.load.engine.Resource;
import com.example.calenderapp.DashboardBar.Dashboard;
import com.example.calenderapp.Login.Login;
import com.example.calenderapp.R;
import com.example.calenderapp.login.Helper.LoginHelper;
import com.example.calenderapp.tips.Helpers.TipsHelper;
import com.example.calenderapp.tips.ui.view.CreateTipsActivity;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.AllOf.allOf;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;

@RunWith(AndroidJUnit4ClassRunner.class)
public class TipsTests extends TestCase {
    @Rule
    public ActivityScenarioRule<Dashboard> rule = new ActivityScenarioRule(Dashboard.class);
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);
    private TipsHelper myHelper = new TipsHelper();

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
    @Test
    public void GivenDashboard_WhenUserClicksOnTips_ThenExpectTipsListIsDisplayed()
    {
        myHelper.CheckDashboardScreenIsDisplayed();
        myHelper.PerformGoToTipList();
        myHelper.CheckTipListViewIsDisplayed();
    }

    @Test
    public void GivenCreateTipsScreen_WhenUserCreatesTipWithoutImage_ThenExpectTipIsDisplayedOnTipsList()
    {
        String tipTitle ="Tip without Image";
        String tipType = "Exercise";
        String tipDescription = "Some Tip without image";
        String TipOnList = tipTitle + " (" +tipType + ") " + "Not completed";

        GotoCreateTipsScreen();
        myHelper.AddTipWithoutImage(tipTitle,tipDescription,tipType);
        SystemClock.sleep(3000);
        myHelper.CheckTipListViewIsDisplayed();

        myHelper.CheckTipAtPositionIsInTipList(TipOnList,1);
    }

    @Test
    public void GivenCreateTipsScreen_WhenUserCreatesTipWithImage_ThenExpectTipIsDisplayedOnTipsList()
    {

        String tipTitle ="Tip With image";
        String tipType = "Exercise";
        String tipDescription = "Some Tip with image";
        String TipOnList = tipTitle + " (" +tipType + ") " + "Not completed";

        GotoCreateTipsScreen();
        CreateMockImage();
        myHelper.PerfromOnImageClick();
        myHelper.AddTipWithoutImage(tipTitle,tipDescription,tipType);
        SystemClock.sleep(3000);
        myHelper.CheckTipListViewIsDisplayed();
    }



    //region NavigationHelper
    private void GotoCreateTipsScreen()
    {
        myHelper.CheckDashboardScreenIsDisplayed();
        myHelper.PerformGoToTipList();
        myHelper.CheckTipListViewIsDisplayed();
        myHelper.PerformGoToCreateTip();
        myHelper.CheckCreateTipIsDisplayed();
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
    //endregion
}
