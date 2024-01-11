package com.example.calenderapp.calenderView.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.calenderapp.events.Event;
import com.example.calenderapp.events.model.EventModel;
import com.example.calenderapp.events.source.EventRepository;

import java.time.LocalDate;
import java.util.List;

public class EventListViewModel extends AndroidViewModel {

    private EventRepository repository;

    private MutableLiveData<List<EventModel>> currentEventsForUser = new MutableLiveData<>();
    private MutableLiveData<List<EventModel>> eventsOfDay = new MutableLiveData<>();


    public EventListViewModel(@NonNull Application application) {
        super(application);
        repository = new EventRepository();
    }

    public MutableLiveData<List<EventModel>> getAllEvents()
    {
        currentEventsForUser = repository.getEventModelList();
        return currentEventsForUser;
    }
    public MutableLiveData<List<EventModel>> getEventsOfDay(LocalDate date)
    {
        eventsOfDay = repository.getEventsOfDateFromFirebase(date);
        return eventsOfDay;
    }

    public void removeEventFromRepository(EventModel eventModel)
    {
        repository.RemoveEventFromRepo(eventModel);
    }
}
