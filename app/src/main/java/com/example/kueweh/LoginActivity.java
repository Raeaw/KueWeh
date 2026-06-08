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
    private TextView tvToRegister, tvLupaPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        SharedPreferences sharedPref = getSharedPreferences("KueWehSession", MODE_PRIVATE);

        if (sharedPref.getBoolean("isLoggedIn", false)) {
            String savedEmail = sharedPref.getString("userEmail", "");
            if (savedEmail.equals("admin@kueweh.com")) {
                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
            } else {
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
        tvLupaPassword = findViewById(R.id.tvLupaPassword);

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
                    boolean match = BCrypt.checkpw(password, user.passwordHash);

                    if (match) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("userEmail", user.email);
                        editor.putString("userName", user.namaLengkap);
                        // Simpan juga foto profil ke session jika ada
                        if (user.profileImageUrl != null && !user.profileImageUrl.isEmpty()) {
                            editor.putString("profileImageUrl", user.profileImageUrl);
                        }
                        editor.apply();

                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Selamat Datang!", Toast.LENGTH_SHORT).show();
                            if (user.email.equals("admin@kueweh.com")) {
                                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                            } else {
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

        // ← Tombol Lupa Password dihubungkan ke ForgotPasswordActivity
        tvLupaPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        tvToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }
}