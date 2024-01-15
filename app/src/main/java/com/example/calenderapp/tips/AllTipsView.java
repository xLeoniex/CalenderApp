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
import android.widget.ListView;
import android.widget.Toast;

import com.example.calenderapp.DashboardBar.MenuHelper;
import com.example.calenderapp.Points.AllEventsView;
import com.example.calenderapp.Points.ToDoneEventView;
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

    Button btn_addTip;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> allTips = new ArrayList<>();

    ArrayList<String> tipsIDs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tips_view);

        btn_addTip = findViewById(R.id.btn_addTip);
        listView = findViewById(R.id.list_allTips);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,allTips);

        tipsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allTips.clear();
                tipsIDs.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String ID = (String) dataSnapshot.getKey();
                    tipsIDs.add(ID);
                    if (ID != null) {
                        String name = dataSnapshot.child("tipTitle").getValue(String.class);
                        String type = dataSnapshot.child("tipType").getValue(String.class);
                        String state = dataSnapshot.child("tipState").getValue(String.class);
                        if (name != null && type != null && state != null) {
                            String out = name + " (" +type + ") ";
                           if(state.equals("inProgress")) {
                                //ToDo (Ehsan) oder auch hier alle zeigen und die erledigte mit grünes Hintergrund
                               out = out + "Not completed";
                           }
                            allTips.add(out);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //Int Position gibt uns an wo der Item sich befindet
                String ID = tipsIDs.get(position);
                openTip(ID);
            }
        });

        btn_addTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo: (Ibrahim) mache ein Intent zu deinem Tip-Add
                Intent createTipsIntent = new Intent(AllTipsView.this,CreateTipsActivity.class);
                startActivity(createTipsIntent);
            }
        });
    }

    public void openTip(String ID){
        tipsRef.child(ID).child("tipState").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String state = dataSnapshot.getValue(String.class);
                if (state != null && state.equals("inProgress")) {
                    Intent intent = new Intent(getApplicationContext(), OpenTipView.class);
                    intent.putExtra("tip-ID",ID);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(AllTipsView.this, "The tip has already been issued!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error",
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