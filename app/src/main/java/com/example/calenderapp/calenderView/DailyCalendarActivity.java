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
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.calenderapp.DashboardBar.MenuHelper;
import com.example.calenderapp.Points.ToDoneEventView;
import com.example.calenderapp.calenderView.ui.viewmodel.EventListViewModel;
import com.example.calenderapp.databinding.ActivityDailyCalendarBinding;
import com.example.calenderapp.databinding.ActivityWeekViewBinding;
import com.example.calenderapp.events.Event;
import com.example.calenderapp.events.EventAdapter;
import com.example.calenderapp.events.HourAdapter;
import com.example.calenderapp.events.HourEvent;
import com.example.calenderapp.R;
import com.example.calenderapp.events.model.EventModel;
import com.example.calenderapp.events.source.EventRepository;
import com.example.calenderapp.events.ui.view.CreateEventsActivity;
import com.example.calenderapp.events.utils.EventSorting;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyCalendarActivity extends AppCompatActivity{
    private TextView monthDayText;
    private TextView dayOfWeekTV;
    private ListView hourListView;
    private ActivityDailyCalendarBinding binding;
    private EventListViewModel eventListViewModel;


    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser user = mAuth.getCurrentUser();

    private EventRepository myeventRepository;


    private ListView eventListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_calendar);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_daily_calendar);
        eventListViewModel = new ViewModelProvider(this).get(EventListViewModel.class);
        // normal click on the element -> Done and info
        binding.hourListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(binding.getRoot().getRootView(),"Done window",Snackbar.LENGTH_SHORT).show();
                EventModel currentevent = (EventModel) parent.getItemAtPosition(position);
                String ID = currentevent.getEventId();
                Intent intent = new Intent(getApplicationContext(), ToDoneEventView.class);
                intent.putExtra("event-ID",ID);
                startActivity(intent);
                finish();
            }
        });
        binding.hourListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog popUpDialog = new AlertDialog.Builder(DailyCalendarActivity.this).create();
                popUpDialog.setTitle("Event Handler");
                popUpDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar.make(binding.getRoot().getRootView(),"Edit window",Snackbar.LENGTH_SHORT).show();
                        EventModel currentevent = (EventModel) parent.getItemAtPosition(position);
                        Intent editIntent = new Intent(DailyCalendarActivity.this, CreateEventsActivity.class);
                        editIntent.putExtra("Name",currentevent.getEventName());
                        editIntent.putExtra("Date",currentevent.getEventDate());
                        editIntent.putExtra("StartingTime",currentevent.getStartingTime());
                        editIntent.putExtra("EndingTime",currentevent.getEndingTime());
                        editIntent.putExtra("Description",currentevent.getEventDescription());
                        editIntent.putExtra("Id",currentevent.getEventId());
                        editIntent.putExtra("State",currentevent.getEventState());
                        editIntent.putExtra("Recurring",currentevent.getRecurringEventType());
                        editIntent.putExtra("Type",currentevent.getEventType());
                        editIntent.putExtra("Weight",currentevent.getEventWeight());
                        editIntent.putExtra("Url",currentevent.getEventImageUrl());
                        startActivity(editIntent);
                    }
                });
                popUpDialog.setButton(Dialog.BUTTON_NEGATIVE, "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventModel currenEventModel = (EventModel) parent.getItemAtPosition(position);
                        Snackbar.make(binding.getRoot().getRootView(),"Delete window",Snackbar.LENGTH_SHORT).show();
                        eventListViewModel.removeEventFromRepository(currenEventModel);
                    }
                });
                popUpDialog.show();
                return false;
            }
        });

        initWidgets();
        try {
            setDayView();
        }catch (Exception e){
            setDayViewFromNotification();
        }


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

    private void setDayViewFromNotification()
    {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = LocalDate.parse(dateFormat.format(currentDate));
        monthDayText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayOfWeekTV.setText(dayOfWeek);
        setHourAdapter();
    }


    private void setHourAdapter()
    {
        eventListViewModel.getEventsOfDay(CalendarUtils.selectedDate).observe(this, new Observer<List<EventModel>>() {
            @Override
            public void onChanged(List<EventModel> eventModels) {
                List<EventModel> dailyEvents = new ArrayList<>();
                dailyEvents.addAll(eventModels);
                EventSorting.sortEventsByStartingTime(dailyEvents);
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