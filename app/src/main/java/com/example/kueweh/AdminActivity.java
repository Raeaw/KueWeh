package com.example.kueweh;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavAdmin);

        // Set halaman pertama yang muncul adalah Produk
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_admin, new AdminProdukFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_admin_produk) {
                selectedFragment = new AdminProdukFragment();
            } else if (item.getItemId() == R.id.nav_admin_pesanan) {
                selectedFragment = new AdminPesananFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_admin, selectedFragment)
                        .commit();
            }
            return true;
        });
    }
}