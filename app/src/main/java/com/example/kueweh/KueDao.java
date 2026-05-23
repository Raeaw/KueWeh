package com.example.kueweh;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface KueDao {

    // Method untuk menambah kue baru ke database
    @Insert
    void insertKue(Kue kue);

    // Method untuk mengambil semua kue dari database
    @Query("SELECT * FROM tabel_kue")
    List<Kue> getAllKue();
}