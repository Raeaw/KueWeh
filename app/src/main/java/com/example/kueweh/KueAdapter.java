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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class KueAdapter extends RecyclerView.Adapter<KueAdapter.KueViewHolder> {

    private Context context;
    private List<Kue> kueList;
    private boolean isAdmin = false;

    public KueAdapter(Context context, List<Kue> kueList) {
        this.context = context;
        this.kueList = kueList;
    }

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
        TextView tvKategori = holder.itemView.findViewById(R.id.tvKategoriKue);
        if (tvKategori != null && kue.getKategori() != null) {
            tvKategori.setText(kue.getKategori());
        }
        holder.tvHarga.setText(kue.getHarga());
        holder.tvRating.setText(kue.getRating() + " " + kue.getUlasan());

        Glide.with(context).load(kue.getImageUrl()).into(holder.imgKue);

        if (isAdmin) {
            holder.btnFavoriteLayout.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                CharSequence[] options = new CharSequence[]{"Edit Produk", "Hapus Produk"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Pilih Aksi untuk " + kue.getNama());
                builder.setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(context, EditProductActivity.class);
                        intent.putExtra("KUE_ID", kue.getId());
                        context.startActivity(intent);
                    } else {
                        tampilkanKonfirmasiHapus(kue, position);
                    }
                });
                builder.show();
            });
        } else {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("KUE_ID", kue.getId());
                context.startActivity(intent);
            });

            holder.btnFavoriteLayout.setVisibility(View.VISIBLE);

            android.content.SharedPreferences sharedPref = context.getSharedPreferences("KueWehSession", Context.MODE_PRIVATE);
            String currentEmail = sharedPref.getString("userEmail", "");

            FavoritDao favDao = AppDatabase.getInstance(context).favoritDao();

            // 1. Cek status favorit di background
            new Thread(() -> {
                Favorit isFav = favDao.cekFavorit(currentEmail, kue.getNama());
                boolean isFavorited = (isFav != null);

                // 2. Kembalikan ke UI Thread untuk mengubah warna dan memasang aksi klik
                ((android.app.Activity) context).runOnUiThread(() -> {
                    holder.imgFavoriteHeart.setColorFilter(android.graphics.Color.parseColor(isFavorited ? "#FF3B30" : "#BDBDBD"));

                    // AKSI KLIK HARUS DI DALAM SINI (UI THREAD)
                    holder.btnFavoriteLayout.setOnClickListener(v -> {
                        new Thread(() -> {
                            Favorit cekLagi = favDao.cekFavorit(currentEmail, kue.getNama());
                            if (cekLagi != null) {
                                favDao.hapusFavorit(currentEmail, kue.getNama()); // Unlove
                                ((android.app.Activity) context).runOnUiThread(() -> {
                                    holder.imgFavoriteHeart.setColorFilter(android.graphics.Color.parseColor("#BDBDBD"));
                                });
                            } else {
                                favDao.insertFavorit(new Favorit(currentEmail, kue.getNama())); // Love
                                ((android.app.Activity) context).runOnUiThread(() -> {
                                    holder.imgFavoriteHeart.setColorFilter(android.graphics.Color.parseColor("#FF3B30"));
                                });
                            }
                        }).start();
                    });
                });
            }).start();
        }
    }

    private void tampilkanKonfirmasiHapus(Kue kue, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Hapus Produk")
                .setMessage("Apakah kamu yakin ingin menghapus " + kue.getNama() + "?")
                .setPositiveButton("Ya, Hapus", (dialog, which) -> {
                    new Thread(() -> {
                        AppDatabase.getInstance(context).kueDao().deleteKue(kue);

                        // PERBAIKAN: Gunakan ((Activity) context) agar aman di Fragment
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            kueList.remove(position);
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
        CardView btnFavoriteLayout;
        ImageView imgFavoriteHeart;
        TextView tvNama, tvHarga, tvRating;
        ImageView imgKue;

        public KueViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaKue);
            tvHarga = itemView.findViewById(R.id.tvHargaKue);
            tvRating = itemView.findViewById(R.id.tvRating);
            imgKue = itemView.findViewById(R.id.imgKue);

            // PERBAIKAN: Wajib diinisialisasi agar tidak NullPointerException
            btnFavoriteLayout = itemView.findViewById(R.id.btnFavoriteLayout);
            imgFavoriteHeart = itemView.findViewById(R.id.imgFavoriteHeart);
        }
    }
}