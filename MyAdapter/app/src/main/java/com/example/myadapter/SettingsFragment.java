package com.example.myadapter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class SettingsFragment extends PreferenceFragmentCompat {

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private SwitchPreferenceCompat notificationsSwitch;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        notificationsSwitch = findPreference("notifications_enabled");

        // Register the permission callback
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission is granted. Schedule the work.
                notificationsSwitch.setChecked(true);
                scheduleNewsCheck();
            } else {
                // Permission is denied. Uncheck the switch.
                notificationsSwitch.setChecked(false);
            }
        });

        notificationsSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean enabled = (boolean) newValue;
            if (enabled) {
                requestNotificationPermission();
            } else {
                WorkManager.getInstance(getContext()).cancelAllWorkByTag("news_check");
            }
            // Return false because we are handling the state of the switch manually
            return false;
        });

        findPreference("notification_interval").setOnPreferenceChangeListener((preference, newValue) -> {
            // Re-schedule if the interval changes
            if (notificationsSwitch.isChecked()) {
                scheduleNewsCheck();
            }
            return true;
        });
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted
                notificationsSwitch.setChecked(true);
                scheduleNewsCheck();
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            // For older versions, permission is granted by default
            notificationsSwitch.setChecked(true);
            scheduleNewsCheck();
        }
    }

    private void scheduleNewsCheck() {
        WorkManager.getInstance(getContext()).cancelAllWorkByTag("news_check");

        long interval = Long.parseLong(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("notification_interval", "900000"));

        if (interval > 0) {
            // Add a network constraint to the work request
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(NewsCheckWorker.class, interval, TimeUnit.MILLISECONDS)
                    .setConstraints(constraints)
                    .addTag("news_check")
                    .build();
            
            WorkManager.getInstance(getContext()).enqueue(workRequest);
        }
    }
}
