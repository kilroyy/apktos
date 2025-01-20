package com.example.apktos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apktos.DetailLpj;
import com.example.apktos.R;
import com.example.apktos.model.modelLpj;

import java.util.List;

public class AdapterLpj extends RecyclerView.Adapter<AdapterLpj.modelLpjViewHolder> {

    private Context context;
    private List<modelLpj> modelLpjList;
    String role;

    public AdapterLpj(Context context, List<modelLpj> modelLpjList, String role) {
        this.context = context;
        this.modelLpjList = modelLpjList;
        this.role = role;
    }

    @NonNull
    @Override
    public modelLpjViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lpj, parent, false);
        return new modelLpjViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull modelLpjViewHolder holder, int position) {
        modelLpj modelLpj = modelLpjList.get(position);

        holder.nmkegiatan.setText(modelLpj.getNamaKegiatan());
        holder.tnggal.setText(modelLpj.getTanggal());

        // Aksi saat item RecyclerView diklik
        holder.itemView.setOnClickListener(v -> {
            // Ambil key dari Firebase
            String lpjkey = modelLpj.getLaporanKey(); // Ambil key dari model atau snapshot jika ada

            // Kirim data ke DetailEvent
            Intent intent = new Intent(context, DetailLpj.class);
            intent.putExtra("modelLpj", modelLpj); // Kirim objek modelEvent
            intent.putExtra("Lpjkey", lpjkey); // Kirim key unik
            intent.putExtra("role", role);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return modelLpjList != null ? modelLpjList.size() : 0;
    }

    // ViewHolder untuk menyimpan referensi view
    public static class modelLpjViewHolder extends RecyclerView.ViewHolder {
        TextView nmkegiatan, tnggal;

        public modelLpjViewHolder(@NonNull View itemView) {
            super(itemView);
            nmkegiatan = itemView.findViewById(R.id.tvNamaKegiatan);
            tnggal = itemView.findViewById(R.id.tvTanggal);
        }
    }

}
