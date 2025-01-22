package com.example.presensiguru;

public class QrCode {
    private int id;
    private int guruId;
    private String data;

    public QrCode(int id, int guruId, String data) {
        this.id = id;
        this.guruId = guruId;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public int getGuruId() {
        return guruId;
    }

    public String getData() {
        return data;
    }
}
