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

public class PointsAchievment extends AppCompatActivity {
    TextView achievments;
    RadioGroup radioGroup;
    AppCompatRadioButton radioBtnMonth, radioBtnWeek;

    ListView listview;
    ArrayList<String> events = new ArrayList<>();
    ArrayAdapter<String> adapter;

    Button btn_Back;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser user = mAuth.getCurrentUser();
    DatabaseReference pointsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("points");
    DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("Events");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_achievment);

        achievments = findViewById(R.id.achievments);
        listview = findViewById(R.id.listview);
        btn_Back = findViewById(R.id.btn_backPointsView);
        radioBtnWeek = findViewById(R.id.btn_radio_Week);
        radioBtnMonth = findViewById(R.id.btn_radio_Month);
        radioGroup = findViewById(R.id.radioGroup);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,events);
        MonthWeekInfos(events, "Month");

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.btn_radio_Month) {
                    radioBtnMonth.setTextColor(Color.WHITE);
                    radioBtnWeek.setTextColor(Color.GRAY);

                    events.clear();
                    achievments.setText("Monthly achevements");
                    events.clear();
                    listview.setAdapter(adapter);
                    MonthWeekInfos(events, "Month");

                } else if (checkedId == R.id.btn_radio_Week) {
                    radioBtnWeek.setTextColor(Color.WHITE);
                    radioBtnMonth.setTextColor(Color.GRAY);

                    events.clear();
                    achievments.setText("Weekly achevements");
                    events.clear();
                    listview.setAdapter(adapter);
                    MonthWeekInfos(events, "Week");

                } else {
                    throw new IllegalStateException("Unexpected value: " + checkedId);
                }
            }
        });


        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PointsView.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void MonthWeekInfos(ArrayList<String> activities, String MonthWeek){
        DatabaseReference objectsRef = pointsRef.child(MonthWeek).child("object");
        objectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

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
                                    adapter.notifyDataSetChanged();
                                    listview.setAdapter(adapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(PointsAchievment.this, "Something wrong, retry later...", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }else {
                    activities.add("You have not yet completed any activities");
                    adapter.notifyDataSetChanged();
                    listview.setAdapter(adapter);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),
                        Toast.LENGTH_LONG).show();
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
        if(MenuHelper.handleMenuItem(item, user, this)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}