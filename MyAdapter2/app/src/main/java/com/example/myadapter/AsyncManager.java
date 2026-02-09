package com.example.myadapter;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncManager {

    // Interface para devolver los resultados a la UI
    public interface AsyncCallback {
        void onComplete(String tipo, long tiempo);
    }

    // 1. MÉTODO: THREAD PURO (El que te daba el error)
    public void runThread(AsyncCallback callback) {
        long startTime = System.currentTimeMillis();
        new Thread(() -> {
            simularCargaDensa(); // Tarea pesada
            long endTime = System.currentTimeMillis() - startTime;

            // Volver al hilo principal para actualizar la tabla
            new Handler(Looper.getMainLooper()).post(() ->
                    callback.onComplete("Java Thread", endTime));
        }).start();
    }

    // 2. MÉTODO: EXECUTOR SERVICE
    public void runExecutor(AsyncCallback callback) {
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            simularCargaDensa();
            long endTime = System.currentTimeMillis() - startTime;

            new Handler(Looper.getMainLooper()).post(() ->
                    callback.onComplete("ExecutorService", endTime));
        });
    }

    // 3. MÉTODO: ASYNC TASK (Deprecado - Para la comparativa)
    @SuppressWarnings("deprecation")
    public void runAsyncTask(AsyncCallback callback) {
        final long startTime = System.currentTimeMillis();
        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                simularCargaDensa();
                return System.currentTimeMillis() - startTime;
            }

            @Override
            protected void onPostExecute(Long tiempo) {
                callback.onComplete("AsyncTask (Deprecated)", tiempo);
            }
        }.execute();
    }

    // Método para que el test dure lo suficiente para ser medido
    private void simularCargaDensa() {
        try {
            Thread.sleep(250); // Simulamos procesamiento de datos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
