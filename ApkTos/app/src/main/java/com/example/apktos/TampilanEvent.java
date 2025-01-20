package com.example.apktos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apktos.Adapter.AdapterEvent;
import com.example.apktos.model.modelEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TampilanEvent extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterEvent adapter;
    private List<modelEvent> modelEventList;
    Button but;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampilanevent);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String status = getIntent().getStringExtra("role");
        modelEventList = new ArrayList<>();
        adapter = new AdapterEvent(this, modelEventList, status);
        recyclerView.setAdapter(adapter);
        but = findViewById(R.id.buttmbhlprn);
        if ("user".equals(status)) {
            but.setVisibility(View.GONE); // Tombol akan menghilang sepenuhnya
        } else {
            but.setVisibility(View.VISIBLE); // Tombol akan muncul
        }
        but.setOnClickListener(v -> {
            Intent intent = new Intent(TampilanEvent.this, TambahEvent.class);
            startActivity(intent);
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("datalpj");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                modelEventList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    modelEvent modelEvent = dataSnapshot.getValue(modelEvent.class);
                    if (modelEvent != null) {
                        modelEvent.setLaporanKey(dataSnapshot.getKey()); // Set key di model modelEvent
                        modelEventList.add(modelEvent);
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
