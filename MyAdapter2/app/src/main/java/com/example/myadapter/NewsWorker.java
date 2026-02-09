package com.example.myadapter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsWorker extends Worker {
    public NewsWorker(@NonNull Context context, @NonNull WorkerParameters params) { super(context, params); }

    @NonNull
    @Override
    public Result doWork() {
        String gid = getApplicationContext().getString(R.string.sheet_gid);
        String urlString = "https://docs.google.com/spreadsheets/d/e/2PACX-1vT-k1-Yj2e9L1e-T8Yj-3Z-1Y/pub?gid=" + gid + "&single=true&output=csv";

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        boolean hasNewData = false;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            NoticiasDB db = new NoticiasDB(getApplicationContext());
            db.clearNoticias(); // Limpiamos los datos antiguos

            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) { // Omitimos la cabecera del CSV
                    isFirstLine = false;
                    continue;
                }

                String[] tokens = line.split(",", -1); // -1 para incluir campos vacíos
                if (tokens.length >= 5) {
                    try {
                        int id = Integer.parseInt(tokens[0]);
                        String titulo = tokens[1];
                        String desc = tokens[2];
                        String fecha = tokens[3];
                        int importancia = Integer.parseInt(tokens[4]);
                        db.insertNoticia(id, titulo, desc, fecha, importancia);
                        hasNewData = true;
                    } catch (NumberFormatException e) {
                        Log.e("NewsWorker", "Error al parsear número en la línea: " + line);
                    }
                }
            }
            db.close();

            if (hasNewData) {
                showNotification("Nuevos datos", "Se han actualizado las noticias.");
            }

            return Result.success();
        } catch (Exception e) {
            Log.e("NewsWorker", "Error al descargar o procesar el CSV", e);
            return Result.failure();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    Log.e("NewsWorker", "Error cerrando el reader", e);
                }
            }
        }
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("news_channel", "Nuevas Noticias", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "news_channel")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(1, builder.build());
    }
}
