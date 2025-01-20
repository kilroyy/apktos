package com.example.apktos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apktos.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterGambar extends RecyclerView.Adapter<AdapterGambar.GambarViewHolder> {

    private Context context;
    public List<String> imageUrls;

    // Constructor untuk menerima context dan list URL gambar
    public AdapterGambar(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = (imageUrls != null) ? imageUrls : new ArrayList<>(); // Pastikan tidak null
    }

    @NonNull
    @Override
    public GambarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout untuk setiap item gambar
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new GambarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GambarViewHolder holder, int position) {
        // Ambil URL gambar berdasarkan posisi
        String imageUrl = imageUrls.get(position);

        // Gunakan Glide untuk memuat gambar ke dalam ImageView
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.loading) // Placeholder saat loading
                .error(R.drawable.warning) // Gambar jika URL gagal dimuat
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size(); // Jumlah item di RecyclerView
    }

    // ViewHolder untuk menyimpan referensi ImageView
    public static class GambarViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public GambarViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView); // Referensi ke ImageView
        }
    }
}
