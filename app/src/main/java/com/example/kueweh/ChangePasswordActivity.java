package com.example.kueweh;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.mindrot.jbcrypt.BCrypt;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etPasswordLama, etPasswordBaru, etKonfirmasi;
    private Button btnSimpan;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );
        setContentView(R.layout.activity_change_password);

        etPasswordLama = findViewById(R.id.etPasswordLama);
        etPasswordBaru = findViewById(R.id.etPasswordBaru);
        etKonfirmasi = findViewById(R.id.etKonfirmasiPasswordBaru);
        btnSimpan = findViewById(R.id.btnSimpanPassword);
        btnBack = findViewById(R.id.btnBackChangePassword);

        btnBack.setOnClickListener(v -> finish());

        SharedPreferences sharedPref = getSharedPreferences("KueWehSession", Context.MODE_PRIVATE);
        String currentEmail = sharedPref.getString("userEmail", "");

        btnSimpan.setOnClickListener(v -> {
            String passLama = etPasswordLama.getText().toString();
            String passBaru = etPasswordBaru.getText().toString();
            String konfirmasi = etKonfirmasi.getText().toString();

            if (passLama.isEmpty() || passBaru.isEmpty() || konfirmasi.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (passBaru.length() < 6) {
                etPasswordBaru.setError("Password baru minimal 6 karakter!");
                return;
            }
            if (!passBaru.equals(konfirmasi)) {
                etKonfirmasi.setError("Konfirmasi password tidak cocok!");
                return;
            }

            new Thread(() -> {
                UserDao userDao = AppDatabase.getInstance(this).userDao();
                User user = userDao.getUserByEmail(currentEmail);

                if (user == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Sesi tidak valid, silakan login ulang.", Toast.LENGTH_SHORT).show());
                    return;
                }

                boolean passLamaBenar = BCrypt.checkpw(passLama, user.passwordHash);
                if (!passLamaBenar) {
                    runOnUiThread(() -> {
                        etPasswordLama.setError("Password lama tidak sesuai!");
                        Toast.makeText(this, "Password lama salah!", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                String hashedBaru = BCrypt.hashpw(passBaru, BCrypt.gensalt());
                userDao.updatePassword(currentEmail, hashedBaru);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Password berhasil diubah! 🎉", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();
        });
    }
}