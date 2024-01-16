package com.example.calenderapp.events.ui.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.calenderapp.events.Event;
import com.example.calenderapp.events.model.EventModel;
import com.example.calenderapp.events.source.EventRepository;
import com.example.calenderapp.events.utils.ErrorMessages;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EventViewModel extends AndroidViewModel {
    //region Class constructor and fields

    // Error Messages
    private ErrorMessages errorMessages;
    // Add repo
    private EventRepository repository;

    // for Testing

    // for spinner onSelectedItems position
    public MutableLiveData<Integer> event_type_spinner_selectedItemPosition = new MutableLiveData<>();
    public MutableLiveData<Integer>  event_recurringType_spinner_selectedItemPosition = new MutableLiveData<>();
    public MutableLiveData<Integer>  event_weight_spinner_selectedItemPosition = new MutableLiveData<>();


    // fields to be observed
    // They have to be public , otherwise i cannot bind them in the XML layout

    public MutableLiveData<String> eventDate = new MutableLiveData<>();
    public MutableLiveData<String> eventName = new MutableLiveData<>();
    public MutableLiveData<String> startingTime = new MutableLiveData<>();
    public MutableLiveData<String> endingTime = new MutableLiveData<>();
    public MutableLiveData<String> eventType = new MutableLiveData<>();
    public MutableLiveData<String> recurringEventType = new MutableLiveData<>();
    public MutableLiveData<String> eventDescription = new MutableLiveData<>();
    public MutableLiveData<String> eventWeight = new MutableLiveData<>();

    public MutableLiveData<String> eventId = new MutableLiveData<>();
    public MutableLiveData<String> eventState = new MutableLiveData<>();
    public MutableLiveData<String> eventImageUrl = new MutableLiveData<>();
    private MutableLiveData<EventModel> eventModelMutableLiveData ;

    public EventViewModel(@NonNull Application application) {
        super(application);
        repository = new EventRepository();
        errorMessages = new ErrorMessages();

    }

    // For Testing Only !!!!
    public EventViewModel(@NonNull Application application, EventRepository repository) {
        super(application);
        this.repository = repository;
    }
    //endregion

    //region methods

    public MutableLiveData<EventModel> getEventDetails(){
        if(eventModelMutableLiveData == null)
        {
            eventModelMutableLiveData = new MutableLiveData<>();
        }
        return eventModelMutableLiveData;
    }

    private EventModel CreateEvent()
    {
        String eventImageUrlValue = eventImageUrl.getValue() !=null ? eventImageUrl.getValue():"NoImage";
        String eventStateValue = eventState.getValue() !=null ? eventState.getValue():"inProgress";
        String eventIdValue = eventId.getValue() !=null ? eventId.getValue():"";
        String eventDateValue = eventDate.getValue() !=null ? eventDate.getValue():"";
        String eventNameValue = eventName.getValue() !=null ? eventName.getValue():"";
        String startingTimeValue = startingTime.getValue()!=null ? startingTime.getValue():"";
        String endingTimeValue = endingTime.getValue()!=null ? endingTime.getValue():"";
        String eventTypeValue = eventType.getValue()!=null ? eventType.getValue():"None";
        String recurringEventTypeValue = recurringEventType.getValue()!=null ? recurringEventType.getValue():"None";
        String eventDescriptionValue = eventDescription.getValue()!=null ? eventDescription.getValue():"Nothing";
        String eventWeightValue = eventWeight.getValue() !=null ?eventWeight.getValue():"None" ;
        EventModel event = new EventModel(eventDateValue,eventImageUrlValue,eventNameValue, startingTimeValue,
                endingTimeValue, eventTypeValue, recurringEventTypeValue, eventDescriptionValue,eventWeightValue,eventIdValue,eventStateValue);

        return event;
    }
    private void CreateEventOnMutableData(EventModel eventModel)
    {
        eventModelMutableLiveData.setValue(eventModel);
    }

    public void PushEventToRepo(EventModel eventModel)
    {
        repository.AddEventToRepo(eventModel);
    }
    public String OnClickHandler()
    {
        String msg;
        EventModel myEvent =  CreateEvent();
        CreateEventOnMutableData(myEvent);
        msg = ValidateAddEvent(myEvent,errorMessages);
        if(msg.equals(errorMessages.getConfirmation_msg_Event_Is_Added_To_Repository()))
        {
            if(myEvent.getEventId().isEmpty())
            {
                PushEventToRepo(myEvent);
                msg = errorMessages.getConfirmation_msg_Event_Is_Added_To_Repository();
            }else
            {
                updateEventToRepo(myEvent);
                msg = errorMessages.getUpdate_msg_Event_Is_Updated_And_Added_To_Repository();
            }

        }

        return msg;
    }

    public void updateEventToRepo(EventModel eventModel)
    {
        repository.updateEvent(eventModel);
    }
    public void updateEvent(EventModel eventModel)
    {

    }


    //endregion

    //region checks

    private boolean IsStartingTimeSmallerThanEndingTime(String startTime,String endTime)
    {
        boolean is_true= true;
        LocalTime _startTime = ConvertStringToTime(startTime);
        LocalTime _endTime = ConvertStringToTime(endTime);
        if(_startTime.isAfter(_endTime) || _startTime == _endTime)
        {
            is_true = false;
        }

        return is_true;
    }
    private boolean IsTimeFormatRight(String time)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try
        {
            LocalTime localTime = LocalTime.parse(time,formatter);
            return true;
        }catch (DateTimeParseException e)
        {
            return false ;
        }
    }



    private String ValidateAddEvent(EventModel model,ErrorMessages msg)
    {
        String StartingTime = model.getStartingTime();
        String EndingTime =model.getEndingTime();
        String name = model.getEventName();
        if(name.isEmpty() || StartingTime.isEmpty() || EndingTime.isEmpty())
        {
            return errorMessages.getError_msg_No_Needed_Elements();
        }
        if(!IsTimeFormatRight(StartingTime) || !IsTimeFormatRight(EndingTime))
        {
            return errorMessages.getError_msg_Time_Is_Not_In_Right_Format();
        }
        if(!IsStartingTimeSmallerThanEndingTime(StartingTime,EndingTime))
        {
            return errorMessages.getError_msg_StartTime_MustBe_Smaller_Than_EndTime();
        }
        return errorMessages.getConfirmation_msg_Event_Is_Added_To_Repository();
    }

    //endregion

    //region Helpers
    private LocalTime ConvertStringToTime(String string_time)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime localTime = LocalTime.parse(string_time,formatter);

        return localTime;
    }
    //endregion

    //region UserInfos

    public String getCurrentUserEmail()
    {
        return repository.getUserEmail();
    }

    public String getCurrentUserUsername()
    {
        return repository.getUserUsername();
    }
    public FirebaseUser getCurrentUser()
    {
        return repository.getCurrentUser();
    }

    public MutableLiveData<Uri> uploadImage(Uri file)
    {
        return repository.uploadEventImageToStorage(file);
    }

    //endregion

}
