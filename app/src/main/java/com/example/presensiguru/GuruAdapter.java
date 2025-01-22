package com.example.presensiguru;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class GuruAdapter extends ArrayAdapter<Guru> {

    private Context context;
    private ArrayList<Guru> guruList;

    public GuruAdapter(Context context, ArrayList<Guru> guruList) {
        super(context, 0, guruList);
        this.context = context;
        this.guruList = guruList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Jika tampilan belum ada, buat tampilan baru
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_guru, parent, false);
        }

        // Dapatkan objek Guru untuk posisi ini
        Guru guru = guruList.get(position);

        // Temukan dan setel tampilan untuk nama dan telepon guru
        TextView tvNama = convertView.findViewById(R.id.tvNamaGuru);
        TextView tvTelepon = convertView.findViewById(R.id.tvTeleponGuru);

        tvNama.setText(guru.getNama());
        tvTelepon.setText(guru.getTelepon());

        return convertView;
    }
}
