package com.example.presensiguru;

public class Guru {
    private int id;
    private String nama;
    private String telepon;
    private String username;
    private String password;
    private String qrCodePath;

    public Guru(int id, String nama, String telepon, String username, String password, String qrCodePath) {
        this.id = id;
        this.nama = nama;
        this.telepon = telepon;
        this.username = username;
        this.password = password;
        this.qrCodePath = qrCodePath;
    }

    // Constructor tanpa ID (digunakan saat menambah guru baru)
    public Guru(String nama, String telepon, String username, String password) {
        this.nama = nama;
        this.telepon = telepon;
        this.username = username;
        this.password = password;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQrCodePath() { return qrCodePath; }

    public void setQrCodePath(String qrCodePath) { this.qrCodePath = qrCodePath; }
}
