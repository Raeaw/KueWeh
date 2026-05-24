package com.example.kueweh;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface KueDao {

    @Insert
    void insertKue(Kue kue);

    @Query("SELECT * FROM tabel_kue")
    List<Kue> getAllKue();

    // TAMBAHKAN QUERY FILTER INI
    @Query("SELECT * FROM tabel_kue WHERE kategori = :kat")
    List<Kue> getKueByKategori(String kat);

    @Query("SELECT * FROM tabel_kue WHERE nama LIKE '%' || :keyword || '%'")
    List<Kue> searchKue(String keyword);

    @Update
    void updateKue(Kue kue);

    @Delete
    void deleteKue(Kue kue);
}