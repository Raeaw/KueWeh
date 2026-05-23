package com.example.kueweh;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class AddProductActivity extends AppCompatActivity {

    private ImageView imgPreview;
    private Button btnPilihFoto, btnSimpan;
    private EditText etNamaKue, etHargaKue;

    // Variabel untuk menyimpan "alamat" foto di memori HP
    private String selectedImageUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        imgPreview = findViewById(R.id.imgPreview);
        btnPilihFoto = findViewById(R.id.btnPilihFoto);
        btnSimpan = findViewById(R.id.btnSimpanProduk);
        etNamaKue = findViewById(R.id.etNamaKue);
        etHargaKue = findViewById(R.id.etHargaKue);

        // Logika modern untuk membuka Galeri HP
        ActivityResultLauncher<String> ambilFotoDariGaleri = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // Simpan alamat URI dalam bentuk String ke variabel
                        selectedImageUri = uri.toString();

                        // Tampilkan foto di layar menggunakan Glide
                        Glide.with(this).load(uri).into(imgPreview);
                    }
                }
        );

        // Saat tombol "Pilih Foto" ditekan, buka folder gambar (image/*)
        btnPilihFoto.setOnClickListener(v -> ambilFotoDariGaleri.launch("image/*"));

        // Saat tombol "Simpan" ditekan, masukkan ke SQLite
        btnSimpan.setOnClickListener(v -> {
            String nama = etNamaKue.getText().toString();
            String harga = etHargaKue.getText().toString();

            if (nama.isEmpty() || harga.isEmpty()) {
                Toast.makeText(this, "Nama dan harga wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Jika foto kosong, kita beri nilai default / string kosong
            if (selectedImageUri.isEmpty()) {
                selectedImageUri = "https://via.placeholder.com/150"; // Gambar darurat
            }

            // Insert ke database di thread terpisah (atau main thread karena sudah di-allow)
            new Thread(() -> {
                // Rating dan Ulasan kita beri default 0 untuk produk baru
                Kue kueBaru = new Kue(nama, harga, "0.0", "(0)", selectedImageUri);
                AppDatabase.getInstance(AddProductActivity.this).kueDao().insertKue(kueBaru);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Produk berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                    finish(); // Tutup halaman form ini dan kembali ke Admin Dashboard
                });
            }).start();
        });
    }
}