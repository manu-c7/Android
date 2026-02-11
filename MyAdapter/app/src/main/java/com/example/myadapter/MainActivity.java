package com.example.myadapter;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;

import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements NewsListFragment.OnNewsItemSelectedListener {

    private NewsListFragment newsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add a LayoutTransition to animate changes in the toolbar
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        toolbar.setLayoutTransition(layoutTransition);

        newsListFragment = (NewsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (newsListFragment == null) {
            // For two-pane layout
            newsListFragment = (NewsListFragment) getSupportFragmentManager().findFragmentById(R.id.list_container);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (newsListFragment != null) {
                    newsListFragment.filter(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newsListFragment != null) {
                    newsListFragment.filter(newText);
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (newsListFragment == null) {
            return super.onOptionsItemSelected(item);
        }

        int itemId = item.getItemId();
        if (itemId == R.id.action_sort_by_date) {
            newsListFragment.sort(Comparator.comparing(NewsItem::getPublicationDate).reversed());
            return true;
        } else if (itemId == R.id.action_sort_by_importance) {
            newsListFragment.sort(Comparator.comparingInt(NewsItem::getImportance).reversed());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewsItemSelected(NewsItem newsItem) {
        View detailContainer = findViewById(R.id.detail_container);

        if (detailContainer != null) {
            // Two-pane layout
            NewsDetailFragment fragment = NewsDetailFragment.newInstance(newsItem);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        } else {
            // Single-pane layout
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("EXTRA_ITEM", newsItem);
            startActivity(intent);
        }
    }
}
