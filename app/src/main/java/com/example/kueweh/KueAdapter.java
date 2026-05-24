package com.example.kueweh;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class KueAdapter extends RecyclerView.Adapter<KueAdapter.KueViewHolder> {

    private Context context;
    private List<Kue> kueList;
    private boolean isAdmin = false; // Default-nya false (untuk kustomer)

    // Constructor 1: Untuk Kustomer biasa (di HomeFragment)
    public KueAdapter(Context context, List<Kue> kueList) {
        this.context = context;
        this.kueList = kueList;
    }

    // Constructor 2: Untuk Admin (di AdminActivity)
    public KueAdapter(Context context, List<Kue> kueList, boolean isAdmin) {
        this.context = context;
        this.kueList = kueList;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public KueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_kue, parent, false);
        return new KueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KueViewHolder holder, int position) {
        Kue kue = kueList.get(position);
        holder.tvNama.setText(kue.getNama());
        holder.tvHarga.setText(kue.getHarga());

        // Gabungkan teks rating dan ulasan ke dalam satu TextView
        holder.tvRating.setText("⭐ " + kue.getRating() + " " + kue.getUlasan());

        Glide.with(context).load(kue.getImageUrl()).into(holder.imgKue);

        // LOGIKA KHUSUS JIKA DIJALANKAN DI HALAMAN ADMIN
        if (isAdmin) {
            holder.itemView.setOnClickListener(v -> {
                CharSequence[] options = new CharSequence[]{"Edit Produk", "Hapus Produk"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Pilih Aksi untuk " + kue.getNama());
                builder.setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // 1. OPSI EDIT: Pindah ke EditProductActivity membawa ID Kue
                        Intent intent = new Intent(context, EditProductActivity.class);
                        intent.putExtra("KUE_ID", kue.getId());
                        context.startActivity(intent);
                    } else {
                        // 2. OPSI HAPUS: Munculkan konfirmasi hapus
                        tampilkanKonfirmasiHapus(kue, position);
                    }
                });
                builder.show();
            });
        }
    }

    private void tampilkanKonfirmasiHapus(Kue kue, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Hapus Produk")
                .setMessage("Apakah kamu yakin ingin menghapus " + kue.getNama() + "?")
                .setPositiveButton("Ya, Hapus", (dialog, which) -> {
                    // Jalankan operasi delete database di background thread
                    new Thread(() -> {
                        AppDatabase.getInstance(context).kueDao().deleteKue(kue);

                        // Update tampilan list secara real-time
                        kueList.remove(position);
                        ((AdminActivity) context).runOnUiThread(() -> {
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, kueList.size());
                            Toast.makeText(context, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show();
                        });
                    }).start();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return kueList.size();
    }

    public static class KueViewHolder extends RecyclerView.ViewHolder {
        // Hapus variabel tvUlasan
        TextView tvNama, tvHarga, tvRating;
        ImageView imgKue;

        public KueViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaKue);
            tvHarga = itemView.findViewById(R.id.tvHargaKue);
            // Sesuaikan ID ini dengan yang ada di XML kamu
            tvRating = itemView.findViewById(R.id.tvRating);
            imgKue = itemView.findViewById(R.id.imgKue);
        }
    }
}