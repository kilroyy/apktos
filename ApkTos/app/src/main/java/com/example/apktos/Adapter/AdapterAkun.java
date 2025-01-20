package com.example.apktos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apktos.R;
import com.example.apktos.model.modelAkun;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdapterAkun extends RecyclerView.Adapter<AdapterAkun.UserViewHolder> {

    private Context context;
    private List<modelAkun> userList;
    private RefreshListener refreshListener;

    // Constructor dengan RefreshListener
    public AdapterAkun(Context context, List<modelAkun> userList, RefreshListener refreshListener) {
        this.context = context;
        this.userList = userList;
        this.refreshListener = refreshListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_akun, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        modelAkun user = userList.get(position);

        // Set data ke view
        holder.tvNama.setText("Nama: " + user.getNama());
        holder.tvEmail.setText("NIS/NIP: " + user.getEmail());
        holder.tvLevel.setText("Level: " + user.getLevel());
        holder.passw.setText("Password: " + user.getPassword());

        holder.btnHapus.setOnClickListener(v -> {
            if (user != null && user.getUserId() != null) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                databaseReference.child(user.getUserId()).removeValue()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                                userList.remove(position); // Hapus dari daftar
                                notifyItemRemoved(position); // Hapus elemen yang dihapus
                                notifyItemRangeChanged(position, userList.size()); // Perbarui posisi item di bawahnya

                                // Panggil refreshListener
                                if (refreshListener != null) {
                                    refreshListener.onRefresh();
                                }
                            } else {
                                Toast.makeText(context, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(context, "Data tidak valid atau UserId kosong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    // ViewHolder
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvEmail, tvLevel, passw;
        ImageButton btnHapus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaakn);
            tvEmail = itemView.findViewById(R.id.tvEmailakn);
            tvLevel = itemView.findViewById(R.id.tvLevelakn);
            passw = itemView.findViewById(R.id.tvpsakn);
            btnHapus = itemView.findViewById(R.id.btnHapus); // ImageButton
        }
    }

    // Interface untuk RefreshListener
    public interface RefreshListener {
        void onRefresh();
    }
}
