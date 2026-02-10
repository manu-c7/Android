package com.example.myadapter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

// Suprimimos advertencias porque Gallery es antiguo
@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {

    private UniversalAdapter adapter;
    private final ArrayList<NewsItem> newsList = new ArrayList<>();
    private List<AdapterView<?>> allViews;
    private ListView listView;
    private GridView gridView;
    private StackView stackView;
    private Gallery gallery;
    private ProgressBar progressBar;

    private static final String CSV_URL = "https://docs.google.com/spreadsheets/d/e/2PACX-1vTlt1YnutG2VNjh6rgFLM1AqKQXY2YwoR-gutF_gh3l_rEIi_ved1h5aPRSoh0OpMtPD4ACOkEsiSPl/pub?output=csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        // 1. Inicializamos el adaptador
        adapter = new UniversalAdapter(this, newsList);

        // 2. Asignamos el adaptador a TODAS las vistas.
        listView.setAdapter(adapter);
        gridView.setAdapter(adapter);
        stackView.setAdapter(adapter);
        gallery.setAdapter(adapter);

        setupListeners();
    }

    private void initViews() {
        listView = findViewById(R.id.listView);
        gridView = findViewById(R.id.gridView);
        stackView = findViewById(R.id.stackView);
        gallery = findViewById(R.id.gallery);
        progressBar = findViewById(R.id.progressBar);

        // Agrupamos las vistas en una lista para facilitar su manejo (ocultar/mostrar)
        allViews = new ArrayList<>(Arrays.asList(listView, gridView, stackView, gallery));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 3. Carga de datos optimizada: Solo descarga si la lista está vacía
        if (newsList.isEmpty()) {
            downloadCsvData();
        }
    }

    private void setupListeners() {
        // Un solo listener para todas las vistas
        AdapterView.OnItemClickListener commonClickListener = (parent, view, position, id) -> {
            Intent intent = new Intent(this, DetailActivity.class);
            NewsItem item = newsList.get(position); // Es seguro usar la lista directa si la posición coincide
            intent.putExtra("EXTRA_ITEM", item);
            startActivity(intent);
        };

        listView.setOnItemClickListener(commonClickListener);
        gridView.setOnItemClickListener(commonClickListener);
        stackView.setOnItemClickListener(commonClickListener);
        gallery.setOnItemClickListener(commonClickListener);

        setupSpinner();
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.spinnerViewType);
        String[] options = {"ListView (Lista)", "GridView (Cuadrícula)", "StackView (Pila)", "Gallery (Galería)"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lógica limpia para cambiar vistas
                updateActiveView(allViews.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateActiveView(AdapterView<?> viewToShow) {
        for (AdapterView<?> view : allViews) {
            view.setVisibility(view == viewToShow ? View.VISIBLE : View.GONE);
        }
    }

    private void downloadCsvData() {
        progressBar.setVisibility(View.VISIBLE);

        Executors.newSingleThreadExecutor().execute(() -> {
            final List<NewsItem> tempNewsList = new ArrayList<>();
            Handler handler = new Handler(Looper.getMainLooper());

            try {
                URL url = new URL(CSV_URL);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));

                String line;
                reader.readLine(); 

                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length >= 5) {
                        tempNewsList.add(new NewsItem(tokens[0], tokens[1], tokens[2], tokens[3], tokens[4]));
                    }
                }
                reader.close();

                handler.post(() -> {
                    newsList.clear();
                    newsList.addAll(tempNewsList);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    if (newsList.isEmpty()) {
                        Toast.makeText(MainActivity.this, "No se encontraron datos", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
