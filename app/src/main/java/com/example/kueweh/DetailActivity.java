package com.example.kueweh;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ImageView imgKue, btnBack;
    private TextView tvNama, tvKategori, tvRating, tvHarga, tvPersonalRating;
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
        tvPersonalRating = findViewById(R.id.tvPersonalRating);

        kueDao = AppDatabase.getInstance(this).kueDao();

        SharedPreferences sharedPref = getSharedPreferences("KueWehSession", MODE_PRIVATE);
        String currentEmail = sharedPref.getString("userEmail", "");
        int kueId = getIntent().getIntExtra("KUE_ID", -1);

        new Thread(() -> {
            Kue kue = kueDao.getKueById(kueId);

            if (kue != null) {
                // 1. Ambil Rata-rata Rating Personal
                float personalAvg = AppDatabase.getInstance(this).pesananDao().getPersonalAverageRating(kue.getNama(), currentEmail);

                // 2. Ambil Jumlah Pembelian oleh User ini
                int orderCount = AppDatabase.getInstance(this).pesananDao().getOrderCountPerUser(kue.getNama(), currentEmail);

                runOnUiThread(() -> {
                    tvNama.setText(kue.getNama());
                    tvHarga.setText(kue.getHarga());
                    tvKategori.setText(kue.getKategori());
                    tvRating.setText("⭐ " + kue.getRating() + " " + kue.getUlasan());
                    Glide.with(DetailActivity.this).load(kue.getImageUrl()).into(imgKue);

                    // 3. Tampilkan Logika Rating + Jumlah Beli
                    if (orderCount > 0) {
                        String ratingText = (personalAvg > 0) ? String.format(Locale.US, "⭐ %.1f", personalAvg) : "Belum dinilai";
                        tvPersonalRating.setText("Rating Anda: " + ratingText + " (" + orderCount + "x beli)");
                    } else {
                        tvPersonalRating.setText("Rating Anda: Belum pernah memesan ini");
                    }
                });
            }
        }).start();

        // Aksi Tombol Kembali
        btnBack.setOnClickListener(v -> finish());

        // Aksi Tambah ke Keranjang
        btnPesan.setText("Tambah ke Keranjang");
        btnPesan.setOnClickListener(v -> {
            new Thread(() -> {
                Kue kue = kueDao.getKueById(kueId);
                if (kue != null) {
                    KeranjangDao keranjangDao = AppDatabase.getInstance(DetailActivity.this).keranjangDao();
                    Keranjang itemAda = keranjangDao.cekItemKeranjang(currentEmail, kue.getNama());

                    if (itemAda != null) {
                        itemAda.setJumlah(itemAda.getJumlah() + 1);
                        keranjangDao.updateKeranjang(itemAda);
                    } else {
                        Keranjang itemBaru = new Keranjang(currentEmail, kue.getNama(), kue.getHarga(), kue.getImageUrl(), 1);
                        keranjangDao.insertKeranjang(itemBaru);
                    }

                    runOnUiThread(() -> {
                        Toast.makeText(DetailActivity.this, "Berhasil masuk ke Keranjang!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }).start();
        });
    }
}