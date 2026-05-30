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

        holder.containerItems.removeAllViews();

        int totalHargaBatch = 0;
        int countPending = 0;
        int countDiproses = 0;
        int countSelesai = 0;
        int totalItems = batch.items.size();

        // 1. Render setiap item dan pasang tombol aksinya
        for (Pesanan item : batch.items) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_pesanan, holder.containerItems, false);

            TextView tvNama = itemView.findViewById(R.id.tvNamaPesanan);
            TextView tvHarga = itemView.findViewById(R.id.tvHargaPesanan);
            ImageView img = itemView.findViewById(R.id.imgPesanan);
            TextView tvRatingItem = itemView.findViewById(R.id.tvRatingItem);

            // Komponen Admin Per Item (yang baru ditambahkan di XML)
            LinearLayout layoutAdminControl = itemView.findViewById(R.id.layoutAdminItemControl);
            TextView tvItemStatus = itemView.findViewById(R.id.tvAdminItemStatus);
            Button btnItemAksi = itemView.findViewById(R.id.btnAdminItemAksi);

            tvNama.setText(item.getNamaKue());
            tvHarga.setText(item.getHargaKue());
            Glide.with(context).load(item.getImageUrl()).into(img);
            tvRatingItem.setVisibility(View.GONE);

            // Tampilkan panel admin
            layoutAdminControl.setVisibility(View.VISIBLE);
            tvItemStatus.setText(item.status);

            // Logika Tombol & Penghitungan Status
            if ("Pending".equals(item.status)) {
                countPending++;
                tvItemStatus.setTextColor(Color.parseColor("#FF9800"));
                btnItemAksi.setVisibility(View.VISIBLE);
                btnItemAksi.setText("Terima");
                btnItemAksi.setBackgroundColor(Color.parseColor("#2196F3"));
                btnItemAksi.setOnClickListener(v -> updateItemStatus(item.id, "Diproses", position));

            } else if ("Diproses".equals(item.status)) {
                countDiproses++;
                tvItemStatus.setTextColor(Color.parseColor("#2196F3"));
                btnItemAksi.setVisibility(View.VISIBLE);
                btnItemAksi.setText("Selesai");
                btnItemAksi.setBackgroundColor(Color.parseColor("#4CAF50"));
                btnItemAksi.setOnClickListener(v -> updateItemStatus(item.id, "Selesai", position));

            } else {
                countSelesai++;
                tvItemStatus.setTextColor(Color.parseColor("#4CAF50"));
                btnItemAksi.setVisibility(View.GONE); // Hilangkan tombol jika sudah selesai
            }

            // Hitung harga
            String hargaSaja = item.getHargaKue().split(" \\(")[0];
            String hargaBersih = hargaSaja.replaceAll("[^0-9]", "");
            if (!hargaBersih.isEmpty()) {
                totalHargaBatch += Integer.parseInt(hargaBersih);
            }

            holder.containerItems.addView(itemView);
        }

        // 2. Set Teks Rangkuman Rombongan (Batch Indicator)
        if (countSelesai == totalItems) {
            holder.tvStatus.setText("Status: Semua Selesai ✅");
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
        } else if (countPending == totalItems) {
            holder.tvStatus.setText("Status: Belum Diproses ⏳");
            holder.tvStatus.setTextColor(Color.parseColor("#FF9800"));
        } else {
            holder.tvStatus.setText("Status: Sebagian Diproses (" + countSelesai + "/" + totalItems + ") 🔄");
            holder.tvStatus.setTextColor(Color.parseColor("#2196F3"));
        }

        NumberFormat formatRupiah = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        holder.tvTotal.setText("Total Pendapatan: Rp " + formatRupiah.format(totalHargaBatch));
    }

    // Fungsi untuk memperbarui status 1 item spesifik
    private void updateItemStatus(int itemId, String statusBaru, int batchPosition) {
        new Thread(() -> {
            AppDatabase.getInstance(context).pesananDao().updateStatusItem(itemId, statusBaru);

            // Perbarui data lokal di adapter agar sinkron
            for (Pesanan p : batchList.get(batchPosition).items) {
                if (p.id == itemId) {
                    p.status = statusBaru;
                    break;
                }
            }

            ((Activity) context).runOnUiThread(() -> {
                // Refresh HANYA kotak batch yang sedang diubah
                notifyItemChanged(batchPosition);
            });
        }).start();
    }

    @Override
    public int getItemCount() { return batchList.size(); }

    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail, tvWaktu, tvTotal, tvStatus;
        LinearLayout containerItems;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvAdminPembeliEmail);
            tvWaktu = itemView.findViewById(R.id.tvAdminWaktuPesan);
            tvTotal = itemView.findViewById(R.id.tvAdminTotalBatch);
            tvStatus = itemView.findViewById(R.id.tvAdminStatusBatch);
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