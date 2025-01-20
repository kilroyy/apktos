package com.example.apktos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.apktos.Adapter.AdapterAkun;
import com.example.apktos.model.modelAkun;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TampilAkun extends AppCompatActivity implements AdapterAkun.RefreshListener {

    private RecyclerView recyclerView;
    Button regi;
    private AdapterAkun userAdapter;
    private List<modelAkun> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampil_akun);

        recyclerView = findViewById(R.id.recyclerViewakun);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        regi = findViewById(R.id.regis);
        userList = new ArrayList<>();
        userAdapter = new AdapterAkun(this, userList, TampilAkun.this);
        recyclerView.setAdapter(userAdapter);
        loadUsersFromFirebase();

        regi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TampilAkun.this, TambahAkun.class);
                startActivity(intent);
            }
        });
    }

    private void loadUsersFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    modelAkun user = userSnapshot.getValue(modelAkun.class);
                    if (user != null) {
                        user.setUserId(userSnapshot.getKey()); // Set key sebagai userId
                        userList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TampilAkun.this, "Gagal memuat data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onRefresh() {
        // Panggil loadData() untuk merefresh RecyclerView
        loadUsersFromFirebase();
    }
}
