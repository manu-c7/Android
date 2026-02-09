package com.example.myadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private final List<Noticia> lista;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Noticia item);
    }

    public NewsAdapter(List<Noticia> lista, OnItemClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Noticia n = lista.get(position);
        holder.titulo.setText(n.getTitulo());

        // Animación de entrada (Slide + Fade)
        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationX(100f);
        holder.itemView.animate().alpha(1f).translationX(0f).setDuration(500).start();

        holder.itemView.setOnClickListener(v -> listener.onItemClick(n));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titulo;

        public ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tvTitulo);
        }
    }
}
