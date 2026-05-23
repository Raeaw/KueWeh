package com.example.kueweh;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String namaLengkap;
    public String email;
    public String passwordHash; // Yang disimpan adalah hasil hash, bukan password asli

    public User(String namaLengkap, String email, String passwordHash) {
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.passwordHash = passwordHash;
    }
}