package com.example.apktos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.apktos.R;
import com.example.apktos.model.modelEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TambahEvent extends AppCompatActivity {

    EditText etNamaLaporan, etTanggalKegiatan, etAnggaran, etDeskripsi, etPenanggungJawab;
    Button btnSubmit;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambahevent);

        databaseReference = FirebaseDatabase.getInstance().getReference("datalpj");

        // Inisialisasi View
        etNamaLaporan = findViewById(R.id.tmbhlpr);
        etTanggalKegiatan = findViewById(R.id.tmbhtnggl);
        etAnggaran = findViewById(R.id.tmbhanggaran);
        etDeskripsi = findViewById(R.id.tmbhdesk);
        etPenanggungJawab = findViewById(R.id.tmbhnmjawab);
        btnSubmit = findViewById(R.id.tmbhbtn);

        // Tombol Submit
        btnSubmit.setOnClickListener(v -> addLaporan());
    }

    private void addLaporan() {
        // Ambil input dari EditText
        String NamaLaporan = etNamaLaporan.getText().toString().trim();
        String TanggalKegiatan = etTanggalKegiatan.getText().toString().trim();
        String Anggaran = etAnggaran.getText().toString().trim();
        String Deskripsi = etDeskripsi.getText().toString().trim();
        String PenanggungJawab = etPenanggungJawab.getText().toString().trim();

        // Validasi input
        if (TextUtils.isEmpty(NamaLaporan) || TextUtils.isEmpty(TanggalKegiatan) ||
                TextUtils.isEmpty(Anggaran) || TextUtils.isEmpty(Deskripsi) || TextUtils.isEmpty(PenanggungJawab)) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dapatkan key terakhir dan tambahkan data baru
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot snapshot = task.getResult();

                // Tentukan key baru
                String newKey = "lpj1"; // Default key jika tidak ada data
                if (snapshot.exists()) {
                    int maxKey = 0;
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String key = child.getKey(); // Ambil key (misalnya: lpj1, lpj2)
                        if (key != null && key.startsWith("lpj")) {
                            try {
                                int currentKey = Integer.parseInt(key.replace("lpj", ""));
                                if (currentKey > maxKey) {
                                    maxKey = currentKey;
                                }
                            } catch (NumberFormatException ignored) {
                                // Abaikan jika key tidak dapat diubah ke angka
                            }
                        }
                    }
                    newKey = "lpj" + (maxKey + 1);
                }

                // Buat objek modelEvent
                modelEvent modelEvent = new modelEvent(NamaLaporan, TanggalKegiatan, Anggaran,
                        Deskripsi, PenanggungJawab);

                // Tambahkan data baru dengan key yang ditentukan
                databaseReference.child(newKey).setValue(modelEvent)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                            clearFields(); // Kosongkan semua input field
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Gagal menambahkan Data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Gagal mendapatkan data dari database.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method untuk mengosongkan input field setelah data berhasil ditambahkan
    private void clearFields() {
        etNamaLaporan.setText("");
        etTanggalKegiatan.setText("");
        etAnggaran.setText("");
        etDeskripsi.setText("");
        etPenanggungJawab.setText("");
    }
}