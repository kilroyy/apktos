package com.example.apktos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apktos.model.modelEvent;
import com.example.apktos.model.modelSaran;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TambahSaran extends AppCompatActivity {

    Button btt;
    EditText saran;
    String nm,nis;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_saran);
        databaseReference = FirebaseDatabase.getInstance().getReference("pelaporan");
        btt = findViewById(R.id.tmbhbtnsrn);
        saran = findViewById(R.id.saraninput);
        nm = getIntent().getStringExtra("nama");
        nis = getIntent().getStringExtra("email");

        btt.setOnClickListener(v -> addLaporan());
    }

    private void addLaporan() {
        // Ambil input dari EditText
        String srn = saran.getText().toString().trim();

        // Validasi input
        if (TextUtils.isEmpty(srn) || TextUtils.isEmpty(nm) ||
                TextUtils.isEmpty(nis)) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dapatkan key terakhir dan tambahkan data baru
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot snapshot = task.getResult();

                // Tentukan key baru
                String newKey = "lapor1"; // Default key jika tidak ada data
                if (snapshot.exists()) {
                    int maxKey = 0;
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String key = child.getKey(); // Ambil key (misalnya: lpj1, lpj2)
                        if (key != null && key.startsWith("lapor")) {
                            try {
                                int currentKey = Integer.parseInt(key.replace("lapor", ""));
                                if (currentKey > maxKey) {
                                    maxKey = currentKey;
                                }
                            } catch (NumberFormatException ignored) {
                                // Abaikan jika key tidak dapat diubah ke angka
                            }
                        }
                    }
                    newKey = "lapor" + (maxKey + 1);
                }

                // Buat objek modelEvent
                modelSaran modsaran = new modelSaran(nm, nis, srn);

                // Tambahkan data baru dengan key yang ditentukan
                databaseReference.child(newKey).setValue(modsaran)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                            clearFields(); // Kosongkan semua input field
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Gagal menambahkan data " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Gagal mendapatkan data dari database.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method untuk mengosongkan input field setelah data berhasil ditambahkan
    private void clearFields() {
        saran.setText("");
    }
}