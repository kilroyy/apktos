package com.example.apktos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apktos.Adapter.AdapterSaran;
import com.example.apktos.model.modelEvent;
import com.example.apktos.model.modelSaran;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TampilanSaran extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterSaran adapsaran;
    Button buttm;
    private List<modelSaran> modsar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampilan_saran);

        recyclerView = findViewById(R.id.recyclerViewSaran);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String nm = getIntent().getStringExtra("nama");
        String ns = getIntent().getStringExtra("email");
        String status = getIntent().getStringExtra("role");
        buttm = findViewById(R.id.buttmbhsrn);
        if ("admin".equals(status)) {
            buttm.setVisibility(View.GONE); // Tombol akan menghilang sepenuhnya
        } else {
            buttm.setVisibility(View.VISIBLE); // Tombol akan muncul
        }
        buttm.setOnClickListener(v -> {
            Intent intent = new Intent(TampilanSaran.this, TambahSaran.class);
            intent.putExtra("nama", nm);
            intent.putExtra("email", ns);
            startActivity(intent);
        });

        modsar = new ArrayList<>();
        adapsaran = new AdapterSaran(this, modsar);
        recyclerView.setAdapter(adapsaran);



        // Ambil data dari Firebase
        FirebaseDatabase.getInstance().getReference("pelaporan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modsar.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    modelSaran event = dataSnapshot.getValue(modelSaran.class);
                    if (event != null) {
                        event.setLaporanKey(dataSnapshot.getKey()); // Set key unik
                        modsar.add(event);
                    }
                }
                adapsaran.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TampilanSaran.this, "Gagal memuat data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
