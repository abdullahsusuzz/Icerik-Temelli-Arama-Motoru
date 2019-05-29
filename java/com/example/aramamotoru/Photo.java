package com.example.aramamotoru;

public class Photo {
    int id;
    String imgbyte;
    String object1;
    String object2;

    public int getId() {
        return id;
    }

    public String getImgbyte() {
        return imgbyte;
    }

    public String getObject1() {
        return object1;
    }

    public String getObject2() {
        return object2;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImagePath(String imgbyte) {
        this.imgbyte = imgbyte;
    }

    public void setObject1(String object1) {
        this.object1 = object1;
    }

    public void setObject2(String object2) {
        this.object2 = object2;
    }

    public Photo(int id, String imgbyte, String object1, String object2) {
        this.id = id;
        this.imgbyte = imgbyte;
        this.object1 = object1;
        this.object2 = object2;
    }

    public Photo(String imgbyte, String object1, String object2) {
        this.imgbyte = imgbyte;
        this.object1 = object1;
        this.object2 = object2;
    }
}
