package com.example.calenderapp.tips.ui.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.calenderapp.R;
import com.example.calenderapp.databinding.ActivityCreateTipsBinding;
import com.example.calenderapp.tips.model.HandlerItems.TipNotificationChannel;
import com.example.calenderapp.tips.model.TipModel;
import com.example.calenderapp.tips.ui.viewmodel.TipViewModel;
import com.example.calenderapp.tips.ui.viewmodel.handlers.NotificationChannelHelper;
import com.example.calenderapp.tips.ui.viewmodel.handlers.TipNotificationPublisher;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Random;

public class CreateTipsActivity extends AppCompatActivity {
    private ActivityCreateTipsBinding binding;
    private TipViewModel tipViewModel;
    private Uri currentUri ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_create_tips);
        tipViewModel = new ViewModelProvider(this).get(TipViewModel.class);
        binding.setTipViewModel(tipViewModel);
        binding.setLifecycleOwner(CreateTipsActivity.this);
        tipViewModel.tipState.setValue("inProgress");

        // Notification Channel:
        TipNotificationChannel tipNotificationChannel = new TipNotificationChannel("TIP_ID","Tips_Notifications", NotificationManager.IMPORTANCE_HIGH);
        NotificationChannelHelper notificationChannelHelper = new NotificationChannelHelper(this,tipNotificationChannel);
        notificationChannelHelper.SetNotificationChannel();


        ActivityResultLauncher<Intent> galleryIntentActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == AppCompatActivity.RESULT_OK)
                        {
                            Intent data  = result.getData();
                            currentUri = data.getData();
                            binding.TipImageView.setImageURI(currentUri);
                        }
                    }
                }
        );
        tipViewModel.getDataFromRepository().observe(this, new Observer<List<TipModel>>() {
            @Override
            public void onChanged(List<TipModel> tipModels) {
                if(!tipModels.isEmpty()){
                    Random random = new Random();
                    int idx = random.nextInt(tipModels.size());
                    Log.d("AlarmStarted","Alarm is startin...." );
                    setRepeatingAlarm(tipModels.get(idx));
                }
            }
        });
        tipViewModel.getTipDetails().observe(this, new Observer<TipModel>() {
            @Override
            public void onChanged(TipModel tipModel) {
            }
        });
        // Type Spinner
        tipViewModel.tipType_spinner_position.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                String item = binding.TipTypeSpinner.getItemAtPosition(integer).toString();
                tipViewModel.tipType.setValue(item);
            }
        });
        binding.AddTipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"Add is Clicked",Snackbar.LENGTH_SHORT).show();
                tipViewModel.uploadImageToRepo(currentUri).observe(CreateTipsActivity.this, new Observer<Uri>() {
                    @Override
                    public void onChanged(Uri uri) {
                        if(uri != null)
                        {
                            tipViewModel.tipImageUrl.setValue(uri.toString());
                        }else {
                            tipViewModel.tipImageUrl.setValue("NoImage");
                        }
                        String msg = tipViewModel.OnClickHandler();
                        Snackbar.make(v,msg,Snackbar.LENGTH_SHORT).show();

                    }
                });
            }
        });
        binding.TipImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"Pick a source",Snackbar.LENGTH_SHORT).show();
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                galleryIntentActivityLauncher.launch(galleryIntent);
            }
        });
    }

    private void setRepeatingAlarm(TipModel tipModel) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, TipNotificationPublisher.class);
        alarmIntent.putExtra("TipTitle",tipModel.getTipTitle());
        alarmIntent.putExtra("TipText",tipModel.getTipDescription());
        alarmIntent.putExtra("TipId","10");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        long intervalMillis = 30*60*1000; //every half hour

        if (alarmManager != null) {
            alarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + intervalMillis,
                    intervalMillis,
                    pendingIntent
            );
            Log.d("AlarmStarted","Alarm is startin...." );
        }
        //ToDo: (Ibrahim) Cancel Button --> to View, Home-Button, Profile-Button
    }

}