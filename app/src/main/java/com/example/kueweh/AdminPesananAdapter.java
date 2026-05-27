package com.example.kueweh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        // Set Info Pembeli
        holder.tvEmail.setText(batch.userEmail);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy - HH:mm", new Locale("id", "ID"));
        holder.tvWaktu.setText(sdf.format(new Date(batch.timestamp)).toUpperCase());

        holder.containerItems.removeAllViews();
        int totalHargaBatch = 0;

        // Render daftar kue yang dibeli pada transaksi ini
        for (Pesanan item : batch.items) {
            // Kita pakai ulang item_pesanan.xml untuk menghemat layout
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_pesanan, holder.containerItems, false);

            TextView tvNama = itemView.findViewById(R.id.tvNamaPesanan);
            TextView tvHarga = itemView.findViewById(R.id.tvHargaPesanan);
            ImageView img = itemView.findViewById(R.id.imgPesanan);
            TextView tvRatingItem = itemView.findViewById(R.id.tvRatingItem);

            tvNama.setText(item.getNamaKue());
            tvHarga.setText(item.getHargaKue());
            Glide.with(context).load(item.getImageUrl()).into(img);

            // Sembunyikan fitur klik rating karena ini halaman Admin
            tvRatingItem.setVisibility(View.GONE);

            // Hitung total pendapatan dari batch ini
            String hargaSaja = item.getHargaKue().split(" \\(")[0];
            String hargaBersih = hargaSaja.replaceAll("[^0-9]", "");
            if (!hargaBersih.isEmpty()) {
                totalHargaBatch += Integer.parseInt(hargaBersih);
            }

            holder.containerItems.addView(itemView);
        }

        // Tampilkan Total
        NumberFormat formatRupiah = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        holder.tvTotal.setText("Total Pendapatan: Rp " + formatRupiah.format(totalHargaBatch));
    }

    @Override
    public int getItemCount() { return batchList.size(); }

    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail, tvWaktu, tvTotal;
        LinearLayout containerItems;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvAdminPembeliEmail);
            tvWaktu = itemView.findViewById(R.id.tvAdminWaktuPesan);
            tvTotal = itemView.findViewById(R.id.tvAdminTotalBatch);
            containerItems = itemView.findViewById(R.id.containerAdminItems);
        }
    }

    // Kelas Model Internal untuk menampung Grup Pesanan Admin
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