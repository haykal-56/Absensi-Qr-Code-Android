package com.example.presensiguru;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Nama database dan versinya
    public static final String DATABASE_NAME = "presensi.db";
    public static final int DATABASE_VERSION = 1;

    // Nama tabel guru
    public static final String TABLE_GURU = "guru";
    public static final String COL_GURU_ID = "id";
    public static final String COL_GURU_NAMA = "nama";
    public static final String COL_GURU_TELEPON = "telepon";
    public static final String COL_GURU_USERNAME = "username";
    public static final String COL_GURU_PASSWORD = "password";
    public static final String COL_GURU_QR_CODE = "qr_code";
    public static final String COL_GURU_QR_CODE_PATH = "qr_code_path";

    // Nama tabel presensi
    public static final String TABLE_PRESENSI = "presensi";
    public static final String COL_PRESENSI_ID = "id";
    public static final String COL_PRESENSI_GURU_ID = "guru_id";
    public static final String COL_PRESENSI_GURU_USERNAME = "guru_nama";
    public static final String COL_PRESENSI_TANGGAL = "tanggal";
    public static final String COL_PRESENSI_STATUS = "status"; // 'Masuk' atau 'Keluar'

    // Nama tabel admin
    public static final String TABLE_ADMIN = "admin";
    public static final String COL_ADMIN_ID = "id";
    public static final String COL_ADMIN_USERNAME = "username";
    public static final String COL_ADMIN_PASSWORD = "password";

    // Nama tabel qrcode
    public static final String TABLE_QRCODE = "qrcode";
    public static final String COL_QRCODE_ID = "id";
    public static final String COL_QRCODE_GURU_ID = "guru_id"; // Foreign key
    public static final String COL_QRCODE_DATA = "qrcode_data"; // Store the QR code data
    public static final String COL_QRCODE_PATH = "qr_code_path"; // Kolom baru untuk menyimpan path QR Code

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Membuat tabel Guru
        String createGuruTable = "CREATE TABLE " + TABLE_GURU + " (" +
                COL_GURU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_GURU_NAMA + " TEXT NOT NULL, " +
                COL_GURU_TELEPON + " TEXT NOT NULL, " +
                COL_GURU_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COL_GURU_PASSWORD + " TEXT NOT NULL, " +
                COL_GURU_QR_CODE + " TEXT, " + // Menggunakan qr_code untuk menyimpan data QR Code
                COL_GURU_QR_CODE_PATH + " TEXT)"; // Menambahkan kolom baru qr_code_path

        db.execSQL(createGuruTable);
        insertGuru(db,"Meiyanti", "085439583095", "guru1", "guru1");
        insertGuru(db,"Amir Rizky Al Haj", "089583751203", "guru2", "guru2");
        insertGuru(db,"Muhammad Dadang Suganda", "081142047894", "guru3", "guru3");
        insertGuru(db,"Darna Susila", "085278394921", "guru4", "guru4");
        insertGuru(db,"Sukrisyam", "085289234819", "guru5", "guru5");
        insertGuru(db,"Nurhaliza", "08991238492", "guru6", "guru6");

        // Membuat tabel Presensi
        String createPresensiTable = "CREATE TABLE " + TABLE_PRESENSI + " (" +
                COL_PRESENSI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRESENSI_GURU_ID + " INTEGER, " +
                COL_PRESENSI_TANGGAL + " TEXT, " +
                COL_PRESENSI_STATUS + " TEXT, " +
                "FOREIGN KEY(" + COL_PRESENSI_GURU_ID + ") REFERENCES " + TABLE_GURU + "(" + COL_GURU_ID + ") " +
                ");";
        db.execSQL(createPresensiTable);


        // Membuat tabel Admin
        String createAdminTable = "CREATE TABLE " + TABLE_ADMIN + " (" +
                COL_ADMIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ADMIN_USERNAME + " TEXT, " +
                COL_ADMIN_PASSWORD + " TEXT)";
        db.execSQL(createAdminTable);
        insertAdmin(db, "admin", "admin");

        // Membuat tabel QRCode
        String createQRCodeTable = "CREATE TABLE " + TABLE_QRCODE + " (" +
                COL_QRCODE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_QRCODE_GURU_ID + " INTEGER NOT NULL, " +
                COL_QRCODE_DATA + " TEXT NOT NULL, " +
                COL_QRCODE_PATH + " TEXT, " + // Menambahkan kolom baru untuk qr_code_path
                "FOREIGN KEY (" + COL_QRCODE_GURU_ID + ") REFERENCES " + TABLE_GURU + "(" + COL_GURU_ID + ")" +
                ")";
        db.execSQL(createQRCodeTable);
        // Add sample QR code data
        insertQRCode(db,1, "Meiyanti");
        insertQRCode(db,2, "Amir Rizky Al Haj");
        insertQRCode(db,3, "Muhammmad Dadang Suganda");
        insertQRCode(db,4, "Darna Susila");
        insertQRCode(db,5, "Sukrisyam");
        insertQRCode(db,6, "Nurhaliza");

    }

    public Cursor getAllTeachersWithQRCode() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT g.id AS guru_id, g.nama, g.telepon, q.qrcode_data " +
                "FROM " + TABLE_GURU + " g " +
                "LEFT JOIN " + TABLE_QRCODE + " q ON g.id = q.guru_id";
        return db.rawQuery(query, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Menghapus tabel jika ada versi baru
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GURU);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRESENSI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN);
        onCreate(db);
    }

    private void insertGuru(SQLiteDatabase db, String nama, String telepon, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_GURU_NAMA, nama);
        values.put(COL_GURU_TELEPON, telepon);
        values.put(COL_GURU_USERNAME, username);
        values.put(COL_GURU_PASSWORD, password);
        db.insert(TABLE_GURU, null, values);
    }

    private void insertAdmin(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_ADMIN_USERNAME, username);
        values.put(COL_ADMIN_PASSWORD, password);
        db.insert(TABLE_ADMIN, null, values);
    }

    public void insertQRCode(SQLiteDatabase db, int guruId, String qrCodeData) {
        ContentValues values = new ContentValues();
        values.put(COL_QRCODE_GURU_ID, guruId);
        values.put(COL_QRCODE_DATA, qrCodeData);
        db.insert(TABLE_QRCODE, null, values);
    }

    // FUNGSI lOGIN SEBAGAI ADMIN
    public boolean isAdmin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ADMIN,
                new String[]{COL_ADMIN_ID},
                COL_ADMIN_USERNAME + "=? AND " + COL_ADMIN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // FUNGSI LOGIN SEBAGAI GURU
    public boolean isGuru(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GURU,
                new String[]{COL_GURU_ID},
                COL_GURU_USERNAME + "=? AND " + COL_GURU_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // Fungsi untuk insert guru ke database
    public long insertGuru(String nama, String username, String password, String telepon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_GURU_NAMA, nama);
        values.put(COL_GURU_USERNAME, username);
        values.put(COL_GURU_PASSWORD, password);
        values.put(COL_GURU_TELEPON, telepon);

        // Mengembalikan ID dari guru yang baru di-insert
        long id = db.insert(TABLE_GURU, null, values);
        db.close();
        return id;
    }

    // Fungsi untuk insert QR code ke database
    public boolean insertQRCode(String guru_id, String qrCodePath, String qrcodeData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QRCODE_GURU_ID, guru_id);
        values.put(COL_QRCODE_PATH, qrCodePath);
        values.put(COL_QRCODE_DATA, qrcodeData); // Tambahkan data QR code

        // Mengembalikan ID dari QR code yang baru di-insert
        long id = db.insert("qrcode", null, values);
        db.close();
        return id != -1;
    }

    // Fungsi untuk mendapatkan QR code path dari guru_id
    public String getQRCodePath(String guruId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String qrCodePath = null;
        Cursor cursor = db.query(TABLE_QRCODE, new String[]{COL_GURU_QR_CODE_PATH},
                COL_QRCODE_GURU_ID + "=?", new String[]{guruId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            qrCodePath = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_QR_CODE_PATH));
            cursor.close();
        }
        db.close();
        return qrCodePath;
    }

    // Menambahkan data presensi
    public boolean insertPresensi(int guru_id, String tanggal, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PRESENSI_GURU_ID, guru_id);
        contentValues.put(COL_PRESENSI_TANGGAL, tanggal);
        contentValues.put(COL_PRESENSI_STATUS, status);
        long result = db.insert(TABLE_PRESENSI, null, contentValues);
        return result != -1;
    }


    // Mengambil data presensi berdasarkan guru
    public Cursor getPresensiByGuru(int guru_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRESENSI + " WHERE " + COL_PRESENSI_GURU_ID + " = ?", new String[]{String.valueOf(guru_id)});
    }

    // Mengambil semua data guru
    public ArrayList<Guru> getAllGurus() {
        ArrayList<Guru> guruList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM guru", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String nama = cursor.getString(cursor.getColumnIndexOrThrow("nama"));
                String telepon = cursor.getString(cursor.getColumnIndexOrThrow("telepon"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                String qrCodePath = cursor.getString(cursor.getColumnIndexOrThrow("qr_code_path"));

                Guru guru = new Guru(id, nama, telepon, username, password, qrCodePath);
                guruList.add(guru);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return guruList;
    }

    public boolean addGuru(String nama, String telepon, String username, String password, String qrCodePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nama", nama);
        values.put("telepon", telepon);
        values.put("username", username);
        values.put("password", password);
        values.put("qr_code_path", qrCodePath); // Simpan path QR Code

        // Menyimpan guru di tabel guru
        long guruId = db.insert(TABLE_GURU, null, values);

        // Memastikan guruId valid sebelum menyimpan ke tabel QR Code
        if (guruId != -1) { // Jika penyisipan berhasil
            ContentValues qrCodeValues = new ContentValues();
            qrCodeValues.put(COL_QRCODE_GURU_ID, guruId); // Id dari guru yang baru ditambahkan
            qrCodeValues.put(COL_QRCODE_DATA, username); // Data QR Code (username digunakan sebagai QR Code)
            qrCodeValues.put(COL_QRCODE_PATH, qrCodePath); // Path file QR Code

            // Menyisipkan ke tabel QR Code
            long qrCodeId = db.insert(TABLE_QRCODE, null, qrCodeValues);
            db.close();
            return qrCodeId != -1; // Mengembalikan true jika penyisipan QR Code berhasil
        }

        db.close();
        return false; // Mengembalikan false jika penyisipan guru gagal
    }


    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM guru WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // FUNGSI UNTUK REGISTRASI
    public long registerGuru(Guru guru) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_GURU_NAMA, guru.getNama());
        contentValues.put(COL_GURU_TELEPON, guru.getTelepon());
        contentValues.put(COL_GURU_USERNAME, guru.getUsername());
        contentValues.put(COL_GURU_PASSWORD, guru.getPassword());

        long id = db.insert(TABLE_GURU, null, contentValues);
        db.close();
        return  id;
    }

