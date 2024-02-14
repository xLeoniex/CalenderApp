/*
 * *************************************************
 *   Author :           Ahmed Ibrahim Almohamed
 *   SubAuthor :        None
 *   Beschreibung :     an ErrorMessages class to generate error messages related to the creation
 *                      of an Event
 *   Letzte Änderung :  16/01/2024, 13:14
 * *************************************************
 */

package com.example.calenderapp.events.utils;

public class ErrorMessages {
    private final String Error_msg_No_Needed_Elements = "ERROR: EventName,StartingTime and Ending Time are mandatory";
    private final String Error_msg_StartTime_MustBe_Smaller_Than_EndTime = "ERROR: Starting Time is after the EndingTime";
    private final String Confirmation_msg_Event_Is_Added_To_Repository = "Confirm: Event is added to the firebase repository";
    private final String Update_msg_Event_Is_Updated_And_Added_To_Repository = "Updated: Event is updated and added to the firebase repository";
    private final String Error_msg_Time_Is_Not_In_Right_Format = "ERROR: Time must be in format: HH:mm";
    public ErrorMessages() {

    }

    public String getError_msg_No_Needed_Elements() {
        return Error_msg_No_Needed_Elements;
    }

    public String getError_msg_StartTime_MustBe_Smaller_Than_EndTime() {
        return Error_msg_StartTime_MustBe_Smaller_Than_EndTime;
    }

    public String getConfirmation_msg_Event_Is_Added_To_Repository() {
        return Confirmation_msg_Event_Is_Added_To_Repository;
    }

    public String getError_msg_Time_Is_Not_In_Right_Format() {
        return Error_msg_Time_Is_Not_In_Right_Format;
    }

    public String getUpdate_msg_Event_Is_Updated_And_Added_To_Repository() {
        return Update_msg_Event_Is_Updated_And_Added_To_Repository;
    }
}
