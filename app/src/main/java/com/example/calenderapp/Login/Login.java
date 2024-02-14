/*
 * *************************************************
 *   Author :           Ehsan Khademi
 *   SubAuthor :        None
 *   Beschreibung :     In dieser Ansicht kann sich der Benutzer
 *                      mit Email und Passwort einloggen, diese Ansicht
 *                      ist der Launcher der Anwendung, wenn er eingeloggt
 *                      ist, wechselt die Ansicht zum Dashboard. Von dieser
 *                      Ansicht aus sollte man zusätzlich zu Registrierung
 *                      und Passwort zurücksetzen gelangen.
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

import com.example.calenderapp.DashboardBar.Dashboard;
import com.example.calenderapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    //Firebase Variable zu registrieren
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    //Für TextView Click to Login
    TextView textView;
    TextView resetPassword;


    //Dies hier ist der Launch, daher sollte zuerst untersucht werden, ob den Anwender angemeldet ist
    //Von der Website!
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //Wenn angemeldet ist dann Dashboard öffnen
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase-Authentifizierungsvariable initialisieren
        mAuth = FirebaseAuth.getInstance();

        // Layout-Variablen zuweisen
        editTextPassword = findViewById(R.id.password);
        editTextEmail = findViewById(R.id.email);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        resetPassword = findViewById(R.id.resetPassword);
        textView = findViewById(R.id.RegisterNow);

        // Klicklistener für "Registrieren"-Text definieren
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zu Registrierungsansicht navigieren
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        // Klicklistener für "Login"-Button definieren
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Progress-Bar anzeigen
                progressBar.setVisibility(View.VISIBLE);

                // E-Mail und Passwort aus den Feldern abrufen
                String email, password;
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                // Überprüfen, ob die Felder nicht leer sind
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Enter an Email-Address", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Enter a Password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                // Anmelden mit E-Mail und Passwort
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Anmeldung erfolgreich, zur Hauptansicht navigieren
                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Anmeldung fehlgeschlagen, Fehlermeldung anzeigen
                                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // Klicklistener für "Passwort vergessen"-Text definieren
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zur Passwort-Zurücksetzen-Ansicht navigieren
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });


    }

}


