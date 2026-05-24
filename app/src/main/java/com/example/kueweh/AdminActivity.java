package com.example.kueweh;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView rvAdminKue;
    private FloatingActionButton fabTambah;
    private KueAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        rvAdminKue = findViewById(R.id.rvAdminKue);
        fabTambah = findViewById(R.id.fabTambahProduk);

        android.widget.ImageView btnLogoutAdmin = findViewById(R.id.btnLogoutAdmin);

        btnLogoutAdmin.setOnClickListener(v -> {
            // Hapus data sesi admin dari SQLite / SharedPreferences
            android.content.SharedPreferences sharedPref = getSharedPreferences("KueWehSession", MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();

            android.widget.Toast.makeText(AdminActivity.this, "Berhasil Keluar dari Admin", android.widget.Toast.LENGTH_SHORT).show();

            // Arahkan kembali ke Login dan kunci agar tidak bisa di-back
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Atur RecyclerView menjadi Grid (sama seperti di Home)
        rvAdminKue.setLayoutManager(new GridLayoutManager(this, 2));

        // Pindah ke halaman form saat tombol (+) ditekan
        fabTambah.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, AddProductActivity.class));
        });
    }

    // Gunakan onResume agar daftar ter-refresh otomatis setelah Admin menambah kue baru
    @Override
    protected void onResume() {
        super.onResume();
        loadProduk();
    }

    private void loadProduk() {
        List<Kue> daftarKue = AppDatabase.getInstance(this).kueDao().getAllKue();
        // TAMBAHKAN PARAMETER true DI AKHIR AGAR MODE ADMIN AKTIF
        adapter = new KueAdapter(this, daftarKue, true);
        rvAdminKue.setAdapter(adapter);
    }
}