package com.example.kueweh;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminPesananAdapter extends RecyclerView.Adapter<AdminPesananAdapter.AdminViewHolder> {

    private Context context;
    private List<AdminBatch> batchList;

    public AdminPesananAdapter(Context context, List<AdminBatch> batchList) {
        this.context = context;
        this.batchList = batchList;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_batch, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        AdminBatch batch = batchList.get(position);

        holder.tvEmail.setText(batch.userEmail);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy - HH:mm", new Locale("id", "ID"));
        holder.tvWaktu.setText(sdf.format(new Date(batch.timestamp)).toUpperCase());

        // Ambil status dari item pertama di batch ini
        String statusSaatIni = batch.items.get(0).status;

        // Atur Tampilan berdasarkan Status
        holder.tvStatus.setText("Status: " + statusSaatIni);

        if ("Pending".equals(statusSaatIni)) {
            holder.tvStatus.setTextColor(Color.parseColor("#FF9800")); // Orange
            holder.btnAksi.setVisibility(View.VISIBLE);
            holder.btnAksi.setText("Terima Pesanan");
            holder.btnAksi.setBackgroundColor(Color.parseColor("#2196F3")); // Biru

            holder.btnAksi.setOnClickListener(v -> updateStatus(batch.timestamp, "Diproses", position));

        } else if ("Diproses".equals(statusSaatIni)) {
            holder.tvStatus.setTextColor(Color.parseColor("#2196F3")); // Biru
            holder.btnAksi.setVisibility(View.VISIBLE);
            holder.btnAksi.setText("Selesaikan");
            holder.btnAksi.setBackgroundColor(Color.parseColor("#4CAF50")); // Hijau

            holder.btnAksi.setOnClickListener(v -> updateStatus(batch.timestamp, "Selesai", position));

        } else {
            // Jika status Selesai atau lainnya
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Hijau
            holder.btnAksi.setVisibility(View.GONE); // Sembunyikan tombol
        }

        holder.containerItems.removeAllViews();
        int totalHargaBatch = 0;

        for (Pesanan item : batch.items) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_pesanan, holder.containerItems, false);

            TextView tvNama = itemView.findViewById(R.id.tvNamaPesanan);
            TextView tvHarga = itemView.findViewById(R.id.tvHargaPesanan);
            ImageView img = itemView.findViewById(R.id.imgPesanan);
            TextView tvRatingItem = itemView.findViewById(R.id.tvRatingItem);

            tvNama.setText(item.getNamaKue());
            tvHarga.setText(item.getHargaKue());
            Glide.with(context).load(item.getImageUrl()).into(img);
            tvRatingItem.setVisibility(View.GONE);

            String hargaSaja = item.getHargaKue().split(" \\(")[0];
            String hargaBersih = hargaSaja.replaceAll("[^0-9]", "");
            if (!hargaBersih.isEmpty()) {
                totalHargaBatch += Integer.parseInt(hargaBersih);
            }

            holder.containerItems.addView(itemView);
        }

        NumberFormat formatRupiah = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        holder.tvTotal.setText("Total Pendapatan: Rp " + formatRupiah.format(totalHargaBatch));
    }

    // Fungsi untuk memperbarui status di Database dan UI
    private void updateStatus(long timestamp, String statusBaru, int position) {
        new Thread(() -> {
            AppDatabase.getInstance(context).pesananDao().updateStatusBatch(timestamp, statusBaru);

            // Perbarui data lokal di adapter dan refresh kartu tersebut
            for (Pesanan p : batchList.get(position).items) {
                p.status = statusBaru;
            }

            ((Activity) context).runOnUiThread(() -> {
                notifyItemChanged(position);
                android.widget.Toast.makeText(context, "Pesanan " + statusBaru, android.widget.Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    @Override
    public int getItemCount() { return batchList.size(); }

    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail, tvWaktu, tvTotal, tvStatus;
        Button btnAksi;
        LinearLayout containerItems;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvAdminPembeliEmail);
            tvWaktu = itemView.findViewById(R.id.tvAdminWaktuPesan);
            tvTotal = itemView.findViewById(R.id.tvAdminTotalBatch);
            tvStatus = itemView.findViewById(R.id.tvAdminStatusBatch);
            btnAksi = itemView.findViewById(R.id.btnAdminAksiPesanan);
            containerItems = itemView.findViewById(R.id.containerAdminItems);
        }
    }

    public static class AdminBatch {
        public String userEmail;
        public long timestamp;
        public List<Pesanan> items;

        public AdminBatch(String userEmail, long timestamp, List<Pesanan> items) {
            this.userEmail = userEmail;
            this.timestamp = timestamp;
            this.items = items;
        }
    }
}