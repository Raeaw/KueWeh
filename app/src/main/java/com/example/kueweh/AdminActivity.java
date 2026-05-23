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
        // Ambil data langsung dari SQLite
        List<Kue> daftarKue = AppDatabase.getInstance(this).kueDao().getAllKue();
        adapter = new KueAdapter(this, daftarKue);
        rvAdminKue.setAdapter(adapter);
    }
}