package com.example.apktos.model;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class modelEvent implements Serializable {

    @PropertyName("NamaLaporan")
    private String namaLaporan;

    @PropertyName("TanggalKegiatan")
    private String tanggalKegiatan;

    @PropertyName("Anggaran")
    private String anggaran;

    @PropertyName("Deskripsi")
    private String deskripsi;

    @PropertyName("PenanggungJawab")
    private String penanggungJawab;

    private String laporanKey;  // Penambahan atribut laporanKey

    // Constructor tanpa parameter (dibutuhkan oleh Firebase)
    public modelEvent() {
    }

    // Constructor dengan parameter
    public modelEvent(String namaLaporan, String tanggalKegiatan, String anggaran, String deskripsi, String penanggungJawab) {
        this.namaLaporan = namaLaporan;
        this.tanggalKegiatan = tanggalKegiatan;
        this.anggaran = anggaran;
        this.deskripsi = deskripsi;
        this.penanggungJawab = penanggungJawab;
    }

    // Getter dan Setter untuk laporanKey
    public String getLaporanKey() {
        return laporanKey;
    }

    public void setLaporanKey(String laporanKey) {
        this.laporanKey = laporanKey;
    }

    // Getter dan Setter dengan anotasi @PropertyName
    @PropertyName("NamaLaporan")
    public String getNamaLaporan() {
        return namaLaporan;
    }

    @PropertyName("NamaLaporan")
    public void setNamaLaporan(String namaLaporan) {
        this.namaLaporan = namaLaporan;
    }

    @PropertyName("TanggalKegiatan")
    public String getTanggalKegiatan() {
        return tanggalKegiatan;
    }

    @PropertyName("TanggalKegiatan")
    public void setTanggalKegiatan(String tanggalKegiatan) {
        this.tanggalKegiatan = tanggalKegiatan;
    }

    @PropertyName("Anggaran")
    public String getAnggaran() {
        return anggaran;
    }

    @PropertyName("Anggaran")
    public void setAnggaran(String anggaran) {
        this.anggaran = anggaran;
    }

    @PropertyName("Deskripsi")
    public String getDeskripsi() {
        return deskripsi;
    }

    @PropertyName("Deskripsi")
    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    @PropertyName("PenanggungJawab")
    public String getPenanggungJawab() {
        return penanggungJawab;
    }

    @PropertyName("PenanggungJawab")
    public void setPenanggungJawab(String penanggungJawab) {
        this.penanggungJawab = penanggungJawab;
    }
}
