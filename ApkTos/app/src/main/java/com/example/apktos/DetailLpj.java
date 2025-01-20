package com.example.apktos;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.apktos.Adapter.AdapterGambar;
import com.example.apktos.model.modelLpj;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailLpj extends AppCompatActivity {

    private TextView tvNamaKegiatan, tvTanggal, tvPemasukan, tvPengeluaran, tvSisaUang, tvKeterangan;
    private RecyclerView recyclerViewGambar;
    private Button btnEdit, btnDelete, butctk;
    private String lpjKey;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_lpj);

        // Inisialisasi Views
        tvNamaKegiatan = findViewById(R.id.tvDetailNamaKegiatan);
        tvTanggal = findViewById(R.id.tvDetailTanggal);
        tvPemasukan = findViewById(R.id.tvDetailPemasukan);
        tvPengeluaran = findViewById(R.id.tvDetailPengeluaran);
        tvSisaUang = findViewById(R.id.tvDetailSisaUang);
        tvKeterangan = findViewById(R.id.tvDetailKeterangan);
        recyclerViewGambar = findViewById(R.id.recyclerViewGambar);
        btnEdit = findViewById(R.id.buteditlpj);
        btnDelete = findViewById(R.id.buthpslpj);
        butctk = findViewById(R.id.butpdflpj);

        // Ambil Data dari Intent
        databaseReference = FirebaseDatabase.getInstance().getReference("laporanlpj");
        modelLpj laporan = (modelLpj) getIntent().getSerializableExtra("modelLpj");
        lpjKey = getIntent().getStringExtra("Lpjkey");
        String rul = getIntent().getStringExtra("role");

        if ("user".equals(rul)) {
            btnEdit.setVisibility(View.GONE); // Tombol akan menghilang sepenuhnya
            btnDelete.setVisibility(View.GONE);
            butctk.setVisibility(View.GONE);
        } else {
            btnDelete.setVisibility(View.VISIBLE); // Tombol akan muncul
            btnEdit.setVisibility(View.VISIBLE);
            butctk.setVisibility(View.VISIBLE);
        }

        if (laporan != null) {
            tvNamaKegiatan.setText(laporan.getNamaKegiatan());
            tvTanggal.setText(laporan.getTanggal());
            tvPemasukan.setText("RP."+laporan.getPemasukan());
            tvPengeluaran.setText("RP."+laporan.getPengeluaran());
            tvSisaUang.setText("RP."+laporan.getSisaUang());
            tvKeterangan.setText(laporan.getKeterangan());

            List<String> gambarList = laporan.getGambarAsList();
            recyclerViewGambar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            AdapterGambar gambarAdapter = new AdapterGambar(this, gambarList);
            recyclerViewGambar.setAdapter(gambarAdapter);
        }

        butctk.setOnClickListener(v -> {
            AdapterGambar adapterGambar = (AdapterGambar) recyclerViewGambar.getAdapter();
            if (adapterGambar != null) {
                generatePdf(laporan, adapterGambar);
            } else {
                Toast.makeText(this, "Gambar tidak ditemukan!", Toast.LENGTH_SHORT).show();
            }
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(DetailLpj.this, EditLpj.class);
            intent.putExtra("modelLpj", laporan);
            intent.putExtra("Lpjkey", lpjKey);
            startActivity(intent);
            finish();
        });

        btnDelete.setOnClickListener(v -> hapusData());
    }

    // Fungsi untuk memuat gambar dan membuat PDF
    private void generatePdf(modelLpj laporan, AdapterGambar adapterGambar) {
        List<String> imageUrls = adapterGambar.imageUrls;
        List<Bitmap> bitmapList = new ArrayList<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                for (String url : imageUrls) {
                    Bitmap bitmap = Glide.with(this)
                            .asBitmap()
                            .load(url)
                            .submit()
                            .get();
                    bitmapList.add(bitmap);
                }

                runOnUiThread(() -> createPdfWithImages(laporan, bitmapList));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Gagal memuat gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    // Membuat PDF dengan data dan gambar
    private void createPdfWithImages(modelLpj laporan, List<Bitmap> bitmapList) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int startX = 50;
        int startY = 50;
        int lineSpacing = 30;

        // Header
        paint.setTextSize(16);
        paint.setFakeBoldText(true);
        canvas.drawText("LAPORAN PERTANGGUNG JAWABAN", startX, startY, paint);
        canvas.drawText("OSIS SMA N 3 KOTA SORONG", startX, startY + lineSpacing, paint);
        canvas.drawText("Periode 2025-2026", startX, startY + 2 * lineSpacing, paint);

        startY += 4 * lineSpacing;

        // Detail Laporan
        paint.setTextSize(14);
        paint.setFakeBoldText(true);

        canvas.drawText("Nama Kegiatan:", startX, startY, paint);
        paint.setFakeBoldText(false);
        canvas.drawText(laporan.getNamaKegiatan(), startX + 150, startY, paint);
        startY += lineSpacing;

        paint.setFakeBoldText(true);
        canvas.drawText("Waktu Pelaksanaan:", startX, startY, paint);
        paint.setFakeBoldText(false);
        canvas.drawText(laporan.getTanggal(), startX + 150, startY, paint);
        startY += lineSpacing;

        paint.setFakeBoldText(true);
        canvas.drawText("Rincian Keuangan:", startX, startY, paint);
        paint.setFakeBoldText(false);
        String rincianKeuangan = "Pengeluaran: RP. " + laporan.getPengeluaran() + "\n" +
                "Pemasukan: RP. " + laporan.getPemasukan() + "\n" +
                "Sisa Saldo: RP. " + laporan.getSisaUang();
        for (String line : rincianKeuangan.split("\n")) {
            canvas.drawText(line, startX + 150, startY, paint);
            startY += lineSpacing;
        }

        paint.setFakeBoldText(true);
        canvas.drawText("Keterangan:", startX, startY, paint);
        paint.setFakeBoldText(false);
        canvas.drawText(laporan.getKeterangan(), startX + 150, startY, paint);
        startY += 2 * lineSpacing;

        // Dokumentasi Kegiatan (Gambar)
        paint.setFakeBoldText(true);
        canvas.drawText("Dokumentasi Kegiatan:", startX, startY, paint);
        startY += lineSpacing;

        int imageX = startX;
        int imageY = startY;
        int imageMaxWidth = 150;
        int imageMaxHeight = 150;

        for (Bitmap bitmap : bitmapList) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, imageMaxWidth, imageMaxHeight, false);
            canvas.drawBitmap(scaledBitmap, imageX, imageY, paint);

            imageY += imageMaxHeight + 20;

            // Buat halaman baru jika gambar tidak cukup ruang
            if (imageY + imageMaxHeight > pageInfo.getPageHeight()) {
                pdfDocument.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                imageY = 50;
            }
        }

        pdfDocument.finishPage(page);
        savePdfToMediaStore(pdfDocument, "Laporan LPJ Osis"+ ".pdf");
    }


    // Simpan PDF ke MediaStore
    private void savePdfToMediaStore(PdfDocument pdfDocument, String fileName) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

                Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                if (uri != null) {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    pdfDocument.writeTo(outputStream);
                    outputStream.close();
                    Toast.makeText(this, "PDF berhasil disimpan di Documents.", Toast.LENGTH_SHORT).show();
                }
            } else {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName);
                pdfDocument.writeTo(new FileOutputStream(file));
                Toast.makeText(this, "PDF berhasil disimpan di: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Gagal menyimpan PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }
    }

    private void hapusData() {
        if (lpjKey != null) {
            databaseReference.child(lpjKey).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(DetailLpj.this, "Laporan berhasil dihapus!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(DetailLpj.this, "Gagal menghapus laporan!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Key laporan tidak valid!", Toast.LENGTH_SHORT).show();
        }
    }


    
}
