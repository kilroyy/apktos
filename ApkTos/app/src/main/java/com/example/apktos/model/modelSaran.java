package com.example.apktos.model;

import com.google.firebase.database.PropertyName;
import java.io.Serializable;

public class modelSaran implements Serializable {

    @PropertyName("Nama")
    private String nama;

    @PropertyName("NIS")
    private String nis;

    @PropertyName("Isi")
    private String isi;

    private String laporanKey; // Key unik dari Firebase

    // Constructor tanpa parameter (dibutuhkan oleh Firebase)
    public modelSaran() {
    }

    // Constructor dengan parameter
    public modelSaran(String nama, String nis, String isi) {
        this.nama = nama;
        this.nis = nis;
        this.isi = isi;
    }

    // Getter dan Setter untuk laporanKey
    public String getLaporanKey() {
        return laporanKey;
    }

    public void setLaporanKey(String laporanKey) {
        this.laporanKey = laporanKey;
    }

    // Getter dan Setter dengan anotasi @PropertyName
    @PropertyName("Nama")
    public String getNama() {
        return nama;
    }

    @PropertyName("Nama")
    public void setNama(String nama) {
        this.nama = nama;
    }

    @PropertyName("NIS")
    public String getNis() {
        return nis;
    }

    @PropertyName("NIS")
    public void setNis(String nis) {
        this.nis = nis;
    }

    @PropertyName("Isi")
    public String getIsi() {
        return isi;
    }

    @PropertyName("Isi")
    public void setIsi(String isi) {
        this.isi = isi;
    }
}
