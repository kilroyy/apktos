package com.example.apktos;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Admin extends AppCompatActivity {
    Button butlpj,butevent,btsar,btakn;
    ImageButton btnLogout;
    private TextView namain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
        String nama = sharedPreferences.getString("nama", "No nama");
        String role = sharedPreferences.getString("role", "No role");
        String nis = sharedPreferences.getString("email", "No email");
        namain = findViewById(R.id.namaini);
        // Tampilkan email di TextView
        namain.setText(nama);
        btnLogout = findViewById(R.id.logutadmin);
        butevent = findViewById(R.id.tomeve);
        butlpj = findViewById(R.id.tomlpj);
        btsar = findViewById(R.id.tomsar);
        btakn = findViewById(R.id.tomakun);
        if ("user".equals(role)) {
            btakn.setVisibility(View.GONE); // Tombol akan menghilang sepenuhnya
        } else {
            btakn.setVisibility(View.VISIBLE); // Tombol akan muncul
        }
        // Tombol Logout
        btnLogout.setOnClickListener(v -> logout());
        butevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, TampilanEvent.class);
                intent.putExtra("role", role);
                startActivity(intent);
            }
        });

        butlpj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, TampilanLpj.class);
                intent.putExtra("role", role);
                startActivity(intent);
            }
        });

        btsar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, TampilanSaran.class);
                intent.putExtra("role", role);
                intent.putExtra("nama", nama);
                intent.putExtra("email", nis);
                startActivity(intent);
            }
        });

        btakn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, TampilAkun.class);
                startActivity(intent);
            }
        });

    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Hapus semua data sesi
        editor.apply();

        // Kembali ke halaman login
        startActivity(new Intent(Admin.this, Login.class));
        finish();
    }


}