package com.example.myadapter;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SlidingPaneLayout slidingPaneLayout = findViewById(R.id.sliding_pane_layout);
        slidingPaneLayout.setLockMode(SlidingPaneLayout.LOCK_MODE_LOCKED);

        scheduleNewsWorker();
    }

    private void scheduleNewsWorker() {
        PeriodicWorkRequest newsRequest = 
            new PeriodicWorkRequest.Builder(NewsWorker.class, 1, TimeUnit.HOURS)
            .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "news_update_work",
            ExistingPeriodicWorkPolicy.KEEP,
            newsRequest);
    }
}
