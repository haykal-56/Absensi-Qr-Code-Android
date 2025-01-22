package com.example.presensiguru;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class QrCodeGuruActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_STORAGE = 112;
    private RecyclerView recyclerView;
    private QrCodeAdapter qrCodeAdapter;
    private DatabaseHelper dbHelper;
    private List<QrCode> qrCodeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_guru);

        recyclerView = findViewById(R.id.recyclerViewQrCode);
        dbHelper = new DatabaseHelper(this);

        // Meminta izin untuk menulis ke penyimpanan
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }

        loadQRCodeData();

        // Set up RecyclerView
        qrCodeAdapter = new QrCodeAdapter(qrCodeList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(qrCodeAdapter);

        // Handle Add QR Code
        Button btnAddQrCode = findViewById(R.id.btnAddQrCode);
        btnAddQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Contoh pembuatan QR code
                createQRCode("Contoh Data QR");
            }
        });
    }

    private void loadQRCodeData() {
        qrCodeList = dbHelper.getAllQRCodes();
        if (qrCodeAdapter != null) {
            qrCodeAdapter.updateQRCodeList(qrCodeList); // Update the adapter with new data
        }
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    private void createQRCode(String data) {
        try {
            BitMatrix bitMatrix = new com.google.zxing.qrcode.QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            // Simpan ke database
            int guruId = 1; // Ganti dengan ID guru yang sesuai
            dbHelper.addQRCode(guruId, data); // Simpan data QR code

            // Tampilkan QR code
            ImageView imageView = findViewById(R.id.imageViewQrCode);
            imageView.setImageBitmap(bitmap);

            // Simpan QR code ke file
            saveQRCodeToFile(bitmap, data);

            // Reload data QR Code untuk ditampilkan
            loadQRCodeData();

        } catch (WriterException e) {
            Log.e("QrCodeGuruActivity", "Error generating QR code", e);
            Toast.makeText(this, "Gagal membuat QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveQRCodeToFile(Bitmap bitmap, String qrCodeData) {
        try {
            File file = new File(getExternalFilesDir(null), qrCodeData + ".png");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            Toast.makeText(this, "QR Code disimpan: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("QrCodeGuruActivity", "Error saving QR code", e);
            Toast.makeText(this, "Gagal menyimpan QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteQRCode(final int qrCodeId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hapus QR Code")
                .setMessage("Apakah Anda yakin ingin menghapus QR Code ini?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteQRCode(qrCodeId);
                        loadQRCodeData(); // Reload data
                        Toast.makeText(QrCodeGuruActivity.this, "QR Code berhasil dihapus!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null);
        builder.create().show();
    }

    public void downloadQRCode(String qrCodeData) {
        try {
            // Generate QR Code Bitmap
            BitMatrix bitMatrix = new com.google.zxing.qrcode.QRCodeWriter().encode(qrCodeData, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            // Simpan QR Code ke file
            saveQRCodeToFile(bitmap, qrCodeData);
        } catch (WriterException e) {
            Log.e("QrCodeGuruActivity", "Error generating QR code for download", e);
            Toast.makeText(this, "Gagal mengunduh QR Code", Toast.LENGTH_SHORT).show();
        }
    }
}
