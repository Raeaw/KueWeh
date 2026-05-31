package com.example.kueweh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNama, etEmail, etPassword, etKonfirmasi;
    private Button btnRegister;
    private TextView tvToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Membuat layar menembus status bar (Edge-to-Edge)
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );
        setContentView(R.layout.activity_register);

        etNama = findViewById(R.id.etRegisterNama);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etKonfirmasi = findViewById(R.id.etRegisterKonfirmasi);
        btnRegister = findViewById(R.id.btnRegister);
        tvToLogin = findViewById(R.id.tvToLogin);

        btnRegister.setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            String konfirmasi = etKonfirmasi.getText().toString();

            // 1. Cek apakah ada kolom yang kosong
            if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || konfirmasi.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Validasi Format Email (Hanya mengizinkan format email yang benar)
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Format email tidak valid! (Contoh: nama@email.com)");
                etEmail.requestFocus();
                return;
            }

            // 3. Validasi Panjang Password (Minimal 6 karakter)
            if (password.length() < 6) {
                etPassword.setError("Password minimal 6 karakter");
                etPassword.requestFocus();
                return;
            }

            // 4. Validasi Kecocokan Konfirmasi Password
            if (!password.equals(konfirmasi)) {
                etKonfirmasi.setError("Konfirmasi password tidak cocok");
                etKonfirmasi.requestFocus();
                return;
            }

            // Jalankan operasi database di thread terpisah
            new Thread(() -> {
                UserDao userDao = AppDatabase.getInstance(RegisterActivity.this).userDao();

                // Validasi email unik (Mencegah email yang sama didaftarkan 2 kali)
                if (userDao.getUserByEmail(email) != null) {
                    runOnUiThread(() -> {
                        etEmail.setError("Email sudah terdaftar!");
                        etEmail.requestFocus();
                        Toast.makeText(RegisterActivity.this, "Gunakan email lain atau silakan Login", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                // Amankan password menggunakan BCrypt Hash
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                // Simpan entitas User baru ke SQLite lokal
                User userBaru = new User(nama, email, hashedPassword);
                userDao.registerUser(userBaru);

                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Pendaftaran Berhasil!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                });
            }).start();
        });

        tvToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
}