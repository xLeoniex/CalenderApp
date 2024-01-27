package com.example.calenderapp.login.Helper;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasToString;

import android.os.SystemClock;
import android.view.View;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.calenderapp.Login.Login;
import com.example.calenderapp.Login.Register;
import com.example.calenderapp.R;

public class LoginHelper {

    public LoginHelper() {
    }

    // region Checks

    public void CheckIsLoginScreenDisplayed()
    {
        onView(withId(R.id.LoginLayout)).check(matches(isDisplayed()));
    }

    public void CheckDashboardScreenIsDisplayed()
    {
        onView(withId(R.id.dashboardLayout)).check(matches(isDisplayed()));
    }

    public void CheckResetPasswordScreenIsDisplayed()
    {
        onView(withId(R.id.resetPasswordLayout)).check(matches(isDisplayed()));
    }
    public void CheckRegisterScreenIsDisplayed()
    {
        onView(withId(R.id.registerLayout)).check(matches(isDisplayed()));
    }
    public void CheckRegisteredSnackbarMsg(String username)
    {
        String msg = "Account Created " + username+ " .";
        CheckToastMessage(msg);
    }
    //endregion

    //region Main Actions
    public void EditEmail(String value)
    {
        EditingEditBoxesAndTexts(R.id.email,value);
    }

    public void EditPassword(String value)
    {
        EditingEditBoxesAndTexts(R.id.password,value);
    }

    public void EditUsername(String value)
    {
        EditingEditBoxesAndTexts(R.id.username,value);
    }
    public void EditConfirmPassword(String value)
    {
        EditingEditBoxesAndTexts(R.id.confirmPassword,value);
    }
    public void PerformLoginClick()
    {
        PerformClick(R.id.btn_login);
    }

    public void PerformGoToRestPasswordClick()
    {
        PerformClick(R.id.resetPassword);
    }

    public void PerformGoToRegisterScreenClick()
    {
        PerformClick(R.id.RegisterNow);
    }

    public void PerformRestPassword()
    {
        PerformClick(R.id.btn_resetPassword);
    }

    public void PerformProfileMenuClick()
    {
        PerformClick(R.id.profileMenu);
    }

    public void PerformLogoutFromMenuBar()
    {
        onView(withId(R.id.profileMenu)).perform((click()));
        SystemClock.sleep(1000);
        onData(hasToString("Logout")).perform(click());
    }


    public void PerformRegisterClick()
    {
        PerformClick(R.id.btn_register);
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

    //region Toasts

    private void CheckToastMessage(String msg)
    {
        onView(withText(msg)).check(matches(withEffectiveVisibility(
                ViewMatchers.Visibility.VISIBLE
        )));
    }
    //endregion
}
