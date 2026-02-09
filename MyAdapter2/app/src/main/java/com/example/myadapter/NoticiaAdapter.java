package com.example.myadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NoticiaAdapter extends RecyclerView.Adapter<NoticiaAdapter.NoticiaViewHolder> {

    private List<Noticia> noticias;
    private OnItemClickListener listener;
    private int lastPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(Noticia noticia);
    }

    public NoticiaAdapter(List<Noticia> noticias, OnItemClickListener listener) {
        this.noticias = noticias;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoticiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el diseño de la fila (asegúrate de tener item_noticia.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_noticia, parent, false);
        return new NoticiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticiaViewHolder holder, int position) {
        Noticia noticia = noticias.get(position);
        holder.bind(noticia, listener);

        // Aplicamos la animación de entrada (Slide + Fade)
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return noticias.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            // Animación de desplazamiento desde la derecha
            TranslateAnimation slide = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f
            );
            slide.setDuration(500);

            // Animación de transparencia
            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setDuration(700);

            viewToAnimate.startAnimation(slide);
            viewToAnimate.startAnimation(fadeIn);
            lastPosition = position;
        }
    }

    public static class NoticiaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFecha, tvImportancia;

        public NoticiaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvImportancia = itemView.findViewById(R.id.tvImportancia);
        }

        public void bind(final Noticia noticia, final OnItemClickListener listener) {
            tvTitulo.setText(noticia.getTitulo());
            tvFecha.setText(noticia.getFecha());
            tvImportancia.setText("Prioridad: " + noticia.getImportancia());

            itemView.setOnClickListener(v -> listener.onItemClick(noticia));
        }
    }
}