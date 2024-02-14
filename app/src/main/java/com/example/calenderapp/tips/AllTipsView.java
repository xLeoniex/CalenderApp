/*
 * *************************************************
 *   Author :           Ehsan Khademi
 *   SubAuthor :        None
 *   Beschreibung :     Diese Ansicht zeigt alle Tips, die Liste sollte auswählbar sein,
 *                      man sollte die Möglichkeit haben, einen neuen Tip zu erstellen,
 *                      alle Tips zurückzusetzen oder auch zu löschen.
 *   Letzte Änderung :  13/02/2024
 * *************************************************
 */
package com.example.calenderapp.tips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.calenderapp.DashboardBar.MenuHelper;
import com.example.calenderapp.R;
import com.example.calenderapp.tips.ui.view.CreateTipsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllTipsView extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    DatabaseReference tipsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("Tips");
    Button  btn_reset, btn_delete;
    ImageButton btn_addTip;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> allTips = new ArrayList<>();

    ArrayList<String> tipsIDs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tips_view);

        // Initialisierung der UI-Elemente und des Adapters für die ListView
        btn_addTip = findViewById(R.id.btn_addTip);
        btn_delete = findViewById(R.id.btn_deleteAll);
        btn_reset = findViewById(R.id.btn_resetAll);
        listView = findViewById(R.id.list_allTips);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allTips);
        final boolean[] emptyList = {false};

        // Listener für Änderungen in der Datenbankstruktur für Tipps
        tipsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allTips.clear();
                tipsIDs.clear();
                if (snapshot.getChildrenCount() == 0) {
                    String tmp = "No tips available";
                    allTips.add(tmp);
                    emptyList[0] = true;
                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String ID = (String) dataSnapshot.getKey();
                        if (ID != null) {
                            // Tip-Daten aus der Datenbank lesen
                            String name = dataSnapshot.child("tipTitle").getValue(String.class);
                            String type = dataSnapshot.child("tipType").getValue(String.class);
                            String state = dataSnapshot.child("tipState").getValue(String.class);
                            if (name != null && type != null && state != null) {
                                // Zusammenstellung des Listeneintrags basierend auf den Tip-Daten
                                String out = name + " (" + type + ") ";
                                if (state.equals("inProgress")) {
                                    out = out + "Not completed";
                                }
                                allTips.add(out);
                                tipsIDs.add(ID);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Bei Fehler in der Datenbankabfrage
            }
        });

        // Listener für Klicks auf ListView-Einträge
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Behandlung des Klick-Ereignisses, falls die Liste nicht leer ist
                if (!emptyList[0]) {
                    String ID = tipsIDs.get(position);
                    Intent intent = new Intent(getApplicationContext(), OpenTipView.class);
                    intent.putExtra("tip-ID", ID);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // Button-ClickListener für das Hinzufügen neuer Tipps
        btn_addTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createTipsIntent = new Intent(AllTipsView.this, CreateTipsActivity.class);
                startActivity(createTipsIntent);
            }
        });

        // Button-ClickListener für das Zurücksetzen aller Tipps auf den Status "inProgress"
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String ID = (String) dataSnapshot.getKey();
                            if (ID != null) {
                                // Tipps auf den Status "inProgress" zurücksetzen
                                DatabaseReference tipRef = tipsRef.child(ID);
                                tipRef.child("tipState").setValue("inProgress");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Bei Fehler in der Datenbankabfrage
                    }
                });
            }
        });

        // Button-ClickListener für das Löschen aller Tipps
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Alle Tipps aus der Datenbank löschen
                tipsRef.removeValue();
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