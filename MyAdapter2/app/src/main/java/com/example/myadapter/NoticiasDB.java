package com.example.myadapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoticiasDB extends SQLiteOpenHelper {

    public NoticiasDB(Context context) {
        super(context, "noticias.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE noticias (id INTEGER PRIMARY KEY, titulo TEXT, descripcion TEXT, fecha TEXT, importancia INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS noticias");
        onCreate(db);
    }

    public void insertarNoticia(int id, String titulo, String desc, String fecha, int importancia) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("titulo", titulo);
        values.put("descripcion", desc);
        values.put("fecha", fecha);
        values.put("importancia", importancia);

        // INSERT o REPLACE para no duplicar si la noticia ya existe
        db.insertWithOnConflict("noticias", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // EL MÉTODO QUE BUSCA TU LISTFRAGMENT
    public Cursor obtenerNoticias(String query, String orderBy) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sortOrder = (orderBy != null && orderBy.equalsIgnoreCase("importancia")) ? "importancia DESC" : "fecha DESC";

        if (query != null && !query.isEmpty()) {
            return db.query("noticias", null, "titulo LIKE ?", new String[]{"%" + query + "%"}, null, null, sortOrder);
        } else {
            return db.query("noticias", null, null, null, null, null, sortOrder);
        }
    }
}
