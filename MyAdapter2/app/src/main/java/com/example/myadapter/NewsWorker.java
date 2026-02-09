package com.example.myadapter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NewsWorker extends Worker {
    public NewsWorker(@NonNull Context context, @NonNull WorkerParameters params) { super(context, params); }
    @NonNull
    @Override
    public Result doWork() {
        // 1. Obtener GID de strings.xml (esto debería hacerse en un contexto con acceso a recursos)
        String gid = getApplicationContext().getString(R.string.sheet_gid);
        // 2. Descargar CSV y comparar con datos locales (lógica de descarga omitida por brevedad)
        // 3. Si hay ID nuevo, lanzar notificación
        showNotification("Nuevos datos", "Se han actualizado las noticias.");
        return Result.success();
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
