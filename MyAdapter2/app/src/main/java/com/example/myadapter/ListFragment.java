package com.example.myadapter;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    // --- ESTO ES LO QUE FALTA (Variables de clase) ---
    private RecyclerView recyclerView;
    private NoticiaAdapter adapter;
    private List<Noticia> listaNoticias; // <--- La variable que causaba el error
    private NoticiasDB dbHelper;
    // -------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // Inicializamos componentes
        recyclerView = view.findViewById(R.id.recyclerViewNoticias);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new NoticiasDB(getContext());

        // Inicializamos la lista aquí para evitar el NullPointerException
        listaNoticias = new ArrayList<>();

        return view;
    }

    public void cargarDatos(String query, String orderBy) {
        // Ahora el método ya reconoce a listaNoticias
        if (listaNoticias == null) {
            listaNoticias = new ArrayList<>();
        }

        listaNoticias.clear();

        // Tu lógica de carga de la base de datos...
        Cursor cursor = dbHelper.obtenerNoticias(query, orderBy);
        // ... (resto del código)
    }
}
