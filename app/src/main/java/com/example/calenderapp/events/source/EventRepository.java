/*
 * *************************************************
 *   Author :           Ahmed Ibrahim Almohamed
 *   SubAuthor :        None
 *   Beschreibung :     Repository class to handel Firebase queries and bind it
 *                      to the EventViewModel
 *   Letzte Änderung :  14/02/2024, 18:23
 * *************************************************
 */

package com.example.calenderapp.events.source;

import static com.example.calenderapp.calenderView.CalendarUtils.selectedDate;

import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.calenderapp.events.Event;
import com.example.calenderapp.events.model.EventModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.nio.channels.ScatteringByteChannel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventRepository {
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser currentUser;
    private StorageReference storageRef;
    private FirebaseStorage storage;

    private MutableLiveData<List<EventModel>> eventModelList = new MutableLiveData<>();
    private MutableLiveData<List<EventModel>> eventListOfDate = new MutableLiveData<>();
    private MutableLiveData<List<EventModel>> eventListOfMonth = new MutableLiveData<>();
    private MutableLiveData<Uri> currentUrl = new MutableLiveData<>();




    public EventRepository() {
        database = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = database.getReference("users").child(currentUser.getUid()).child("Events");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("Images").child("Gallery");
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
                    LocalDate parsedDate;
                    try {
                        parsedDate = LocalDate.parse(currentEventModel.getEventDate());
                    }catch(Exception e){
                        Date currentDate = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        parsedDate = LocalDate.parse(dateFormat.format(currentDate));
                    }
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

    public MutableLiveData<List<EventModel>> getEventsOfMonth(LocalDate month)
    {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<EventModel> currentEventsOfMonth = new ArrayList<>();
                EventModel currentEventModel = new EventModel();
                currentEventsOfMonth.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    currentEventModel = dataSnapshot.getValue(EventModel.class);
                    Month currentMonth;
                    try {
                        currentMonth = LocalDate.parse(currentEventModel.getEventDate()).getMonth();
                    }catch(Exception e){
                        Date currentDate = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        currentMonth = LocalDate.parse(dateFormat.format(currentDate)).getMonth();
                    }
                    int currentMonthValue = currentMonth.getValue();
                    int givenMonthValue = month.getMonth().getValue();
                    if(currentMonthValue == givenMonthValue)
                    {
                        Log.d("DateToFind","Date is :"+ currentEventModel.getEventDate());
                        currentEventsOfMonth.add(currentEventModel);
                    }
                }
                eventListOfMonth.postValue(currentEventsOfMonth);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return eventListOfMonth;
    }
    public MutableLiveData<Uri> uploadEventImageToStorage(Uri uri)
    {
        if(uri != null)
        {
            storageRef.child(uri.getLastPathSegment()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageRef.child(uri.getLastPathSegment()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri url) {
                            currentUrl.setValue(url);
                            //Log.d("Uploadint","iamge has  url of : " +url.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("UploadFails",e.toString());
                }
            });
        }else {
            currentUrl.setValue(null);
        }

        return currentUrl;
    }

    public void updateEvent(EventModel eventModel)
    {
        if(eventModel !=null)
        {
            reference.child(eventModel.getEventId()).setValue(eventModel);
        }
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
