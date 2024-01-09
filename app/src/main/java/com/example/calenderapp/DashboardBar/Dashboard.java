package com.example.calenderapp.DashboardBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.calenderapp.calenderView.MainActivity;
import com.example.calenderapp.Login.Login;
import com.example.calenderapp.Points.PointsView;
import com.example.calenderapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Dashboard extends AppCompatActivity {

    //ToDo: wir brauchen überall in der App Profil und HomeButton (Leonie,Ehsan,Ibrahim)
    //Zwei-Variabeln, Einmal für User und einmal für Firebase Authentification
    FirebaseAuth auth;

    Button btn_calender, btn_points, btn_tips;
    FirebaseUser user;
    TextView textView;
    TextView textWelcome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        textWelcome = findViewById(R.id.totalPoints);
        btn_calender = findViewById(R.id.btn_calenderView);
        btn_points = findViewById(R.id.btn_pointsView);
        btn_tips = findViewById(R.id.btn_tipsView);

        //Firebase-Variablen
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //nun schauen ob user login ist bzw. exisitiert
        if(user == null || user.getDisplayName() == null){
            //Dann muss sich Loggen --> zu login aktivity
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }else{
            String welcome = textWelcome.getText().toString();
            welcome = welcome + " "+ user.getDisplayName() + "!";
            textWelcome.setText(welcome);
        }
        btn_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PointsView.class);
                startActivity(intent);
                finish();
            }
        });
        btn_tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo: zu Tips-Anzeige/erstellen gehen (Ibrahim)
            }
        });

    }

    //Bar-Menus --> strg + O drucken
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