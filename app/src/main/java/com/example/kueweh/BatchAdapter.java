package com.example.kueweh;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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

        // 1. Format Tanggal dan Jam
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM", new Locale("id", "ID"));
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", new Locale("id", "ID"));

        holder.tvTanggal.setText(sdfDate.format(new Date(batch.timestamp)).toUpperCase());
        holder.tvJam.setText(sdfTime.format(new Date(batch.timestamp)));

        // 2. Bersihkan container agar tidak menumpuk ganda saat layar di-scroll
        holder.containerItems.removeAllViews();

        int totalHargaBatch = 0;
        float totalRatingBatch = 0;
        int countItemRating = 0;

        // 3. Render setiap item (kue) ke dalam batch ini
        for (Pesanan item : batch.items) {
            // Inflate layout item_pesanan.xml secara manual ke dalam containerItems
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_pesanan, holder.containerItems, false);

            TextView tvNama = itemView.findViewById(R.id.tvNamaPesanan);
            TextView tvHarga = itemView.findViewById(R.id.tvHargaPesanan);
            ImageView img = itemView.findViewById(R.id.imgPesanan);

            // PASTIKAN: Kamu sudah menambahkan TextView dengan id "tvRatingItem" di layout item_pesanan.xml
            TextView tvRatingItem = itemView.findViewById(R.id.tvRatingItem);

            tvNama.setText(item.getNamaKue());
            tvHarga.setText(item.getHargaKue());

            // Logika Teks Rating
            tvRatingItem.setText(item.getRating() == 0 ? "⭐ Beri Rating" : "⭐ " + item.getRating());
            Glide.with(context).load(item.getImageUrl()).into(img);

            // Hitung Total Harga Batch secara matematis
            // Karena teksnya berbentuk "Rp 50.000 (2x)", kita potong ambil harganya saja sebelum tanda kurung
            String hargaSaja = item.getHargaKue().split(" \\(")[0];
            String hargaBersih = hargaSaja.replaceAll("[^0-9]", "");
            if (!hargaBersih.isEmpty()) {
                totalHargaBatch += Integer.parseInt(hargaBersih);
            }

            // Hitung Rata-rata Rating
            if (item.getRating() > 0) {
                totalRatingBatch += item.getRating();
                countItemRating++;
            }

            // Aksi Klik untuk Memberi Rating
            tvRatingItem.setOnClickListener(v -> tampilkanDialogRating(item));

            // Masukkan view ke dalam container
            holder.containerItems.addView(itemView);
        }

        // 4. Tampilkan Total & Rata-rata Rating di Footer Batch
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
                        // 1. Simpan rating baru ke transaksi ini
                        item.setRating(ratingBaru);
                        PesananDao pesananDao = AppDatabase.getInstance(context).pesananDao();
                        pesananDao.updatePesanan(item);

                        // 2. Ambil nilai rata-rata sejati dan jumlah akun pengulas
                        float trueGlobalAvg = pesananDao.getTrueGlobalAverageRating(item.getNamaKue());
                        int reviewerCount = pesananDao.getReviewerCount(item.getNamaKue());

                        // 3. Update database tabel_kue agar Home menampilkan angka global
                        KueDao kueDao = AppDatabase.getInstance(context).kueDao();
                        Kue kueKatalog = kueDao.getKueByName(item.getNamaKue());
                        if (kueKatalog != null) {
                            kueKatalog.setRating(String.format(Locale.US, "%.1f", trueGlobalAvg));
                            // Format ulasan kembali menjadi angka, contoh: (12)
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

    // ViewHolder untuk layout Batch
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