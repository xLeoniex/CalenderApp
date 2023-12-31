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

public class PointsAchievment extends AppCompatActivity {
    TextView achievments;
    Switch swt_weekMonth;

    ListView listview;
    ArrayList<String> events = new ArrayList<>();
    ArrayAdapter<String> adapter;

    Button btn_Back;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser user = mAuth.getCurrentUser();
    DatabaseReference pointsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("points");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_achievment);

        swt_weekMonth = findViewById(R.id.swt_weekMonth);
        achievments = findViewById(R.id.achievments);
        listview = findViewById(R.id.listview);
        btn_Back = findViewById(R.id.btn_backPointsView);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,events);
        MonthWeekInfos(events, "Month");

        swt_weekMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    events.clear();
                    achievments.setText("Weekly achevements");
                    MonthWeekInfos(events, "Week");
                }else{
                    events.clear();
                    achievments.setText("Monthly achevements");
                    MonthWeekInfos(events, "Month");
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
                activities.clear();
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
                adapter.notifyDataSetChanged();
                listview.setAdapter(adapter);
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