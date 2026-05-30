package com.example.kueweh;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Jangan lupa import untuk List, ArrayList, Map, dan LinkedHashMap
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RiwayatPesananActivity extends AppCompatActivity {

    private RecyclerView rvRiwayat;
    private TextView tvKosong;
    private ImageView btnBack;

    // UBAH: Gunakan BatchAdapter, bukan PesananAdapter
    private BatchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_pesanan);

        rvRiwayat = findViewById(R.id.rvRiwayatPesanan);
        tvKosong = findViewById(R.id.tvRiwayatKosong);
        btnBack = findViewById(R.id.btnBackRiwayat);

        rvRiwayat.setLayoutManager(new LinearLayoutManager(this));

        // Aksi tombol kembali
        btnBack.setOnClickListener(v -> finish());

        // Ambil email user yang sedang login
        SharedPreferences sharedPref = getSharedPreferences("KueWehSession", Context.MODE_PRIVATE);
        String currentEmail = sharedPref.getString("userEmail", "");

        // Ambil SEMUA data riwayat pesanan dari SQLite (bentuknya masih rata/flat)
        List<Pesanan> allPesanan = AppDatabase.getInstance(this).pesananDao().getPesananByUser(currentEmail);

        // Cek apakah kosong
        if (allPesanan.isEmpty()) {
            rvRiwayat.setVisibility(View.GONE);
            tvKosong.setVisibility(View.VISIBLE);
        } else {
            rvRiwayat.setVisibility(View.VISIBLE);
            tvKosong.setVisibility(View.GONE);

            // --- LOGIKA GROUPING BERDASARKAN BATCH (TIMESTAMP) ---

            // 1. Kelompokkan item yang memiliki timestamp sama ke dalam Map
            Map<Long, List<Pesanan>> groupedMap = new LinkedHashMap<>();
            for (Pesanan p : allPesanan) {
                if (!groupedMap.containsKey(p.getTimestamp())) {
                    groupedMap.put(p.getTimestamp(), new ArrayList<>());
                }
                groupedMap.get(p.getTimestamp()).add(p);
            }

            // 2. Ubah Map tersebut menjadi List<BatchPesanan> agar bisa dibaca oleh RecyclerView
            List<BatchPesanan> batchList = new ArrayList<>();
            for (Map.Entry<Long, List<Pesanan>> entry : groupedMap.entrySet()) {
                batchList.add(new BatchPesanan(entry.getKey(), entry.getValue()));
            }

            // 3. Masukkan data yang sudah berkelompok ke BatchAdapter
            adapter = new BatchAdapter(this, batchList);
            rvRiwayat.setAdapter(adapter);
        }
    }
}