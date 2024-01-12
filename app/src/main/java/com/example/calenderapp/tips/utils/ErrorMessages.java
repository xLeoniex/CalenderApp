package com.example.calenderapp.tips.utils;

public class ErrorMessages {
    private final String Error_msg_No_Needed_Elements = "ERROR : Tip title and type are mandatory";
    private final String Confirmation_msg_Tip_Is_Pushed_To_Repository = "Confirm: tip is added to database";
    public ErrorMessages() {
    }

    public String getError_msg_No_Needed_Elements() {
        return Error_msg_No_Needed_Elements;
    }

    public String getConfirmation_msg_Tip_Is_Pushed_To_Repository() {
        return Confirmation_msg_Tip_Is_Pushed_To_Repository;
    }
}
