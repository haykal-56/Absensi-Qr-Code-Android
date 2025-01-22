package com.example.presensiguru;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class DataPresensiActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Presensi> presensiList;
    private PresensiAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_presensi);

        listView = findViewById(R.id.lvPresensi);
        dbHelper = new DatabaseHelper(this);

        presensiList = dbHelper.getAllPresensi();
        adapter = new PresensiAdapter(this, presensiList);
        listView.setAdapter(adapter);
    }
}
