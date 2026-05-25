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

        // TAMBAHAN: Hubungkan ke layout XML
        tvPersonalRating = findViewById(R.id.tvPersonalRating);

        kueDao = AppDatabase.getInstance(this).kueDao();

        // 1. Ambil email user aktif untuk mencari rating personalnya dan transaksi keranjang
        SharedPreferences sharedPref = getSharedPreferences("KueWehSession", MODE_PRIVATE);
        String currentEmail = sharedPref.getString("userEmail", "");

        // 2. Ambil ID yang dikirim dari HomeFragment
        int kueId = getIntent().getIntExtra("KUE_ID", -1);

        // 3. Ambil data spesifik dari Database SQLite
        new Thread(() -> {
            Kue kue = kueDao.getKueById(kueId);

            if (kue != null) {
                // Minta SQLite menghitung rata-rata rating personal dari user ini untuk kue ini
                float personalAvg = AppDatabase.getInstance(this).pesananDao().getPersonalAverageRating(kue.getNama(), currentEmail);

                runOnUiThread(() -> {
                    // Masukkan data ke komponen UI
                    tvNama.setText(kue.getNama());
                    tvHarga.setText(kue.getHarga());
                    tvKategori.setText(kue.getKategori());

                    // Menampilkan Rating Global + Jumlah Ulasan
                    tvRating.setText("⭐ " + kue.getRating() + " " + kue.getUlasan());
                    Glide.with(DetailActivity.this).load(kue.getImageUrl()).into(imgKue);

                    // Menampilkan Rating Personal
                    if (personalAvg > 0) {
                        tvPersonalRating.setText(String.format(Locale.US, "Rating Anda: ⭐ %.1f", personalAvg));
                    } else {
                        tvPersonalRating.setText("Rating Anda: Belum memberikan ulasan");
                    }
                });
            }
        }).start();

        // 4. Aksi Tombol Kembali (Back)
        btnBack.setOnClickListener(v -> finish());

        // 5. Aksi Tombol Pesan (Keranjang)
        btnPesan.setText("Tambah ke Keranjang");

        btnPesan.setOnClickListener(v -> {
            new Thread(() -> {
                Kue kue = kueDao.getKueById(kueId);

                if (kue != null) {
                    KeranjangDao keranjangDao = AppDatabase.getInstance(DetailActivity.this).keranjangDao();

                    // Cek apakah item ini sudah ada di keranjang
                    Keranjang itemAda = keranjangDao.cekItemKeranjang(currentEmail, kue.getNama());

                    if (itemAda != null) {
                        // Jika sudah ada, tambahkan kuantitasnya
                        itemAda.setJumlah(itemAda.getJumlah() + 1);
                        keranjangDao.updateKeranjang(itemAda);
                    } else {
                        // Jika belum ada, masukkan sebagai item baru dengan jumlah 1
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