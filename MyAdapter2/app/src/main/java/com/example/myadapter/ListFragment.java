package com.example.myadapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO: Create and set an adapter for the RecyclerView
        return view;
    }
    // Dentro de ListFragment.java

    // 1. Asegúrate de que el método sea PUBLIC para que MainActivity lo vea
    public void cargarDatos(String query, String orderBy) {
        if (listaNoticias == null) {
            listaNoticias = new ArrayList<>();
        }

        listaNoticias.clear();

        // 2. Usamos el dbHelper para traer los datos
        // Asegúrate de que en NoticiasDB el método se llame 'obtenerNoticias' o 'getNoticias'
        // Si en tu NoticiasDB lo llamaste 'getNoticias', cámbialo aquí abajo también.
        Cursor cursor = dbHelper.obtenerNoticias(query, orderBy);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                int importancia = cursor.getInt(cursor.getColumnIndexOrThrow("importancia"));

                listaNoticias.add(new Noticia(id, titulo, desc, fecha, importancia));
            } while (cursor.moveToNext());
            cursor.close();
        }

        // 3. Avisamos al adapter que hay datos nuevos para que los dibuje
        if (adapter == null) {
            adapter = new NoticiaAdapter(listaNoticias, noticia -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).mostrarDetalle(noticia);
                }
            });
            recyclerView.setAdapter(adapter);
        } else {
            // Esto dispara las animaciones que creamos
            adapter.notifyDataSetChanged();
        }
    }
}
