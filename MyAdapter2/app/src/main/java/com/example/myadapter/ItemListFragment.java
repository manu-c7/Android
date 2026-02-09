package com.example.myadapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Dummy data for now, will be replaced with real data
        NewsAdapter adapter = new NewsAdapter(new ArrayList<>(), item -> {
            // Handle item click, navigate to detail
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, ItemDetailFragment.newInstance(item.getTitulo()))
                    .commit();
        });
        recyclerView.setAdapter(adapter);
        return view;
    }
}
