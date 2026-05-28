package com.example.kueweh;

import android.content.Intent;
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
import java.util.List;

public class EditProductActivity extends AppCompatActivity {

    private ImageView imgPreview;
    private Button btnPilihFoto, btnSimpan;
    private EditText etNamaKue, etHargaKue;
    private Spinner spinnerKategori;

    private KueDao kueDao;
    private Kue kueDataLama;
    private String selectedImageUri = "";
    private int kueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        imgPreview = findViewById(R.id.imgEditPreview);
        btnPilihFoto = findViewById(R.id.btnEditPilihFoto);
        btnSimpan = findViewById(R.id.btnSimpanPerubahan);
        etNamaKue = findViewById(R.id.etEditNamaKue);
        etHargaKue = findViewById(R.id.etEditHargaKue);
        spinnerKategori = findViewById(R.id.spinnerEditKategori);

        kueDao = AppDatabase.getInstance(this).kueDao();

        // Panggil Formatter Otomatis untuk Harga
        setupRupiahFormatter(etHargaKue);

        // Setup Dropdown Spinner Kategori
        String[] daftarKategori = {"Cake", "Cookies", "Drink"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, daftarKategori);
        spinnerKategori.setAdapter(spinnerAdapter);

        // Tangkap ID Kue yang dikirim dari Adapter
        kueId = getIntent().getIntExtra("KUE_ID", -1);

        // Ambil data lama dari SQLite dan tampilkan di form
        new Thread(() -> {
            List<Kue> semuaKue = kueDao.getAllKue();
            for (Kue k : semuaKue) {
                if (k.getId() == kueId) {
                    kueDataLama = k;
                    break;
                }
            }

            if (kueDataLama != null) {
                runOnUiThread(() -> {
                    etNamaKue.setText(kueDataLama.getNama());
                    etHargaKue.setText(kueDataLama.getHarga());
                    selectedImageUri = kueDataLama.getImageUrl();
                    Glide.with(this).load(selectedImageUri).into(imgPreview);

                    int spinnerPosition = spinnerAdapter.getPosition(kueDataLama.getKategori());
                    spinnerKategori.setSelection(spinnerPosition);
                });
            }
        }).start();

        // Ambil Foto Baru dari Galeri
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

        // Proses Simpan Perubahan (Update)
        btnSimpan.setOnClickListener(v -> {
            String namaBaru = etNamaKue.getText().toString().trim();
            String hargaBaru = etHargaKue.getText().toString().trim();
            String kategoriBaru = spinnerKategori.getSelectedItem().toString();

            if (namaBaru.isEmpty()) {
                Toast.makeText(this, "Nama produk wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validasi Harga dengan Pop-Up
            String rawHarga = hargaBaru.replaceAll("[^0-9]", "");
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

            new Thread(() -> {
                kueDataLama.setNama(namaBaru);
                kueDataLama.setHarga(hargaBaru);
                kueDataLama.setKategori(kategoriBaru);
                kueDataLama.setImageUrl(selectedImageUri);

                kueDao.updateKue(kueDataLama);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Perubahan berhasil disimpan!", Toast.LENGTH_SHORT).show();
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