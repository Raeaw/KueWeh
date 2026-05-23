package com.example.kueweh; // Pastikan ini sesuai dengan nama package-mu

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Menghubungkan Java dengan layout fragment_profile.xml
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView tvName = view.findViewById(R.id.tvProfileName);
        TextView tvEmail = view.findViewById(R.id.tvProfileEmail);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Ambil data dari Sesi (SharedPreferences)
        SharedPreferences sharedPref = getActivity().getSharedPreferences("KueWehSession", Context.MODE_PRIVATE);
        tvName.setText(sharedPref.getString("userName", "User"));
        tvEmail.setText(sharedPref.getString("userEmail", "email@kue.com"));

        // Logika Logout
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear(); // Hapus semua data login
            editor.apply();

            Toast.makeText(getContext(), "Berhasil Keluar", Toast.LENGTH_SHORT).show();

            // Kembali ke halaman Login
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            // Mencegah user menekan tombol back ke profil setelah logout
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}