package com.example.aramamotoru;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class dataSource {
    SQLiteDatabase db;
    sql_layer bdb;
    public static byte[] deger ;

    public dataSource(Context c){
        bdb = new sql_layer(c);
    }

    public void open(){
        db = bdb.getWritableDatabase();
    }
    public  void close(){
        bdb.close();
    }


    public int createRecort(Photo pth) throws IOException {
        ContentValues val = new ContentValues();

        open();

        /*List<String> images =new  ArrayList<String>();
        images = listele();

        if(images.contains(pth.imgbyte)){
            return 0;
        }
*/



        /*FileInputStream fs = new FileInputStream(pth.getImgbyte());
        byte[] imgbyte = new byte[fs.available()];
        fs.read(imgbyte);*/
        //val.put("id",1);
        val.put("image",pth.getImgbyte());
        val.put("object1",pth.getObject1());
        val.put("object2",pth.getObject2());
        int lastId = (int)db.insert("resimler",null,val);

        close();
        return lastId;
    }

    public List<String> listele() throws IOException {
        open();
        String koloums[] ={"id","image","object1","object2"};
        List<String> list = new ArrayList<>();
        Cursor c = db.query("resimler",koloums,null,null,null,null,null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            //int id = c.getInt(0);

            int id = Integer.parseInt(c.getString(0));
            String path = c.getString(1);



            String obj1 = c.getString(2);
            String obj2 = c.getString(3);
            list.add(path);
            System.out.println("----------------------------------------------------------------------");
            System.out.println(id+","+path+","+obj1+","+obj2);
            c.moveToNext();


        }
        close();
        c.close();
        return list;
    }

    public List<String> search(String key) throws IOException {
        open();
        String koloums[] ={"id","image","object1","object2"};
        List<String> list = new ArrayList<>();
        Cursor c = db.rawQuery("select * from resimler where object1=?",
                new String[]{String.valueOf(key)});
        c.moveToFirst();
        while(!c.isAfterLast()){
            //int id = c.getInt(0);

            int id = Integer.parseInt(c.getString(0));
            String path = c.getString(1);



            String obj1 = c.getString(2);
            String obj2 = c.getString(3);
            list.add(path);
            System.out.println("----------------------------------------------------------------------");
            System.out.println(id+","+path+","+obj1+","+obj2);
            c.moveToNext();


        }
        close();
        c.close();
        return list;
    }

    public List<String> searchKey() throws IOException {
        open();
        String koloums[] ={"id","image","object1","object2"};
        List<String> listKey = new ArrayList<>();
        Cursor c = db.query("resimler",koloums,null,null,null,null,null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            //int id = c.getInt(0);

            int id = Integer.parseInt(c.getString(0));
            String path = c.getString(1);



            String obj1 = c.getString(2);
            String obj2 = c.getString(3);

            listKey.add(obj1);
            System.out.println("----------------------------------------------------------------------");
            System.out.println(id+","+path+","+obj1+","+obj2);
            c.moveToNext();


        }
        close();
        c.close();
        return listKey;
    }


    public void delete() throws IOException {
        open();
        String koloums[] ={"id","image","object1","object2"};
        List<String> list = new ArrayList<>();
        Cursor c = db.rawQuery("delete from resimler",null);

        c.moveToNext();



        close();
        c.close();

    }





}

