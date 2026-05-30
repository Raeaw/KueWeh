package com.example.kueweh;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BatchAdapter extends RecyclerView.Adapter<BatchAdapter.BatchViewHolder> {

    private Context context;
    private List<BatchPesanan> batchList;

    public BatchAdapter(Context context, List<BatchPesanan> batchList) {
        this.context = context;
        this.batchList = batchList;
    }

    @NonNull
    @Override
    public BatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_batch_riwayat, parent, false);
        return new BatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchViewHolder holder, int position) {
        BatchPesanan batch = batchList.get(position);

        SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM", new Locale("id", "ID"));
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", new Locale("id", "ID"));

        holder.tvTanggal.setText(sdfDate.format(new Date(batch.timestamp)).toUpperCase());
        holder.tvJam.setText(sdfTime.format(new Date(batch.timestamp)));

        holder.containerItems.removeAllViews();

        int totalHargaBatch = 0;
        float totalRatingBatch = 0;
        int countItemRating = 0;

        for (Pesanan item : batch.items) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_pesanan, holder.containerItems, false);

            TextView tvNama = itemView.findViewById(R.id.tvNamaPesanan);
            TextView tvHarga = itemView.findViewById(R.id.tvHargaPesanan);
            ImageView img = itemView.findViewById(R.id.imgPesanan);
            TextView tvRatingItem = itemView.findViewById(R.id.tvRatingItem);

            // Komponen Status Baru
            TextView tvBadgePelanggan = itemView.findViewById(R.id.tvStatusPesananBadge);
            LinearLayout layoutAdminControl = itemView.findViewById(R.id.layoutAdminItemControl);

            // Sembunyikan panel admin di halaman pelanggan
            layoutAdminControl.setVisibility(View.GONE);

            tvNama.setText(item.getNamaKue());
            tvHarga.setText(item.getHargaKue());
            Glide.with(context).load(item.getImageUrl()).into(img);

            // 1. Logika Status & Warna Badge
            String statusSaatIni = item.status != null ? item.status : "Pending";
            tvBadgePelanggan.setText(statusSaatIni);

            if ("Pending".equals(statusSaatIni)) {
                tvBadgePelanggan.setBackgroundColor(Color.parseColor("#FFF3E0"));
                tvBadgePelanggan.setTextColor(Color.parseColor("#FF9800"));
            } else if ("Diproses".equals(statusSaatIni)) {
                tvBadgePelanggan.setBackgroundColor(Color.parseColor("#E3F2FD"));
                tvBadgePelanggan.setTextColor(Color.parseColor("#2196F3"));
            } else {
                tvBadgePelanggan.setBackgroundColor(Color.parseColor("#E8F5E9"));
                tvBadgePelanggan.setTextColor(Color.parseColor("#4CAF50"));
            }

            // 2. Logika Teks Rating
            tvRatingItem.setText(item.getRating() == 0 ? "⭐ Rating" : "⭐ " + item.getRating());

            // 3. Logika Kunci Klik Rating (Berdasarkan Status)
            if ("Selesai".equals(statusSaatIni)) {
                tvRatingItem.setTextColor(Color.parseColor("#1E1C18")); // Warna Normal
                tvRatingItem.setOnClickListener(v -> tampilkanDialogRating(item)); // Dialog bisa dibuka
            } else {
                tvRatingItem.setTextColor(Color.parseColor("#BDBDBD")); // Warna Abu-abu (Terkunci)
                tvRatingItem.setOnClickListener(v -> Toast.makeText(context, "Pesanan harus Selesai terlebih dahulu untuk memberi ulasan.", Toast.LENGTH_SHORT).show());
            }

            // Perhitungan Harga dan Rata-rata
            String hargaSaja = item.getHargaKue().split(" \\(")[0];
            String hargaBersih = hargaSaja.replaceAll("[^0-9]", "");
            if (!hargaBersih.isEmpty()) {
                totalHargaBatch += Integer.parseInt(hargaBersih);
            }

            if (item.getRating() > 0) {
                totalRatingBatch += item.getRating();
                countItemRating++;
            }

            holder.containerItems.addView(itemView);
        }

        NumberFormat formatRupiah = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        holder.tvTotal.setText("Total: Rp " + formatRupiah.format(totalHargaBatch));

        float avg = countItemRating > 0 ? (totalRatingBatch / countItemRating) : 0;
        holder.tvAvgRating.setText("Rating: ⭐ " + String.format(Locale.US, "%.1f", avg));
    }

    private void tampilkanDialogRating(Pesanan item) {
        CharSequence[] levels = {"⭐ 1", "⭐ 2", "⭐ 3", "⭐ 4", "⭐ 5"};
        new AlertDialog.Builder(context)
                .setTitle("Beri Rating untuk " + item.getNamaKue())
                .setItems(levels, (dialog, which) -> {
                    float ratingBaru = which + 1;

                    new Thread(() -> {
                        item.setRating(ratingBaru);
                        PesananDao pesananDao = AppDatabase.getInstance(context).pesananDao();
                        pesananDao.updatePesanan(item);

                        float trueGlobalAvg = pesananDao.getTrueGlobalAverageRating(item.getNamaKue());
                        int reviewerCount = pesananDao.getReviewerCount(item.getNamaKue());

                        KueDao kueDao = AppDatabase.getInstance(context).kueDao();
                        Kue kueKatalog = kueDao.getKueByName(item.getNamaKue());
                        if (kueKatalog != null) {
                            kueKatalog.setRating(String.format(Locale.US, "%.1f", trueGlobalAvg));
                            kueKatalog.setUlasan("(" + reviewerCount + ")");
                            kueDao.updateKue(kueKatalog);
                        }

                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() -> {
                                Toast.makeText(context, "Terima kasih atas ratingnya!", Toast.LENGTH_SHORT).show();
                                ((Activity) context).recreate();
                            });
                        }
                    }).start();
                }).show();
    }

    @Override
    public int getItemCount() {
        return batchList.size();
    }

    public static class BatchViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggal, tvJam, tvTotal, tvAvgRating;
        LinearLayout containerItems;

        public BatchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tvBatchTanggal);
            tvJam = itemView.findViewById(R.id.tvBatchJam);
            containerItems = itemView.findViewById(R.id.containerItems);
            tvTotal = itemView.findViewById(R.id.tvBatchTotal);
            tvAvgRating = itemView.findViewById(R.id.tvBatchAvgRating);
        }
    }
}