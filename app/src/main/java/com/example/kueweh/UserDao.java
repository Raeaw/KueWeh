package com.example.kueweh;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {

    @Insert
    void registerUser(User user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Update
    void updateUser(User user);

    // Ganti password berdasarkan email
    @Query("UPDATE users SET passwordHash = :newHash WHERE email = :email")
    void updatePassword(String email, String newHash);

    // Simpan URL foto profil
    @Query("UPDATE users SET profileImageUrl = :imageUrl WHERE email = :email")
    void updateProfileImage(String email, String imageUrl);
}