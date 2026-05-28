package com.example.kueweh;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Menyembunyikan Action Bar (Judul atas) agar Splash Screen tampil penuh
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        int waktuLoading = 2500;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Setelah 2 detik, otomatis pindah ke LoginActivity
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);

            // Hancurkan SplashActivity agar user tidak bisa kembali ke halaman ini saat menekan tombol "Back"
            finish();
        }, waktuLoading);
    }
}