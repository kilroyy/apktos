package com.example.apktos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.apktos.Adapter.AdapterLpj;
import com.example.apktos.model.modelLpj;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TampilanLpj extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdapterLpj adapter;
    private List<modelLpj> modelLpjList;
    Button but;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampilan_lpj);

        recyclerView = findViewById(R.id.recyclerViewLpj);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String status = getIntent().getStringExtra("role");
        modelLpjList = new ArrayList<>();
        adapter = new AdapterLpj(this, modelLpjList, status);
        recyclerView.setAdapter(adapter);

        but = findViewById(R.id.buttmblpj);
        if ("user".equals(status)) {
            but.setVisibility(View.GONE); // Tombol akan menghilang sepenuhnya
        } else {
            but.setVisibility(View.VISIBLE); // Tombol akan muncul
        }
        but.setOnClickListener(v -> {
            Intent intent = new Intent(TampilanLpj.this, TambahLpj.class);
            startActivity(intent);
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("laporanlpj");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                modelLpjList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    modelLpj modelLpj = dataSnapshot.getValue(modelLpj.class);
                    if (modelLpj != null) {
                        modelLpj.setLaporanKey(dataSnapshot.getKey()); // Set key di model modelEvent
                        modelLpjList.add(modelLpj);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
    }
}