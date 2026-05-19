package com.example.kueweh;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvKue;
    private KueAdapter adapter;
    private List<Kue> kueList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvKue = findViewById(R.id.rvKue);
        kueList = new ArrayList<>();

        // 1. Menyiapkan Data Dummy (Mirip seperti JSON di Figma)
        // Gunakan URL gambar dummy atau biarkan kosong jika ingin melihat struktur layoutnya dulu
        kueList.add(new Kue("Chocolate Fudge Cake", "Rp 125.000", "4.8", "(124)", "https://juliemarieeats.com/wp-content/uploads/2023/08/Chocolate-Fudge-Cake-10-scaled.jpg"));
        kueList.add(new Kue("Red Velvet Cake", "Rp 135.000", "4.9", "(156)", "https://t3.ftcdn.net/jpg/02/64/84/68/360_F_264846889_3FTwwhQItDUy95Wdeaf8Qg4YLiiLNvrG.jpg"));
        kueList.add(new Kue("Tiramisu Cake", "Rp 145.000", "4.7", "(109)", "https://www.jiffymix.com/wp-content/uploads/2020/06/Tiramisu-Cake-2.jpg"));
        kueList.add(new Kue("Chocolate Chip Cookies", "Rp 45.000", "4.6", "(302)", "https://images.aws.nestle.recipes/resized/5b069c3ed2feea79377014f6766fcd49_Original_NTH_Chocolate_Chip_Cookie_1080_850.jpg"));
        kueList.add(new Kue("Matcha Latte", "Rp 35.000", "4.7", "(145)", "https://thumbs.dreamstime.com/b/matcha-latte-art-heart-shape-top-wooden-table-some-gr-green-tea-powder-tools-tea-making-japanese-style-87708022.jpg"));

        // 2. Menghubungkan Data ke RecyclerView menggunakan Adapter
        adapter = new KueAdapter(this, kueList);
        rvKue.setAdapter(adapter);
    }
}