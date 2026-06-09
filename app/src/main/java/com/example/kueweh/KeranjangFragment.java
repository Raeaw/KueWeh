package com.example.kueweh;

import android.app.AlertDialog;
import android.content.Context;
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
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class KeranjangFragment extends Fragment implements KeranjangAdapter.KeranjangListener {

    private RecyclerView rvKeranjang;
    private View emptyStateKeranjang;
    private TextView tvTotalHarga;
    private Button btnCheckout;
    private KeranjangAdapter adapter;
    private List<Keranjang> keranjangList;
    private String currentEmail;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keranjang, container, false);

        rvKeranjang = view.findViewById(R.id.rvKeranjang);
        emptyStateKeranjang = view.findViewById(R.id.emptyStateKeranjang);
        tvTotalHarga = view.findViewById(R.id.tvTotalHargaKeranjang);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        rvKeranjang.setLayoutManager(new LinearLayoutManager(getContext()));
        db = AppDatabase.getInstance(getContext());

        SharedPreferences sharedPref = getActivity().getSharedPreferences("KueWehSession", Context.MODE_PRIVATE);
        currentEmail = sharedPref.getString("userEmail", "");

        // Handle status bar padding hanya pada header, bukan seluruh fragment
        View header = view.findViewById(R.id.tvHeaderKeranjang);
        ViewCompat.setOnApplyWindowInsetsListener(header, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int padding18dp = (int) (18 * getResources().getDisplayMetrics().density);
            v.setPadding(
                    v.getPaddingLeft(),
                    statusBarHeight + padding18dp,
                    v.getPaddingRight(),
                    padding18dp
            );
            return insets;
        });

        loadKeranjang();

        btnCheckout.setOnClickListener(v -> {
            if (keranjangList == null || keranjangList.isEmpty()) {
                Toast.makeText(getContext(), "Keranjang kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                long batchTimestamp = System.currentTimeMillis();

                for (Keranjang item : keranjangList) {
                    int hargaSatuan = Integer.parseInt(item.getHargaKue().replaceAll("[^0-9]", ""));
                    int totalPerItem = hargaSatuan * item.getJumlah();
                    String hargaFinal = "Rp " + NumberFormat.getNumberInstance(new Locale("id", "ID")).format(totalPerItem) + " (" + item.getJumlah() + "x)";

                    Pesanan pesanan = new Pesanan(currentEmail, item.getNamaKue(), hargaFinal, item.getImageUrl(), batchTimestamp, "Pending");
                    db.pesananDao().insertPesanan(pesanan);
                }

                db.keranjangDao().clearKeranjang(currentEmail);

                getActivity().runOnUiThread(() -> {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Pembayaran Berhasil 🎉")
                            .setMessage("Terima kasih! Pesanan kamu sedang diproses.")
                            .setPositiveButton("OK", (dialog, which) -> loadKeranjang())
                            .setCancelable(false)
                            .show();
                });
            }).start();
        });

        return view;
    }

    private void loadKeranjang() {
        keranjangList = db.keranjangDao().getKeranjangByUser(currentEmail);

        if (keranjangList.isEmpty()) {
            rvKeranjang.setVisibility(View.GONE);
            emptyStateKeranjang.setVisibility(View.VISIBLE);
            tvTotalHarga.setText("Rp 0");
        } else {
            rvKeranjang.setVisibility(View.VISIBLE);
            emptyStateKeranjang.setVisibility(View.GONE);
            adapter = new KeranjangAdapter(getContext(), keranjangList, this);
            rvKeranjang.setAdapter(adapter);
            hitungTotalHarga();
        }
    }

    private void hitungTotalHarga() {
        int total = 0;
        for (Keranjang item : keranjangList) {
            String hargaBersih = item.getHargaKue().replaceAll("[^0-9]", "");
            if (!hargaBersih.isEmpty()) {
                total += Integer.parseInt(hargaBersih) * item.getJumlah();
            }
        }
        NumberFormat formatRupiah = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        tvTotalHarga.setText("Rp " + formatRupiah.format(total));
    }

    @Override
    public void onKeranjangUpdated() {
        hitungTotalHarga();
        if (keranjangList.isEmpty()) {
            rvKeranjang.setVisibility(View.GONE);
            emptyStateKeranjang.setVisibility(View.VISIBLE);
            tvTotalHarga.setText("Rp 0");
        }
    }
}