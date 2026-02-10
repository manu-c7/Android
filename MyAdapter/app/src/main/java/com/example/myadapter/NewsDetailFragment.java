package com.example.myadapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NewsDetailFragment extends Fragment {

    private static final String ARG_NEWS_ITEM = "ARG_NEWS_ITEM";

    private NewsItem newsItem;

    public static NewsDetailFragment newInstance(NewsItem newsItem) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NEWS_ITEM, newsItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            newsItem = (NewsItem) getArguments().getSerializable(ARG_NEWS_ITEM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = view.findViewById(R.id.tvDetailTitle);
        TextView content = view.findViewById(R.id.tvDetailContent);
        ImageView image = view.findViewById(R.id.ivDetailImage);

        if (newsItem != null) {
            title.setText(newsItem.getTitle());
            content.setText(newsItem.getContent());

            if (newsItem.getThumbUrl() != null && !newsItem.getThumbUrl().isEmpty()) {
                new ImageDownloader().download(newsItem.getThumbUrl(), image);
            }
        }
    }
}
