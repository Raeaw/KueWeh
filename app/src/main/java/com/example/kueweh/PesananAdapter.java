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

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.PesananViewHolder> {

    private Context context;
    private List<Pesanan> pesananList;

    public PesananAdapter(Context context, List<Pesanan> pesananList) {
        this.context = context;
        this.pesananList = pesananList;
    }

    @NonNull
    @Override
    public PesananViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pesanan, parent, false);
        return new PesananViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PesananViewHolder holder, int position) {
        Pesanan pesanan = pesananList.get(position);
        holder.tvNama.setText(pesanan.getNamaKue());
        holder.tvHarga.setText(pesanan.getHargaKue());
        Glide.with(context).load(pesanan.getImageUrl()).into(holder.imgPesanan);
    }

    @Override
    public int getItemCount() { return pesananList.size(); }

    public static class PesananViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvHarga;
        ImageView imgPesanan;

        public PesananViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaPesanan);
            tvHarga = itemView.findViewById(R.id.tvHargaPesanan);
            imgPesanan = itemView.findViewById(R.id.imgPesanan);
        }
    }
}