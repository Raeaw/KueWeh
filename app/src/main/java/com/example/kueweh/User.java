package com.example.kueweh;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String namaLengkap;
    public String email;
    public String passwordHash;
    public String profileImageUrl; // Foto profil pengguna (URI string)

    public User(String namaLengkap, String email, String passwordHash) {
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.passwordHash = passwordHash;
        this.profileImageUrl = "";
    }
}