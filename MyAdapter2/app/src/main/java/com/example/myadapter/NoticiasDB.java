package com.example.myadapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoticiasDB extends SQLiteOpenHelper {
    public NoticiasDB(Context context) { super(context, "news.db", null, 1); }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE noticias (id INTEGER PRIMARY KEY, titulo TEXT, desc TEXT, fecha TEXT, importancia INTEGER)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int newV) {}

    public Cursor getNoticias(String query, String sort) {
        String orderBy = sort.equals("importancia") ? "importancia DESC" : "fecha DESC";
        return getReadableDatabase().rawQuery("SELECT * FROM noticias WHERE titulo LIKE ? ORDER BY " + orderBy, new String[]{"%" + query + "%"});
    }

    public void insertNoticia(int id, String titulo, String desc, String fecha, int importancia) {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("titulo", titulo);
        values.put("desc", desc);
        values.put("fecha", fecha);
        values.put("importancia", importancia);
        getWritableDatabase().insertWithOnConflict("noticias", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    
    public void clearNoticias() {
        getWritableDatabase().delete("noticias", null, null);
    }
}
