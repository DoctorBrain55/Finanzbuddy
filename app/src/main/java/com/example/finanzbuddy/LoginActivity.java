package com.example.finanzbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // UI-Elemente für die Login-Seite
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    ProgressBar progressBar;
    TextView registerTextView;
    FirebaseAuth mAuth; // Firebase-Authentifizierung

    @Override
    public void onStart() {
        super.onStart();
        // Prüft, ob der Benutzer bereits angemeldet ist
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Falls angemeldet, direkte Weiterleitung zur MainActivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Anpassung des UI-Layouts an Bildschirmränder
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase-Authentifizierung initialisieren
        mAuth = FirebaseAuth.getInstance();

        // UI-Elemente mit XML-Layout verknüpfen
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        registerTextView = findViewById(R.id.registerNow);

        // Weiterleitung zur Registrierungsseite bei Klick auf "Jetzt registrieren"
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Login-Button: Überprüfung der Eingabe und Anmeldung über Firebase
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE); // Ladeanzeige aktivieren

                // E-Mail und Passwort aus den Eingabefeldern holen
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                // Überprüfung, ob E-Mail und Passwort ausgefüllt sind
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Geben Sie Ihre E-Mail-Adresse ein", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Geben Sie Ihr Passwort ein", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Anmeldung bei Firebase mit E-Mail und Passwort
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE); // Ladeanzeige deaktivieren
                                if (task.isSuccessful()) {
                                    // Erfolgreiche Anmeldung -> Weiterleitung zur MainActivity
                                    Toast.makeText(LoginActivity.this, "Login erfolgreich.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Anmeldung fehlgeschlagen -> Fehlermeldung anzeigen
                                    Toast.makeText(LoginActivity.this, "Authentifizierung fehlgeschlagen.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
