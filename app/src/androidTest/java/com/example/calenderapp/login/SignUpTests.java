package com.example.calenderapp.login;

import android.Manifest;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.calenderapp.Login.Login;
import com.example.calenderapp.Login.Register;
import com.example.calenderapp.login.Helper.LoginHelper;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignUpTests extends TestCase {
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
    public void GivenNewUser_WhenUserSetupNewAccountWithEmailAndPassword_ThenExpectTheUserToBeRegistered()
    {
        /* Run this Test only once !!!*/
        String userName ="New_User";
        String email = "newuser123@gmail.com";
        String Password = "123456789";

        myHelper.CheckIsLoginScreenDisplayed();
        myHelper.PerformGoToRegisterScreenClick();
        myHelper.CheckRegisterScreenIsDisplayed();

        myHelper.EditUsername(userName);
        myHelper.EditEmail(email);
        myHelper.EditPassword(Password);
        myHelper.EditConfirmPassword(Password);
        myHelper.PerformRegisterClick();
        SystemClock.sleep(1000);
        myHelper.CheckDashboardScreenIsDisplayed();
    }



}
