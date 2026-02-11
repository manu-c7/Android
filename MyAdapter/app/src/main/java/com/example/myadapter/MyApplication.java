package com.example.myadapter;

import android.app.Application;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ProcessLifecycleOwner.get().getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_START) {
                // App came to foreground
                triggerImmediateNewsCheck();
            }
        });
    }

    private void triggerImmediateNewsCheck() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NewsCheckWorker.class).build();
        WorkManager.getInstance(this).enqueue(workRequest);
    }
}
