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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    
    // UI-Elemente für die Registrierung
    TextInputEditText editTextUsername, editTextEmail, editTextPassword;
    Button buttonRegistration;
    ProgressBar progressBar;
    TextView loginTextView;
    FirebaseAuth mAuth; // Firebase-Authentifizierung

    @Override
    public void onStart() {
        super.onStart();
        // Prüft, ob der Benutzer bereits angemeldet ist
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Falls bereits angemeldet, direkte Weiterleitung zur MainActivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Anpassung der UI an Bildschirmränder
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase-Authentifizierung initialisieren
        mAuth = FirebaseAuth.getInstance();

        // UI-Elemente mit XML-Layout verknüpfen
        editTextUsername = findViewById(R.id.username);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonRegistration = findViewById(R.id.btn_registration);
        loginTextView = findViewById(R.id.loginNow);
        progressBar = findViewById(R.id.progressBar);

        // Falls der Benutzer schon ein Konto hat, Weiterleitung zur LoginActivity
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Registrierung-Button: Überprüfung der Eingabe und Anmeldung in Firebase
        buttonRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE); // Ladeanzeige aktivieren

                // Nutzereingaben abrufen
                String username, email, password;
                username = String.valueOf(editTextUsername.getText());
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                // Eingaben validieren
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(RegisterActivity.this, "Geben Sie Ihren Benutzernamen ein", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "Geben Sie Ihre E-Mail-Adresse ein", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Geben Sie ein sicheres Passwort ein", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Nutzer in Firebase registrieren
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE); // Ladeanzeige deaktivieren
                                if (task.isSuccessful()) {
                                    // Registrierung erfolgreich -> Nutzer in Firebase-Datenbank speichern
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if (user != null) {
                                        String userId = user.getUid(); // Eindeutige User-ID erhalten

                                        // Referenz zur Firebase-Realtime-Datenbank erstellen
                                        DatabaseReference databaseRef = FirebaseDatabase.getInstance(
                                                "https://finanzbuddy-f0ccc-default-rtdb.europe-west1.firebasedatabase.app/"
                                        ).getReference("users");

                                        // HashMap zur Speicherung der Benutzerdaten
                                        HashMap<String, Object> userData = new HashMap<>();
                                        userData.put("username", username);
                                        userData.put("email", email);

                                        // Speichert die Daten in der Firebase-Realtime-Datenbank unter der User-ID
                                        databaseRef.child(userId).setValue(userData)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(RegisterActivity.this, "Ihr Konto wurde erfolgreich erstellt und gespeichert.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(RegisterActivity.this, "Fehler beim Speichern der Nutzerdaten.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }

                                    // Erfolgreiche Registrierung -> Bestätigung für den Benutzer
                                    Toast.makeText(RegisterActivity.this, "Ihr Konto wurde erfolgreich erstellt.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // Falls die Registrierung fehlschlägt -> Fehlermeldung anzeigen
                                    Toast.makeText(RegisterActivity.this, "Registrierung fehlgeschlagen.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
