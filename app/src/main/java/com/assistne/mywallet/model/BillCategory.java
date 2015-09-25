package com.assistne.mywallet.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by assistne on 15/9/18.
 */
public class BillCategory implements Parcelable{
    public static final int SYSTEM_INCOME = 1;
    public static final int CUSTOM_INCOME = 2;
    public static final int ALL_INCOME = 3;
    public static final int SYSTEM_SPENT = -1;
    public static final int CUSTOM_SPENT = -2;
    public static final int ALL_SPENT = -3;
    public static final int NEW_CATEGORY = 0;


    private int id;
    private int parentId;
    private int count;
    private int type;
    private String name;
    private int backgroundResId;
    private int activated;

    public BillCategory() {
        count = 0;
        activated = 1;
    }

    protected BillCategory(Parcel in) {
        name = in.readString();
        backgroundResId = in.readInt();
        type = in.readInt();
        parentId = in.readInt();
        count = in.readInt();
        activated = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getActivated() {
        return activated;
    }

    public void setActivated(int activated) {
        this.activated = activated;
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
        dest.writeInt(type);
        dest.writeInt(parentId);
        dest.writeInt(count);
        dest.writeInt(activated);
    }
}
