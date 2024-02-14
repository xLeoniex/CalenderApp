package com.example.calenderapp.tips.ui.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calenderapp.DashboardBar.MenuHelper;
import com.example.calenderapp.R;
import com.example.calenderapp.databinding.ActivityCreateTipsBinding;
import com.example.calenderapp.tips.AllTipsView;
import com.example.calenderapp.tips.model.HandlerItems.TipNotificationChannel;
import com.example.calenderapp.tips.model.TipModel;
import com.example.calenderapp.tips.ui.viewmodel.TipViewModel;
import com.example.calenderapp.tips.ui.viewmodel.handlers.NotificationChannelHelper;
import com.example.calenderapp.tips.ui.viewmodel.handlers.TipNotificationPublisher;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Random;

public class CreateTipsActivity extends AppCompatActivity {
    private ActivityCreateTipsBinding binding;
    private TipViewModel tipViewModel;
    private Uri currentUri ;
    TextView choseImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_create_tips);
        tipViewModel = new ViewModelProvider(this).get(TipViewModel.class);
        binding.setTipViewModel(tipViewModel);
        binding.setLifecycleOwner(CreateTipsActivity.this);
        tipViewModel.tipState.setValue("inProgress");
        choseImage = findViewById(R.id.chooseImageText);


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
                            binding.chooseImageText.setVisibility(View.GONE);
                        }
                    }
                }
        );

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
                        Intent intent = new Intent(getApplicationContext(), AllTipsView.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });
        binding.TipImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"Pick a source",Snackbar.LENGTH_SHORT).show();
                //Intent galleryIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.photos");
                Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (galleryIntent != null) {
                    startActivity(galleryIntent);
                } else {
                    Toast.makeText(CreateTipsActivity.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                }

                galleryIntentActivityLauncher.launch(galleryIntent);
            }
        });
        binding.CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllTipsView.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FirebaseUser currentUser = tipViewModel.getCurrentUser();
        if(MenuHelper.handleMenuItem(item, currentUser, this)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}