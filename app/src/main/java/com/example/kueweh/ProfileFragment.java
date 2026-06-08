package com.example.kueweh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

public class ProfileFragment extends Fragment {

    private ImageView imgProfilePicture;
    private TextView tvAvatarInitial;
    private String currentEmail;
    private SharedPreferences sharedPref;

    // Launcher untuk membuka galeri foto
    private ActivityResultLauncher<String[]> pilihFotoLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Daftarkan launcher SEBELUM onCreateView (wajib)
        pilihFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null && getContext() != null) {
                        // Minta izin akses permanen ke URI
                        requireContext().getContentResolver()
                                .takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        String uriString = uri.toString();

                        // Simpan ke SharedPreferences untuk sesi cepat
                        sharedPref.edit().putString("profileImageUrl", uriString).apply();

                        // Simpan ke database Room secara permanen
                        new Thread(() -> {
                            AppDatabase.getInstance(requireContext())
                                    .userDao().updateProfileImage(currentEmail, uriString);
                        }).start();

                        // Tampilkan foto baru
                        tampilkanFotoProfil(uriString);
                        Toast.makeText(getContext(), "Foto profil berhasil diubah!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView tvName = view.findViewById(R.id.tvProfileName);
        TextView tvEmail = view.findViewById(R.id.tvProfileEmail);
        Button btnLogout = view.findViewById(R.id.btnLogout);
        tvAvatarInitial = view.findViewById(R.id.tvAvatarInitial);
        imgProfilePicture = view.findViewById(R.id.imgProfilePicture);

        LinearLayout btnRiwayat = view.findViewById(R.id.btnRiwayatPesanan);
        LinearLayout btnUbahPassword = view.findViewById(R.id.btnUbahPassword);
        LinearLayout containerAvatar = view.findViewById(R.id.containerAvatar);

        sharedPref = requireActivity().getSharedPreferences("KueWehSession", Context.MODE_PRIVATE);
        currentEmail = sharedPref.getString("userEmail", "");
        String nama = sharedPref.getString("userName", "User");

        tvName.setText(nama);
        tvEmail.setText(currentEmail);

        // Set inisial avatar dari nama
        if (!nama.isEmpty()) {
            tvAvatarInitial.setText(String.valueOf(nama.charAt(0)).toUpperCase());
        }

        // Load foto profil: cek SharedPreferences dulu (cepat), lalu DB
        String savedImageUrl = sharedPref.getString("profileImageUrl", "");
        if (!savedImageUrl.isEmpty()) {
            tampilkanFotoProfil(savedImageUrl);
        } else {
            // Cek di database jika belum ada di SharedPreferences
            new Thread(() -> {
                User user = AppDatabase.getInstance(requireContext()).userDao().getUserByEmail(currentEmail);
                if (user != null && user.profileImageUrl != null && !user.profileImageUrl.isEmpty()) {
                    // Simpan ke SharedPreferences untuk next session
                    sharedPref.edit().putString("profileImageUrl", user.profileImageUrl).apply();
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> tampilkanFotoProfil(user.profileImageUrl));
                    }
                }
            }).start();
        }

        // Klik avatar untuk ganti foto
        containerAvatar.setOnClickListener(v -> {
            pilihFotoLauncher.launch(new String[]{"image/*"});
        });

        // Klik Riwayat Pesanan
        btnRiwayat.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RiwayatPesananActivity.class));
        });

        // Klik Ubah Password
        btnUbahPassword.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            sharedPref.edit().clear().apply();
            Toast.makeText(getContext(), "Berhasil Keluar", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void tampilkanFotoProfil(String imageUrl) {
        if (imgProfilePicture == null || tvAvatarInitial == null) return;

        try {
            Uri uri = Uri.parse(imageUrl);
            Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .into(imgProfilePicture);

            imgProfilePicture.setVisibility(View.VISIBLE);
            tvAvatarInitial.setVisibility(View.GONE);
        } catch (Exception e) {
            imgProfilePicture.setVisibility(View.GONE);
            tvAvatarInitial.setVisibility(View.VISIBLE);
        }
    }
}