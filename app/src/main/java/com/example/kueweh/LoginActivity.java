package com.example.kueweh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cek Session: Jika pengguna sudah login sebelumnya, langsung lempar ke MainActivity
        SharedPreferences sharedPref = getSharedPreferences("KueWehSession", MODE_PRIVATE);

        // Jika pengguna sudah login sebelumnya
        if (sharedPref.getBoolean("isLoggedIn", false)) {
            // Ambil email yang tersimpan di sesi terakhir
            String savedEmail = sharedPref.getString("userEmail", "");

            if (savedEmail.equals("admin@kueweh.com")) {
                // Jika yang terakhir login adalah Admin, arahkan ke AdminActivity
                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
            } else {
                // Jika kustomer biasa, arahkan ke MainActivity
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }

            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvToRegister = findViewById(R.id.tvToRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                UserDao userDao = AppDatabase.getInstance(LoginActivity.this).userDao();
                User user = userDao.getUserByEmail(email);

                if (user != null) {
                    // Komparasi password mentah dengan string hash di SQLite Room
                    boolean match = BCrypt.checkpw(password, user.passwordHash);

                    if (match) {
                        // Simpan Status Login Lokal di SharedPreferences
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("userEmail", user.email);
                        editor.putString("userName", user.namaLengkap);
                        editor.apply();

                        // LOGIKA PENGECEKAN ADMIN (OPSI 1)
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Selamat Datang!", Toast.LENGTH_SHORT).show();

                            if (user.email.equals("admin@kueweh.com")) {
                                // Jika Admin, masuk ke AdminActivity
                                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                            } else {
                                // Jika Customer biasa, masuk ke MainActivity (Home)
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Password salah!", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Email tidak terdaftar!", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });

        tvToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }
}