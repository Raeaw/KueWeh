package com.example.kueweh;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class AddProductActivity extends AppCompatActivity {

    private ImageView imgPreview;
    private Button btnPilihFoto, btnSimpan;
    private EditText etNamaKue, etHargaKue;
    private Spinner spinnerKategori; // Deklarasikan Spinner
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
        spinnerKategori = findViewById(R.id.spinnerKategori); // Inisialisasi

        // 1. BUAT DAFTAR PILIHAN UNTUK SPINNER
        String[] daftarKategori = {"Cake", "Cookies", "Drink"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, daftarKategori);
        spinnerKategori.setAdapter(spinnerAdapter);

        // Ambil Foto dari Galeri
        ActivityResultLauncher<String> ambilFotoDariGaleri = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri.toString();
                        Glide.with(this).load(uri).into(imgPreview);
                    }
                }
        );

        btnPilihFoto.setOnClickListener(v -> ambilFotoDariGaleri.launch("image/*"));

        // Simpan Produk ke SQLite
        btnSimpan.setOnClickListener(v -> {
            String nama = etNamaKue.getText().toString().trim();
            String harga = etHargaKue.getText().toString().trim();

            // 2. AMBIL KATEGORI YANG DIPILIH OLEH ADMIN
            String kategoriDipilih = spinnerKategori.getSelectedItem().toString();

            if (nama.isEmpty() || harga.isEmpty()) {
                Toast.makeText(this, "Nama dan harga wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedImageUri.isEmpty()) {
                selectedImageUri = "https://via.placeholder.com/150";
            }

            new Thread(() -> {
                // 3. MASUKKAN KATEGORI YANG DIPILIH KE DALAM CONSTRUCTOR KUE
                Kue kueBaru = new Kue(nama, harga, "0.0", "(0)", selectedImageUri, kategoriDipilih);
                AppDatabase.getInstance(AddProductActivity.this).kueDao().insertKue(kueBaru);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Produk berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();
        });
    }
}