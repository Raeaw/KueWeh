package com.example.kueweh; // Sesuaikan dengan nama package-mu

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Ini memberi tahu Room untuk membuat tabel bernama "tabel_kue"
@Entity(tableName = "tabel_kue")
public class Kue {

    // ID ini wajib ada untuk database, autoGenerate = true berarti AI (Auto Increment)
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nama;
    private String harga;
    private String rating;
    private String ulasan;
    private String imageUrl;

    // Constructor bawaan
    public Kue(String nama, String harga, String rating, String ulasan, String imageUrl) {
        this.nama = nama;
        this.harga = harga;
        this.rating = rating;
        this.ulasan = ulasan;
        this.imageUrl = imageUrl;
    }

    // Getter dan Setter sangat penting agar Room bisa membaca dan menulis data
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