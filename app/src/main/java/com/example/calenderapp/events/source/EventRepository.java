package com.example.calenderapp.events.source;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.calenderapp.events.model.EventModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser currentUser;

    private MutableLiveData<List<EventModel>> eventModelList = new MutableLiveData<>();
    private MutableLiveData<List<EventModel>> eventListOfDate = new MutableLiveData<>();





    public EventRepository() {
        database = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = database.getReference("users").child(currentUser.getUid()).child("Events");

    }
    public void AddEventToRepo(EventModel eventModel)
    {
        if(eventModel !=null)
        {
            DatabaseReference pushRef =  reference.push();
            String mKey = pushRef.getKey();
            eventModel.setEventId(mKey);
            pushRef.setValue(eventModel);
        }
    }
    public void RemoveEventFromRepo(EventModel eventModel)
    {
        if(eventModel !=null && eventModel.getEventId() != "")
        {
            reference.child(eventModel.getEventId()).removeValue();
        }
    }



    public MutableLiveData<List<EventModel>> getEventModelList() {

        List<EventModel> eventModelListInRepository = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                EventModel currentEventModel = new EventModel();
                String currentKey;
                eventModelListInRepository.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    currentEventModel = dataSnapshot.getValue(EventModel.class);
                    eventModelListInRepository.add(currentEventModel);
                }
                eventModelList.postValue(eventModelListInRepository);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return eventModelList;
    }

    public MutableLiveData<List<EventModel>> getEventsOfDateFromFirebase(LocalDate date)
    {
        List<EventModel> eventModelListOfDateInRepository = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventModelListOfDateInRepository.clear();
                EventModel currentEventModel = new EventModel();
                final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
                String currentKey;
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    currentKey = dataSnapshot.getKey();
                    Log.d("DataKey",currentKey);
                    currentEventModel = dataSnapshot.getValue(EventModel.class);


                    LocalDate parsedDate = LocalDate.parse(currentEventModel.getEventDate());
                    if(parsedDate.isEqual(date))
                    {
                        eventModelListOfDateInRepository.add(currentEventModel);
                    }
                }
                eventListOfDate.postValue(eventModelListOfDateInRepository);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return eventListOfDate;
    }

    public String getUserEmail()
    {
        return currentUser.getEmail();
    }

    public String getUserUsername()
    {
        return currentUser.getUid();
    }

    public FirebaseUser getCurrentUser()
    {
        return currentUser;
    }
}
