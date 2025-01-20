package com.example.apktos.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apktos.DetailEvent;
import com.example.apktos.R;
import com.example.apktos.model.modelEvent;

import java.util.List;

public class AdapterEvent extends RecyclerView.Adapter<AdapterEvent.modelEventViewHolder> {

    private Context context;
    private List<modelEvent> modelEventList;
    String role;

    // Constructor
    public AdapterEvent(Context context, List<modelEvent> modelEventList, String role) {
        this.context = context;
        this.modelEventList = modelEventList;
        this.role = role;
    }

    @NonNull
    @Override
    public modelEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new modelEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull modelEventViewHolder holder, int position) {
        modelEvent modelEvent = modelEventList.get(position);

        holder.tvNamaLaporan.setText(modelEvent.getNamaLaporan());
        holder.tvTanggalKegiatan.setText(modelEvent.getTanggalKegiatan());

        // Aksi saat item RecyclerView diklik
        holder.itemView.setOnClickListener(v -> {
            // Ambil key dari Firebase
            String laporanKey = modelEvent.getLaporanKey(); // Ambil key dari model atau snapshot jika ada

            // Kirim data ke DetailEvent
            Intent intent = new Intent(context, DetailEvent.class);
            intent.putExtra("modelEvent", modelEvent); // Kirim objek modelEvent
            intent.putExtra("LaporanKey", laporanKey); // Kirim key unik
            intent.putExtra("role", role);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return modelEventList != null ? modelEventList.size() : 0;
    }

    // ViewHolder untuk menyimpan referensi view
    public static class modelEventViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaLaporan, tvTanggalKegiatan;

        public modelEventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaLaporan = itemView.findViewById(R.id.tvNamaLaporan);
            tvTanggalKegiatan = itemView.findViewById(R.id.tvTanggalKegiatan);
        }
    }
}
