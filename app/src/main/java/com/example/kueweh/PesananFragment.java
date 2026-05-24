package com.example.kueweh;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PesananFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pesanan, container, false);

        RecyclerView rvPesanan = view.findViewById(R.id.rvPesanan);
        rvPesanan.setLayoutManager(new LinearLayoutManager(getContext()));

        // Ambil email user aktif
        SharedPreferences sharedPref = getActivity().getSharedPreferences("KueWehSession", Context.MODE_PRIVATE);
        String currentEmail = sharedPref.getString("userEmail", "");

        // Ambil data transaksi khusus milik email tersebut dari database
        List<Pesanan> listPesanan = AppDatabase.getInstance(getContext()).pesananDao().getPesananByUser(currentEmail);

        // Pasang ke adapter
        PesananAdapter adapter = new PesananAdapter(getContext(), listPesanan);
        rvPesanan.setAdapter(adapter);

        return view;
    }
}