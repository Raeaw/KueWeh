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

public class KeranjangAdapter extends RecyclerView.Adapter<KeranjangAdapter.KeranjangViewHolder> {

    private Context context;
    private List<Keranjang> keranjangList;
    private KeranjangListener listener;

    public interface KeranjangListener {
        void onKeranjangUpdated();
    }

    public KeranjangAdapter(Context context, List<Keranjang> keranjangList, KeranjangListener listener) {
        this.context = context;
        this.keranjangList = keranjangList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public KeranjangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_keranjang, parent, false);
        return new KeranjangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeranjangViewHolder holder, int position) {
        Keranjang item = keranjangList.get(position);
        holder.tvNama.setText(item.getNamaKue());
        holder.tvHarga.setText(item.getHargaKue());
        holder.tvJumlah.setText(String.valueOf(item.getJumlah()));
        Glide.with(context).load(item.getImageUrl()).into(holder.imgKeranjang);

        KeranjangDao dao = AppDatabase.getInstance(context).keranjangDao();

        holder.btnPlus.setOnClickListener(v -> {
            item.setJumlah(item.getJumlah() + 1);
            dao.updateKeranjang(item);
            notifyItemChanged(position);
            listener.onKeranjangUpdated();
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (item.getJumlah() > 1) {
                item.setJumlah(item.getJumlah() - 1);
                dao.updateKeranjang(item);
                notifyItemChanged(position);
            } else {
                dao.deleteKeranjang(item);
                keranjangList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, keranjangList.size());
            }
            listener.onKeranjangUpdated();
        });
    }

    @Override
    public int getItemCount() { return keranjangList.size(); }

    public static class KeranjangViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvHarga, tvJumlah, btnPlus, btnMinus;
        ImageView imgKeranjang;

        public KeranjangViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvKeranjangNama);
            tvHarga = itemView.findViewById(R.id.tvKeranjangHarga);
            tvJumlah = itemView.findViewById(R.id.tvKeranjangJumlah);
            btnPlus = itemView.findViewById(R.id.btnKeranjangPlus);
            btnMinus = itemView.findViewById(R.id.btnKeranjangMinus);
            imgKeranjang = itemView.findViewById(R.id.imgKeranjang);
        }
    }
}