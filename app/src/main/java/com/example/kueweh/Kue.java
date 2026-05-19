package com.example.kueweh;

public class Kue {
    private String nama;
    private String harga;
    private String rating;
    private String ulasan;
    private String imageUrl;

    public Kue(String nama, String harga, String rating, String ulasan, String imageUrl) {
        this.nama = nama;
        this.harga = harga;
        this.rating = rating;
        this.ulasan = ulasan;
        this.imageUrl = imageUrl;
    }

    // Getter methods
    public String getNama() { return nama; }
    public String getHarga() { return harga; }
    public String getRating() { return rating; }
    public String getUlasan() { return ulasan; }
    public String getImageUrl() { return imageUrl; }
}
