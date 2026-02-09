package com.example.myadapter;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class ItemListFragment extends Fragment {

    private NewsAdapter adapter;
    private NoticiasDB db;
    private final List<Noticia> newsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        db = new NoticiasDB(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NewsAdapter(newsList, item -> getParentFragmentManager().beginTransaction()
                .replace(R.id.detail_container, ItemDetailFragment.newInstance(item.getTitulo()))
                .commit());
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadNewsFromDb();
            swipeRefreshLayout.setRefreshing(false);
        });

        loadNewsFromDb();
        return view;
    }

    private void loadNewsFromDb() {
        int originalSize = newsList.size();
        newsList.clear();
        adapter.notifyItemRangeRemoved(0, originalSize);
        Cursor cursor = db.getNoticias("", ""); // Obtener todas las noticias
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("desc"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                int importancia = cursor.getInt(cursor.getColumnIndexOrThrow("importancia"));
                newsList.add(new Noticia(titulo, desc, fecha, importancia));
            }
            cursor.close();
        }
        adapter.notifyItemRangeInserted(0, newsList.size());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (db != null) {
            db.close();
        }
    }
}
