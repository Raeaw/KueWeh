package com.example.kueweh;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FavoritDao {

    @Insert
    void insertFavorit(Favorit favorit);

    @Query("DELETE FROM tabel_favorit WHERE userEmail = :email AND namaKue = :nama")
    void hapusFavorit(String email, String nama);

    @Query("SELECT * FROM tabel_favorit WHERE userEmail = :email AND namaKue = :nama LIMIT 1")
    Favorit cekFavorit(String email, String nama);

    // INNER JOIN: Mengambil detail lengkap kue yang disukai user tertentu
    @Query("SELECT tabel_kue.* FROM tabel_kue INNER JOIN tabel_favorit ON tabel_kue.nama = tabel_favorit.namaKue WHERE tabel_favorit.userEmail = :email")
    List<Kue> getFavoritKueByUser(String email);
}