/*
 * *************************************************
 *   Author :           Ehsan Khademi
 *   SubAuthor :        None
 *   Beschreibung :     Hier sollte eine Punkteanzeige für Monat und Woche erfolgen,
 *                      bei Betätigung von RadioButtons werden entsprechende Animationen
 *                      ausgeführt.
 *   Letzte Änderung :  13/02/2024
 * *************************************************
 */
package com.example.calenderapp.Points;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calenderapp.Animations.HighScoreAnimation;
import com.example.calenderapp.Animations.KeepPowerAnimation;
import com.example.calenderapp.Animations.LowPointsAnimation;
import com.example.calenderapp.DashboardBar.MenuHelper;
import com.example.calenderapp.R;
import com.example.calenderapp.tips.AllTipsView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class PointsView extends AppCompatActivity {
    String time;
    RadioGroup radioGroup;
    AppCompatRadioButton radioBtnMonth, radioBtnWeek;

    TextView total, points, highScoreTitle, highScore, totalLast, pointsLast;
    ListView recentEvents;

    Button btn_ViewAll;

    ArrayList<String> monthActivities = new ArrayList<>();

    ArrayList<String> weekActivities = new ArrayList<>();
    ArrayList<String> dataToShow = new ArrayList<>();
    ArrayAdapter<String> adapter;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser user = mAuth.getCurrentUser();

    DatabaseReference pointsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("points");
    DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("Events");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_view);

        //Ob man sich in Woche oder Monat Ansicht befindet
        time = getIntent().getStringExtra("time");

        total = findViewById(R.id.totalPoints);
        points = findViewById(R.id.num_points);
        highScoreTitle = findViewById(R.id.highScore);
        highScore = findViewById(R.id.num_highScore);
        totalLast = findViewById(R.id.totalLastPoints);
        pointsLast = findViewById(R.id.num_lastPoints);
        recentEvents = findViewById(R.id.list_recentEvents);
        btn_ViewAll = findViewById(R.id.btn_viewAchievments);
        radioBtnWeek = findViewById(R.id.btn_radio_Week);
        radioBtnMonth = findViewById(R.id.btn_radio_Month);
        radioGroup = findViewById(R.id.radioGroup);

        adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,dataToShow);

        // Überprüfen des übergebenen Zeitraums (Woche oder Monat) und entsprechende Aktionen ausführen
        if(time != null){
            if(time.equals("Week")){
                radioBtnWeek.setChecked(true);
                setTextforWeek();
            }else{
                radioBtnMonth.setChecked(true);
                setTextforMonth();
            }
        }else{
            // Standardmäßige Anzeige für den Monat
            viewTotalPoints("Month");
            MonthWeekInfos(monthActivities,  "Month");
        }

        // RadioGroup-Listener für die Auswahl zwischen Woche und Monat
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.btn_radio_Month) {
                    setTextforMonth();
                    runAnimation("Month");

                } else if (checkedId == R.id.btn_radio_Week) {
                    setTextforWeek();
                    runAnimation("Week");
                } else {
                    throw new IllegalStateException("Unexpected value: " + checkedId);
                }
            }
        });




        btn_ViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PointsAchievment.class);
                startActivity(intent);
                finish();
            }
        });


    }

    // Funktion zum Abrufen der abgeschlossenen Aktivitäten aus der Datenbank für den Monat oder die Woche
    public void MonthWeekInfos(ArrayList<String> activities, String MonthWeek){
        DatabaseReference objectsRef = pointsRef.child(MonthWeek).child("object");
        objectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String ID = (String) dataSnapshot.getKey();

                        if (ID != null) {
                            //Event-Daten aus dem Datenbank ablesen
                            DatabaseReference eventRef = eventsRef.child(ID); // Referenz zum spezifischen Event
                            eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //WICHTIG: Alle Aktualisierungen der von der DatenBank abhängen müssen in diesen Block geschehen
                                    if (dataSnapshot.exists()) {
                                        String name = dataSnapshot.child("eventName").getValue(String.class);
                                        String date = dataSnapshot.child("eventDate").getValue(String.class);
                                        String startingTime = dataSnapshot.child("startingTime").getValue(String.class);
                                        String endingTime = dataSnapshot.child("endingTime").getValue(String.class);
                                        String weight = dataSnapshot.child("eventWeight").getValue(String.class);

                                        //Element für die Liste erstellen
                                        if (name != null && date != null && startingTime != null && endingTime != null && weight != null) {
                                            String out = name + " at  " + date + " (" + startingTime + "-" + endingTime + ") " + " level: " + weight;
                                            activities.add(out);
                                        }
                                    }
                                    dataToShow = prepareDataToShow(activities);
                                    recentEvents.setAdapter(adapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(PointsView.this, "Something wrong, retry later...", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }else {
                    //Falls noch keine Punkte in dem Zeitraum gesammelt wurde
                    dataToShow.add("You have not yet completed any activities");
                    recentEvents.setAdapter(adapter);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }
    //Anzeige der Totalen Punktzahl für den Monat oder Woche
    public void viewTotalPoints(String MonthOrWeek){
        pointsRef.child(MonthOrWeek).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String sumPoints = dataSnapshot.child("Total_Current_"+MonthOrWeek).getValue(String.class);
                String lastPoints = dataSnapshot.child("Total_Last_"+MonthOrWeek).getValue(String.class);
                String highScorePoints = dataSnapshot.child("High_Score_"+MonthOrWeek).getValue(String.class);
                points.setText(sumPoints);
                pointsLast.setText(lastPoints);
                highScore.setText(highScorePoints);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    //Um letzte 3 elemente der Aktivitäten Array zuspeichern
    private ArrayList<String> prepareDataToShow(ArrayList<String> activities) {
        dataToShow.clear();
        int size = activities.size();
        if (size > 0) {
            // Falls mehr als 0 Elemente vorhanden sind
            int endIndex = Math.min(size, 3);
            for (int i = 0; i < endIndex; i++) {
                dataToShow.add(activities.get(i));
            }
        }
        return dataToShow;
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

    //Die Punkte miteinander vergleichen und je nach dem eine Animatin ausführen
    public void runAnimation(String MonthOrWeek){
        int min;
        if(MonthOrWeek.equals("Month")){
            min = 30;
        }else{
            min = 7;
        }
        pointsRef.child(MonthOrWeek).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String sumPoints = dataSnapshot.child("Total_Current_"+MonthOrWeek).getValue(String.class);
                String highScorePoints = dataSnapshot.child("High_Score_"+MonthOrWeek).getValue(String.class);
                int currentPoints = Integer.parseInt(Objects.requireNonNull(sumPoints));
                int currentHighScore = Integer.parseInt(Objects.requireNonNull(highScorePoints));

                if(currentPoints <= min) {
                    //Minimale Punktzahl wurde nicht erreicht
                    Intent intent = new Intent(getApplicationContext(), LowPointsAnimation.class);
                    intent.putExtra("time", MonthOrWeek);
                    startActivity(intent);
                    finish();
                }else {
                    if(currentHighScore < currentPoints) {
                        //High Score wurde erreicht
                        Intent intent = new Intent(getApplicationContext(), HighScoreAnimation.class);
                        intent.putExtra("time", MonthOrWeek);
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent(getApplicationContext(), KeepPowerAnimation.class);
                        intent.putExtra("time", MonthOrWeek);
                        startActivity(intent);
                        finish();
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    // Setzt den Text entsprechend für den Monat
    public void setTextforMonth(){
        radioBtnMonth.setTextColor(Color.WHITE);
        radioBtnWeek.setTextColor(Color.GRAY);

        total.setText("Monthly points so far:");
        highScoreTitle.setText("Full month high score:");
        totalLast.setText("Total points last month:");
        viewTotalPoints("Month");
        monthActivities.clear();
        dataToShow.clear();
        recentEvents.setAdapter(adapter);
        MonthWeekInfos(monthActivities,  "Month");
    }

    // Setzt den Text entsprechend für die Woche
    public void setTextforWeek(){
        radioBtnWeek.setTextColor(Color.WHITE);
        radioBtnMonth.setTextColor(Color.GRAY);

        total.setText("Weekly points so far:");
        highScoreTitle.setText("Full week high score:");
        totalLast.setText("Total points last week:");

        viewTotalPoints("Week");
        weekActivities.clear();
        dataToShow.clear();
        recentEvents.setAdapter(adapter);
        MonthWeekInfos(weekActivities,  "Week");
    }
}