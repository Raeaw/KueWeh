package com.example.kueweh;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tabel_keranjang")
public class Keranjang {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userEmail;
    private String namaKue;
    private String hargaKue;
    private String imageUrl;
    private int jumlah; // Tambahan kolom untuk menyimpan jumlah barang (1, 2, 3, dst)

    public Keranjang(String userEmail, String namaKue, String hargaKue, String imageUrl, int jumlah) {
        this.userEmail = userEmail;
        this.namaKue = namaKue;
        this.hargaKue = hargaKue;
        this.imageUrl = imageUrl;
        this.jumlah = jumlah;
    }

    // Getter dan Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getNamaKue() { return namaKue; }
    public void setNamaKue(String namaKue) { this.namaKue = namaKue; }
    public String getHargaKue() { return hargaKue; }
    public void setHargaKue(String hargaKue) { this.hargaKue = hargaKue; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }
}