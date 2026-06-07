package com.example.kueweh;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RiwayatPesananActivity extends AppCompatActivity {

    private RecyclerView rvRiwayat;
    private LinearLayout tvKosong;   // sekarang LinearLayout bukan TextView
    private ImageView btnBack;
    private BatchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );
        setContentView(R.layout.activity_riwayat_pesanan);

        rvRiwayat = findViewById(R.id.rvRiwayatPesanan);
        tvKosong = findViewById(R.id.tvRiwayatKosong);
        btnBack = findViewById(R.id.btnBackRiwayat);

        rvRiwayat.setLayoutManager(new LinearLayoutManager(this));
        btnBack.setOnClickListener(v -> finish());

        SharedPreferences sharedPref = getSharedPreferences("KueWehSession", Context.MODE_PRIVATE);
        String currentEmail = sharedPref.getString("userEmail", "");

        List<Pesanan> allPesanan = AppDatabase.getInstance(this).pesananDao().getPesananByUser(currentEmail);

        if (allPesanan.isEmpty()) {
            rvRiwayat.setVisibility(View.GONE);
            tvKosong.setVisibility(View.VISIBLE);
        } else {
            rvRiwayat.setVisibility(View.VISIBLE);
            tvKosong.setVisibility(View.GONE);

            Map<Long, List<Pesanan>> groupedMap = new LinkedHashMap<>();
            for (Pesanan p : allPesanan) {
                if (!groupedMap.containsKey(p.getTimestamp())) {
                    groupedMap.put(p.getTimestamp(), new ArrayList<>());
                }
                groupedMap.get(p.getTimestamp()).add(p);
            }

            List<BatchPesanan> batchList = new ArrayList<>();
            for (Map.Entry<Long, List<Pesanan>> entry : groupedMap.entrySet()) {
                batchList.add(new BatchPesanan(entry.getKey(), entry.getValue()));
            }

            adapter = new BatchAdapter(this, batchList);
            rvRiwayat.setAdapter(adapter);
        }
    }
}