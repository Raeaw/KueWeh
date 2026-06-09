package com.example.kueweh;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.bumptech.glide.Glide;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ImageView imgKue, btnBack, imgDetailFavoriteHeart;
    private TextView tvNama, tvKategori, tvRating, tvHarga, tvPersonalRating;
    private Button btnPesan;
    private CardView btnDetailFavoriteLayout;
    private KueDao kueDao;
    private FavoritDao favDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Membuat layar menembus status bar (Edge-to-Edge)
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );
        setContentView(R.layout.activity_detail);

        imgKue = findViewById(R.id.imgDetailKue);
        btnBack = findViewById(R.id.btnBack);
        tvNama = findViewById(R.id.tvDetailNama);
        tvKategori = findViewById(R.id.tvDetailKategori);
        tvRating = findViewById(R.id.tvDetailRating);
        tvHarga = findViewById(R.id.tvDetailHarga);
        btnPesan = findViewById(R.id.btnPesanSekarang);
        tvPersonalRating = findViewById(R.id.tvPersonalRating);

        // Komponen Favorit Baru
        btnDetailFavoriteLayout = findViewById(R.id.btnDetailFavoriteLayout);
        imgDetailFavoriteHeart = findViewById(R.id.imgDetailFavoriteHeart);

        AppDatabase db = AppDatabase.getInstance(this);
        kueDao = db.kueDao();
        favDao = db.favoritDao();

        SharedPreferences sharedPref = getSharedPreferences("KueWehSession", MODE_PRIVATE);
        String currentEmail = sharedPref.getString("userEmail", "");
        int kueId = getIntent().getIntExtra("KUE_ID", -1);

        new Thread(() -> {
            Kue kue = kueDao.getKueById(kueId);

            if (kue != null) {
                float personalAvg = db.pesananDao().getPersonalAverageRating(kue.getNama(), currentEmail);
                int orderCount = db.pesananDao().getOrderCountPerUser(kue.getNama(), currentEmail);

                // Cek status favorit dari database
                Favorit isFav = favDao.cekFavorit(currentEmail, kue.getNama());
                boolean isFavorited = (isFav != null);

                runOnUiThread(() -> {
                    tvNama.setText(kue.getNama());
                    tvHarga.setText(kue.getHarga());
                    tvKategori.setText(kue.getKategori());
                    tvRating.setText("⭐ " + kue.getRating() + " " + kue.getUlasan());
                    Glide.with(DetailActivity.this).load(kue.getImageUrl()).into(imgKue);

                    if (orderCount > 0) {
                        String ratingText = (personalAvg > 0) ? String.format(Locale.US, "⭐ %.1f", personalAvg) : "Belum dinilai";
                        tvPersonalRating.setText("Rating Anda: " + ratingText + " (" + orderCount + "x beli)");
                    } else {
                        tvPersonalRating.setText("Rating Anda: Belum pernah memesan ini");
                    }

                    // Set warna awal hati (Merah jika sudah dilike, Abu-abu jika belum)
                    imgDetailFavoriteHeart.setColorFilter(android.graphics.Color.parseColor(isFavorited ? "#FF3B30" : "#BDBDBD"));

                    // Aksi klik tombol Favorit
                    btnDetailFavoriteLayout.setOnClickListener(v -> {
                        new Thread(() -> {
                            Favorit cekLagi = favDao.cekFavorit(currentEmail, kue.getNama());
                            if (cekLagi != null) {
                                favDao.hapusFavorit(currentEmail, kue.getNama()); // Hapus dari Favorit
                                runOnUiThread(() -> imgDetailFavoriteHeart.setColorFilter(android.graphics.Color.parseColor("#BDBDBD")));
                            } else {
                                favDao.insertFavorit(new Favorit(currentEmail, kue.getNama())); // Tambah ke Favorit
                                runOnUiThread(() -> imgDetailFavoriteHeart.setColorFilter(android.graphics.Color.parseColor("#FF3B30")));
                            }
                        }).start();
                    });
                });
            }
        }).start();

        // Aksi Tombol Kembali
        btnBack.setOnClickListener(v -> finish());

        // Aksi Tambah ke Keranjang
//        btnPesan.setText("Tambah ke Keranjang");
        btnPesan.setOnClickListener(v -> {
            new Thread(() -> {
                Kue kue = kueDao.getKueById(kueId);
                if (kue != null) {
                    KeranjangDao keranjangDao = db.keranjangDao();
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