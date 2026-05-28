package com.example.kueweh;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FavoriteFragment extends Fragment {

    private RecyclerView rvFavorite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        rvFavorite = view.findViewById(R.id.rvFavorite);
        rvFavorite.setLayoutManager(new GridLayoutManager(getContext(), 2));

        loadFavoriteItems();

        return view;
    }

    private void loadFavoriteItems() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("KueWehSession", Context.MODE_PRIVATE);
        String currentEmail = sharedPref.getString("userEmail", "");

        new Thread(() -> {
            // Gunakan INNER JOIN dari DAO tadi
            List<Kue> listFavorit = AppDatabase.getInstance(getContext()).favoritDao().getFavoritKueByUser(currentEmail);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // Panggil KueAdapter, set isAdmin = false
                    KueAdapter adapter = new KueAdapter(getContext(), listFavorit, false);
                    rvFavorite.setAdapter(adapter);
                });
            }
        }).start();
    }
}