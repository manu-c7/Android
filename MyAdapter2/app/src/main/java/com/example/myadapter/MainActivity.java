package com.example.myadapter;

import android.os.Bundle;
import android.util.Log; // Importante para depurar
import androidx.appcompat.app.AppCompatActivity;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG_NOTICIAS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Activity iniciada");

        // 1. Configurar el diseño de dos paneles
        SlidingPaneLayout slidingPaneLayout = findViewById(R.id.sliding_pane_layout);
        // Desbloqueado para que se pueda deslizar en móviles
        slidingPaneLayout.setLockMode(SlidingPaneLayout.LOCK_MODE_UNLOCKED);

        // 2. CARGA INMEDIATA (Lo que te faltaba)
        // Aquí llamamos a la lógica que descarga el CSV ahora mismo para la UI
        ejecutarCargaInicial();

        // 3. Programar el trabajo de fondo (para notificaciones futuras)
        scheduleNewsWorker();
    }

    private void ejecutarCargaInicial() {
        Log.d(TAG, "Ejecutando carga inicial de datos...");

        // Obtenemos el GID dinámico según el idioma
        String gid = getString(R.string.sheet_gid);
        String urlCsv = "https://docs.google.com/spreadsheets/d/e/2PACX-1vTlt1YnutG2VNjh6rgFLM1AqKQXY2YwoR-gutF_gh3l_rEIi_ved1h5aPRSoh0OpMtPD4ACOkEsiSPl/pub?output=csv&gid=" + gid;

        // Usamos el AsyncManager que creamos antes para no bloquear la UI
        new AsyncManager().runExecutor((metodo, tiempo) -> {
            Log.d(TAG, "Datos procesados con " + metodo + " en " + tiempo + "ms");

            // Una vez descargado e insertado en SQLite, avisamos al Fragment
            actualizarInterfaz();
        });
    }

    public void actualizarInterfaz() {
        // Buscamos el fragmento de la lista y le pedimos que refresque
        ListFragment fragment = (ListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.list_container);
        if (fragment != null) {
            runOnUiThread(() -> fragment.cargarDatos("", "fecha"));
        }
    }

    private void scheduleNewsWorker() {
        // Esto se encargará de las notificaciones cada hora, NO de la UI de ahora
        PeriodicWorkRequest newsRequest =
                new PeriodicWorkRequest.Builder(NewsWorker.class, 1, TimeUnit.HOURS)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "news_update_work",
                ExistingPeriodicWorkPolicy.KEEP,
                newsRequest);
    }
}