package com.example.myadapter;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        TextView title = findViewById(R.id.tvDetailTitle);
        TextView content = findViewById(R.id.tvDetailContent);
        ImageView image = findViewById(R.id.ivDetailImage);

        NewsItem item = (NewsItem) getIntent().getSerializableExtra("EXTRA_ITEM");

        if (item != null) {
            title.setText(item.getTitle());
            content.setText(item.getContent());

            if (item.getThumbUrl() != null && !item.getThumbUrl().isEmpty()) {
                new ImageDownloader().download(item.getThumbUrl(), image);
            }
        }
    }
}
