package com.example.kueweh;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface KeranjangDao {

    @Insert
    void insertKeranjang(Keranjang keranjang);

    @Update
    void updateKeranjang(Keranjang keranjang);

    @Delete
    void deleteKeranjang(Keranjang keranjang);

    // Cek apakah produk sudah ada di keranjang untuk user tertentu
    @Query("SELECT * FROM tabel_keranjang WHERE userEmail = :email AND namaKue = :nama LIMIT 1")
    Keranjang cekItemKeranjang(String email, String nama);

    // Ambil semua isi keranjang milik user
    @Query("SELECT * FROM tabel_keranjang WHERE userEmail = :email")
    List<Keranjang> getKeranjangByUser(String email);

    // Kosongkan keranjang setelah checkout sukses
    @Query("DELETE FROM tabel_keranjang WHERE userEmail = :email")
    void clearKeranjang(String email);
}