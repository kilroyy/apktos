package com.example.apktos.model;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class modelLpj implements Serializable {

    @PropertyName("NamaKegiatan")
    private String namaKegiatan;

    @PropertyName("Tanggal")
    private String tanggal;

    @PropertyName("Pemasukan")
    private String pemasukan;

    @PropertyName("Pengeluaran")
    private String pengeluaran;

    @PropertyName("SisaUang")
    private String sisaUang;

    @PropertyName("Keterangan")
    private String keterangan;

    @PropertyName("gambar")
    private Object gambar; // Diubah ke Object untuk mendukung String dan List

    private String laporanKey; // Key unik untuk laporan

    // Constructor tanpa parameter (dibutuhkan oleh Firebase)
    public modelLpj() {
    }

    // Constructor dengan parameter
    public modelLpj(String namaKegiatan, String tanggal, String pemasukan, String pengeluaran, String sisaUang, String keterangan, Object gambar) {
        this.namaKegiatan = namaKegiatan;
        this.tanggal = tanggal;
        this.pemasukan = pemasukan;
        this.pengeluaran = pengeluaran;
        this.sisaUang = sisaUang;
        this.keterangan = keterangan;
        this.gambar = gambar;
    }

    // Getter dan Setter untuk laporanKey
    public String getLaporanKey() {
        return laporanKey;
    }

    public void setLaporanKey(String laporanKey) {
        this.laporanKey = laporanKey;
    }

    // Getter dan Setter dengan anotasi @PropertyName
    @PropertyName("NamaKegiatan")
    public String getNamaKegiatan() {
        return namaKegiatan;
    }

    @PropertyName("NamaKegiatan")
    public void setNamaKegiatan(String namaKegiatan) {
        this.namaKegiatan = namaKegiatan;
    }

    @PropertyName("Tanggal")
    public String getTanggal() {
        return tanggal;
    }

    @PropertyName("Tanggal")
    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    @PropertyName("Pemasukan")
    public String getPemasukan() {
        return pemasukan;
    }

    @PropertyName("Pemasukan")
    public void setPemasukan(String pemasukan) {
        this.pemasukan = pemasukan;
    }

    @PropertyName("Pengeluaran")
    public String getPengeluaran() {
        return pengeluaran;
    }

    @PropertyName("Pengeluaran")
    public void setPengeluaran(String pengeluaran) {
        this.pengeluaran = pengeluaran;
    }

    @PropertyName("SisaUang")
    public String getSisaUang() {
        return sisaUang;
    }

    @PropertyName("SisaUang")
    public void setSisaUang(String sisaUang) {
        this.sisaUang = sisaUang;
    }

    @PropertyName("Keterangan")
    public String getKeterangan() {
        return keterangan;
    }

    @PropertyName("Keterangan")
    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    @PropertyName("gambar")
    public Object getGambar() {
        return gambar;
    }

    @PropertyName("gambar")
    public void setGambar(Object gambar) {
        this.gambar = gambar;
    }

    /**
     * Utility method untuk mengonversi gambar menjadi List<String>.
     * Ini berguna jika gambar disimpan sebagai String tunggal atau List di Firebase.
     */
    public List<String> getGambarAsList() {
        if (gambar instanceof String) {
            // Jika gambar adalah String, pecah menggunakan koma
            return Arrays.asList(((String) gambar).split(", "));
        } else if (gambar instanceof List) {
            // Jika gambar sudah berupa List, langsung return
            return (List<String>) gambar;
        }
        return new ArrayList<>(); // Kembalikan List kosong jika null atau tipe lain
    }
}
