package com.example.calenderapp.calenderView;

import static com.example.calenderapp.calenderView.CalendarUtils.daysInMonthArray;
import static com.example.calenderapp.calenderView.CalendarUtils.monthYearFromDate;
import static com.example.calenderapp.calenderView.CalendarUtils.selectedDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.calenderapp.DashboardBar.MenuHelper;
import com.example.calenderapp.R;
import com.example.calenderapp.calenderView.ui.viewmodel.EventListViewModel;
import com.example.calenderapp.databinding.ActivityMainBinding;
import com.example.calenderapp.events.model.EventModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener
{
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private EventListViewModel eventListViewModel;

    private ActivityMainBinding binding;

    //Aktuelle User
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser user = mAuth.getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        eventListViewModel = new ViewModelProvider(this).get(EventListViewModel.class);
        initWidgets();
        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();
    }

    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    private void setMonthView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> currentDates = new ArrayList<>();
        eventListViewModel.getEventOfMonth(selectedDate).observe(this, new Observer<List<EventModel>>() {
            @Override
            public void onChanged(List<EventModel> eventModels) {
                currentDates.clear();

                // set date into a list :
                for(EventModel eventModel: eventModels)
                {
                    currentDates.add(LocalDate.parse(eventModel.getEventDate()));
                }
                ArrayList<LocalDate> daysInMonth = daysInMonthArray();

                CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth,currentDates, MainActivity.this);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
                calendarRecyclerView.setLayoutManager(layoutManager);
                calendarRecyclerView.setAdapter(calendarAdapter);
            }
        });


    }

    public void previousMonthAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        if(date != null)
        {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }

    public void weeklyAction(View view)
    {
        startActivity(new Intent(this, WeekViewActivity.class));
    }

    public void dailyAction(View view) {
        startActivity(new Intent(this, DailyCalendarActivity.class));
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