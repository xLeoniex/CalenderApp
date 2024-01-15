package com.example.calenderapp.Points;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import java.text.ParseException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class ToDoneEventView extends AppCompatActivity {
    Spinner spinner;
    ArrayList<String> eventLevels = new ArrayList<>();
    ArrayAdapter<String> adapter;

    String eventID; //wird von Intent übergeben
    TextView name, date, time, description, typ;
    String endingTime,startingTime, eventDate, eventDescription, eventName, eventType, eventWeight;
    Button btn_done, btn_cancel;
    String point;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    DatabaseReference eventsRef;
    DatabaseReference pointsRef;

    DataChecker dataChecker = new DataChecker();

    KonfettiView viewKonfetti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_done_event_view);

        eventsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("Events");
        pointsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("points");
        eventID = getIntent().getStringExtra("event-ID");

        name = findViewById(R.id.eventName);
        date = findViewById(R.id.eventDate);
        time = findViewById(R.id.eventTime);
        description = findViewById(R.id.eventDescription);
        typ = findViewById(R.id.eventType);
        btn_done = findViewById(R.id.btn_doneEvent);
        btn_cancel = findViewById(R.id.btn_cancel);
        viewKonfetti = findViewById(R.id.view_konfetti);

        //Spinner Funktionalität definieren
        spinner = findViewById(R.id.eventLevelSpinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,eventLevels);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                //ToDo: (Ehsan) Item einen Wert zuweisen
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        eventLevels.add("Vital"); //5
        eventLevels.add("Significant"); //4
        eventLevels.add("Relevant"); //3
        eventLevels.add("Minor"); //2
        eventLevels.add("Simple"); //1
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setAdapter(adapter);

        //Event-Daten aus dem Datenbank ablesen
        DatabaseReference eventRef = eventsRef.child(eventID); // Referenz zum spezifischen Event
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //WICHTIG: Alle Aktualisierungen der von der DatenBank abhängen müssen in diesen Block geschehen
                if (dataSnapshot.exists()) {
                    // Daten für das Event gefunden
                    endingTime = dataSnapshot.child("endingTime").getValue(String.class);
                    startingTime = dataSnapshot.child("startingTime").getValue(String.class);
                    eventDate = dataSnapshot.child("eventDate").getValue(String.class);
                    eventDescription = dataSnapshot.child("eventDescription").getValue(String.class);
                    eventName = dataSnapshot.child("eventName").getValue(String.class);
                    eventType = dataSnapshot.child("eventType").getValue(String.class);
                    eventWeight = dataSnapshot.child("eventWeight").getValue(String.class);
                    name.setText(eventName);
                    date.setText(eventDate);
                    String tmp = startingTime + " - " + endingTime;
                    time.setText(tmp);
                    description.setText(eventDescription);
                    typ.setText(eventType);
                    spinner.setSelection(eventLevels.indexOf(eventWeight));
                } else {
                    Toast.makeText(ToDoneEventView.this, "Something wrong, retry!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ToDoneEventView.this, "Something wrong, retry later...", Toast.LENGTH_SHORT).show();
            }
        });


        //Buttons-Definieren
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               goBack();
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedValue = spinner.getSelectedItem().toString();
                point = pointsNumber(selectedValue);
                //in Points DatenBank eintragen --> Datum ablesen Monat oder Woche zuordnen
                if(dataChecker.currentMonth(eventDate)) {
                    addpoints("Month");
                }

                if(dataChecker.currentWeek(eventDate)){
                    addpoints("Week");
                }else if(dataChecker.lastWeek(eventDate)){
                    addpoints("lastWeek");
                }else{
                    Toast.makeText(ToDoneEventView.this, "Events is too old to score points with it!", Toast.LENGTH_LONG).show();
                }

                Toast.makeText(ToDoneEventView.this, "You have received " + point + " points.", Toast.LENGTH_SHORT).show();
                //State auf Done setzen
                eventsRef.child(eventID).child("eventState").setValue("Done");
                startKonfetti();


            }
        });
    }



    //Bei einem neuen Event, tut er für Woche/Monat die gesamtpunktzahl erneuern und den event in Datenbank speichern
    public void addpoints(String weekMonth){
        if (weekMonth.equals("Week") || weekMonth.equals("Month")) {
            pointsRef.child(weekMonth).child("object").child(eventID).child("point").setValue(point);
            pointsRef.child(weekMonth).child("object").child(eventID).child("time").setValue(eventDate);
            String tmp = "Total_Current_" + weekMonth;
            pointsRef.child(weekMonth).child(tmp).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String sumPoints = dataSnapshot.getValue(String.class);
                    assert sumPoints != null;
                    int oldSum = Integer.parseInt(sumPoints);
                    int newSum = oldSum + Integer.parseInt(point);
                    pointsRef.child(weekMonth).child(tmp).setValue(Integer.toString(newSum));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Error",
                            Toast.LENGTH_LONG).show();
                }
            });
        }else if(weekMonth.equals("lastWeek")){
                String tmp = "Total_Last_Week";
                pointsRef.child("Week").child(tmp).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String sumPoints = dataSnapshot.getValue(String.class);
                        assert sumPoints != null;
                        int oldSum = Integer.parseInt(sumPoints);
                        int newSum = oldSum + Integer.parseInt(point);
                        pointsRef.child("Week").child(tmp).setValue(Integer.toString(newSum));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Error",
                                Toast.LENGTH_LONG).show();
                    }
                });

        }

    }
    public String pointsNumber(String value){
        switch (value) {
            case "Vital":
                return "5";
            case "Significant":
                return "4";
            case "Relevant":
                return "3";
            case "Minor":
                return "2";
            case "Simple":
                return "1";
            default:
                return "";
        }
    }

    public void goBack() {
        //ToDo: (Ehsan) später anpassen dass an Kalender zurück geht
        Intent intent = new Intent(getApplicationContext(), AllEventsView.class);
        startActivity(intent);
        finish();
    }

    public void startKonfetti() {
        viewKonfetti.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(12, 5f))
                .setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 2000L);

        //eine Verzögerung hinzuzufügen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Hier wird der Code ausgeführt, der nach der Konfetti-Animation erfolgen soll
                goBack();
            }
        }, 2000L);
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