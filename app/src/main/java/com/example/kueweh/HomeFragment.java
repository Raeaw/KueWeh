package com.example.kueweh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView rvKue = view.findViewById(R.id.rvKue);
        rvKue.setLayoutManager(new GridLayoutManager(getContext(), 2));

        KueDao kueDao = AppDatabase.getInstance(getContext()).kueDao();

        // 1. Ambil data dari database lokal
        List<Kue> kueList = kueDao.getAllKue();

        // 2. LOGIKA SEEDING (Otomatis memasukkan Data Dummy jika database kosong)
        if (kueList.isEmpty()) {
            kueDao.insertKue(new Kue("Chocolate Fudge Cake", "Rp 125.000", "4.8", "(124)", "https://juliemarieeats.com/wp-content/uploads/2023/08/Chocolate-Fudge-Cake-10-scaled.jpg"));
            kueDao.insertKue(new Kue("Red Velvet Cake", "Rp 135.000", "4.9", "(156)", "https://t3.ftcdn.net/jpg/02/64/84/68/360_F_264846889_3FTwwhQItDUy95Wdeaf8Qg4YLiiLNvrG.jpg"));
            kueDao.insertKue(new Kue("Tiramisu Cake", "Rp 145.000", "4.7", "(109)", "https://www.jiffymix.com/wp-content/uploads/2020/06/Tiramisu-Cake-2.jpg"));
            kueDao.insertKue(new Kue("Chocolate Chip Cookies", "Rp 45.000", "4.6", "(302)", "https://images.aws.nestle.recipes/resized/5b069c3ed2feea79377014f6766fcd49_Original_NTH_Chocolate_Chip_Cookie_1080_850.jpg"));
            kueDao.insertKue(new Kue("Matcha Latte", "Rp 35.000", "4.7", "(145)", "https://thumbs.dreamstime.com/b/matcha-latte-art-heart-shape-top-wooden-table-some-gr-green-tea-powder-tools-tea-making-japanese-style-87708022.jpg"));

            // Setelah dimasukkan, ambil ulang datanya agar tidak kosong di layar
            kueList = kueDao.getAllKue();
        }

        // 3. Masukkan data ke Adapter
        KueAdapter adapter = new KueAdapter(getContext(), kueList);
        rvKue.setAdapter(adapter);

        return view;
    }
}