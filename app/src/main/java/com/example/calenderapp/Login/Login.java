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
        if(currentUser != null){
            //Wenn angemeldet ist dann Main öffnen
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Firebase variable Authentification zuweisen
        mAuth = FirebaseAuth.getInstance();

        //Die Variablen der Layout zuweisen
        editTextPassword = findViewById(R.id.password);
        editTextEmail = findViewById(R.id.email); //es gibt zwei felder dann die zu heißen
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);

        //Diesen Text ist auch wie ein Button
        resetPassword = findViewById(R.id.resetPassword);
        textView = findViewById(R.id.RegisterNow);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Wir mussen ein Intent erstellen, um login-Aktivity zuzugreifen
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });
        //Login Button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Progress-Bar anzeigen
                progressBar.setVisibility(View.VISIBLE);
                //Email-Passwort speichern
                String email, password;
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                //Untersuchen, ob die Felder leer sind
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Login.this, "Enter a Email-Adress",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this, "Enter a Password",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //Schau in der Docu beim Anmelden Punkt3
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    //einen Intent um MainAktivity zu offnen
                                    Intent intent = new Intent(getApplicationContext(),Dashboard.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }
        });
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Wir mussen ein Intent erstellen, um login-Aktivity zuzugreifen
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });

    }
}