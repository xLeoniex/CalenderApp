package com.example.calenderapp.calenderView;


import static com.example.calenderapp.calenderView.CalendarUtils.*;
import static com.example.calenderapp.calenderView.CalendarUtils.selectedDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DailyCalendarActivity extends AppCompatActivity
{

    private TextView monthDayText;
    private TextView dayOfWeekTV;

    private ListView hourListView;

    //Aktuelle User
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
        initWidgets();
        binding = DataBindingUtil.setContentView(this,R.layout.activity_week_view);
        eventListViewModel = new ViewModelProvider(this).get(EventListViewModel.class);
        myeventRepository = new EventRepository();
    }

    private void initWidgets()
    {
        monthDayText = findViewById(R.id.monthDayText);
        dayOfWeekTV = findViewById(R.id.dayOfWeekTV);
        eventListView = findViewById(R.id.hourListView);
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
        HourAdapter hourAdapter = new HourAdapter(getApplicationContext(), hourEventList());
        hourListView.setAdapter(hourAdapter);

        eventListViewModel.getEventsOfDay(selectedDate).observe(this, new Observer<List<EventModel>>() {
            @Override
            public void onChanged(List<EventModel> eventModels) {
                List<EventModel> dailyEvents = new ArrayList<>();
                dailyEvents.addAll(eventModels);
                ArrayList<HourEvent> hourEvents = hourEventList();
                HourAdapter hourAdapter = new HourAdapter(getApplicationContext(), hourEvents);
                //EventAdapter eventAdapter = new EventAdapter(getApplicationContext(), dailyEvents);
                eventListView.setAdapter(hourAdapter);
                //Snackbar.make(binding.getRoot().getRootView(),"No events on this date",Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    private ArrayList<HourEvent> hourEventList()
    {
        ArrayList<HourEvent> list = new ArrayList<>();

        for(int hour = 0; hour < 24; hour++)
        {
            LocalTime time = LocalTime.of(hour, 0);
            List<EventModel> eventList = (List<EventModel>) myeventRepository.getEventsOfDateFromFirebase(selectedDate);
            HourEvent hourEvent = new HourEvent(time, eventList);
            list.add(hourEvent);
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