package com.example.myadapter;

import java.io.Serializable;

public class Noticia implements Serializable {
    private final String titulo;
    private final String desc;
    private final String fecha;
    private final int importancia;

    public Noticia(String titulo, String desc, String fecha, int importancia) {
        this.titulo = titulo;
        this.desc = desc;
        this.fecha = fecha;
        this.importancia = importancia;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDesc() {
        return desc;
    }

    public String getFecha() {
        return fecha;
    }

    public int getImportancia() {
        return importancia;
    }
}
