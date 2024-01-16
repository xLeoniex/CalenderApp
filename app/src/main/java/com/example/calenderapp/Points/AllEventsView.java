package com.example.calenderapp.Points;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class AllEventsView extends AppCompatActivity {

    //Liste
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> allEvents = new ArrayList<>();

    ArrayList<String> eventIDs = new ArrayList<>();
    //Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("Events");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events_view);

        listView = findViewById(R.id.list_allEvents);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,allEvents);

        //Liste erstellen

        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allEvents.clear();
                eventIDs.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String ID = (String) dataSnapshot.getKey();
                    if (ID != null) {
                        String name = dataSnapshot.child("eventName").getValue(String.class);
                        String date = dataSnapshot.child("eventDate").getValue(String.class);
                        String startingTime = dataSnapshot.child("startingTime").getValue(String.class);
                        String endingTime = dataSnapshot.child("endingTime").getValue(String.class);
                        String weight = dataSnapshot.child("eventWeight").getValue(String.class);
                        String state = dataSnapshot.child("eventState").getValue(String.class);
                        //ToDo: (Ehsan) Wenn states auf Done ist, soll hintergrund Grün

                        if (name != null && date != null && startingTime != null && endingTime != null && weight != null) {
                            String out = name + " at  " + date + " (" + startingTime + "-"+ endingTime + ") " + " level: " + weight;
                            allEvents.add(out);
                            eventIDs.add(ID);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

        //Um Elemente aufrufen zu lassen:
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //Int Position gibt uns an wo der Item sich befindet
                String ID = eventIDs.get(position);
                Intent intent = new Intent(getApplicationContext(), ToDoneEventView.class);
                intent.putExtra("event-ID",ID);
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
        if(MenuHelper.handleMenuItem(item, user, this)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}