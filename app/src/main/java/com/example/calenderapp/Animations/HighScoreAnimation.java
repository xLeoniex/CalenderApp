/*
 * *************************************************
 *   Author :           Ehsan Khademi
 *   SubAuthor :        None
 *   Beschreibung :     zeigt eine Animation an, die den Benutzer
 *                      darüber informiert, dass er entweder den
 *                      wöchentlichen oder den monatlichen Höchstwert
 *                      erreicht hat. Je nachdem, ob der Benutzer den
 *                      monatlichen oder den wöchentlichen Modus aktiviert
 *                      hat, wird eine entsprechende Nachricht angezeigt
 *                      Letzte Änderung :  13/02/2024
 * *************************************************
 */
package com.example.calenderapp.Animations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.calenderapp.Points.PointsView;
import com.example.calenderapp.R;
import com.example.calenderapp.tips.AllTipsView;

public class HighScoreAnimation extends AppCompatActivity {
    String time;
    TextView message;
    LottieAnimationView lottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score_animation);
        // Die Zeit (Woche oder Monat) wird aus den Intent-Extras abgerufen.
        time = getIntent().getStringExtra("time");

        // Die TextView message und das LottieAnimationView lottie werden initialisiert.
        message = findViewById(R.id.message);
        lottie = findViewById(R.id.lottie);

        // Je nachdem, ob es sich um den Monats- oder Wochenmodus handelt, wird eine entsprechende Nachricht gesetzt.
        if (time.equals("Month")) {
            message.setText("Congratulations! Monthly high score reached");
        } else {
            message.setText("Congratulations! Weekly high score reached");
        }

        // Animationen für die Nachricht und die Animation werden gestartet.
        message.animate().translationY(-1400).setDuration(4000).setStartDelay(0);
        lottie.animate().translationY(2000).setDuration(2000).setStartDelay(2900);

        // Ein Handler wird verwendet, um nach einer Verzögerung von 5 Sekunden zur PointsView-Activity zu navigieren.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), PointsView.class);
                intent.putExtra("time", time);
                startActivity(intent);
                finish();
            }
        }, 5000);

    }
}