package com.example.myadapter;

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
