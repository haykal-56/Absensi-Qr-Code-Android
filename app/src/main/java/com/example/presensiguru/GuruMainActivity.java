package com.example.presensiguru;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GuruMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guru_main);

        Button btnProfil = findViewById(R.id.btnProfil);
        Button btnScanQR = findViewById(R.id.btnScanQR);
        Button btnLogout = findViewById(R.id.btnLogoutGuru);

        // Set onClickListener untuk tombol Profil
        btnProfil.setOnClickListener(v -> startActivity(new Intent(GuruMainActivity.this, ProfilActivity.class)));

        // Set onClickListener untuk tombol Scan QR Code
        btnScanQR.setOnClickListener(v -> startActivity(new Intent(GuruMainActivity.this, ScanQRCodeActivity.class)));

        // Set onClickListener untuk tombol Logout
        btnLogout.setOnClickListener(v -> {
            logout();
        });
    }

    private void logout() {
        // Hapus sesi pengguna jika menggunakan SessionManager atau metode penyimpanan lainnya
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.logoutSession(); // Pastikan Anda sudah mengimplementasikan fungsi logoutUser()

        // Arahkan kembali ke LoginActivity
        Intent intent = new Intent(GuruMainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear back stack
        startActivity(intent);
        finish(); // Menutup GuruMainActivity
    }
}
