package com.example.presensiguru;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegistrasi;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager; // Inisialisasi SessionManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Pastikan Anda memiliki layout yang sesuai

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegistrasi = findViewById(R.id.tvRegistrasi);
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this); // Inisialisasi SessionManager

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // Listener untuk registrasi
        tvRegistrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrasiGuruActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Silakan masukkan username dan password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cek login sebagai admin
        if (dbHelper.isAdmin(username, password)) {
            Intent intent = new Intent(LoginActivity.this, AdminMainActivity.class);
            startActivity(intent);
            finish();
        }
        // Cek login sebagai guru
        else if (dbHelper.isGuru(username, password)) {
            sessionManager.createLoginSession(username); // Simpan sesi pengguna
            Intent intent = new Intent(LoginActivity.this, GuruMainActivity.class);
            startActivity(intent);
            finish();
        }
        // Jika login gagal
        else {
            Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show();
        }
    }
}
