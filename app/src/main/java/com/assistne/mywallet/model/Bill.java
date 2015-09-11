package com.assistne.mywallet.model;

import com.assistne.mywallet.R;

import java.util.Date;

/**
 * Created by assistne on 15/9/11.
 */
public class Bill {
    private int emotion = R.drawable.main_good1;
    private String location = "";
    private int categoryId;
    private float price = (float)0.00;
    private Date date ;

    public int getEmotion() {
        return emotion;
    }

    public void setEmotion(int emotion) {
        this.emotion = emotion;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

//    Todo
    public String getCategory(){
        return "购物卡";
    }
}
