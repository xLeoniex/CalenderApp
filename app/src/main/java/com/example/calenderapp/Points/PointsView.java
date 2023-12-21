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

    TextView total, points;
    ListView recentEvents;

    Button btn_ViewAll;

    ArrayList<String> monthActivities = new ArrayList<>();

    ArrayList<String> weekActivities = new ArrayList<>();
    ArrayList<String> dataToShow = new ArrayList<>();
    ArrayAdapter<String> adapter;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser user = mAuth.getCurrentUser();
    //ToDo: Points und total Points später in Firebase als String speicehrn
    DatabaseReference pointsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("points");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_view);

        swt_weekMonth = findViewById(R.id.swt_weekMonth);
        total = findViewById(R.id.totalPoints);
        points = findViewById(R.id.num_points);
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
                    total.setText("Total Points of the week");
                    viewTotalPoints("Week");
                    MonthWeekInfos(weekActivities,  "Week");


                }else{
                    total.setText("Total Points of the month");
                    viewTotalPoints("Month");
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
                activities.clear();
                dataToShow.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String ID = (String) dataSnapshot.getKey();

                    if (ID != null) {
                        String time = dataSnapshot.child("time").getValue(String.class); // Zeitwert
                        String point = dataSnapshot.child("point").getValue(String.class); // Punktwert

                        if (time != null && point != null) {
                            String out = ID + ", Done at " + time + ", Points: " + point;
                            activities.add(out);
                        }
                    }
                }
                dataToShow = prepareDataToShow(activities);
                recentEvents.setAdapter(adapter);
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
        pointsRef.child(MonthOrWeek).child("Total_Current_"+MonthOrWeek).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String sumPoints = dataSnapshot.getValue(String.class);
                points.setText(sumPoints);

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
        int size = activities.size();

        if (size > 0) {
            // Falls mehr als 0 Elemente vorhanden sind
            int endIndex = Math.min(size, 3);
            for (int i = 0; i < endIndex; i++) {
                dataToShow.add(activities.get(i));
            }
        } else {
            // Falls die Liste leer ist
            dataToShow.add("You have not yet completed any activities");
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