//    public long insertGuru(String nama, String username, String password, String telepon) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COL_GURU_NAMA, nama);
//        values.put(COL_GURU_USERNAME, username);
//        values.put(COL_GURU_PASSWORD, password);
//        values.put(COL_GURU_TELEPON, telepon);
//
//        // Mengembalikan ID dari guru yang baru di-insert
//        long id = db.insert(TABLE_GURU, null, values);
//        db.close();
//        return id;
//    }

    // Hapus guru berdasarkan ID
    public boolean deleteGuru(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_GURU, COL_GURU_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    // Update data guru
    public boolean updateGuru(Guru guru) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_GURU_NAMA, guru.getNama());
        contentValues.put(COL_GURU_TELEPON, guru.getTelepon());
        contentValues.put(COL_GURU_USERNAME, guru.getUsername());
        contentValues.put(COL_GURU_PASSWORD, guru.getPassword());
        return db.update(TABLE_GURU, contentValues, COL_GURU_ID + " = ?", new String[]{String.valueOf(guru.getId())}) > 0;
    }

    // CRUD QR CODE
    public void addQRCode(int guruId, String qrCodeData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QRCODE_GURU_ID, guruId);
        values.put(COL_QRCODE_DATA, qrCodeData);
        db.insert(TABLE_QRCODE, null, values);
        db.close();
    }

    public List<QrCode> getAllQRCodes() {
        List<QrCode> qrCodeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_QRCODE, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_QRCODE_ID));
                int guruId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_QRCODE_GURU_ID));
                String data = cursor.getString(cursor.getColumnIndexOrThrow(COL_QRCODE_DATA));
                qrCodeList.add(new QrCode(id, guruId, data));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return qrCodeList;
    }

    public void deleteQRCode(int qrCodeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QRCODE, COL_QRCODE_ID + "=?", new String[]{String.valueOf(qrCodeId)});
        db.close();
    }

    // Menambahkan metode untuk mencatat presensi
    public void addPresensi(String guruId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Mencari guru_id berdasarkan username
        String username = getGuruIdByUsername(guruId);
        values.put(COL_PRESENSI_GURU_ID, guruId);
        values.put(COL_PRESENSI_TANGGAL, getCurrentDate());
        values.put(COL_PRESENSI_STATUS, status);

        // Menyimpan data presensi
        db.insert(TABLE_PRESENSI, null, values);
        db.close();
    }

    private String getGuruIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GURU, new String[]{COL_GURU_ID}, COL_GURU_USERNAME + "=?", new String[]{username}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String guruId = cursor.getString(0);
            cursor.close();
            return guruId;
        }
        return null;
    }

    public String getGuruUsernameById(int guruId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String username = null;
        Cursor cursor = db.query(TABLE_GURU, new String[]{COL_GURU_USERNAME},
                COL_GURU_ID + "=?", new String[]{String.valueOf(guruId)}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                username = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_USERNAME));
            }
            cursor.close();
        }
        db.close();
        return username;
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Menambahkan metode untuk mendapatkan semua data presensi
    public ArrayList<Presensi> getAllPresensi() {
        ArrayList<Presensi> presensiList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRESENSI, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRESENSI_ID));
                int guruId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRESENSI_GURU_ID));
                String tanggal = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRESENSI_TANGGAL));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRESENSI_STATUS));

                Presensi presensi = new Presensi(id, guruId, tanggal, status);
                presensiList.add(presensi);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return presensiList;
    }

}
