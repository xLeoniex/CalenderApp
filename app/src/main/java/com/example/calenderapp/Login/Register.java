/*
 * *************************************************
 *   Author :           Ehsan Khademi
 *   SubAuthor :        None
 *   Beschreibung :     ermöglicht es Benutzern, sich für ein neues Konto
 *                      zu registrieren, indem sie eine E-Mail-Adresse,
 *                      ein Passwort und einen Benutzernamen eingeben.
*                       Nach der erfolgreichen Registrierung werden ihre
*                       Daten in der Firebase-Authentifizierung und in Realtime
*                       Database gespeichert und sie werden automatisch zum Login
*                       weitergeleitet. (Es werden auch alle default-Tipps zugewiesen)
 *                      Letzte Änderung :  13/02/2024
 * *************************************************
 */
package com.example.calenderapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calenderapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {
    TextInputEditText editTextUsername ,editTextEmail, editTextPassword, editTextConfirmPassword;
    Button buttonReg;
    //Firebase Variable zu registrieren
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    //Für TextView Click to Login
    TextView textView;

    //reeltime Datenbank zugriff
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbref = db.getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase-Authentifizierungsvariable initialisieren
        mAuth = FirebaseAuth.getInstance();

        // Layout-Variablen zuweisen
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextUsername = findViewById(R.id.username);
        editTextConfirmPassword = findViewById(R.id.confirmPassword);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        // Klicklistener für "Jetzt einloggen"-Text definieren
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zu Loginansicht wechseln
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        // Klicklistener für "Registrieren"-Button definieren
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Progress-Bar anzeigen
                progressBar.setVisibility(View.VISIBLE);

                // E-Mail, Passwort, Benutzername und Bestätigung des Passworts aus den Feldern abrufen
                String email, password, username, confirmPassword;
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                confirmPassword = editTextConfirmPassword.getText().toString();
                username = editTextUsername.getText().toString();

                // Überprüfen, ob die Felder nicht leer sind und das Passwort mit der Bestätigung übereinstimmt
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Register.this, "Enter an Email-Address", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Enter a Password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                // Benutzer mit E-Mail und Passwort erstellen
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Progress-Bar schließen
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Anmeldung erfolgreich, Profil-Updates durchführen und Daten speichern
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(username).build();
                                    assert user != null;
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Benutzer erfolgreich erstellt, Daten speichern
                                                        Toast.makeText(Register.this, "Account Created " + username + ".", Toast.LENGTH_SHORT).show();
                                                        String uid = user.getUid();
                                                        dbref.child(uid).child("name").setValue(username);
                                                        dbref.child(uid).child("points").child("Month").child("High_Score_Month").setValue("0");
                                                        dbref.child(uid).child("points").child("Month").child("Total_Current_Month").setValue("0");
                                                        dbref.child(uid).child("points").child("Month").child("Total_Last_Month").setValue("0");
                                                        dbref.child(uid).child("points").child("Week").child("High_Score_Week").setValue("0");
                                                        dbref.child(uid).child("points").child("Week").child("Total_Current_Week").setValue("0");
                                                        dbref.child(uid).child("points").child("Week").child("Total_Last_Week").setValue("0");
                                                        defaultTips(uid);
                                                        // Zu Loginansicht wechseln
                                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            });
                                } else {
                                    // Anmeldung fehlgeschlagen, Fehlermeldung anzeigen
                                    Toast.makeText(Register.this, "Register failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
    public void defaultTips(String uid){
        // User Tip-Referenz
        DatabaseReference userTipRef = dbref.child(uid).child("Tips");
        // Default-Tipps-Referenz
        DatabaseReference defaultTipsRef = FirebaseDatabase.getInstance().getReference("Tips");

        defaultTipsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Iteration über die Default-Tipps
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    // Schlüssel des Default-Tipps abrufen
                    String ID = dataSnapshot.getKey();
                    if (ID != null) {
                        // Daten des Default-Tipps abrufen
                        String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                        String tipDescription = dataSnapshot.child("tipDescription").getValue(String.class);
                        String tipState = dataSnapshot.child("tipState").getValue(String.class);
                        String tipTitle = dataSnapshot.child("tipTitle").getValue(String.class);
                        String tipType = dataSnapshot.child("tipType").getValue(String.class);

                        // Neuen Schlüssel für den Benutzer-Tipp erstellen und Daten speichern
                        String sKey = userTipRef.push().getKey();
                        if(sKey != null){
                            userTipRef.child(sKey).child("imageUrl").setValue(imageUrl);
                            userTipRef.child(sKey).child("tipDescription").setValue(tipDescription);
                            userTipRef.child(sKey).child("tipState").setValue(tipState);
                            userTipRef.child(sKey).child("tipTitle").setValue(tipTitle);
                            userTipRef.child(sKey).child("tipType").setValue(tipType);
                            userTipRef.child(sKey).child("tipId").setValue(sKey);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error defaultTips", "Some thing went wrong from Database");
            }
        });
    }
}