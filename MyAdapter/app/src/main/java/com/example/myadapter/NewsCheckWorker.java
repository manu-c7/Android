package com.example.myadapter;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.opencsv.CSVReader;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class NewsCheckWorker extends Worker {

    private static final String TAG = "NewsWorker";
    private static final String CHANNEL_ID = "news_channel";

    public NewsCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private String getUrlForCurrentLanguage() {
        String language = Locale.getDefault().getLanguage();
        switch (language) {
            case "en":
                return "https://docs.google.com/spreadsheets/d/e/2PACX-1vT3gYOrCad2830q8ulaGFH-oe9Xt0j_onz3sZ59UcB_TKFZzxBT-q4FjUnGDfY_cfww1Q1i0_xknMPc/pub?gid=1677284472&single=true&output=csv";
            case "fr":
                return "https://docs.google.com/spreadsheets/d/e/2PACX-1vT3gYOrCad2830q8ulaGFH-oe9Xt0j_onz3sZ59UcB_TKFZzxBT-q4FjUnGDfY_cfww1Q1i0_xknMPc/pub?gid=1900680414&single=true&output=csv";
            case "es":
            default:
                return "https://docs.google.com/spreadsheets/d/e/2PACX-1vT3gYOrCad2830q8ulaGFH-oe9Xt0j_onz3sZ59UcB_TKFZzxBT-q4FjUnGDfY_cfww1Q1i0_xknMPc/pub?gid=0&single=true&output=csv";
        }
    }

    private String getSeenNewsPreferenceKey() {
        return "seen_news_" + Locale.getDefault().getLanguage();
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Worker started.");
        try {
            String newsUrl = getUrlForCurrentLanguage();
            Log.d(TAG, "Checking for news at URL: " + newsUrl);

            SharedPreferences prefs = getApplicationContext().getSharedPreferences("news_prefs", Context.MODE_PRIVATE);
            String prefKey = getSeenNewsPreferenceKey();
            Set<String> seenNews = new HashSet<>(prefs.getStringSet(prefKey, new HashSet<>()));
            Log.d(TAG, "Found " + seenNews.size() + " seen news items for language: " + Locale.getDefault().getLanguage());

            URL url = new URL(newsUrl);
            CSVReader reader = new CSVReader(new InputStreamReader(url.openStream()));
            reader.skip(1); // Skip header

            String[] nextLine;
            int newNewsCount = 0;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length > 0 && !seenNews.contains(nextLine[0])) {
                    newNewsCount++;
                    seenNews.add(nextLine[0]);
                }
            }
            Log.d(TAG, "Found " + newNewsCount + " new news items.");

            if (newNewsCount > 0) {
                createNotificationChannel();
                showNotification(newNewsCount);
                prefs.edit().putStringSet(prefKey, seenNews).apply();
                Log.d(TAG, "Saved new seen items. Total seen news now: " + seenNews.size());
            }

            Log.d(TAG, "Worker finished successfully.");
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Worker failed.", e);
            return Result.failure();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getApplicationContext().getString(R.string.notification_channel_name);
            String description = getApplicationContext().getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel created or already exists.");
        }
    }

    private void showNotification(int newNewsCount) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Notification permission not granted. Cannot show notification.");
            return;
        }

        Log.d(TAG, "Showing notification for " + newNewsCount + " new items.");
        String contentText = getApplicationContext().getString(R.string.notification_text, newNewsCount);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_calendar)
                .setContentTitle(getApplicationContext().getString(R.string.notification_title))
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1, builder.build());
    }
}
