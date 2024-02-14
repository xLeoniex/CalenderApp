/*
 * *************************************************
 *   Author :           Ehsan Khademi
 *   SubAuthor :        None
 *   Beschreibung :     In dieser Ansicht kann der Benutzer
 *                      seinen Passwort zurücksetzen
 *                      oder zum Login zurückkehren.
 *                      Letzte Änderung :  13/02/2024
 * *************************************************
 */
package com.example.calenderapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calenderapp.Points.ToDoneEventView;
import com.example.calenderapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    Button btnReset;
    TextInputEditText edtEmail;
    String strEmail;
    ProgressBar progressBar;

    //Für TextView Click to Login
    TextView textView;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Variablen mit Layout verbinden
        btnReset = findViewById(R.id.btn_resetPassword);
        edtEmail = findViewById(R.id.email);
        textView = findViewById(R.id.backLogin);
        progressBar = findViewById(R.id.progressBar);

        // Authentifizierungsobjekt erhalten
        auth = FirebaseAuth.getInstance();

        // Klicklistener für Zurück-Button definieren
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zum Anmeldebildschirm navigieren
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        // Klicklistener für Zurücksetzen-Button definieren
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Eingegebene E-Mail-Adresse abrufen und Passwort zurücksetzen
                strEmail = edtEmail.getText().toString().trim();
                if (!TextUtils.isEmpty(strEmail)) {
                    resetPassword();
                } else {
                    // Fehlermeldung anzeigen
                    Toast.makeText(ForgotPassword.this, "Enter an email and retry!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    // Methode zum Zurücksetzen des Passworts
    public void resetPassword() {
        // Fortschrittsbalken anzeigen
        progressBar.setVisibility(View.VISIBLE);
        btnReset.setVisibility(View.INVISIBLE);

        // Passwortzurücksetzungs-E-Mail senden
        auth.sendPasswordResetEmail(strEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Erfolgsmeldung anzeigen und zum Anmeldebildschirm wechseln
                Toast.makeText(ForgotPassword.this, "Reset Password link has been sent to your registered Email", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Fehlermeldung anzeigen und UI wiederherstellen
                Toast.makeText(ForgotPassword.this, "Reset Password Failed", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                btnReset.setVisibility(View.VISIBLE);
            }
        });
        // Fortschrittsbalken und Button wieder sichtbar machen
        progressBar.setVisibility(View.GONE);
        btnReset.setVisibility(View.VISIBLE);
    }
}