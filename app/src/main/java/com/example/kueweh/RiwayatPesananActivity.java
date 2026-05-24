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
import java.util.List;

public class RiwayatPesananActivity extends AppCompatActivity {

    private RecyclerView rvRiwayat;
    private TextView tvKosong;
    private ImageView btnBack;
    private PesananAdapter adapter;

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

        // Ambil data riwayat pesanan dari SQLite
        List<Pesanan> riwayatList = AppDatabase.getInstance(this).pesananDao().getPesananByUser(currentEmail);

        // Tampilkan data ke RecyclerView atau tampilkan teks "Kosong"
        if (riwayatList.isEmpty()) {
            rvRiwayat.setVisibility(View.GONE);
            tvKosong.setVisibility(View.VISIBLE);
        } else {
            rvRiwayat.setVisibility(View.VISIBLE);
            tvKosong.setVisibility(View.GONE);

            adapter = new PesananAdapter(this, riwayatList);
            rvRiwayat.setAdapter(adapter);
        }
    }
}