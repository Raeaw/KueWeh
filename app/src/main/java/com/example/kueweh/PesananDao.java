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
}