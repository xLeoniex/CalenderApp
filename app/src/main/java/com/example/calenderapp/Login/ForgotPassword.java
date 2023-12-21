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

import com.example.calenderapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

        //Variablen mit Layout verbinden
        btnReset = findViewById(R.id.btn_resetPassword);
        edtEmail = findViewById(R.id.email);
        textView = findViewById(R.id.backLogin);
        progressBar = findViewById(R.id.progressBar);

        //Authetification Variable
        auth = FirebaseAuth.getInstance();

        //Reset Button definieren
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail = edtEmail.getText().toString().trim();
                if(!TextUtils.isEmpty(strEmail)){
                    resetPassword();
                }else{
                    //ToDo: Toast email eingeben
                }
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Wir mussen ein Intent erstellen, um login-Aktivity zuzugreifen
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void resetPassword(){
        //Progressbar anzeigen
        progressBar.setVisibility(View.VISIBLE);
        btnReset.setVisibility(View.INVISIBLE);

        //Vordefinierte Funktion bei der Firebase (mit Sucess und unsucess)
        auth.sendPasswordResetEmail(strEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ForgotPassword.this, "Reset Password link has been sent to your registered Email", Toast.LENGTH_SHORT).show();
                //Bei erfolg zu Login wechseln
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotPassword.this, "Reset Password Failed", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                btnReset.setVisibility(View.VISIBLE);
            }
        });
        progressBar.setVisibility(View.GONE);
        btnReset.setVisibility(View.VISIBLE);

    }
}