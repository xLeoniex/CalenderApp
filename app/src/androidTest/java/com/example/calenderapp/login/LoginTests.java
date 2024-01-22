package com.example.calenderapp.login;


import android.Manifest;
import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.GrantPermissionRule;

import com.bumptech.glide.manager.Lifecycle;
import com.example.calenderapp.Login.Login;
import com.example.calenderapp.login.Helper.LoginHelper;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginTests extends TestCase {

    @Rule
    public ActivityScenarioRule<Login> rule = new ActivityScenarioRule(Login.class);
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);
    private LoginHelper myHelper = new LoginHelper();

    @Before
    public void StartLoginActivity()
    {
        ActivityScenario<Login> scenario = ActivityScenario.launch(Login.class);
    }
    @After
    public void tearDown()
    {
        rule.getScenario().close();
    }


    @Test
    public void GivenOnLoginScreen_WhenLoginScreen_ThenExpectLoginScreenIsDisplayed()
    {
        myHelper.CheckIsLoginScreenDisplayed();

    }

    @Test
    public void GivenEmailAndPassword_WhenUserClickOnLoginIsSuccessful_ThenExpectDashboardScreenIsDisplayed()
    {
        String email = "tester123@gmail.com";
        String password = "123456789";

        myHelper.CheckIsLoginScreenDisplayed();
        myHelper.EditEmail(email);
        myHelper.EditPassword(password);
        myHelper.PerformLoginClick();
        SystemClock.sleep(1000);
        myHelper.CheckDashboardScreenIsDisplayed();
        myHelper.PerformLogoutFromMenuBar();
        myHelper.CheckIsLoginScreenDisplayed();
    }

    @Test
    public void GivenRestPasswordScreenAndEmail_WhenUserClickOnRestPassword_ThenExpectRestPasswordMessage()
    {
        String email = "tester123@gmail.com";

        SystemClock.sleep(1000);
        myHelper.CheckIsLoginScreenDisplayed();
        myHelper.PerformGoToRestPasswordClick();
        myHelper.CheckResetPasswordScreenIsDisplayed();
        myHelper.EditEmail(email);
        myHelper.PerformRestPassword();
        SystemClock.sleep(1000);
        myHelper.CheckIsLoginScreenDisplayed();
    }

    //region Helper


    //endregion
}
