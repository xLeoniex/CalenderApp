/*
 * *************************************************
 *   Author :           Ehsan Khademi
 *   SubAuthor :        None
 *   Beschreibung :     Diese Ansicht wird verwendet, um einen Tip zu
*                       öffnen und seine Daten anzuzeigen. Der Tip kann gelöscht,
*                       zurückgesetzt oder abgeschlossen werden.
 *   Letzte Änderung :  13/02/2024
 * *************************************************
 */
package com.example.calenderapp.tips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.calenderapp.DashboardBar.MenuHelper;
import com.example.calenderapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Locale;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class OpenTipView extends AppCompatActivity {
    TextView name, description, type;
    ImageView image;
    Button btn_done, btn_delete, btn_cancel;
    String tipID;
    String tipDate;
    String point;
    String imageURL , nameStr, descriptionStr, typeStr, state;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    DatabaseReference tipsRef;
    DatabaseReference pointsRef;

    KonfettiView viewKonfetti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_tip_view);

        // Erhalten der Tip-ID aus der übergebenen Liste
        tipID = getIntent().getStringExtra("tip-ID");

        // Initialisierung der UI-Elemente
        name = findViewById(R.id.tipName);
        description = findViewById(R.id.tipDescription);
        type = findViewById(R.id.tipType);
        image = findViewById(R.id.tipImageView);
        btn_done = findViewById(R.id.btn_doneTip);
        btn_delete = findViewById(R.id.btn_deleteTip);
        btn_cancel = findViewById(R.id.btn_cancel);
        viewKonfetti = findViewById(R.id.view_konfetti);

        // Standardpunkte setzen
        point = "1";
        pointsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("points");
        tipsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("Tips");

        // Lesen der Tip-Daten aus der Datenbank
        DatabaseReference tipRef = tipsRef.child(tipID);
        tipRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Tip-Daten gefunden
                    imageURL = dataSnapshot.child("imageUrl").getValue(String.class);
                    descriptionStr = dataSnapshot.child("tipDescription").getValue(String.class);
                    nameStr = dataSnapshot.child("tipTitle").getValue(String.class);
                    typeStr = dataSnapshot.child("tipType").getValue(String.class);
                    state = dataSnapshot.child("tipState").getValue(String.class);

                    // Setzen der UI-Elemente mit den Tip-Daten
                    name.setText(nameStr);
                    description.setText(descriptionStr);
                    type.setText(typeStr);

                    // Laden des Bildes, falls vorhanden, oder setzen eines Standardbildes
                    if (imageURL.equals("NoImage") || imageURL.isEmpty()) {
                        image.setImageResource(R.drawable.relax_icon);
                    } else {
                        Glide.with(OpenTipView.this)
                                .load(imageURL)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(image);
                    }

                    // Anpassen des Button-Textes basierend auf dem Tip-Zustand
                    if (state != null && state.equals("inProgress")) {
                        btn_done.setText("COLLECT A POINT");
                    } else {
                        btn_done.setText("RESET TIP");
                    }
                } else {
                    Toast.makeText(OpenTipView.this, "Something wrong, retry!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OpenTipView.this, "Something wrong, retry later...", Toast.LENGTH_SHORT).show();
            }
        });

        // Button-ClickListener für die Zurück- und Löschen-Aktionen
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipRef.removeValue();
                Toast.makeText(OpenTipView.this, "Tip has been deleted.", Toast.LENGTH_SHORT).show();
                goBack();
            }
        });

        // Button-ClickListener für die Abschlussaktion
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Datum des Abschlusses erhalten
                tipRef.child("tipState").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        state = dataSnapshot.getValue(String.class);
                        if (state != null && state.equals("inProgress")) {
                            Date currentDate = new Date();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            tipDate = dateFormat.format(currentDate);

                            // Punkte hinzufügen und Benachrichtigung anzeigen
                            addpoint("Month");
                            addpoint("Week");
                            Toast.makeText(OpenTipView.this, "You have received one point.", Toast.LENGTH_SHORT).show();

                            // Tip-Zustand auf "Done" setzen und Konfetti-Animation starten
                            tipRef.child("tipState").setValue("Done");
                            startKonfetti();
                        } else {
                            tipRef.child("tipState").setValue("inProgress");
                            goBack();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Error",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void goBack() {
        //Intent zurück zu der Tipp-Hauptansicht
        Intent intent = new Intent(getApplicationContext(), AllTipsView.class);
        startActivity(intent);
        finish();
    }


    public void addpoint(String weekMonth){
        // Wählen zwischen wöchentliche oder monatliche Aktivität handelt:
        if (weekMonth.equals("Week") || weekMonth.equals("Month")) {
            // Punkte und Zeitpunkt der Aktivität in der Datenbank speichern
            pointsRef.child(weekMonth).child("object").child(tipID).child("point").setValue(point);
            pointsRef.child(weekMonth).child("object").child(tipID).child("time").setValue(tipDate);

            // Gesamtpunktzahl aktualisieren
            String tmp = "Total_Current_" + weekMonth;
            pointsRef.child(weekMonth).child(tmp).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String sumPoints = dataSnapshot.getValue(String.class);
                    assert sumPoints != null;
                    int oldSum = Integer.parseInt(sumPoints);
                    int newSum = oldSum + 1;
                    pointsRef.child(weekMonth).child(tmp).setValue(Integer.toString(newSum));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Error",
                            Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void startKonfetti() {
        //Konfetti-Animation Einstellungen
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
        //nach der Konfetti-Animation zurueck gehen
        new Handler().postDelayed(this::goBack, 3000L);
    }


    //Buttons für Aktionsleiste aktivieren
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