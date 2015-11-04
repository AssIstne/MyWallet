package com.assistne.mywallet.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.assistne.mywallet.R;
import com.assistne.mywallet.db.MyWalletDatabaseUtils;
import com.assistne.mywallet.util.GlobalUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private int categoryId = BillCategory.NO_CATEGORY;
    private String description = "";
    private float price = (float)0.00;
    private long dateInMills;
    private int id;
    private String imagePath = "";
    private boolean isIncome = false;


    public Bill() {
        dateInMills = Calendar.getInstance(Locale.CHINA).getTimeInMillis();
    }

    private Bill(Parcel in) {
        id = in.readInt();
        emotion = in.readInt();
        location = in.readString();
        categoryId = in.readInt();
        description = in.readString();
        price = in.readFloat();
        dateInMills = in.readLong();
        imagePath = in.readString();
        isIncome = in.readByte() == 1;
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

    public long getDateInMills() {
        return dateInMills;
    }

    public void setDateInMills(long dateInMills) {
        this.dateInMills = dateInMills;
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
        dest.writeLong(dateInMills);
        dest.writeString(imagePath);
        dest.writeByte((byte)(isIncome ? 1 : 0));
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
        return format.format(GlobalUtils.getDateFromMills(dateInMills)) + " " + location;
    }

    public BillCategory getBillCategory(Context context) {
        return MyWalletDatabaseUtils.getInstance(context).getCategory(categoryId);
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public void setIsIncome(boolean isIncome) {
        this.isIncome = isIncome;
    }
}
