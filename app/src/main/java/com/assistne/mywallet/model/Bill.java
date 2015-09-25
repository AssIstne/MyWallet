package com.assistne.mywallet.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.assistne.mywallet.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by assistne on 15/9/11.
 */
public class Bill implements Parcelable{
    public static final int EMOTION_BAD = 0;
    public static final int EMOTION_NORMAL = 1;
    public static final int EMOTION_GOOD = 2;

    public static final int[] EMOTIONS = {R.drawable.main_bad1, R.drawable.main_normal1, R.drawable.main_good1};
    private int emotion = EMOTION_NORMAL;
    private String location = "";
    private int categoryId;
    private String description;
    private float price = (float)0.00;
    private Date date ;
    private int id;


    public Bill() {

    }

    private Bill(Parcel in) {
        id = in.readInt();
        emotion = in.readInt();
        location = in.readString();
        categoryId = in.readInt();
        description = in.readString();
        price = in.readFloat();
        date = (Date)in.readSerializable();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmotionRes() {
        return EMOTIONS[emotion];
    }


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(emotion);
        dest.writeString(location);
        dest.writeInt(categoryId);
        dest.writeString(description);
        dest.writeFloat(price);
        dest.writeSerializable(date);
    }

    public static final Parcelable.Creator<Bill> CREATOR = new Parcelable.Creator<Bill>() {
        public Bill createFromParcel(Parcel in) {
            return new Bill(in);
        }

        public Bill[] newArray(int size) {
            return new Bill[size];
        }
    };

    public String getInfo() {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd kk:mm", Locale.CHINA);
        return format.format(date) + " " + location;
    }

}
