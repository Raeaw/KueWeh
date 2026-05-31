package com.example.kueweh;

import android.content.Intent;
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
    private Spinner spinnerKategori;
    private String selectedImageUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Membuat layar menembus status bar (Edge-to-Edge)
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );
        setContentView(R.layout.activity_add_product);

        imgPreview = findViewById(R.id.imgPreview);
        btnPilihFoto = findViewById(R.id.btnPilihFoto);
        btnSimpan = findViewById(R.id.btnSimpanProduk);
        etNamaKue = findViewById(R.id.etNamaKue);
        etHargaKue = findViewById(R.id.etHargaKue);
        spinnerKategori = findViewById(R.id.spinnerKategori);

        // Panggil Formatter Otomatis untuk Harga
        setupRupiahFormatter(etHargaKue);

        // Buat Daftar Pilihan untuk Spinner
        String[] daftarKategori = {"Cake", "Cookies", "Drink"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, daftarKategori);
        spinnerKategori.setAdapter(spinnerAdapter);

        // Ambil Foto dari Galeri
        // KODE BARU: Menggunakan OpenDocument dan Minta Izin Permanen
        ActivityResultLauncher<String[]> ambilFotoDariGaleri = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        // KUNCI IZIN AKSES SECARA PERMANEN
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        selectedImageUri = uri.toString();
                        Glide.with(this).load(uri).into(imgPreview);
                    }
                }
        );

        // KODE BARU: Perhatikan penggunaan new String[]
        btnPilihFoto.setOnClickListener(v -> ambilFotoDariGaleri.launch(new String[]{"image/*"}));

        // Simpan Produk ke SQLite
        btnSimpan.setOnClickListener(v -> {
            String nama = etNamaKue.getText().toString().trim();
            String harga = etHargaKue.getText().toString().trim();
            String kategoriDipilih = spinnerKategori.getSelectedItem().toString();

            if (nama.isEmpty()) {
                Toast.makeText(this, "Nama produk wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validasi Harga dengan Pop-Up
            String rawHarga = harga.replaceAll("[^0-9]", "");
            if (rawHarga.isEmpty()) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Harga Tidak Valid ⚠️")
                        .setMessage("Kolom harga tidak boleh kosong atau berisi huruf. Silakan masukkan angka harga dengan benar.")
                        .setPositiveButton("Perbaiki", (dialog, which) -> {
                            etHargaKue.requestFocus();
                        })
                        .show();
                return; // Hentikan proses simpan
            }

            if (selectedImageUri.isEmpty()) {
                selectedImageUri = "https://via.placeholder.com/150";
            }

            new Thread(() -> {
                // Harga yang masuk ke database sudah berwujud "Rp XX.XXX"
                Kue kueBaru = new Kue(nama, harga, "0.0", "(0)", selectedImageUri, kategoriDipilih);
                AppDatabase.getInstance(AddProductActivity.this).kueDao().insertKue(kueBaru);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Produk berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();
        });
    }

    // Fungsi TextWatcher untuk format Rupiah
    private void setupRupiahFormatter(android.widget.EditText editText) {
        editText.addTextChangedListener(new android.text.TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (!s.toString().equals(current)) {
                    editText.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^0-9]", "");

                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = java.text.NumberFormat.getNumberInstance(new java.util.Locale("id", "ID")).format(parsed);
                        current = "Rp " + formatted;
                    } else {
                        current = "";
                    }

                    editText.setText(current);
                    editText.setSelection(current.length());
                    editText.addTextChangedListener(this);
                }
            }
        });
    }
}