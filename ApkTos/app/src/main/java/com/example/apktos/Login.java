package com.example.apktos;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLoggedIn()) {
            SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
            String role = sharedPreferences.getString("role", "");
            navigateToRole(role); // Navigasi langsung ke halaman berdasarkan role
            finish();
            return;
        }
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean isValid = false;

                for (DataSnapshot userSnapshot : task.getResult().getChildren()) {
                    String dbEmail = userSnapshot.child("email").getValue(String.class);
                    String dbPassword = userSnapshot.child("password").getValue(String.class);
                    String role = userSnapshot.child("level").getValue(String.class);
                    String nama = userSnapshot.child("nama").getValue(String.class);

                    if (email.equals(dbEmail) && password.equals(dbPassword)) {
                        isValid = true;

                        // Simpan sesi login
                        saveLoginSession(email, role, nama);

                        // Navigasi ke halaman sesuai role
                        navigateToRole(role);
                        break;
                    }
                }

                if (!isValid) {
                    Toast.makeText(Login.this, "NIS atau password salah!", Toast.LENGTH_SHORT).show();
                }
            } else {
                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                Toast.makeText(Login.this, "Database error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveLoginSession(String email, String role, String nama) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("email", email);
        editor.putString("role", role);
        editor.putString("nama", nama);
        editor.apply();
    }

    private boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginSession", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void navigateToRole(String role) {
        if ("admin".equals(role)) {
            startActivity(new Intent(Login.this, Admin.class));
        } else if ("user".equals(role)) {
            startActivity(new Intent(Login.this, Admin.class));
        } else {
            Toast.makeText(Login.this, "Role tidak valid!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}