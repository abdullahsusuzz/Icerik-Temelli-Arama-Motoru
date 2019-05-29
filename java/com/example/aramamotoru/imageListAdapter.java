package com.example.aramamotoru;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.aramamotoru.MainActivity.imageUri;

public class imageListAdapter extends BaseAdapter {
    private Context context;
    private String[] items;

    public imageListAdapter(Context context, String[] items) {
        super();
        this.context = context;
        this.items = items;
    }



    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private class ViewHolder{
        ImageView imageView;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ImageView img;
        if(view ==null){
            img = new ImageView(context);
            view = img;
            img.setPadding(3,0,0,0);

        }
        else{
            img = (ImageView)view;
        }
        Picasso.with(context)
                .load(items[position])
                .placeholder(R.drawable.crop__ic_done)
                .resize(800,600)
        .into(img);
        img.setPadding(0,0,0,0);




        return view;
    }
}
