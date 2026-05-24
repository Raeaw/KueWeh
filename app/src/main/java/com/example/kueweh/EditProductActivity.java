package com.example.kueweh;

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

        // 1. Setup Dropdown Spinner Kategori
        String[] daftarKategori = {"Cake", "Cookies", "Drink"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, daftarKategori);
        spinnerKategori.setAdapter(spinnerAdapter);

        // 2. Tangkap ID Kue yang dikirim dari Adapter
        kueId = getIntent().getIntExtra("KUE_ID", -1);

        // 3. Ambil data lama dari SQLite dan tampilkan di form
        new Thread(() -> {
            // Kita cari object Kue yang memiliki ID cocok
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

                    // Set Spinner agar otomatis memilih kategori lama
                    int spinnerPosition = spinnerAdapter.getPosition(kueDataLama.getKategori());
                    spinnerKategori.setSelection(spinnerPosition);
                });
            }
        }).start();

        // Ambil Foto Baru dari Galeri (Opsional)
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

        // 4. Proses Simpan Perubahan (Update)
        btnSimpan.setOnClickListener(v -> {
            String namaBaru = etNamaKue.getText().toString().trim();
            String hargaBaru = etHargaKue.getText().toString().trim();
            String kategoriBaru = spinnerKategori.getSelectedItem().toString();

            if (namaBaru.isEmpty() || hargaBaru.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                // Update value objek data lama dengan data baru dari form
                kueDataLama.setNama(namaBaru);
                kueDataLama.setHarga(hargaBaru);
                kueDataLama.setKategori(kategoriBaru);
                kueDataLama.setImageUrl(selectedImageUri);

                // Eksekusi Update ke SQLite Room
                kueDao.updateKue(kueDataLama);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Perubahan berhasil disimpan!", Toast.LENGTH_SHORT).show();
                    finish(); // Kembali ke Dashboard Admin
                });
            }).start();
        });
    }
}