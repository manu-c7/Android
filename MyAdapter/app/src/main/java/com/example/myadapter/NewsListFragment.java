package com.example.myadapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.opencsv.CSVReader;

import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class NewsListFragment extends Fragment {

    private UniversalAdapter adapter;
    private final ArrayList<NewsItem> newsList = new ArrayList<>();
    private final ArrayList<NewsItem> allNews = new ArrayList<>();
    private GridView gridView;
    private ProgressBar progressBar;

    public interface OnNewsItemSelectedListener {
        void onNewsItemSelected(NewsItem newsItem);
    }

    private OnNewsItemSelectedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnNewsItemSelectedListener) {
            listener = (OnNewsItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnNewsItemSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridView = view.findViewById(R.id.gridView);
        progressBar = view.findViewById(R.id.progressBar);

        adapter = new UniversalAdapter(getContext(), newsList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            if (listener != null) {
                listener.onNewsItemSelected(newsList.get(position));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Always download fresh data when the fragment becomes visible
        downloadCsvData();
    }

    private String getUrlForCurrentLanguage() {
        String language = Locale.getDefault().getLanguage();
        switch (language) {
            case "en":
                return "https://docs.google.com/spreadsheets/d/e/2PACX-1vT3gYOrCad2830q8ulaGFH-oe9Xt0j_onz3sZ59UcB_TKFZzxBT-q4FjUnGDfY_cfww1Q1i0_xknMPc/pub?gid=1677284472&single=true&output=csv";
            case "fr":
                return "https://docs.google.com/spreadsheets/d/e/2PACX-1vT3gYOrCad2830q8ulaGFH-oe9Xt0j_onz3sZ59UcB_TKFZzxBT-q4FjUnGDfY_cfww1Q1i0_xknMPc/pub?gid=1900680414&single=true&output=csv";
            case "es":
            default:
                return "https://docs.google.com/spreadsheets/d/e/2PACX-1vT3gYOrCad2830q8ulaGFH-oe9Xt0j_onz3sZ59UcB_TKFZzxBT-q4FjUnGDfY_cfww1Q1i0_xknMPc/pub?gid=0&single=true&output=csv";
        }
    }

    private void downloadCsvData() {
        progressBar.setVisibility(View.VISIBLE);

        Executors.newSingleThreadExecutor().execute(() -> {
            final List<NewsItem> tempNewsList = new ArrayList<>();
            Handler handler = new Handler(Looper.getMainLooper());

            try (CSVReader reader = new CSVReader(new InputStreamReader(new URL(getUrlForCurrentLanguage()).openStream()))) {
                reader.skip(1); // Skip header row
                String[] tokens;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

                while ((tokens = reader.readNext()) != null) {
                    if (tokens.length >= 5) {
                        Date date = new Date(); // Default date
                        int importance = 0; // Default importance

                        if (tokens.length >= 7) {
                            try {
                                date = dateFormat.parse(tokens[5]);
                                importance = Integer.parseInt(tokens[6]);
                            } catch (Exception e) {
                                Log.e("CSV_PARSE_ERROR", "Failed to parse date/importance for line: " + String.join(",", tokens), e);
                            }
                        }
                        tempNewsList.add(new NewsItem(tokens[0], tokens[1], tokens[2], tokens[3], tokens[4], date, importance));
                    }
                }

                handler.post(() -> {
                    allNews.clear();
                    allNews.addAll(tempNewsList);
                    filter(""); // Show all news initially
                    progressBar.setVisibility(View.GONE);

                    if (allNews.isEmpty()) {
                        Toast.makeText(getContext(), R.string.no_data_found, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), getString(R.string.connection_error, e.getMessage()), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    public void filter(String query) {
        newsList.clear();
        if (query.isEmpty()) {
            newsList.addAll(allNews);
        } else {
            for (NewsItem item : allNews) {
                if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    newsList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void sort(Comparator<NewsItem> comparator) {
        Collections.sort(newsList, comparator);
        adapter.notifyDataSetChanged();
    }
}
