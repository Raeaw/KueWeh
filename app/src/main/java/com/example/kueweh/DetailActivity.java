package com.example.kueweh;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {

    private ImageView imgKue, btnBack;
    private TextView tvNama, tvKategori, tvRating, tvHarga;
    private Button btnPesan;
    private KueDao kueDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imgKue = findViewById(R.id.imgDetailKue);
        btnBack = findViewById(R.id.btnBack);
        tvNama = findViewById(R.id.tvDetailNama);
        tvKategori = findViewById(R.id.tvDetailKategori);
        tvRating = findViewById(R.id.tvDetailRating);
        tvHarga = findViewById(R.id.tvDetailHarga);
        btnPesan = findViewById(R.id.btnPesanSekarang);

        kueDao = AppDatabase.getInstance(this).kueDao();

        // 1. Ambil ID yang dikirim dari HomeFragment
        int kueId = getIntent().getIntExtra("KUE_ID", -1);

        // 2. Ambil data spesifik dari Database SQLite
        new Thread(() -> {
            Kue kue = kueDao.getKueById(kueId);

            if (kue != null) {
                runOnUiThread(() -> {
                    // 3. Masukkan data ke komponen UI
                    tvNama.setText(kue.getNama());
                    tvHarga.setText(kue.getHarga());
                    tvKategori.setText(kue.getKategori());
                    tvRating.setText("⭐ " + kue.getRating() + " " + kue.getUlasan());
                    Glide.with(DetailActivity.this).load(kue.getImageUrl()).into(imgKue);
                });
            }
        }).start();

        // 4. Aksi Tombol Kembali (Back)
        btnBack.setOnClickListener(v -> finish()); // Menutup halaman ini dan kembali ke Home

        // 5. Aksi Tombol Pesan (Untuk saat ini kita beri efek Toast dulu)
        btnPesan.setOnClickListener(v -> {
            // Ambil email user yang sedang aktif login dari SharedPreferences
            android.content.SharedPreferences sharedPref = getSharedPreferences("KueWehSession", MODE_PRIVATE);
            String currentEmail = sharedPref.getString("userEmail", "");

            new Thread(() -> {
                // Ambil data kue yang sedang ditampilkan saat ini
                Kue kue = kueDao.getKueById(kueId);

                if (kue != null) {
                    // Buat objek transaksi pesanan baru
                    Pesanan pesananBaru = new Pesanan(currentEmail, kue.getNama(), kue.getHarga(), kue.getImageUrl());

                    // Simpan ke SQLite
                    AppDatabase.getInstance(DetailActivity.this).pesananDao().insertPesanan(pesananBaru);

                    runOnUiThread(() -> {
                        Toast.makeText(DetailActivity.this, "Kue berhasil dipesan! Cek di menu Pesanan.", Toast.LENGTH_LONG).show();
                        finish(); // Tutup halaman detail dan kembali ke Home
                    });
                }
            }).start();
        });
    }
}