package com.example.kueweh;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tabel_kue")
public class Kue {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nama;
    private String harga;
    private String rating;
    private String ulasan;
    private String imageUrl;
    private String kategori; // 1. TAMBAHKAN KOLOM INI (Cake, Cookies, Drink)

    // 2. UPDATE CONSTRUCTOR (Tambahkan String kategori di akhir)
    public Kue(String nama, String harga, String rating, String ulasan, String imageUrl, String kategori) {
        this.nama = nama;
        this.harga = harga;
        this.rating = rating;
        this.ulasan = ulasan;
        this.imageUrl = imageUrl;
        this.kategori = kategori;
    }

    // Getter dan Setter untuk Kategori
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    // (Getter & Setter lainnya di bawah ini biarkan tetap sama seperti sebelumnya)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getHarga() { return harga; }
    public void setHarga(String harga) { this.harga = harga; }
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
    public String getUlasan() { return ulasan; }
    public void setUlasan(String ulasan) { this.ulasan = ulasan; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}