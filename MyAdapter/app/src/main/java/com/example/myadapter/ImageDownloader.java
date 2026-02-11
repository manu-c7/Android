package com.example.myadapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Executors;

public class ImageDownloader {

    private final android.os.Handler handler = new Handler(Looper.getMainLooper());

    // Overloaded method for backward compatibility
    public void download(String url, ImageView imageView) {
        download(url, imageView, null);
    }

    public void download(String url, ImageView imageView, Runnable onComplete) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                InputStream inputStream = new URL(url).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                handler.post(() -> {
                    imageView.setImageBitmap(bitmap);
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
