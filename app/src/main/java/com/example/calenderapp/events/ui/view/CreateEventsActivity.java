package com.example.calenderapp.events.ui.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.calenderapp.calenderView.CalendarUtils;
import com.example.calenderapp.calenderView.MainActivity;
import com.example.calenderapp.calenderView.WeekViewActivity;
import com.example.calenderapp.DashboardBar.MenuHelper;
import com.example.calenderapp.R;
import com.example.calenderapp.databinding.ActivityCreateEventsBinding;
import com.example.calenderapp.events.model.EventModel;
import com.example.calenderapp.events.ui.viewmodel.EventViewModel;
import com.example.calenderapp.events.utils.ErrorMessages;
import com.google.android.material.snackbar.Snackbar;

public class CreateEventsActivity extends AppCompatActivity {
    private EventViewModel myEventViewModel;
    private ActivityCreateEventsBinding binding;
    private ErrorMessages eventErrorMessages;
    private Uri currentUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_create_events);

        myEventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        binding.setEventViewModel(myEventViewModel);
        binding.setLifecycleOwner(CreateEventsActivity.this);
        eventErrorMessages = new ErrorMessages();

        ActivityResultLauncher<Intent> galleryIntentActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == AppCompatActivity.RESULT_OK)
                        {
                            Intent data  = result.getData();
                            currentUri = data.getData();
                            binding.EventImageView.setImageURI(currentUri);
                        }
                    }
                }
        );


        // if we are updating events

        Intent update = getIntent();
        if(update.hasExtra("Id"))
        {
            binding.AddBtn.setText("Update");
            myEventViewModel.eventName.setValue(update.getStringExtra("Name"));
            myEventViewModel.eventId.setValue(update.getStringExtra("Id"));
            myEventViewModel.eventDescription.setValue(update.getStringExtra("Description"));
            myEventViewModel.eventState.setValue(update.getStringExtra("State"));
            myEventViewModel.endingTime.setValue(update.getStringExtra("EndingTime"));
            myEventViewModel.startingTime.setValue(update.getStringExtra("StartingTime"));
            myEventViewModel.eventDate.setValue(update.getStringExtra("Date"));
            myEventViewModel.eventImageUrl.setValue(update.getStringExtra("Url"));
            Glide.with(this).
                    load(update.getStringExtra("Url")).
                    diskCacheStrategy(DiskCacheStrategy.ALL).
                    into(binding.EventImageView);
        }

        // if we are adding a new event then grab the date
        myEventViewModel.eventDate.setValue(CalendarUtils.selectedDate.toString());
        myEventViewModel.getEventDetails().observe(this, new Observer<EventModel>() {
            @Override
            public void onChanged(EventModel eventModel) {
            }
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
                myEventViewModel.uploadImage(currentUri).observe(CreateEventsActivity.this, new Observer<Uri>() {
                    @Override
                    public void onChanged(Uri uri) {
                        if(uri != null)
                        {
                            myEventViewModel.eventImageUrl.setValue(uri.toString());
                        }else {
                            myEventViewModel.eventImageUrl.setValue("NoImage");
                        }
                        String msg = myEventViewModel.OnClickHandler();
                        Snackbar.make(v,msg,Snackbar.LENGTH_SHORT).show();
                        Intent backtomonthlyIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(backtomonthlyIntent);
                        finish();

                    }
                });

            }
        });

        binding.CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backtomonthlyIntent = new Intent(CreateEventsActivity.this, MainActivity.class);
                startActivity(backtomonthlyIntent);
            }
        });
        binding.EventImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                galleryIntentActivityLauncher.launch(galleryIntent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent update = getIntent();
        if(update.hasExtra("Id"))
        {
            myEventViewModel.eventType.setValue(update.getStringExtra("Type"));
            int typeIdx = getIndex(binding.EventTypeSpinner,update.getStringExtra("Type"));
            binding.EventTypeSpinner.setSelection(typeIdx);
            myEventViewModel.eventWeight.setValue(update.getStringExtra("Weight"));
            int weightIdx = getIndex(binding.EventPointsEventSpinner,update.getStringExtra("Weight"));
            binding.EventPointsEventSpinner.setSelection(weightIdx);
            myEventViewModel.recurringEventType.setValue(update.getStringExtra("Recurring"));
            int recurringIdx = getIndex(binding.EventPointsEventSpinner,update.getStringExtra("Recurring"));
            binding.EventPointsEventSpinner.setSelection(recurringIdx);

        }

    }

    //endregion
    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

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