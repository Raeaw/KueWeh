package com.example.kueweh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class AdminProdukFragment extends Fragment {

    private RecyclerView rvAdminKue;
    private FloatingActionButton fabTambah;
    private KueAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Arahkan ke layout Fragment-nya
        View view = inflater.inflate(R.layout.fragment_admin_produk, container, false);

        // Inisialisasi komponen menggunakan 'view.findViewById'
        rvAdminKue = view.findViewById(R.id.rvAdminKue);
        fabTambah = view.findViewById(R.id.fabTambahProduk);
        ImageView btnLogoutAdmin = view.findViewById(R.id.btnLogoutAdmin);

        // Logika Logout
        btnLogoutAdmin.setOnClickListener(v -> {
            SharedPreferences sharedPref = requireActivity().getSharedPreferences("KueWehSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(getContext(), "Berhasil Keluar dari Admin", Toast.LENGTH_SHORT).show();

            // Arahkan kembali ke Login
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        // Atur RecyclerView menjadi Grid (sama seperti di Home)
        rvAdminKue.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Pindah ke halaman form saat tombol (+) ditekan
        fabTambah.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddProductActivity.class));
        });

        return view;
    }

    // Gunakan onResume agar daftar ter-refresh otomatis setelah Admin menambah kue baru
    @Override
    public void onResume() {
        super.onResume();
        loadProduk();
    }

    private void loadProduk() {
        if (getContext() != null) {
            List<Kue> daftarKue = AppDatabase.getInstance(getContext()).kueDao().getAllKue();
            // Parameter true di akhir agar mode Admin aktif
            adapter = new KueAdapter(getContext(), daftarKue, true);
            rvAdminKue.setAdapter(adapter);
        }
    }
}