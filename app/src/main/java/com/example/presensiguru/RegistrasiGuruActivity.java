package com.example.presensiguru;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegistrasiGuruActivity extends AppCompatActivity {

    EditText etNama, etNoTelp, etUsername, etPassword;
    Button btnRegistrasi;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi_guru);

        etNama = findViewById(R.id.etNama);
        etNoTelp = findViewById(R.id.etNoTelepon);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegistrasi = findViewById(R.id.btnDaftar);
        dbHelper = new DatabaseHelper(this);

        btnRegistrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = etNama.getText().toString();
                String noTelp = etNoTelp.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(noTelp) ||
                        TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegistrasiGuruActivity.this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show();
                } else {
                    // Simpan data ke database
                    Guru newGuru = new Guru(nama, noTelp, username, password);
                    long guruId = dbHelper.registerGuru(newGuru); // Simpan dan dapatkan ID guru

                    // Generate QR Code
                    String qrCodeFilePath = generateQRCode(nama); // Menggunakan nama sebagai data untuk QR Code

                    // Simpan path QR Code dan data ke database menggunakan nama guru
                    dbHelper.insertQRCode(nama, qrCodeFilePath, "Nama Guru: " + nama);

                    Toast.makeText(RegistrasiGuruActivity.this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrasiGuruActivity.this, LoginActivity.class));
                    finish(); // Optional: Close the activity to prevent going back to registration
                }
            }
        });
    }

    private String generateQRCode(String data) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
            return saveQRCodeToFile(bitmap, data); // Kembalikan path file QR Code
        } catch (WriterException e) {
            Toast.makeText(this, "Gagal menghasilkan QR Code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private String saveQRCodeToFile(Bitmap bitmap, String namaGuru) {
        try {
            String filename = namaGuru.replace(" ", "_") + "_" + System.currentTimeMillis() + ".png"; // Ubah spasi menjadi _
            File file = new File(getExternalFilesDir(null), filename);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath(); // Kembalikan path QR Code
        } catch (IOException e) {
            Toast.makeText(this, "Gagal menyimpan QR Code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
