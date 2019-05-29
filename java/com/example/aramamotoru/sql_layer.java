package com.example.aramamotoru;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class sql_layer extends SQLiteOpenHelper {
    public sql_layer(Context c){
        super(c,"resimler",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "create table resimler(id integer primary key autoincrement, " +
                "image blob not null," +
                "object1 text not null," +
                "object2 text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists resimler");

    }


}
