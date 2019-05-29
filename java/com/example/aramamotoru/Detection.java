package com.example.aramamotoru;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Detection extends AppCompatActivity {

    private static final int RESULTS_TO_SHOW = 10;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;


    private final Interpreter.Options tfliteOptions = new Interpreter.Options();

    private Interpreter tflite;


    private List<String> labelList;

    private ByteBuffer imgData = null;

    private float[][] labelProbArray = null;

    private byte[][] labelProbArrayB = null;
    private String[] topLables = null;
    private String[] topConfidence = null;


    private String chosen;
    private boolean quant;


    private int DIM_IMG_SIZE_X = 299;
    private int DIM_IMG_SIZE_Y = 299;
    private int DIM_PIXEL_SIZE = 3;

    private int[] intValues;

    private ImageView selected_image;


    private PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });

    dataSource vk;
    public static String imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // get all selected classifier data from classifiers
        chosen = (String) getIntent().getStringExtra("chosen");
        quant = true;

        intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];

        super.onCreate(savedInstanceState);

        try{
            tflite = new Interpreter(loadModelFile(), tfliteOptions);
            labelList = loadLabelList();
        } catch (Exception ex){
            ex.printStackTrace();
        }



        imgData = ByteBuffer.allocateDirect(
                            DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);

        imgData.order(ByteOrder.nativeOrder());


        labelProbArrayB= new byte[1][labelList.size()];


        setContentView(R.layout.activity_detection);




        selected_image = (ImageView) findViewById(R.id.imageView);


        topLables = new String[RESULTS_TO_SHOW];
        topConfidence = new String[RESULTS_TO_SHOW];






        selected_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap_orig = ((BitmapDrawable)selected_image.getDrawable()).getBitmap();
                Bitmap bitmap = getResizedBitmap(bitmap_orig, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y);
                convertBitmapToByteBuffer(bitmap);

                tflite.run(imgData, labelProbArrayB);

               printTopKLabels();


            }
        });

        Uri blackUri = Uri.parse("content://com.android.providers.media.documents/document/image%3A321");

        ArrayList<String> topImageLabels = new ArrayList<String>();
        String objec;
        String objec2;
        Bitmap normalImage = null;
        Uri uri = Uri.parse(getIntent().getStringExtra("resimYolu"));
        try {
            Bitmap bitmap_orig = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            Bitmap bitmap = getResizedBitmap(bitmap_orig, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y);
            convertBitmapToByteBuffer(bitmap);

            tflite.run(imgData, labelProbArrayB);


            normalImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

            selected_image.setImageBitmap(normalImage);

            System.out.println("----------------------------------");
            topImageLabels = printTopKLabels();
            objec = topImageLabels.get(9);
            System.out.println("----------------------------------");

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            System.out.println(uri.toString()+"-------------------------------------++++++++++++");
            Bitmap resizeImageBitmap = getResizedBitmap(imageBitmap, 300, 300);
            //convertBitmapToByteBuffer(resizeImageBitmap);
            Bitmap darkBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black);
            Bitmap resizeDarkBitmap = getResizedBitmap(darkBitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y);

            //System.out.println("---------------"+bitmap.getByteCount()+"-------------------------------------------------------------");
            //Bitmap scalled = Bitmap.createScaledBitmap(a,200,2000,true);




            /*int detectedList[][]= {{0,0,0,0,0},
                                {0,0,0,0,0},
                                {0,0,0,0,0},
                                {0,0,0,0,0},
                                {0,0,0,0,0}};
*/
            int detectedList[][]= {{0,0,0},
                    {0,0,0},
                    {0,0,0}};

            ArrayList<String> topCellLabels = new ArrayList<String>();

            int a =0;
            Bitmap[] imageSplite = splitBitmap(resizeImageBitmap);
            for(int i =0;i<3;i++) {
                for (int j = 0; j < 3; j++) {
                    Bitmap resultBitmap = overlay(resizeDarkBitmap, imageSplite[a]);

                    Bitmap bitmaps = getResizedBitmap(resultBitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y);
                    convertBitmapToByteBuffer(bitmaps);

                    System.out.println(a);
                    tflite.run(imgData, labelProbArrayB);


                    topCellLabels = printTopKLabels();
                    objec = topImageLabels.get(9);
                    objec2 = topImageLabels.get(8);

                    if (topCellLabels.contains(objec) || topCellLabels.contains(objec2)){
                        detectedList[i][j] = 1;
                    }
                    a++;

                }
            }

            int ar1=0;
            int ac1=0;
            int ar2=0;
            int ac2=0;
            int ar3=0;
            int ac3=0;

            for (int i =0;i<3;i++){
                if (detectedList[0][i]==1){
                    ar1=1;
                }
                if (detectedList[1][i]==1){
                    ar2=1;
                }
                if (detectedList[2][i]==1){
                    ar2=1;
                }
                if (detectedList[i][0]==1){
                    ac1=1;
                }
                if (detectedList[i][1]==1){
                    ac2=1;
                }
                if (detectedList[i][2]==1){
                    ac3=1;
                }
                for(int j =0;j<3;j++){
                    System.out.print(detectedList[i][j]);
                    System.out.print(" ");
                }
                System.out.println();
            }
            Paint p1 = new Paint();
            p1.setAntiAlias(true);
            p1.setColor(Color.GREEN);
            p1.setStrokeWidth(10);
            Bitmap mutableBitmap = normalImage.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mutableBitmap);
            int minx=0;
            int miny=0;
            int maxx=0;
            int maxy=0;

            if(ar1==1){
                minx=0;
            }
            else if(ar2==1){
                minx =1;
            }
            else
                minx=2;

            if(ar3==1){
                maxx=3;
            }
            else if(ar2==1){
                maxx =2;
            }
            else
                maxx=1;

            if(ac1==1){
                miny=0;
            }
            else if(ac2==1){
                miny =1;
            }
            else
                miny=2;


            if(ac3==0){
                maxy=3;
            }
            else if(ac2==1){
                maxy =2;
            }
            else
                maxy=1;

            int weigh = mutableBitmap.getWidth()/3;
            int heigh = mutableBitmap.getHeight()/3;

            //canvas.drawLine(10,40,300,40,p1);


            canvas.drawLine(20+minx*weigh,20+miny*heigh,maxx*weigh-20,miny*heigh+20,p1);
            canvas.drawLine(20+minx*weigh,20+miny*heigh,minx*weigh+20,maxy*heigh-20,p1);
            canvas.drawLine(maxx*weigh-10,20+miny*heigh,maxx*weigh-20,maxy*heigh-20,p1);
            canvas.drawLine(20+minx*weigh,maxy*heigh-20,maxx*weigh-20,maxy*heigh-20,p1);

            selected_image.setImageBitmap(mutableBitmap);

            System.out.println(mutableBitmap.getHeight());
            System.out.println(mutableBitmap.getWidth());
            //selected_image.setImageBitmap(resultBitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }





    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("inception_quant.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];


                imgData.put((byte) ((val >> 16) & 0xFF));
                imgData.put((byte) ((val >> 8) & 0xFF));
                imgData.put((byte) (val & 0xFF));


            }
        }
    }

    private List<String> loadLabelList() throws IOException {
        List<String> labelList = new ArrayList<String>();
        System.out.println("readerin ustu---------------------------------------------------------------");
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(this.getAssets().open("labels.txt")));
        System.out.println("readein altı---------------------------------------------------------------");
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    private ArrayList<String> printTopKLabels() {
        ArrayList<String> top10 = new ArrayList<String>();
        for (int i = 0; i < labelList.size(); ++i) {
            if(quant){
                sortedLabels.add(
                        new AbstractMap.SimpleEntry<>(labelList.get(i), (labelProbArrayB[0][i] & 0xff) / 255.0f));
            } else {
                sortedLabels.add(
                        new AbstractMap.SimpleEntry<>(labelList.get(i), labelProbArray[0][i]));
            }
            if (sortedLabels.size() > RESULTS_TO_SHOW) {
                sortedLabels.poll();
            }
        }

        final int size = sortedLabels.size();
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> label = sortedLabels.poll();
            topLables[i] = label.getKey();
            topConfidence[i] = String.format("%.0f%%",label.getValue()*100);
        }
        for(int i=0;i<10;i++) {
            System.out.println(topLables[i]);
            top10.add(topLables[i]);
        }


        return  top10;
    }


    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }


    public Bitmap[] splitBitmap( Bitmap bitmap){
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
        Bitmap[] imgs = new Bitmap[9];
        int a =0;
        for(int  i=0;i<=200;i =i+100){
            for(int j = 0;j<=200;j =j+100){
                imgs[a] = Bitmap.createBitmap(scaledBitmap, j, i, 100 , 100);
                a++;
            }
        }
        return imgs;
    }


    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, new Matrix(), null);

        return bmOverlay;
    }
}
