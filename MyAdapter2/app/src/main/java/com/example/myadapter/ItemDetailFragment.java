package com.example.myadapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ItemDetailFragment extends Fragment {

    private static final String ARG_ITEM_ID = "item_id";

    public static ItemDetailFragment newInstance(String item_id) {
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, item_id);
        ItemDetailFragment fragment = new ItemDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView itemDetail = view.findViewById(R.id.item_detail);
        if (getArguments() != null) {
            itemDetail.setText(getArguments().getString(ARG_ITEM_ID));
        }
    }
}
