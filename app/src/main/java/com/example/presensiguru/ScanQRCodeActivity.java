package com.example.presensiguru;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.LuminanceSource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScanQRCodeActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int PICK_IMAGE_REQUEST = 102;
    private static final int CAMERA_REQUEST_CODE = 103;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private ImageView ivQRCodeImage;
    private Button btnUploadImage, btnOpenCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        ivQRCodeImage = findViewById(R.id.ivQRCodeImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnOpenCamera = findViewById(R.id.btnOpenCamera); // New button for opening the camera

        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }

        // Button for uploading an image
        btnUploadImage.setOnClickListener(v -> openFileChooser());

        // Button to open the camera
        btnOpenCamera.setOnClickListener(v -> openCamera());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih QR Code"), PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Kamera tidak tersedia", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            ivQRCodeImage.setImageURI(imageUri);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                scanQRCode(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the photo from the camera
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ivQRCodeImage.setImageBitmap(photo); // Display the captured image

            // Scan the captured image for QR Code
            scanQRCode(photo);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void scanQRCode(Bitmap bitmap) {
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            LuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decode(binaryBitmap);

            if (result != null) {
                String username = result.getText();
                Toast.makeText(this, "Username: " + username, Toast.LENGTH_LONG).show();

                String status = getAttendanceStatus();
                dbHelper.addPresensi(username, status);

                Toast.makeText(this, "Presensi " + status + " tercatat untuk username: " + username, Toast.LENGTH_SHORT).show();
            }
        } catch (NotFoundException e) {
            Toast.makeText(this, "QR Code tidak ditemukan", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Gagal memindai QR Code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private String getAttendanceStatus() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        if (currentTime.compareTo("08:00") >= 0 && currentTime.compareTo("09:00") <= 0) {
            return "masuk";
        } else if (currentTime.compareTo("16:00") >= 0 || currentTime.compareTo("07:00") <= 0) {
            return "keluar";
        }
        return "tidak terdaftar";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
