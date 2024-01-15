package com.example.calenderapp.Points;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calenderapp.DashboardBar.MenuHelper;
import com.example.calenderapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PointsView extends AppCompatActivity {
    Switch swt_weekMonth;

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

        swt_weekMonth = findViewById(R.id.swt_weekMonth);
        total = findViewById(R.id.totalPoints);
        points = findViewById(R.id.num_points);
        highScoreTitle = findViewById(R.id.highScore);
        highScore = findViewById(R.id.num_highScore);
        totalLast = findViewById(R.id.totalLastPoints);
        pointsLast = findViewById(R.id.num_lastPoints);
        recentEvents = findViewById(R.id.list_recentEvents);
        btn_ViewAll = findViewById(R.id.btn_viewAchievments);

        adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,dataToShow);


        viewTotalPoints("Month");
        MonthWeekInfos(monthActivities,  "Month");


        swt_weekMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    total.setText("Weekly points so far:");
                    highScoreTitle.setText("Full week high score:");
                    totalLast.setText("Total points last week:");

                    viewTotalPoints("Week");
                    weekActivities.clear();
                    dataToShow.clear();
                    recentEvents.setAdapter(adapter);
                    MonthWeekInfos(weekActivities,  "Week");


                }else{
                    total.setText("Monthly points so far:");
                    highScoreTitle.setText("Full month high score:");
                    totalLast.setText("Total points last month:");
                    viewTotalPoints("Month");
                    monthActivities.clear();
                    dataToShow.clear();
                    recentEvents.setAdapter(adapter);
                    MonthWeekInfos(monthActivities,  "Month");

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

    //Aufruden der erledigte Aktivitäten aus der DatenBank für den Monat oder Woche
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
}