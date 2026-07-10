# Kue Weh! 

Aplikasi Android untuk pemesanan kue, cookies, dan minuman secara online. Dibangun dengan Java native menggunakan arsitektur Activity + Fragment, dan Room sebagai local database.

## Fitur

### Pelanggan
- **Autentikasi**: Register, Login, Lupa Password, Ubah Password (password di-hash dengan BCrypt)
- **Home**: Jelajahi katalog kue dengan filter kategori (Cake, Cookies, Drink) dan pencarian real-time
- **Detail Produk**: Lihat detail kue, rating global, dan rating personal berdasarkan riwayat pembelian
- **Favorit**: Simpan produk favorit untuk akses cepat
- **Keranjang Belanja**: Tambah/kurangi jumlah item, checkout batch
- **Riwayat Pesanan**: Lihat pesanan yang dikelompokkan per transaksi (batch), lengkap dengan status per item
- **Rating & Ulasan**: Beri rating setelah pesanan berstatus "Selesai" — rating personal dan rata-rata global dihitung otomatis
- **Profil**: Ubah foto profil, ubah password, logout

### Admin
- **Dashboard Produk**: Tambah, edit, dan hapus produk kue (nama, harga, kategori, foto)
- **Kelola Pesanan**: Pantau semua pesanan masuk dari seluruh pelanggan, diproses per batch dan per item
- **Update Status**: Ubah status pesanan per item (Pending → Diproses → Selesai)

## Tech Stack

| Komponen | Teknologi |
|---|---|
| Bahasa | Java |
| Database lokal | Room (SQLite) |
| Image loading | Glide |
| Hashing password | jBCrypt |
| UI | Material Components, RecyclerView, CardView, ConstraintLayout |
| Build system | Gradle (Kotlin DSL) |
| Min SDK | 24 |
| Target/Compile SDK | 36 |

## Struktur Proyek

```
app/src/main/java/com/example/kueweh/
├── Activity (UI + Logic per layar)
│   ├── SplashActivity, LoginActivity, RegisterActivity
│   ├── ForgotPasswordActivity, ChangePasswordActivity
│   ├── MainActivity (pelanggan), AdminActivity (admin)
│   ├── DetailActivity, AddProductActivity, EditProductActivity
│   └── RiwayatPesananActivity
├── Fragment (halaman dalam bottom navigation)
│   ├── HomeFragment, FavoriteFragment, KeranjangFragment, ProfileFragment
│   └── AdminProdukFragment, AdminPesananFragment
├── Adapter (RecyclerView)
│   ├── KueAdapter, KeranjangAdapter, PesananAdapter
│   └── BatchAdapter, AdminPesananAdapter
├── Entity (Room)
│   ├── Kue, User, Pesanan, Keranjang, Favorit
├── Dao (Room)
│   ├── KueDao, UserDao, PesananDao, KeranjangDao, FavoritDao
└── AppDatabase.java
```

## Skema Database (Room)

- **tabel_kue** — katalog produk (nama, harga, rating, ulasan, kategori, gambar)
- **users** — akun pengguna (nama, email, password hash, foto profil)
- **tabel_pesanan** — riwayat transaksi per item, dikelompokkan berdasarkan `timestamp` (batch checkout), memiliki `status` dan `rating`
- **tabel_keranjang** — isi keranjang belanja aktif per user
- **tabel_favorit** — daftar produk favorit per user

## Cara Menjalankan

1. Clone repository ini
2. Buka di **Android Studio** (versi terbaru direkomendasikan)
3. Sinkronkan Gradle (Gradle 9.2.1, AGP 9.0.1)
4. Jalankan pada emulator/device dengan Android 7.0 (API 24) ke atas

```bash
./gradlew assembleDebug
```

### Akun Admin
Login menggunakan email berikut untuk masuk ke Dashboard Admin:
```
admin@kueweh.com
```
> Akun ini harus didaftarkan terlebih dahulu lewat halaman Register dengan email persis di atas.

## Catatan Implementasi

- Query database dijalankan menggunakan `allowMainThreadQueries()` untuk operasi baca sederhana, namun sebagian besar operasi tulis (insert/update) dijalankan di background thread (`new Thread(...)`) untuk mencegah UI freeze.
- Foto produk & profil disimpan sebagai URI lokal dengan `takePersistableUriPermission` agar tetap dapat diakses meski aplikasi ditutup.
- Rating produk menggunakan algoritma rata-rata per akun (bukan per transaksi) agar satu pengguna tidak bisa membanjiri rating hanya dengan memesan berkali-kali.

## Lisensi

Proyek ini dibuat untuk keperluan pembelajaran/tugas.
