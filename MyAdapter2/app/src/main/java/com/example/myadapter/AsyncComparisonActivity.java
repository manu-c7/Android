package com.example.myadapter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AsyncComparisonActivity extends AppCompatActivity {

    private TextView threadTime, executorTime, asyncTaskTime;
    private AsyncManager asyncManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_comparison);

        threadTime = findViewById(R.id.thread_time);
        executorTime = findViewById(R.id.executor_time);
        asyncTaskTime = findViewById(R.id.asynctask_time);
        Button startButton = findViewById(R.id.start_button);

        asyncManager = new AsyncManager();

        startButton.setOnClickListener(v -> {
            // Reset texts
            threadTime.setText("Thread:");
            executorTime.setText("Executor:");
            asyncTaskTime.setText("AsyncTask (deprecado):");

            runTests();
        });
    }

    private void runTests() {
        AsyncManager.AsyncCallback callback = (type, time) -> {
            String text = type + ": " + time + " ms";
            switch (type) {
                case "Thread":
                    threadTime.setText(text);
                    break;
                case "Executor":
                    executorTime.setText(text);
                    break;
                case "AsyncTask":
                    // AsyncTask is deprecated because it can cause context leaks,
                    // missed callbacks, or crashes on configuration changes.
                    // It's also inefficient for long-running tasks.
                    // Modern alternatives like Executors, Kotlin Coroutines, or RxJava are preferred.
                    asyncTaskTime.setText(text + " (Deprecado)");
                    break;
            }
        };

        asyncManager.runThread(callback);
        asyncManager.runExecutor(callback);
        asyncManager.runAsyncTask(callback);
    }
}
