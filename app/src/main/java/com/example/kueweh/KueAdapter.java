package com.example.kueweh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class KueAdapter extends RecyclerView.Adapter<KueAdapter.KueViewHolder> {

    private Context context;
    private List<Kue> kueList;

    public KueAdapter(Context context, List<Kue> kueList) {
        this.context = context;
        this.kueList = kueList;
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

        holder.tvNamaKue.setText(kue.getNama());
        holder.tvHargaKue.setText(kue.getHarga());
        holder.tvRating.setText("⭐ " + kue.getRating() + " " + kue.getUlasan());

        // Menggunakan Glide untuk memuat gambar dari URL
        Glide.with(context)
                .load(kue.getImageUrl())
                .into(holder.imgKue);
    }

    @Override
    public int getItemCount() {
        return kueList.size();
    }

    public static class KueViewHolder extends RecyclerView.ViewHolder {
        ImageView imgKue;
        TextView tvNamaKue, tvHargaKue, tvRating;

        public KueViewHolder(@NonNull View itemView) {
            super(itemView);
            imgKue = itemView.findViewById(R.id.imgKue);
            tvNamaKue = itemView.findViewById(R.id.tvNamaKue);
            tvHargaKue = itemView.findViewById(R.id.tvHargaKue);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}