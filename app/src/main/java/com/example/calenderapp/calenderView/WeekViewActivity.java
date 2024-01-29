package com.example.calenderapp.calenderView;

import static com.example.calenderapp.calenderView.CalendarUtils.daysInWeekArray;
import static com.example.calenderapp.calenderView.CalendarUtils.monthYearFromDate;
import static com.example.calenderapp.calenderView.CalendarUtils.selectedDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.calenderapp.DashboardBar.MenuHelper;
import com.example.calenderapp.Points.ToDoneEventView;
import com.example.calenderapp.calenderView.ui.viewmodel.EventListViewModel;
import com.example.calenderapp.databinding.ActivityCreateEventsBinding;
import com.example.calenderapp.databinding.ActivityWeekViewBinding;
import com.example.calenderapp.events.Event;
import com.example.calenderapp.events.EventAdapter;
import com.example.calenderapp.R;
import com.example.calenderapp.events.model.EventModel;
import com.example.calenderapp.events.ui.view.CreateEventsActivity;
import com.example.calenderapp.events.utils.EventSorting;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener
{

    private ActivityWeekViewBinding binding;
    RadioGroup radioGroup;
    AppCompatRadioButton radioBtnMonth, radioBtnWeek;

    private EventListViewModel eventListViewModel;
    private TextView dayOfWeekTV;
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;

    //Aktuelle User
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser user = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_week_view);
        eventListViewModel = new ViewModelProvider(this).get(EventListViewModel.class);
        radioBtnWeek = findViewById(R.id.btn_radio_Week);
        radioBtnMonth = findViewById(R.id.btn_radio_Month);
        dayOfWeekTV = findViewById(R.id.dayOfWeekTV);
        radioGroup = findViewById(R.id.radioGroup);
        initWidgets();
        setWeekView();
        // Normal Click on an item -> Done and info

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.btn_radio_Month) {
                    radioBtnMonth.setTextColor(Color.WHITE);
                    radioBtnWeek.setTextColor(Color.GRAY);
                    monthAction();


                } else if (checkedId == R.id.btn_radio_Week) {
                    radioBtnWeek.setTextColor(Color.WHITE);
                    radioBtnMonth.setTextColor(Color.GRAY);

                } else {
                    throw new IllegalStateException("Unexpected value: " + checkedId);
                }
            }
        });
        binding.eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        // when long click on an item -> Edit or Delete
        binding.eventListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog popUpDialog = new AlertDialog.Builder(WeekViewActivity.this).create();
                popUpDialog.setTitle("Event Handler");
                popUpDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar.make(binding.getRoot().getRootView(),"Edit window",Snackbar.LENGTH_SHORT).show();
                        EventModel currentevent = (EventModel) parent.getItemAtPosition(position);
                        Intent editIntent = new Intent(WeekViewActivity.this, CreateEventsActivity.class);
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

    }

    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        eventListView = findViewById(R.id.eventListView);
    }

    private void setWeekView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);
        ArrayList<LocalDate> daysWithEvents = new ArrayList<>();
        eventListViewModel.getEventOfMonth(selectedDate).observe(this, new Observer<List<EventModel>>() {
            @Override
            public void onChanged(List<EventModel> eventModels) {
                daysWithEvents.clear();
                for(EventModel eventModel: eventModels)
                {
                    daysWithEvents.add(LocalDate.parse(eventModel.getEventDate()));
                }

                CalendarAdapter calendarAdapter = new CalendarAdapter(days,daysWithEvents, WeekViewActivity.this);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
                calendarRecyclerView.setLayoutManager(layoutManager);
                calendarRecyclerView.setAdapter(calendarAdapter);
                String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
                dayOfWeekTV.setText(dayOfWeek);
                setEventAdpater();

            }
        });

    }


    public void previousWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    public void nextWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setEventAdpater();
    }

    private void setEventAdpater()
    {
        eventListViewModel.getEventsOfDay(CalendarUtils.selectedDate).observe(this, new Observer<List<EventModel>>() {
            @Override
            public void onChanged(List<EventModel> eventModels) {
                List<EventModel> dailyEvents = new ArrayList<>();
                dailyEvents.addAll(eventModels);
                EventSorting.sortEventsByStartingTime(eventModels);
                EventAdapter eventAdapter = new EventAdapter(getApplicationContext(), dailyEvents);
                eventListView.setAdapter(eventAdapter);
            }
        });
    }

    public void newEventAction(View view)
    {
        startActivity(new Intent(this, CreateEventsActivity.class));
    }

    public void dailyAction(View view)
    {
        startActivity(new Intent(this, DailyCalendarActivity.class));
    }

    public void monthAction() {
        startActivity(new Intent(this, MainActivity.class));
    }

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