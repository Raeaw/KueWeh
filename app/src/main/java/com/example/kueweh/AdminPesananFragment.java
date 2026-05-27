package com.example.kueweh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminPesananFragment extends Fragment {

    private RecyclerView rvAdminPesanan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_pesanan, container, false);

        rvAdminPesanan = view.findViewById(R.id.rvAdminPesanan);
        rvAdminPesanan.setLayoutManager(new LinearLayoutManager(getContext()));

        loadSemuaPesanan();

        return view;
    }

    private void loadSemuaPesanan() {
        new Thread(() -> {
            // Ambil SEMUA pesanan dari semua user (Pastikan fungsi ini sudah ada di PesananDao.java)
            List<Pesanan> semuaPesanan = AppDatabase.getInstance(getContext()).pesananDao().getAllPesananForAdmin();

            // Logika Grouping berdasarkan Timestamp
            Map<Long, List<Pesanan>> groupedMap = new LinkedHashMap<>();
            for (Pesanan p : semuaPesanan) {
                if (!groupedMap.containsKey(p.getTimestamp())) {
                    groupedMap.put(p.getTimestamp(), new ArrayList<>());
                }
                groupedMap.get(p.getTimestamp()).add(p);
            }

            // Ubah Map menjadi List<AdminBatch>
            List<AdminPesananAdapter.AdminBatch> adminBatchList = new ArrayList<>();
            for (Map.Entry<Long, List<Pesanan>> entry : groupedMap.entrySet()) {
                // Ambil email user dari item pertama di batch tersebut
                String emailPembeli = entry.getValue().get(0).getUserEmail();
                adminBatchList.add(new AdminPesananAdapter.AdminBatch(emailPembeli, entry.getKey(), entry.getValue()));
            }

            // Tampilkan ke UI
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    AdminPesananAdapter adapter = new AdminPesananAdapter(getContext(), adminBatchList);
                    rvAdminPesanan.setAdapter(adapter);
                });
            }
        }).start();
    }
}