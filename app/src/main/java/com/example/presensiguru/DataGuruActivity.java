package com.example.presensiguru;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DataGuruActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Guru> guruList;
    private GuruAdapter adapter;
    private Button btnAddGuru;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_guru);

        listView = findViewById(R.id.lvGuru);
        btnAddGuru = findViewById(R.id.btnAddGuru);
        dbHelper = new DatabaseHelper(this);

        guruList = dbHelper.getAllGurus();
        adapter = new GuruAdapter(this, guruList);
        listView.setAdapter(adapter);

        btnAddGuru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddGuruDialog();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Guru selectedGuru = guruList.get(position);
                showGuruOptionsDialog(selectedGuru);
                return true;
            }
        });
    }

    private void showAddGuruDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_guru, null);
        builder.setView(dialogView);

        // Menghubungkan komponen di dialog
        EditText etNama = dialogView.findViewById(R.id.etNama);
        EditText etTelepon = dialogView.findViewById(R.id.etNoTelepon);
        EditText etUsername = dialogView.findViewById(R.id.etUsername);
        EditText etPassword = dialogView.findViewById(R.id.etPassword);

        builder.setTitle("Tambah Guru")
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nama = etNama.getText().toString();
                        String telepon = etTelepon.getText().toString();
                        String username = etUsername.getText().toString();
                        String password = etPassword.getText().toString();

                        // Validasi input
                        if (nama.isEmpty() || telepon.isEmpty() || username.isEmpty() || password.isEmpty()) {
                            Toast.makeText(DataGuruActivity.this, "Lengkapi semua data!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Buat QR Code untuk username
                            String qrCodePath = generateQRCode(username);

                            if (qrCodePath != null) {
                                // Simpan data guru dan QR Code ke database
                                boolean isAdded = dbHelper.addGuru(nama, telepon, username, password, qrCodePath);
                                if (isAdded) {
                                    // Refresh data setelah menambahkan guru
                                    guruList.clear(); // Kosongkan list saat ini
                                    guruList.addAll(dbHelper.getAllGurus()); // Ambil semua data guru dari database
                                    adapter.notifyDataSetChanged(); // Beritahu adapter bahwa datanya telah berubah

                                    Toast.makeText(DataGuruActivity.this, "Guru berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DataGuruActivity.this, "Gagal menambahkan guru!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(DataGuruActivity.this, "Gagal membuat QR Code!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("Batal", null)
                .create()
                .show();
    }


    private void showGuruOptionsDialog(Guru guru) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Aksi")
                .setItems(new String[]{"Ubah", "Hapus"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            showUpdateGuruDialog(guru);
                        } else if (which == 1) {
                            showDeleteConfirmationDialog(guru);
                        }
                    }
                })
                .show();
    }

    private void showUpdateGuruDialog(Guru guru) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_guru, null);
        final EditText etNama = view.findViewById(R.id.etNama);
        final EditText etTelepon = view.findViewById(R.id.etNoTelepon);
        final EditText etUsername = view.findViewById(R.id.etUsername);
        final EditText etPassword = view.findViewById(R.id.etPassword);

        etNama.setText(guru.getNama());
        etTelepon.setText(guru.getTelepon());
        etUsername.setText(guru.getUsername());
        etPassword.setText(guru.getPassword());

        builder.setView(view);
        builder.setTitle("Ubah Guru")
                .setPositiveButton("Simpan", null)
                .setNegativeButton("Batal", null);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = etNama.getText().toString();
                String telepon = etTelepon.getText().toString();
                String newUsername = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (nama.isEmpty() || telepon.isEmpty() || newUsername.isEmpty() || password.isEmpty()) {
                    Toast.makeText(DataGuruActivity.this, "Lengkapi semua data!", Toast.LENGTH_SHORT).show();
                } else if (!guru.getUsername().equals(newUsername) && dbHelper.isUsernameExists(newUsername)) {
                    Toast.makeText(DataGuruActivity.this, "Username sudah ada, coba yang lain!", Toast.LENGTH_SHORT).show();
                } else {
                    guru.setNama(nama);
                    guru.setTelepon(telepon);
                    guru.setUsername(newUsername);
                    guru.setPassword(password);

                    if (!guru.getUsername().equals(newUsername)) {
                        new GenerateQRCodeTask(newUsername, new QRCodeCallback() {
                            @Override
                            public void onQRCodeGenerated(String qrCodePath) {
                                if (qrCodePath != null) {
                                    guru.setQrCodePath(qrCodePath);
                                    dbHelper.updateGuru(guru);
                                    guruList.clear();
                                    guruList.addAll(dbHelper.getAllGurus());
                                    adapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                    Toast.makeText(DataGuruActivity.this, "Guru berhasil diubah!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DataGuruActivity.this, "Gagal memperbarui QR Code!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).execute();
                    } else {
                        dbHelper.updateGuru(guru);
                        guruList.clear();
                        guruList.addAll(dbHelper.getAllGurus());
                        adapter.notifyDataSetChanged();
                        Toast.makeText(DataGuruActivity.this, "Guru berhasil diubah!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    private void showDeleteConfirmationDialog(Guru guru) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_delete_guru, null);

        builder.setView(view);
        builder.setTitle("Hapus Guru");

        Button btnDelete = view.findViewById(R.id.btnDelete);
        Button btnCancel = view.findViewById(R.id.btnBatal);

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File qrCodeFile = new File(guru.getQrCodePath());
                if (qrCodeFile.exists()) {
                    qrCodeFile.delete(); // Hapus QR Code dari penyimpanan
                }
                dbHelper.deleteGuru(guru.getId());
                guruList.remove(guru);
                adapter.notifyDataSetChanged();
                Toast.makeText(DataGuruActivity.this, "Guru berhasil dihapus!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    // Metode untuk membuat qr code
    private String generateQRCode(String username) {
        try {
            // Buat BitMatrix untuk QR code dengan menggunakan encoder
            BitMatrix bitMatrix = new MultiFormatWriter().encode(username, BarcodeFormat.QR_CODE, 200, 200);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            // Simpan Bitmap sebagai file PNG
            String fileName = "QR_" + username + ".png";
            File qrFile = new File(getExternalFilesDir(null), fileName);
            FileOutputStream fos = new FileOutputStream(qrFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            // Kembalikan path dari file QR code
            return qrFile.getAbsolutePath();
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private class GenerateQRCodeTask extends AsyncTask<Void, Void, String> {
        private String username;
        private QRCodeCallback callback;

        GenerateQRCodeTask(String username, QRCodeCallback callback) {
            this.username = username;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                BitMatrix bitMatrix = new com.google.zxing.qrcode.QRCodeWriter().encode(username, com.google.zxing.BarcodeFormat.QR_CODE, 200, 200);
                BarcodeEncoder encoder = new BarcodeEncoder();
                Bitmap bitmap = encoder.createBitmap(bitMatrix);

                File qrCodeFile = new File(getExternalFilesDir(null), username + "_qrcode.png");
                FileOutputStream fos = new FileOutputStream(qrCodeFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();

                return qrCodeFile.getAbsolutePath();
            } catch (WriterException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            callback.onQRCodeGenerated(result);
        }
    }

    interface QRCodeCallback {
        void onQRCodeGenerated(String qrCodePath);
    }
}
