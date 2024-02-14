/*
 * *************************************************
 *   Author :           Seika Leonie
 *   SubAuthor :        Ahmed Ibrahim Almohamed
 *   Beschreibung :     Main Class for the calendar Acivity
 *   Letzte Änderung :  14/02/2024
 * *************************************************
 */
package com.example.calenderapp.calenderView;

import static com.example.calenderapp.calenderView.CalendarUtils.daysInMonthArray;
import static com.example.calenderapp.calenderView.CalendarUtils.monthYearFromDate;
import static com.example.calenderapp.calenderView.CalendarUtils.selectedDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
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
    RadioGroup radioGroup;
    AppCompatRadioButton radioBtnMonth, radioBtnWeek;
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
        radioBtnWeek = findViewById(R.id.btn_radio_Week);
        radioBtnMonth = findViewById(R.id.btn_radio_Month);
        radioGroup = findViewById(R.id.radioGroup);
        initWidgets();
        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.btn_radio_Month) {
                    radioBtnMonth.setTextColor(Color.WHITE);
                    radioBtnWeek.setTextColor(Color.GRAY);


                } else if (checkedId == R.id.btn_radio_Week) {
                    radioBtnWeek.setTextColor(Color.WHITE);
                    radioBtnMonth.setTextColor(Color.GRAY);
                    weeklyAction();

                } else {
                    throw new IllegalStateException("Unexpected value: " + checkedId);
                }
            }
        });
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
        eventListViewModel.getEventOfMonth(CalendarUtils.selectedDate).observe(this, new Observer<List<EventModel>>() {
            @Override
            public void onChanged(List<EventModel> eventModels) {
                currentDates.clear();

                // set date into a list :
                for(EventModel eventModel: eventModels)
                {
                    try {
                        currentDates.add(LocalDate.parse(eventModel.getEventDate()));
                    }catch(Exception e){}
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
            Intent gotoDailyIntent = new Intent(MainActivity.this,DailyCalendarActivity.class);
            setMonthView();
            startActivity(gotoDailyIntent);
        }
    }

    public void weeklyAction()
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