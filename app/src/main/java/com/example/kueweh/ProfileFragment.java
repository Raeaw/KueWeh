package com.example.kueweh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView tvName = view.findViewById(R.id.tvProfileName);
        TextView tvEmail = view.findViewById(R.id.tvProfileEmail);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Inisialisasi tombol menu Riwayat Pesanan
        LinearLayout btnRiwayat = view.findViewById(R.id.btnRiwayatPesanan);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("KueWehSession", Context.MODE_PRIVATE);
        tvName.setText(sharedPref.getString("userName", "User"));
        tvEmail.setText(sharedPref.getString("userEmail", "email@kue.com"));

        // Aksi klik menu Riwayat Pesanan
        btnRiwayat.setOnClickListener(v -> {
            // Ini akan merah sementara waktu karena RiwayatPesananActivity belum kita buat
            startActivity(new Intent(getActivity(), RiwayatPesananActivity.class));
        });

        // Logika Logout
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(getContext(), "Berhasil Keluar", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}