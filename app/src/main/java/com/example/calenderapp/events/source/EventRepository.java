package com.example.calenderapp.events.source;

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

import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private FirebaseUser currentUser;

    private MutableLiveData<List<EventModel>> eventModelList = new MutableLiveData<>();



    public EventRepository() {
        database = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = database.getReference("users").child(currentUser.getUid()).child("Events");

    }



    public void AddEventToRepo(EventModel eventModel)
    {
        if(eventModel !=null)
        {
            reference.push().setValue(eventModel);
        }
    }
    public void RemoveEventFromRepo(EventModel eventModel)
    {
        if(eventModel !=null && eventModel.getEventName() != "")
        {
            reference.child(eventModel.getEventName()).removeValue();
        }
    }


    public MutableLiveData<List<EventModel>> getEventModelList() {

        List<EventModel> eventModelListInRepository = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                EventModel currentEventModel = new EventModel();
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
}
