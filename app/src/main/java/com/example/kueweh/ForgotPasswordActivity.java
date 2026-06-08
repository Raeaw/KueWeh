package com.example.kueweh;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.mindrot.jbcrypt.BCrypt;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail, etNewPassword, etKonfirmasiPassword;
    private Button btnVerifikasiEmail, btnResetPassword;
    private LinearLayout layoutResetForm;
    private TextView tvBack;
    private String verifiedEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etForgotEmail);
        btnVerifikasiEmail = findViewById(R.id.btnVerifikasiEmail);
        layoutResetForm = findViewById(R.id.layoutResetForm);
        etNewPassword = findViewById(R.id.etNewPassword);
        etKonfirmasiPassword = findViewById(R.id.etKonfirmasiPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvBack = findViewById(R.id.tvBackToLogin);

        // Step 1: Verifikasi email
        btnVerifikasiEmail.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Masukkan email kamu dulu!", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                User user = AppDatabase.getInstance(this).userDao().getUserByEmail(email);
                runOnUiThread(() -> {
                    if (user != null) {
                        verifiedEmail = email;
                        // Tampilkan form reset password
                        etEmail.setEnabled(false);
                        btnVerifikasiEmail.setEnabled(false);
                        btnVerifikasiEmail.setText("✓ Email Terverifikasi");
                        btnVerifikasiEmail.setBackgroundTintList(
                                android.content.res.ColorStateList.valueOf(
                                        android.graphics.Color.parseColor("#4CAF50")));
                        layoutResetForm.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Email ditemukan! Silakan buat password baru.", Toast.LENGTH_SHORT).show();
                    } else {
                        etEmail.setError("Email tidak terdaftar!");
                        Toast.makeText(this, "Email tidak terdaftar.", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        // Step 2: Reset password
        btnResetPassword.setOnClickListener(v -> {
            String newPass = etNewPassword.getText().toString();
            String konfirmasi = etKonfirmasiPassword.getText().toString();

            if (newPass.isEmpty() || konfirmasi.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newPass.length() < 6) {
                etNewPassword.setError("Password minimal 6 karakter!");
                return;
            }
            if (!newPass.equals(konfirmasi)) {
                etKonfirmasiPassword.setError("Konfirmasi password tidak cocok!");
                return;
            }

            new Thread(() -> {
                String hashedPassword = BCrypt.hashpw(newPass, BCrypt.gensalt());
                AppDatabase.getInstance(this).userDao().updatePassword(verifiedEmail, hashedPassword);

                runOnUiThread(() -> {
                    new android.app.AlertDialog.Builder(this)
                            .setTitle("Password Berhasil Diubah! 🎉")
                            .setMessage("Password kamu telah diperbarui. Silakan login dengan password baru.")
                            .setPositiveButton("Login Sekarang", (dialog, which) -> finish())
                            .setCancelable(false)
                            .show();
                });
            }).start();
        });

        tvBack.setOnClickListener(v -> finish());
    }
}