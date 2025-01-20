package com.example.apktos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.apktos.model.modelAkun;
import com.example.apktos.model.modelEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TambahAkun extends AppCompatActivity {

    EditText namaakn, nisakn, passakn;
    Spinner spnlvl;
    Button btnSubmit;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_akun);

        btnSubmit = findViewById(R.id.tmbhregis);
        namaakn = findViewById(R.id.tmbhnamakun);
        nisakn = findViewById(R.id.tmbhnis);
        passakn = findViewById(R.id.tmbhpassakn);
        spnlvl = findViewById(R.id.spinlvl);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        String[] data = {"admin", "user"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnlvl.setAdapter(adapter);

        btnSubmit.setOnClickListener(v -> addLaporan());
    }

    private void addLaporan() {
        // Ambil input dari EditText
        String nama = namaakn.getText().toString().trim();
        String nis = nisakn.getText().toString().trim();
        String password = passakn.getText().toString().trim();
        String lvl = spnlvl.getSelectedItem().toString().trim();

        // Validasi input
        if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(nis) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(lvl)) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dapatkan key terakhir dan tambahkan data baru
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot snapshot = task.getResult();

                // Tentukan key baru
                String newKey = "userid1"; // Default key jika tidak ada data
                if (snapshot.exists()) {
                    int maxKey = 0;
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String key = child.getKey(); // Ambil key (misalnya: lpj1, lpj2)
                        if (key != null && key.startsWith("userid")) {
                            try {
                                int currentKey = Integer.parseInt(key.replace("userid", ""));
                                if (currentKey > maxKey) {
                                    maxKey = currentKey;
                                }
                            } catch (NumberFormatException ignored) {
                                // Abaikan jika key tidak dapat diubah ke angka
                            }
                        }
                    }
                    newKey = "userid" + (maxKey + 1);
                }

                // Buat objek modelEvent
                modelAkun modakn = new modelAkun(nis, lvl, nama, password);

                // Tambahkan data baru dengan key yang ditentukan
                databaseReference.child(newKey).setValue(modakn)
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
        namaakn.setText("");
        nisakn.setText("");
        passakn.setText("");
    }
}