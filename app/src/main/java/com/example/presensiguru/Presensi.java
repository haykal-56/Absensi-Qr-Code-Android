package com.example.presensiguru;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Presensi {
    private int id;
    private int guruId;
    private String tanggal; // Tanggal dalam format "yyyy-MM-dd HH:mm:ss"
    private String status; // Status bisa "masuk" atau "keluar"

    // Constructor
    public Presensi(int id, int guruId, String tanggal, String status) {
        this.id = id;
        this.guruId = guruId;
        this.tanggal = tanggal;
        this.status = status;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public int getGuruId() {
        return guruId;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getStatus() {
        return status;
    }

    // Metode untuk mendapatkan tanggal dalam format yang lebih user-friendly
    public String getFormattedTanggal() {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault());
            Date date = originalFormat.parse(tanggal);
            return targetFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return tanggal; // Kembali ke tanggal asli jika terjadi kesalahan
        }
    }
}
