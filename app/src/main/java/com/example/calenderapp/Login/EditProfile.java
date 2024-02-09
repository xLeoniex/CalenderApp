package com.example.calenderapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calenderapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends AppCompatActivity {
    Button btnEdit, btnPassword, btnDelete;
    TextView edtEmail, back;
    TextInputEditText edtUsername;
    String strEmail, strUsername;
    ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        btnEdit = findViewById(R.id.btn_editProfile);
        btnPassword = findViewById(R.id.btn_passwordChange);
        btnDelete = findViewById(R.id.btn_deletAccount);
        edtEmail = findViewById(R.id.textEmail);
        edtUsername = findViewById(R.id.username);
        progressBar = findViewById(R.id.progressBar);
        back = findViewById(R.id.backHome);

        //User identfizieren
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            strEmail  = user.getEmail();
            strUsername = user.getDisplayName();
            edtEmail.setText(strEmail);
            edtUsername.setText(strUsername);
        }else{
            //Hier können wir dann den Login-Layout öffnen
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        //Username ändern
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(edtUsername.getText().toString())
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    String uid = user.getUid();
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                                    userRef.child("name").setValue(edtUsername.getText().toString());
                                    Toast.makeText(EditProfile.this, "User profile updated.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                progressBar.setVisibility(View.GONE);
            }
        });


        //password ändern
        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                //Vordefinierte Funktion bei der Firebase (mit Sucess und unsucess)
                auth.sendPasswordResetEmail(strEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(EditProfile.this, "Changing Password link has been sent to your registered Email", Toast.LENGTH_SHORT).show();
                        //Bei erfolg zu Login wechseln
                        //Sign-Out befehl der User
                        auth.signOut();
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this, "Reset Password Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                progressBar.setVisibility(View.GONE);
            }
        });

        //Account löschen
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                                    userRef.removeValue();
                                    Toast.makeText(EditProfile.this, "User account deleted.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(EditProfile.this, "Failed to delete account.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                progressBar.setVisibility(View.GONE);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });


    }
}