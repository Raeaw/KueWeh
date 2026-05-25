package com.example.kueweh;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tabel_pesanan")
public class Pesanan {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userEmail; // Untuk membedakan pesanan tiap akun
    private String namaKue;
    private String hargaKue;
    private String imageUrl;
    private long timestamp;
    private float rating;

    public Pesanan(String userEmail, String namaKue, String hargaKue, String imageUrl, long timestamp) {
        this.userEmail = userEmail;
        this.namaKue = namaKue;
        this.hargaKue = hargaKue;
        this.imageUrl = imageUrl;
        this.rating = 0;
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
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
}