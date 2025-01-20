package com.example.apktos.Adapter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.apktos.Login;
import com.example.apktos.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Handler hand = new Handler();
        hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splash.this, Login.class));
                finish();
            }
        }, 4000l);

    }
}