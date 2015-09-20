package com.assistne.mywallet.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by assistne on 15/9/18.
 */
public class BillCategory implements Parcelable{
    private String name;
    private int backgroundResId;

    public BillCategory(String mName, int mBackgroundColor) {
        name = mName;
        backgroundResId = mBackgroundColor;
    }

    protected BillCategory(Parcel in) {
        name = in.readString();
        backgroundResId = in.readInt();
    }

    public static final Creator<BillCategory> CREATOR = new Creator<BillCategory>() {
        @Override
        public BillCategory createFromParcel(Parcel in) {
            return new BillCategory(in);
        }

        @Override
        public BillCategory[] newArray(int size) {
            return new BillCategory[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBackgroundResId() {
        return backgroundResId;
    }

    public void setBackgroundResId(int backgroundResId) {
        this.backgroundResId = backgroundResId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(backgroundResId);
    }
}
