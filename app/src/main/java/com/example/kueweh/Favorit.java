package com.example.kueweh;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tabel_favorit")
public class Favorit {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String userEmail;
    public String namaKue;

    public Favorit(String userEmail, String namaKue) {
        this.userEmail = userEmail;
        this.namaKue = namaKue;
    }
}