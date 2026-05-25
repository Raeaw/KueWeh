package com.example.kueweh;


import java.util.List;

public class BatchPesanan {
    public long timestamp;
    public List<Pesanan> items;
    public float averageRating;

    public BatchPesanan(long timestamp, List<Pesanan> items) {
        this.timestamp = timestamp;
        this.items = items;
    }
}