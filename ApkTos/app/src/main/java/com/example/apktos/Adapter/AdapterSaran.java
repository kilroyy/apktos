package com.example.apktos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apktos.R;
import com.example.apktos.model.modelEvent;
import com.example.apktos.model.modelSaran;

import java.util.List;

public class AdapterSaran extends RecyclerView.Adapter<AdapterSaran.EventViewHolder> {

    private Context context;
    private List<modelSaran> modsaran ;

    // Constructor
    public AdapterSaran(Context context, List<modelSaran> modsaran) {
        this.context = context;
        this.modsaran = modsaran;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saran, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        modelSaran saran = modsaran.get(position);

        holder.tvNama.setText(saran.getNama());
        holder.tvNis.setText(saran.getNis());
        holder.tvIsi.setText(saran.getIsi());
    }

    @Override
    public int getItemCount() {
        return modsaran.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView tvNama, tvNis, tvIsi;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNama = itemView.findViewById(R.id.tvNamasar);
            tvNis = itemView.findViewById(R.id.tvNissar);
            tvIsi = itemView.findViewById(R.id.tvIsisar);
        }
    }
}
