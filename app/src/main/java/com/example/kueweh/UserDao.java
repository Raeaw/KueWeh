package com.example.kueweh;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    // Menyimpan user baru saat Register
    @Insert
    void registerUser(User user);

    // Mencari user berdasarkan email saat Login
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);
}