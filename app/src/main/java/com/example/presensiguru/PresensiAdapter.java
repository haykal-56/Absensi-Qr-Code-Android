package com.example.presensiguru;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PresensiAdapter extends ArrayAdapter<Presensi> {
    private Context context;
    private ArrayList<Presensi> presensiList;

    public PresensiAdapter(Context context, ArrayList<Presensi> presensiList) {
        super(context, 0, presensiList);
        this.context = context;
        this.presensiList = presensiList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Mengambil data presensi pada posisi tertentu
        Presensi presensi = getItem(position);

        // Memeriksa apakah tampilan sudah ada sebelumnya
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_presensi, parent, false);
        }

        // Menampilkan data presensi
        TextView tvGuruId = convertView.findViewById(R.id.tvGuruId);
        TextView tvTanggal = convertView.findViewById(R.id.tvTanggal);
        TextView tvStatus = convertView.findViewById(R.id.tvStatus);

        tvGuruId.setText("Nama Guru: " + presensi.getGuruId());
        tvTanggal.setText("Tanggal: " + presensi.getTanggal());

        // Mengatur warna teks berdasarkan status
        if (presensi.getStatus().equalsIgnoreCase("masuk")) {
            tvStatus.setText("Status: Masuk");
            tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark)); // Hijau untuk presensi masuk
        } else if (presensi.getStatus().equalsIgnoreCase("keluar")) {
            tvStatus.setText("Status: Keluar");
            tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark)); // Merah untuk presensi keluar
        } else {
            tvStatus.setText("Status: Tidak terdaftar");
            tvStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray)); // Abu-abu untuk status tidak terdaftar
        }

        return convertView;
    }
}
