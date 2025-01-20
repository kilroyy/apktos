package com.example.apktos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apktos.model.modelEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditEvent extends AppCompatActivity {

    private EditText etNamaLaporan, etTanggalKegiatan, etAnggaran, etDeskripsi, etPenanggungJawab;
    private Button btnSaveChanges;

    private String laporanKey; // Key unik modelEvent dari Firebase
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        // Inisialisasi view
        etNamaLaporan = findViewById(R.id.editlpr);
        etTanggalKegiatan = findViewById(R.id.edittnggl);
        etAnggaran = findViewById(R.id.edituang);
        etDeskripsi = findViewById(R.id.editdesk);
        etPenanggungJawab = findViewById(R.id.editnmjawab);
        btnSaveChanges = findViewById(R.id.edibtn);

        // Ambil data dari Intent
        modelEvent modelEvent = (modelEvent) getIntent().getSerializableExtra("modelEvent");
        laporanKey = getIntent().getStringExtra("LaporanKey");

        if (modelEvent != null) {
            // Tampilkan data modelEvent ke dalam EditText
            etNamaLaporan.setText(modelEvent.getNamaLaporan());
            etTanggalKegiatan.setText(modelEvent.getTanggalKegiatan());
            etAnggaran.setText(modelEvent.getAnggaran());
            etDeskripsi.setText(modelEvent.getDeskripsi());
            etPenanggungJawab.setText(modelEvent.getPenanggungJawab());
        }

        // Database reference ke "datalpj"
        databaseReference = FirebaseDatabase.getInstance().getReference("datalpj").child(laporanKey);

        // Tombol Simpan Perubahan
        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        // Ambil data dari EditText
        String namaLaporan = etNamaLaporan.getText().toString().trim();
        String tanggalKegiatan = etTanggalKegiatan.getText().toString().trim();
        String anggaran = etAnggaran.getText().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();
        String penanggungJawab = etPenanggungJawab.getText().toString().trim();

        // Validasi input
        if (TextUtils.isEmpty(namaLaporan) || TextUtils.isEmpty(tanggalKegiatan) ||
                TextUtils.isEmpty(anggaran) || TextUtils.isEmpty(deskripsi) || TextUtils.isEmpty(penanggungJawab)) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buat objek modelEvent dengan data baru
        modelEvent updatedModelEvent = new modelEvent(namaLaporan, tanggalKegiatan, anggaran, deskripsi, penanggungJawab);

        // Update data di Firebase
        databaseReference.setValue(updatedModelEvent).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditEvent.this, "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                finish();

                // Tutup activity setelah berhasil
            } else {
                String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                Toast.makeText(EditEvent.this, "Gagal memperbarui data: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}