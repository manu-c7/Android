package com.example.myadapter;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executors;

public class AsyncManager {
    public interface AsyncCallback { void onComplete(String type, long time); }

    public void runThread(AsyncCallback cb) {
        long start = System.currentTimeMillis();
        new Thread(() -> { 
            simulateProcess(); 
            new Handler(Looper.getMainLooper()).post(() -> cb.onComplete("Thread", System.currentTimeMillis() - start));
        }).start();
    }

    public void runExecutor(AsyncCallback cb) {
        long start = System.currentTimeMillis();
        Executors.newSingleThreadExecutor().execute(() -> {
            simulateProcess();
            new Handler(Looper.getMainLooper()).post(() -> cb.onComplete("Executor", System.currentTimeMillis() - start));
        });
    }

    @SuppressWarnings("deprecation")
    public void runAsyncTask(AsyncCallback cb) {
        long start = System.currentTimeMillis();
        new AsyncTask<Void, Void, Long>() {
            protected Long doInBackground(Void... v) { simulateProcess(); return System.currentTimeMillis() - start; }
            protected void onPostExecute(Long t) { cb.onComplete("AsyncTask", t); }
        }.execute();
    }

    private void simulateProcess() { try { Thread.sleep(200); } catch (Exception ignored) {} }
}
