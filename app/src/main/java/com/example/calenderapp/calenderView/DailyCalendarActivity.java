package com.example.calenderapp.calenderView;


import static com.example.calenderapp.calenderView.CalendarUtils.*;
import static com.example.calenderapp.calenderView.CalendarUtils.selectedDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.calenderapp.DashboardBar.MenuHelper;
import com.example.calenderapp.calenderView.ui.viewmodel.EventListViewModel;
import com.example.calenderapp.databinding.ActivityWeekViewBinding;
import com.example.calenderapp.events.Event;
import com.example.calenderapp.events.EventAdapter;
import com.example.calenderapp.events.HourAdapter;
import com.example.calenderapp.events.HourEvent;
import com.example.calenderapp.R;
import com.example.calenderapp.events.model.EventModel;
import com.example.calenderapp.events.source.EventRepository;
import com.example.calenderapp.events.ui.view.CreateEventsActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DailyCalendarActivity extends AppCompatActivity{
    private TextView monthDayText;
    private TextView dayOfWeekTV;
    private ListView hourListView;

    //Aktuelle User
    //DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    //Query applesQuery = ref.child("firebase-test").orderByChild("title").equalTo("Apple");


    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser user = mAuth.getCurrentUser();

    private EventRepository myeventRepository;

    private ActivityWeekViewBinding binding;

    private EventListViewModel eventListViewModel;
    private ListView eventListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_calendar);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_daily_calendar);
        eventListViewModel = new ViewModelProvider(this).get(EventListViewModel.class);
        //myeventRepository = new EventRepository();
        initWidgets();
        setDayView();

    }
    private void initWidgets()
    {
        monthDayText = findViewById(R.id.monthDayText);
        dayOfWeekTV = findViewById(R.id.dayOfWeekTV);
        hourListView = findViewById(R.id.hourListView);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setDayView();
    }

    private void setDayView()
    {
        monthDayText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayOfWeekTV.setText(dayOfWeek);
        setHourAdapter();
    }

    private void setHourAdapter()
    {
 /*       List<HourEvent> hourList = new ArrayList<HourEvent>();
        HourAdapter hourAdapter = new HourAdapter(getApplicationContext(), hourList);
        hourListView.setAdapter(hourAdapter);*/
        eventListViewModel.getEventsOfDay(CalendarUtils.selectedDate).observe(this, new Observer<List<EventModel>>() {
            @Override
            public void onChanged(List<EventModel> eventModels) {
                List<EventModel> dailyEvents = new ArrayList<>();
                dailyEvents.addAll(eventModels);

                //ArrayList<HourEvent> hourEvents = hourEventList();
                //HourAdapter hourAdapter = new HourAdapter(getApplicationContext(), dailyEvents);
                //hourListView.setAdapter(hourAdapter);
                EventAdapter eventAdapter = new EventAdapter(getApplicationContext(), dailyEvents);
                hourListView.setAdapter(eventAdapter);
            }
        });

    }

    private ArrayList<HourEvent> hourEventList()
    {
        ArrayList<HourEvent> list = new ArrayList<>();

        for(int hour = 0; hour < 24; hour++)
        {
            LocalTime time = LocalTime.of(hour, 0);
            //ArrayList<EventModel> eventList = myeventRepository.getEventsOfDateAndTimeFromFirebase(selectedDate,time);
            //HourEvent hourEvent = new HourEvent(time, eventList);
            //list.add(hourEvent);
        }

        return list;
    }

    public void previousDayAction(View view)
    {
        selectedDate = selectedDate.minusDays(1);
        setDayView();
    }

    public void nextDayAction(View view)
    {
        selectedDate = selectedDate.plusDays(1);
        setDayView();
    }

    public void newEventAction(View view)
    {
        startActivity(new Intent(this, CreateEventsActivity.class));
    }

    public void monthAction(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void weeklyAction(View view) {
        startActivity(new Intent(this, WeekViewActivity.class));
    }

    //Home Button + Profile Button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(MenuHelper.handleMenuItem(item, user, this)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}