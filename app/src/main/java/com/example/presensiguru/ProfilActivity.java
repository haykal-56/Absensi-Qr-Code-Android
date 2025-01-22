package com.example.presensiguru;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfilActivity extends AppCompatActivity {

    private TextView tvUsername, tvNoTelepon;
    private Button btnLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        tvUsername = findViewById(R.id.tvUsername);
        tvNoTelepon = findViewById(R.id.tvNoTelepon);
        btnLogout = findViewById(R.id.btnLogout);
        sessionManager = new SessionManager(this);

        // Debugging log to check if user is logged in
        Log.d("ProfilActivity", "Is logged in: " + sessionManager.isLoggedIn());

        if (sessionManager.isLoggedIn()) {
            String username = sessionManager.getUsername();
            tvUsername.setText("Username: " + username);
            String telepon = sessionManager.getKeyTelepon();
            tvNoTelepon.setText("No Telepon:" +telepon);
        } else {
            // Redirect to login activity if not logged in
            Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // Logout button action
        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutSession(); // Call to logout the user
            Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
