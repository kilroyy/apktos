package com.example.apktos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apktos.model.modelEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailEvent extends AppCompatActivity {

    TextView tvNamaLaporan, tvTanggalKegiatan, tvAnggaran, tvDeskripsi, tvPenanggungJawab;
    Button btnEdit, btnDelete, butctk;
    modelEvent modelEvent;
    String laporanKey;
    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        tvNamaLaporan = findViewById(R.id.tvNamaLaporan);
        tvTanggalKegiatan = findViewById(R.id.tvTanggalKegiatan);
        tvAnggaran = findViewById(R.id.tvAnggaran);
        tvDeskripsi = findViewById(R.id.tvDeskripsi);
        tvPenanggungJawab = findViewById(R.id.tvPenanggungJawab);
        btnEdit = findViewById(R.id.butediteve);
        btnDelete = findViewById(R.id.buthpseve);
        String stat = getIntent().getStringExtra("role");
        modelEvent = (modelEvent) getIntent().getSerializableExtra("modelEvent");
        laporanKey = getIntent().getStringExtra("LaporanKey");

        if (modelEvent != null) {
            tvNamaLaporan.setText(modelEvent.getNamaLaporan());
            tvTanggalKegiatan.setText(modelEvent.getTanggalKegiatan());
            tvAnggaran.setText(modelEvent.getAnggaran());
            tvDeskripsi.setText(modelEvent.getDeskripsi());
            tvPenanggungJawab.setText(modelEvent.getPenanggungJawab());
        }

        if ("user".equals(stat)) {
            btnEdit.setVisibility(View.GONE); // Tombol akan menghilang sepenuhnya
            btnDelete.setVisibility(View.GONE);
        } else {
            btnDelete.setVisibility(View.VISIBLE); // Tombol akan muncul
            btnEdit.setVisibility(View.VISIBLE);
        }

        // Tombol Edit
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(DetailEvent.this, EditEvent.class);
            intent.putExtra("modelEvent", modelEvent);
            intent.putExtra("LaporanKey", laporanKey);
            startActivity(intent);
            finish();
        });

        // Tombol Hapus
        btnDelete.setOnClickListener(v -> deleteLaporan());

    }

    private void deleteLaporan() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("datalpj");
        databaseReference.child(laporanKey).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(DetailEvent.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                finish(); // Kembali ke halaman sebelumnya
            } else {
                Toast.makeText(DetailEvent.this, "Gagal menghapus Data", Toast.LENGTH_SHORT).show();
            }
        });
    }


}