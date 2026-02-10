package com.example.myadapter;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            NewsItem item = (NewsItem) getIntent().getSerializableExtra("EXTRA_ITEM");
            if (item != null) {
                NewsDetailFragment fragment = NewsDetailFragment.newInstance(item);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container_view, fragment)
                        .commit();
            }
        }
    }
}
