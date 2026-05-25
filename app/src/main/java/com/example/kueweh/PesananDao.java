package com.example.kueweh;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PesananDao {

    @Insert
    void insertPesanan(Pesanan pesanan);

    // Mengambil riwayat pesanan khusus milik user yang sedang login saat ini (diurutkan dari yang terbaru)
    @Query("SELECT * FROM tabel_pesanan WHERE userEmail = :email ORDER BY id DESC")
    List<Pesanan> getPesananByUser(String email);

    @Update
    void updatePesanan(Pesanan pesanan);

    // Mengambil rata-rata rating sebuah kue (hanya yang sudah diberi rating > 0)
// 1. Menghitung rata-rata rating Global (Tiap akun dihitung 1 suara rata-rata)
    @Query("SELECT COALESCE(AVG(userAvg), 0.0) FROM (SELECT AVG(rating) AS userAvg FROM tabel_pesanan WHERE namaKue = :nama AND rating > 0 GROUP BY userEmail)")
    float getTrueGlobalAverageRating(String nama);

    // 2. Menghitung jumlah akun unik yang memberikan rating
    @Query("SELECT COUNT(DISTINCT userEmail) FROM tabel_pesanan WHERE namaKue = :nama AND rating > 0")
    int getReviewerCount(String nama);

    // 3. Menghitung rata-rata rating khusus milik user yang sedang login
    @Query("SELECT COALESCE(AVG(rating), 0.0) FROM tabel_pesanan WHERE namaKue = :nama AND userEmail = :email AND rating > 0")
    float getPersonalAverageRating(String nama, String email);

    // Menghitung berapa kali user ini sudah memesan item tersebut
    @Query("SELECT COUNT(*) FROM tabel_pesanan WHERE namaKue = :nama AND userEmail = :email")
    int getOrderCountPerUser(String nama, String email);
}