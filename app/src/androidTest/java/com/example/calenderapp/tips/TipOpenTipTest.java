package com.example.calenderapp.tips;

import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.Intents.release;

import static org.hamcrest.core.AllOf.allOf;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.GrantPermissionRule;

import com.example.calenderapp.Login.Login;
import com.example.calenderapp.R;
import com.example.calenderapp.tips.Helpers.TipsHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class TipOpenTipTest extends TestCase {

    private TipsHelper myHelper = new TipsHelper();
    private final  String TipId = "-NpGEpBknpQdqq_kTr16";

    @Rule
    public ActivityScenarioRule<Login> rule = new ActivityScenarioRule<>(Login.class);
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);



    @Before
    public void setUp()
    {
        if (isUserLoggedIn()) {
            // If the user is already logged in, launch the OpenTipView activity
            rule.getScenario().onActivity(activity -> {
                Intent intent = new Intent(activity, OpenTipView.class)
                        .putExtra("tip-ID", TipId);
                activity.startActivity(intent);
            });
            init();
        } else {
            // If the user is not logged in, perform the login
            myHelper.PerformLogin();
            init();

            // Now, launch the OpenTipView activity after login
        }

    }
    @After
    public void tearDown()
    {
        myHelper.PerformLogoutFromMenuBar();
        release();
        rule.getScenario().close();
    }
    @Test
    public void GivenLoggedInUser_WhenUserClickOnATip_ThenExpectTipDetailsIsDisplayed()
        {
            myHelper.PerformGoToTipList();
            SystemClock.sleep(2000);
            myHelper.PerformTipInformationClickAtPosition(0);
            SystemClock.sleep(2000);
            myHelper.CheckTipInformationIsDisplayed();

        }
    private boolean isUserLoggedIn() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser != null;
    }
}
