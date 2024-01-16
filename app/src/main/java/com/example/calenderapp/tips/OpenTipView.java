package com.example.calenderapp.tips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


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
    String imageURL , nameStr, descriptionStr, typeStr;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    DatabaseReference tipsRef;
    DatabaseReference pointsRef;

    KonfettiView viewKonfetti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_tip_view);

        tipID = getIntent().getStringExtra("tip-ID");

        name = findViewById(R.id.tipName);
        description = findViewById(R.id.tipDescription);
        type = findViewById(R.id.tipType);
        image = findViewById(R.id.tipImageView);
        btn_done = findViewById(R.id.btn_doneTip);
        btn_delete = findViewById(R.id.btn_deleteTip);
        btn_cancel = findViewById(R.id.btn_cancel);
        viewKonfetti = findViewById(R.id.view_konfetti);

        point = "1";
        pointsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("points");
        tipsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("Tips");

        //Tip-Daten aus dem Datenbank ablesen
        DatabaseReference tipRef = tipsRef.child(tipID); // Referenz zum spezifischen Event
        tipRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Daten für das Event gefunden
                    imageURL = dataSnapshot.child("imageUrl").getValue(String.class);
                    descriptionStr = dataSnapshot.child("tipDescription").getValue(String.class);
                    nameStr = dataSnapshot.child("tipTitle").getValue(String.class);
                    typeStr = dataSnapshot.child("tipType").getValue(String.class);

                    name.setText(nameStr);
                    description.setText(descriptionStr);
                    type.setText(typeStr);
                    //ToDo: (Ibrahim) Bild noch zeigen lassen aus Datenbank
                } else {
                    Toast.makeText(OpenTipView.this, "Something wrong, retry!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OpenTipView.this, "Something wrong, retry later...", Toast.LENGTH_SHORT).show();
            }
        });
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
                goBack();
            }
        });
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Datum von Zeitpunkt der Erledigung
                Date currentDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                tipDate = dateFormat.format(currentDate);
                addpoint("Month");
                addpoint("Week");
                Toast.makeText(OpenTipView.this, "You have received one point.", Toast.LENGTH_SHORT).show();
                //State auf Done setzen
                tipRef.child("tipState").setValue("Done");
                startKonfetti();
            }
        });
    }

    public void goBack() {
        Intent intent = new Intent(getApplicationContext(), AllTipsView.class);
        startActivity(intent);
        finish();
    }


    public void addpoint(String weekMonth){
        if (weekMonth.equals("Week") || weekMonth.equals("Month")) {
            pointsRef.child(weekMonth).child("object").child(tipID).child("point").setValue(point);
            pointsRef.child(weekMonth).child("object").child(tipID).child("time").setValue(tipDate);
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
        // nach der Konfetti-Animation zurueck gehen
        new Handler().postDelayed(this::goBack, 2000L);
    }
}