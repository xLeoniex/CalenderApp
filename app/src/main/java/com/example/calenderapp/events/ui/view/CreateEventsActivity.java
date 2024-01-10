package com.example.calenderapp.events.ui.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.calenderapp.calenderView.CalendarUtils;
import com.example.calenderapp.calenderView.WeekViewActivity;
import com.example.calenderapp.DashboardBar.MenuHelper;
import com.example.calenderapp.R;
import com.example.calenderapp.databinding.ActivityCreateEventsBinding;
import com.example.calenderapp.events.model.EventModel;
import com.example.calenderapp.events.ui.viewmodel.EventViewModel;
import com.google.android.material.snackbar.Snackbar;

public class CreateEventsActivity extends AppCompatActivity {
    private EventViewModel myEventViewModel;
    private ActivityCreateEventsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_create_events);

        myEventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        binding.setEventViewModel(myEventViewModel);
        binding.setLifecycleOwner(CreateEventsActivity.this);


        // set event date from Calender
        myEventViewModel.eventDate.setValue(CalendarUtils.selectedDate.toString());
        myEventViewModel.getEventDetails().observe(this, (Observer<EventModel>) eventModel -> {
        });

        /// This is for the Spinners to grap their value and set them into the Viewmodel

        myEventViewModel.event_type_spinner_selectedItemPosition.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                String item = binding.EventTypeSpinner.getItemAtPosition(integer).toString();
                myEventViewModel.eventType.setValue(item);
            }
        });
        myEventViewModel.event_recurringType_spinner_selectedItemPosition.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                String recurringType = binding.RecurringEventSpinner.getItemAtPosition(integer).toString();
                myEventViewModel.recurringEventType.setValue(recurringType);
            }
        });

        myEventViewModel.event_weight_spinner_selectedItemPosition.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                String eventWeight = binding.EventPointsEventSpinner.getItemAtPosition(integer).toString();
                myEventViewModel.eventWeight.setValue(eventWeight);
            }
        });

        // Button Click handler
        binding.AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = myEventViewModel.OnClickHandler();
                Snackbar.make(v,msg,Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.ShowEventsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventsActivity.this, WeekViewActivity.class);
                startActivity(intent);
                Log.i("SWITCHSCREENS","go to Event lists");
            }
        });


    }
    //endregion

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(MenuHelper.handleMenuItem(item, myEventViewModel.getCurrentUser(), this)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}