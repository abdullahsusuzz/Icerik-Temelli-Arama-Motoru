package com.example.aramamotoru;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final int IMAGE_GALLERY_REQUEST = 20;

    public static Uri data1;
    public static String data2;
    public static String data3;
    public static String data4;

    private Button galerry;
    private Button inceptionQuant;
    private Button search;
    private EditText key;
    private ImageView imag;
    public static String url;
    private static final int OICK_IMAGE=100;
    public static final int REQUEST_PERMISSION = 300;
    dataSource db = new dataSource(this);


    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_IMAGESS = 111;
    private static final int READ_REQUEST_CODE = 42;

    private GridView gridView;
    private imageListAdapter adapter;

    private RecyclerView.Recycler recycler;


    public static String[] items = {    };



    String selectedItem;
    public static Uri imageUri;

    private String chosen;


    private boolean quant;
    final dataSource ds = new dataSource(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }



        final dataSource ds = new dataSource(this);
        /*try {
            ds.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try {
            items = ds.listele().toArray(new String[0]);
            System.out.println(items[0]);


        } catch (IOException e) {
            e.printStackTrace();
        }


        gridView = (GridView)findViewById(R.id.gridView);
        adapter = new imageListAdapter(MainActivity.this,items);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem =parent.getItemAtPosition(position).toString();

                Intent i = new Intent(MainActivity.this,Detection.class);

                i.putExtra("resimYolu",selectedItem);
                i.putExtra("chosen", chosen);
                i.putExtra("quant", quant);
                startActivity(i);

                System.out.println(selectedItem+"------------------------------------------------");
            }
        });

        //imag = (ImageView)findViewById(R.id.imageView);
        search = (Button)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                key = (EditText)findViewById(R.id.key);
                String object = key.getText().toString();

                try {
                    items = ds.search(object).toArray(new String[0]);
                    adapter = new imageListAdapter(MainActivity.this,items);
                    gridView.setAdapter(adapter);
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        });


        galerry = (Button)findViewById(R.id.galery);
        galerry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosen = "inception_quant.tflite";
                quant = true;



                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent intent = new Intent(Intent.ACTION_PICK,Uri.parse("content://com.android.providers.media.documents/document/image%3A"));
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


                startActivityForResult(intent, REQUEST_IMAGESS);




            }
        });



        inceptionQuant = (Button)findViewById(R.id.camera);
        inceptionQuant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chosen = "inception_quant.tflite";
                quant = true;

                openCameraIntent();
            }
        });

    }

    private void openCameraIntent(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startActivityForResult(intent, REQUEST_IMAGE);
    }
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(getApplicationContext(),"This application needs read, write, and camera permissions to run. Application now closing.",Toast.LENGTH_LONG);
                System.exit(0);
            }
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("1 log------------------------------------------------");
        System.out.println("data2");
        System.out.println(data2);
        if(requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {

            try {
                Uri source_uri = imageUri;
                data2 =imageUri.toString();
                Uri dest_uri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                Crop.of(source_uri, dest_uri).asSquare().start(MainActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else if(requestCode == REQUEST_IMAGESS && resultCode == RESULT_OK) {
            try {
                if(data.getData()!=null) {
                    data2 = data.getData().toString();
                }
                System.out.println("2 log------------------------------------------------");
                Uri source_uri = data.getData();
                Uri dest_uri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                Crop.of(source_uri, dest_uri).asSquare().start(MainActivity.this);
                System.out.println("3 log------------------------------------------------");
                if(data.getData()!= null) {
                    data2 = data.getData().toString();
                }
                if(data.getData()!= null) {
                    data2 = data.getData().toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        else if(requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK){

            System.out.println("4 log------------------------------------------------");

            imageUri = Crop.getOutput(data);
            Intent i = new Intent(MainActivity.this, Classify.class);
            i.putExtra("resID_uri", imageUri);
            i.putExtra("chosen", chosen);
            i.putExtra("quant", quant);
            System.out.println("5 log------------------------------------------------");
            startActivity(i);
        }
        if(requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imag.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}