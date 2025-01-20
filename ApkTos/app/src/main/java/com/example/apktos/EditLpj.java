package com.example.apktos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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

public class EditLpj extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGES = 101;

    private EditText etNamaKegiatan, etTanggal, etPemasukan, etPengeluaran, etKeterangan;
    private Button btnPilihGambar, btnUpdateLaporan;
    private LinearLayout layoutPreviewGambar;

    private String laporanKey; // Key laporan yang akan diedit
    private List<Uri> newImageUris = new ArrayList<>(); // URI gambar baru yang dipilih
    private List<String> uploadedImageUrls = new ArrayList<>(); // URL gambar yang diunggah ke Firebase
    private List<String> existingImageUrls = new ArrayList<>(); // URL gambar yang sudah ada

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lpj);

        laporanKey = getIntent().getStringExtra("Lpjkey");

        // Inisialisasi Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("laporanlpj");
        storageReference = FirebaseStorage.getInstance().getReference("gambar_laporan");

        // Inisialisasi Views
        etNamaKegiatan = findViewById(R.id.editnmkg);
        etTanggal = findViewById(R.id.edittglljp);
        etPemasukan = findViewById(R.id.editpemskn);
        etPengeluaran = findViewById(R.id.editpnglrn);
        etKeterangan = findViewById(R.id.editkete);
        btnPilihGambar = findViewById(R.id.btneditGambar);
        btnUpdateLaporan = findViewById(R.id.btneditaporan);
        layoutPreviewGambar = findViewById(R.id.editlayoutPreviewGambar);

        // Load data laporan berdasarkan key
        loadLaporanData();

        // Pilih gambar baru
        btnPilihGambar.setOnClickListener(v -> pickMultipleImages());

        // Update data
        btnUpdateLaporan.setOnClickListener(v -> {
            if (!newImageUris.isEmpty()) {
                uploadNewImagesAndUpdateData();
            } else {
                updateData(existingImageUrls); // Jika tidak ada gambar baru
            }
        });
    }

    private void uploadNewImagesAndUpdateData() {
        for (Uri imageUri : newImageUris) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
            fileReference.putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    fileReference.getDownloadUrl().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            String imageUrl = task1.getResult().toString();
                            uploadedImageUrls.add(imageUrl);

                            if (uploadedImageUrls.size() == newImageUris.size()) {
                                List<String> allImageUrls = new ArrayList<>(existingImageUrls);
                                allImageUrls.addAll(uploadedImageUrls);
                                updateData(allImageUrls);
                            }
                        }
                    });
                }
            });
        }
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
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    newImageUris.add(imageUri);
                    addImagePreview(imageUri, true); // Tambahkan ke preview
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                newImageUris.add(imageUri);
                addImagePreview(imageUri, true); // Tambahkan ke preview
            }
        }
    }

    private void addImagePreview(Uri imageUri, boolean isNewImage) {
        // Gunakan FrameLayout sebagai container untuk gambar dan tombol hapus
        FrameLayout container = new FrameLayout(this);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(280, 280); // Ukuran setiap item dalam LinearLayout
        containerParams.setMargins(8, 8, 8, 8); // Margin antar item
        container.setLayoutParams(containerParams);

        // ImageView untuk menampilkan gambar
        ImageView imageView = new ImageView(this);
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(260, 260); // Ukuran gambar
        imageParams.gravity = Gravity.CENTER; // Gambar di tengah container
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageURI(imageUri);

        // ImageView untuk tombol hapus
        ImageView deleteButton = new ImageView(this);
        FrameLayout.LayoutParams deleteParams = new FrameLayout.LayoutParams(80, 80);
        deleteParams.gravity = Gravity.END | Gravity.TOP; // Posisi di kanan atas
        deleteParams.setMargins(10, 10, 10, 10); // Margin untuk tombol hapus
        deleteButton.setLayoutParams(deleteParams);
        deleteButton.setImageResource(R.drawable.del); // Ikon delete
        deleteButton.setPadding(5, 5, 5, 5); // Padding untuk ikon
        deleteButton.setBackgroundColor(Color.argb(150, 255, 0, 0)); // Warna background untuk debugging (opsional)
        deleteButton.setOnClickListener(v -> {
            layoutPreviewGambar.removeView(container); // Hapus container dari LinearLayout
            if (isNewImage) {
                newImageUris.remove(imageUri); // Hapus URI dari daftar gambar baru
            } else {
                Toast.makeText(this, "Gambar dihapus!", Toast.LENGTH_SHORT).show();
            }
        });

        // Tambahkan gambar dan tombol hapus ke FrameLayout
        container.addView(imageView);
        container.addView(deleteButton);

        // Tambahkan FrameLayout ke LinearLayout
        layoutPreviewGambar.addView(container);
    }






    private void loadLaporanData() {
        databaseReference.child(laporanKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    modelLpj laporan = snapshot.getValue(modelLpj.class);

                    if (laporan != null) {
                        etNamaKegiatan.setText(laporan.getNamaKegiatan());
                        etTanggal.setText(laporan.getTanggal());
                        etPemasukan.setText(laporan.getPemasukan());
                        etPengeluaran.setText(laporan.getPengeluaran());
                        etKeterangan.setText(laporan.getKeterangan());
                        existingImageUrls = laporan.getGambarAsList();

                        // Tampilkan gambar yang sudah ada
                        for (String imageUrl : existingImageUrls) {
                            addExistingImagePreview(imageUrl);
                        }
                    }
                } else {
                    Toast.makeText(EditLpj.this, "Laporan tidak ditemukan!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditLpj.this, "Gagal memuat data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addExistingImagePreview(String imageUrl) {
        // Gunakan FrameLayout sebagai container untuk gambar dan tombol hapus
        FrameLayout container = new FrameLayout(this);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(220, 220); // Ukuran setiap item
        containerParams.setMargins(8, 8, 8, 8); // Margin antar item
        container.setLayoutParams(containerParams);

        // ImageView untuk menampilkan gambar
        ImageView imageView = new ImageView(this);
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(200, 200);
        imageParams.gravity = Gravity.CENTER; // Gambar di tengah container
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(imageUrl).into(imageView);

        // ImageView untuk tombol hapus
        ImageView deleteButton = new ImageView(this);
        FrameLayout.LayoutParams deleteParams = new FrameLayout.LayoutParams(60, 60); // Ukuran tombol
        deleteParams.gravity = Gravity.END | Gravity.TOP; // Posisi tombol di kanan atas
        deleteParams.setMargins(10, 10, 10, 10); // Margin tombol dari tepi
        deleteButton.setLayoutParams(deleteParams);
        deleteButton.setImageResource(R.drawable.del); // Pastikan ikon delete tersedia
        deleteButton.setPadding(5, 5, 5, 5); // Padding untuk tombol
        deleteButton.setBackgroundColor(Color.argb(150, 255, 0, 0)); // Warna background untuk debugging (opsional)
        deleteButton.setOnClickListener(v -> {
            layoutPreviewGambar.removeView(container); // Hapus container dari LinearLayout
            existingImageUrls.remove(imageUrl); // Hapus dari daftar URL gambar lama
            Toast.makeText(this, "Gambar dihapus!", Toast.LENGTH_SHORT).show();
        });

        // Tambahkan gambar dan tombol hapus ke container
        container.addView(imageView);
        container.addView(deleteButton);

        // Tambahkan container ke LinearLayout
        layoutPreviewGambar.addView(container);
    }


    private void updateData(List<String> imageUrls) {
        String namaKegiatan = etNamaKegiatan.getText().toString().trim();
        String tanggal = etTanggal.getText().toString().trim();
        String pemasukan = etPemasukan.getText().toString().trim();
        String pengeluaran = etPengeluaran.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();
        int pemasukanInt = Integer.parseInt(pemasukan);
        int pengeluaranInt = Integer.parseInt(pengeluaran);
        String sisaUang = String.valueOf(pemasukanInt - pengeluaranInt);

        if (TextUtils.isEmpty(namaKegiatan) || TextUtils.isEmpty(tanggal) ||
                TextUtils.isEmpty(pemasukan) || TextUtils.isEmpty(pengeluaran) || TextUtils.isEmpty(keterangan)) {
            Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        modelLpj updatedLaporan = new modelLpj(namaKegiatan, tanggal, pemasukan, pengeluaran, sisaUang, keterangan, imageUrls);

        databaseReference.child(laporanKey).setValue(updatedLaporan).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditLpj.this, "Laporan berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditLpj.this, "Gagal memperbarui laporan!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

