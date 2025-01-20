package com.example.apktos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.apktos.model.modelLpj;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class TambahLpj extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGES = 101;

    private EditText etNamaKegiatan, etTanggal, etPemasukan, etPengeluaran, etKeterangan;
    private Button btnPilihGambar, btnTambahLaporan;
    private LinearLayout layoutPreviewGambar;

    private String sisauang;
    private int masuk, kluar;

    private List<Uri> imageUris = new ArrayList<>(); // List untuk menyimpan URI gambar yang dipilih
    private List<String> uploadedImageUrls = new ArrayList<>(); // URL gambar yang diunggah ke Firebase
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private String nextKey = ""; // Key berikutnya

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_lpj);

        // Inisialisasi Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("laporanlpj");
        storageReference = FirebaseStorage.getInstance().getReference("gambar_laporan");

        // Inisialisasi Views
        etNamaKegiatan = findViewById(R.id.etNamaKegiatan);
        etTanggal = findViewById(R.id.etTanggal);
        etPemasukan = findViewById(R.id.etPemasukan);
        etPengeluaran = findViewById(R.id.etPengeluaran);
        etKeterangan = findViewById(R.id.etKeterangan);
        btnPilihGambar = findViewById(R.id.btnPilihGambar);
        btnTambahLaporan = findViewById(R.id.btnTambahLaporan);
        layoutPreviewGambar = findViewById(R.id.layoutPreviewGambar);

        // Tombol Pilih Gambar
        btnPilihGambar.setOnClickListener(v -> pickMultipleImages());

        // Tombol Tambah Laporan
        btnTambahLaporan.setOnClickListener(v -> {
            if (!imageUris.isEmpty()) {
                generateNextKeyAndSaveData();
            } else {
                Toast.makeText(TambahLpj.this, "Pilih setidaknya satu gambar!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickMultipleImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), REQUEST_CODE_PICK_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                // Banyak gambar yang dipilih
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);

                    // Tambahkan gambar ke layout preview
                    addImagePreview(imageUri);
                }
            } else if (data.getData() != null) {
                // Satu gambar yang dipilih
                Uri imageUri = data.getData();
                imageUris.add(imageUri);

                // Tambahkan gambar ke layout preview
                addImagePreview(imageUri);
            }
        }
    }

    private void addImagePreview(Uri imageUri) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageURI(imageUri);
        layoutPreviewGambar.addView(imageView);
    }

    private void generateNextKeyAndSaveData() {
        // Ambil key terakhir dari Firebase
        databaseReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String lastKey = data.getKey();
                        if (lastKey != null && lastKey.startsWith("lap")) {
                            int lastNumber = Integer.parseInt(lastKey.replace("lap", ""));
                            nextKey = "lap" + (lastNumber + 1); // Buat key berikutnya
                        } else {
                            nextKey = "lap1"; // Default jika key tidak valid
                        }
                    }
                } else {
                    nextKey = "lap1"; // Default jika data kosong
                }
                // Lanjutkan menyimpan data
                uploadImagesAndSaveData(nextKey);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TambahLpj.this, "Gagal mendapatkan key terakhir!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImagesAndSaveData(String key) {
        String namaKegiatan = etNamaKegiatan.getText().toString().trim();
        String tanggal = etTanggal.getText().toString().trim();
        String pemasukan = etPemasukan.getText().toString().trim();
        String pengeluaran = etPengeluaran.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();
        masuk = Integer.parseInt(pemasukan);
        kluar = Integer.parseInt(pengeluaran);
        sisauang = Integer.toString(masuk - kluar);

        if (TextUtils.isEmpty(namaKegiatan) || TextUtils.isEmpty(tanggal) ||
                TextUtils.isEmpty(pemasukan) || TextUtils.isEmpty(pengeluaran) || TextUtils.isEmpty(sisauang) ||
                TextUtils.isEmpty(keterangan)) {
            Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Uri imageUri : imageUris) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
            fileReference.putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    fileReference.getDownloadUrl().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            String imageUrl = task1.getResult().toString();
                            uploadedImageUrls.add(imageUrl);

                            // Jika semua gambar sudah diunggah, simpan data ke Firebase Database
                            if (uploadedImageUrls.size() == imageUris.size()) {
                                modelLpj laporan = new modelLpj(namaKegiatan, tanggal, pemasukan, pengeluaran, sisauang, keterangan, uploadedImageUrls);
                                databaseReference.child(key).setValue(laporan)
                                        .addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                Toast.makeText(TambahLpj.this, "Laporan berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                                                clearInputFields();
                                            } else {
                                                Toast.makeText(TambahLpj.this, "Gagal menambahkan laporan!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    });
                }
            });
        }
    }

    private void clearInputFields() {
        etNamaKegiatan.setText("");
        etTanggal.setText("");
        etPemasukan.setText("");
        etPengeluaran.setText("");
        etKeterangan.setText("");
        layoutPreviewGambar.removeAllViews();
        imageUris.clear();
        uploadedImageUrls.clear();
    }
}
