package com.example.calenderapp.Animations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.calenderapp.Points.PointsView;
import com.example.calenderapp.R;

public class LowPointsAnimation extends AppCompatActivity {
    String time;
    TextView message;
    LottieAnimationView lottie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_points_animation);
        time = getIntent().getStringExtra("time");

        message = findViewById(R.id.message);
        lottie = findViewById(R.id.lottie);

        if(time.equals("Month")){
            message.setText("This month it is time for more power!");
        }else{
            message.setText("This week it is time for more power!");
        }

        message.animate().translationY(-1400).setDuration(4000).setStartDelay(0);
        lottie.animate().translationY(2000).setDuration(2000).setStartDelay(2900);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), PointsView.class);
                intent.putExtra("time",time);
                startActivity(intent);
                finish();
            }
        },5000);
    }
}