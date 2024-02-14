/*
 * *************************************************
 *   Author :           Ehsan Khademi
 *   SubAuthor :        None
 *   Beschreibung :     Es handelt sich um eine Einzelansicht der Veranstaltung. Diese können hier gelöscht werden.
 *                      Die Besonderheit ist die Punktesammlung, da mit dem Datachecker zuerst geprüft wird,
 *                      ob das Datum in den Rahmen passt.
 *   Letzte Änderung :  13/02/2024
 * *************************************************
 */
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.example.calenderapp.DashboardBar.MenuHelper;
import com.example.calenderapp.R;
import com.example.calenderapp.calenderView.DailyCalendarActivity;
import com.example.calenderapp.calenderView.WeekViewActivity;
import com.example.calenderapp.events.model.EventModel;
import com.example.calenderapp.events.ui.view.CreateEventsActivity;
import com.example.calenderapp.tips.OpenTipView;
import com.google.android.material.snackbar.Snackbar;
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
    ImageView image;
    String ImageUrl,endingTime,startingTime, eventDate, eventDescription, eventName, eventType, eventWeight, recurring;
    Button btn_done, btn_cancel, btn_edit, btn_delete;
    String point, state;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    DatabaseReference eventsRef;
    DatabaseReference eventRef;
    DatabaseReference pointsRef;

    DataChecker dataChecker = new DataChecker();

    KonfettiView viewKonfetti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_done_event_view);

        // Initialisierung der Referenzen und UI-Elemente
        eventsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("Events");
        pointsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("points");
        eventID = getIntent().getStringExtra("event-ID");
        image = findViewById(R.id.DisplayEventImageView);
        name = findViewById(R.id.eventName);
        date = findViewById(R.id.eventDate);
        time = findViewById(R.id.eventTime);
        description = findViewById(R.id.eventDescription);
        typ = findViewById(R.id.eventType);
        btn_done = findViewById(R.id.btn_doneEvent);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_edit = findViewById(R.id.btn_edit);
        btn_delete = findViewById(R.id.btn_delete);
        viewKonfetti = findViewById(R.id.view_konfetti);

        // Spinner Funktionalität definieren
        spinner = findViewById(R.id.eventLevelSpinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventLevels);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
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

        // Event-Daten aus der Datenbank lesen
        eventRef = eventsRef.child(eventID);
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Daten für das Event gefunden
                    ImageUrl = dataSnapshot.child("eventImageUrl").getValue(String.class);
                    endingTime = dataSnapshot.child("endingTime").getValue(String.class);
                    startingTime = dataSnapshot.child("startingTime").getValue(String.class);
                    eventDate = dataSnapshot.child("eventDate").getValue(String.class);
                    eventDescription = dataSnapshot.child("eventDescription").getValue(String.class);
                    eventName = dataSnapshot.child("eventName").getValue(String.class);
                    eventType = dataSnapshot.child("eventType").getValue(String.class);
                    eventWeight = dataSnapshot.child("eventWeight").getValue(String.class);
                    recurring = dataSnapshot.child("recurringEventType").getValue(String.class);

                    // Setzen der UI-Elemente mit den Event-Daten
                    name.setText(eventName);
                    date.setText(eventDate);
                    String tmp = startingTime + " - " + endingTime;
                    time.setText(tmp);
                    description.setText(eventDescription);
                    typ.setText(eventType);
                    spinner.setSelection(eventLevels.indexOf(eventWeight));

                    // Laden des Bildes oder setzen eines Standardbildes
                    if (ImageUrl.equals("NoImage")) {
                        image.setImageResource(R.drawable.relax_icon);
                    } else {
                        Glide.with(ToDoneEventView.this).
                                load(ImageUrl).
                                diskCacheStrategy(DiskCacheStrategy.ALL).
                                into(image);
                    }
                } else {
                    Toast.makeText(ToDoneEventView.this, "Something wrong, retry!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ToDoneEventView.this, "Something wrong, retry later...", Toast.LENGTH_SHORT).show();
            }
        });

        // Button-ClickListener definieren
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventRef.child("eventState").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        state = snapshot.getValue(String.class);
                        if (state != null && state.equals("inProgress")) {
                            String selectedValue = spinner.getSelectedItem().toString();
                            point = pointsNumber(selectedValue);

                            // Punkte hinzufügen und Benachrichtigung anzeigen
                            if (!dataChecker.isFutureDate(eventDate)) {
                                boolean check = false;
                                if (dataChecker.currentMonth(eventDate)) {
                                    addpoints("Month");
                                    check = true;
                                }
                                if (dataChecker.currentWeek(eventDate)) {
                                    addpoints("Week");
                                    check = true;
                                } else if (dataChecker.lastWeek(eventDate)) {
                                    addpoints("lastWeek");
                                    check = true;
                                }
                                if (check) {
                                    Toast.makeText(ToDoneEventView.this, "You have received " + point + " points.", Toast.LENGTH_SHORT).show();
                                    // Event-Status auf "Done" setzen und Konfetti-Animation starten
                                    eventsRef.child(eventID).child("eventState").setValue("Done");
                                    startKonfetti();
                                } else {
                                    Toast.makeText(ToDoneEventView.this, "Event is too old to collect points!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ToDoneEventView.this, "You cannot collect points for events in the future", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ToDoneEventView.this, "Points already collected!", Toast.LENGTH_SHORT).show();
                            goBack();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Editieren des Events
                Intent editIntent = new Intent(ToDoneEventView.this, CreateEventsActivity.class);
                editIntent.putExtra("Name", eventName);
                editIntent.putExtra("Date", eventDate);
                editIntent.putExtra("StartingTime", startingTime);
                editIntent.putExtra("EndingTime", endingTime);
                editIntent.putExtra("Description", eventDescription);
                editIntent.putExtra("Id", eventID);
                editIntent.putExtra("State", state);
                editIntent.putExtra("Recurring", recurring);
                editIntent.putExtra("Type", eventType);
                editIntent.putExtra("Weight", eventWeight);
                editIntent.putExtra("Url", ImageUrl);
                startActivity(editIntent);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Löschen des Events
                eventRef.removeValue();
                Toast.makeText(ToDoneEventView.this, "Event has been deleted.", Toast.LENGTH_SHORT).show();
                goBack();
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
        Intent intent = new Intent(getApplicationContext(), DailyCalendarActivity.class);
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
                .streamFor(300, 4000L);

        //eine Verzögerung hinzuzufügen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Hier wird der Code ausgeführt, der nach der Konfetti-Animation erfolgen soll
                goBack();
            }
        }, 4000L);
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