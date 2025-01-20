package com.example.apktos.model;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class modelAkun implements Serializable {

    @PropertyName("email")
    private String email;

    @PropertyName("level")
    private String level;

    @PropertyName("nama")
    private String nama;

    @PropertyName("password")
    private String password;

    private String userId; // Key unik untuk pengguna

    // Constructor tanpa parameter (dibutuhkan oleh Firebase)
    public modelAkun() {
    }

    // Constructor dengan parameter
    public modelAkun(String email, String level, String nama, String password) {
        this.email = email;
        this.level = level;
        this.nama = nama;
        this.password = password;
    }

    // Getter dan Setter untuk userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter dan Setter dengan anotasi @PropertyName
    @PropertyName("email")
    public String getEmail() {
        return email;
    }

    @PropertyName("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("level")
    public String getLevel() {
        return level;
    }

    @PropertyName("level")
    public void setLevel(String level) {
        this.level = level;
    }

    @PropertyName("nama")
    public String getNama() {
        return nama;
    }

    @PropertyName("nama")
    public void setNama(String nama) {
        this.nama = nama;
    }

    @PropertyName("password")
    public String getPassword() {
        return password;
    }

    @PropertyName("password")
    public void setPassword(String password) {
        this.password = password;
    }
}
