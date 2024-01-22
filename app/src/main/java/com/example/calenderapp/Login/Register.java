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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        //Firebase variable Authentification zuweisen
        mAuth = FirebaseAuth.getInstance();

        //Die Variablen der Layout zuweisen
        editTextPassword = findViewById(R.id.password);
        editTextEmail = findViewById(R.id.email); //es gibt zwei felder dann die zu heißen
        editTextUsername = findViewById(R.id.username);
        editTextConfirmPassword= findViewById(R.id.confirmPassword);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);

        //Diesen Text ist auch wie ein Button
        textView = findViewById(R.id.loginNow);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Wir mussen ein Intent erstellen, um login-Aktivity zuzugreifen
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        //Button definieren
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Progress-Bar anzeigen
                progressBar.setVisibility(View.VISIBLE);
                //Email-Passwort speichern
                String username, email, password, confirmPassword;
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                confirmPassword = editTextConfirmPassword.getText().toString();
                username = editTextUsername.getText().toString();

                //Untersuchen, ob die Felder leer sind
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this, "Enter a Email-Adress",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this, "Enter a Password",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(Register.this, "Passwords do not match",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //Der Code um User zu erstellen unter ... kopieren (Ohne Log und updateUI)
                //https://firebase.google.com/docs/auth/android/password-auth?hl=de#java_1
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Progressbar wieder schließen
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {


                                    //------------------------------------------------------------------------------------------------
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(username).build(); // Hier setzt du den Benutzernamen

                                    assert user != null;
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Snackbar.make(Register.this.getCurrentFocus(),"Account Created " + username+ " .",Snackbar.LENGTH_SHORT).show();
                                                        //Toast.makeText(Register.this, "Account Created " + username+ " .", Toast.LENGTH_SHORT).show();
                                                        //Nach dem User erstellt wurde speichern wir diese in den Datenbank
                                                        String uid = user.getUid();
                                                        dbref.child(uid).child("name").setValue(username);
                                                        dbref.child(uid).child("points").child("Month").child("High_Score_Month").setValue("0");
                                                        dbref.child(uid).child("points").child("Month").child("Total_Current_Month").setValue("0");
                                                        dbref.child(uid).child("points").child("Month").child("Total_Last_Month").setValue("0");
                                                        dbref.child(uid).child("points").child("Week").child("High_Score_Week").setValue("0");
                                                        dbref.child(uid).child("points").child("Week").child("Total_Current_Week").setValue("0");
                                                        dbref.child(uid).child("points").child("Week").child("Total_Last_Week").setValue("0");
                                                        //ToDo: (Ehsan) Default Events zuweisen
                                                    }
                                                }
                                            });

                                    //---------------------------------------------------------------------------------------------------*/
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(Register.this, "Register failed.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }
}