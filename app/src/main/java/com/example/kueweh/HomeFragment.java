package com.example.kueweh;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvKue;
    private LinearLayout emptyStateHome;  // tambahan empty state
    private KueDao kueDao;
    private TextView tvSemua, tvCake, tvCookies, tvDrink;
    private EditText etSearch;
    private KueAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvKue = view.findViewById(R.id.rvKue);
        emptyStateHome = view.findViewById(R.id.emptyStateHome);
        rvKue.setLayoutManager(new GridLayoutManager(getContext(), 2));

        tvSemua = view.findViewById(R.id.tvFilterSemua);
        tvCake = view.findViewById(R.id.tvFilterCake);
        tvCookies = view.findViewById(R.id.tvFilterCookies);
        tvDrink = view.findViewById(R.id.tvFilterDrink);
        etSearch = view.findViewById(R.id.etSearch);

        kueDao = AppDatabase.getInstance(getContext()).kueDao();

        // Seed data awal
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

        // Search real-time
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                if (!keyword.isEmpty()) {
                    ubahWarnaFilter(tvSemua);
                    tampilkanData(kueDao.searchKue(keyword));
                } else {
                    tampilkanData(kueDao.getAllKue());
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Filter chips
        tvSemua.setOnClickListener(v -> {
            ubahWarnaFilter(tvSemua);
            etSearch.setText("");
            tampilkanData(kueDao.getAllKue());
        });
        tvCake.setOnClickListener(v -> {
            ubahWarnaFilter(tvCake);
            etSearch.setText("");
            tampilkanData(kueDao.getKueByKategori("Cake"));
        });
        tvCookies.setOnClickListener(v -> {
            ubahWarnaFilter(tvCookies);
            etSearch.setText("");
            tampilkanData(kueDao.getKueByKategori("Cookies"));
        });
        tvDrink.setOnClickListener(v -> {
            ubahWarnaFilter(tvDrink);
            etSearch.setText("");
            tampilkanData(kueDao.getKueByKategori("Drink"));
        });

        return view;
    }

    private void tampilkanData(List<Kue> list) {
        if (list.isEmpty()) {
            // Tampilkan empty state
            rvKue.setVisibility(View.GONE);
            emptyStateHome.setVisibility(View.VISIBLE);
        } else {
            rvKue.setVisibility(View.VISIBLE);
            emptyStateHome.setVisibility(View.GONE);
            adapter = new KueAdapter(getContext(), list);
            rvKue.setAdapter(adapter);
        }
    }

    private void ubahWarnaFilter(TextView tombolAktif) {
        TextView[] semuaTombol = {tvSemua, tvCake, tvCookies, tvDrink};
        for (TextView tv : semuaTombol) {
            tv.setBackgroundResource(R.drawable.bg_chip_inactive);
            tv.setTextColor(android.graphics.Color.parseColor("#757575"));
            tv.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
        tombolAktif.setBackgroundResource(R.drawable.bg_chip_active);
        tombolAktif.setTextColor(android.graphics.Color.WHITE);
        tombolAktif.setTypeface(null, android.graphics.Typeface.BOLD);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) adapter.notifyDataSetChanged();
    }
}