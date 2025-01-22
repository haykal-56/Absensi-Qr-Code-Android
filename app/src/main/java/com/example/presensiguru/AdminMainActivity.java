package com.example.presensiguru;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminMainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnKelolaDataGuru;
    private Button btnLihatDataPresensi;
    private Button btnQrCodeGuru;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnKelolaDataGuru = findViewById(R.id.btnKelolaDataGuru);
        btnLihatDataPresensi = findViewById(R.id.btnLihatDataPresensi);
        btnQrCodeGuru = findViewById(R.id.btnQrCodeGuru);
        btnLogout = findViewById(R.id.btnLogout);

        // Menampilkan pesan selamat datang
        String username = getIntent().getStringExtra("USERNAME");
        tvWelcome.setText("Selamat Datang, " + username);

        // Mengatur aksi pada tombol
        btnKelolaDataGuru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, DataGuruActivity.class);
                startActivity(intent);
            }
        });

        btnLihatDataPresensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, DataPresensiActivity.class);
                startActivity(intent);
            }
        });

        btnQrCodeGuru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, QrCodeGuruActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
