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

            if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || konfirmasi.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(konfirmasi)) {
                Toast.makeText(this, "Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show();
                return;
            }

            // Jalankan operasi database di thread terpisah (atau gunakan allowMainThreadQueries jika diizinkan di AppDatabase)
            new Thread(() -> {
                UserDao userDao = AppDatabase.getInstance(RegisterActivity.this).userDao();

                // Validasi email unik
                if (userDao.getUserByEmail(email) != null) {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Email sudah terdaftar!", Toast.LENGTH_SHORT).show());
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