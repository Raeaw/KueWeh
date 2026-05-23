package com.example.kueweh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvKue;
    private KueDao kueDao;
    private TextView tvSemua, tvCake, tvCookies, tvDrink;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvKue = view.findViewById(R.id.rvKue);
        rvKue.setLayoutManager(new GridLayoutManager(getContext(), 2));

        tvSemua = view.findViewById(R.id.tvFilterSemua);
        tvCake = view.findViewById(R.id.tvFilterCake);
        tvCookies = view.findViewById(R.id.tvFilterCookies);
        tvDrink = view.findViewById(R.id.tvFilterDrink);

        kueDao = AppDatabase.getInstance(getContext()).kueDao();

        // Seeding Data Dummy awal (Sudah ditambahkan parameter Kategori di akhir)
        List<Kue> kueList = kueDao.getAllKue();
        if (kueList.isEmpty()) {
            kueDao.insertKue(new Kue("Chocolate Fudge Cake", "Rp 125.000", "4.8", "(124)", "https://juliemarieeats.com/wp-content/uploads/2023/08/Chocolate-Fudge-Cake-10-scaled.jpg", "Cake"));
            kueDao.insertKue(new Kue("Red Velvet Cake", "Rp 135.000", "4.9", "(156)", "https://t3.ftcdn.net/jpg/02/64/84/68/360_F_264846889_3FTwwhQItDUy95Wdeaf8Qg4YLiiLNvrG.jpg", "Cake"));
            kueDao.insertKue(new Kue("Tiramisu Cake", "Rp 145.000", "4.7", "(109)", "https://www.jiffymix.com/wp-content/uploads/2020/06/Tiramisu-Cake-2.jpg", "Cake"));
            kueDao.insertKue(new Kue("Chocolate Chip Cookies", "Rp 45.000", "4.6", "(302)", "https://images.aws.nestle.recipes/resized/5b069c3ed2feea79377014f6766fcd49_Original_NTH_Chocolate_Chip_Cookie_1080_850.jpg", "Cookies"));
            kueDao.insertKue(new Kue("Matcha Latte", "Rp 35.000", "4.7", "(145)", "https://thumbs.dreamstime.com/b/matcha-latte-art-heart-shape-top-wooden-table-some-gr-green-tea-powder-tools-tea-making-japanese-style-87708022.jpg", "Drink"));
            kueList = kueDao.getAllKue();
        }

        tampilkanData(kueList);

        // EVENT KLIK FILTER
        tvSemua.setOnClickListener(v -> {
            ubahWarnaFilter(tvSemua);
            tampilkanData(kueDao.getAllKue());
        });

        tvCake.setOnClickListener(v -> {
            ubahWarnaFilter(tvCake);
            tampilkanData(kueDao.getKueByKategori("Cake"));
        });

        tvCookies.setOnClickListener(v -> {
            ubahWarnaFilter(tvCookies);
            tampilkanData(kueDao.getKueByKategori("Cookies"));
        });

        tvDrink.setOnClickListener(v -> {
            ubahWarnaFilter(tvDrink);
            tampilkanData(kueDao.getKueByKategori("Drink"));
        });

        return view;
    }

    private void tampilkanData(List<Kue> list) {
        KueAdapter adapter = new KueAdapter(getContext(), list);
        rvKue.setAdapter(adapter);
    }

    // Fungsi untuk mengubah style tombol yang sedang aktif secara visual
    private void ubahWarnaFilter(TextView tombolAktif) {
        // Set semua tombol ke mode tidak aktif dulu
        TextView[] semuaTombol = {tvSemua, tvCake, tvCookies, tvDrink};
        for (TextView tv : semuaTombol) {
            tv.setBackgroundResource(R.drawable.bg_chip_inactive);
            tv.setTextColor(android.graphics.Color.parseColor("#757575"));
            tv.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
        // Set tombol yang diklik menjadi aktif (Orange)
        tombolAktif.setBackgroundResource(R.drawable.bg_chip_active);
        tombolAktif.setTextColor(android.graphics.Color.WHITE);
        tombolAktif.setTypeface(null, android.graphics.Typeface.BOLD);
    }
}