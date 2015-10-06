package com.assistne.mywallet.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.assistne.mywallet.model.Bill;
import com.assistne.mywallet.model.BillCategory;
import com.assistne.mywallet.util.Constants;

import java.util.ArrayList;

/**
 * Created by assistne on 15/9/21.
 */
public class MyWalletDatabaseUtils {

    public static final int VERSION = 1;
    private static MyWalletDatabaseUtils myWalletDatabaseUtils;
    private SQLiteDatabase db;

    private MyWalletDatabaseUtils(Context context) {
        MyWalletDatabaseHelper helper = new MyWalletDatabaseHelper(context, Constants.DATABASE, null, VERSION);
        db = helper.getWritableDatabase();
    }

    public synchronized static MyWalletDatabaseUtils getInstance(Context context) {
        if (myWalletDatabaseUtils == null) {
            myWalletDatabaseUtils = new MyWalletDatabaseUtils(context);
        }
        return myWalletDatabaseUtils;
    }
/*
  * 获取新增账单页面展示的账单类型
  * type:
  * BillCategory.All_INCOME-所有收入类型，包括系统自带的和用户添加的
  * BillCategory.ALL_SPENT-所有支出类型，包括系统自带的和用户添加的
  * */
    public ArrayList<BillCategory> getActivatedBillCategory(int type) {
        ArrayList<BillCategory> list = new ArrayList<>();
        String typeCondition;
        switch (type) {
            case BillCategory.ALL_INCOME:
                typeCondition = "type > ? ";
                type = 0;
                break;
            case BillCategory.ALL_SPENT:
                typeCondition = "type < ? ";
                type = 0;
                break;
            default:
                typeCondition = "type = ? ";
                break;
        }
        String sql = "select id, name, background_res_id, parent_id, type from Category " +
                "where activated = 1 and " +
                typeCondition +
                "order by count desc";
        Cursor cursor = db.rawQuery(sql, new String[] {String.valueOf(type)});
        if (cursor.moveToFirst()) {
            do {
                BillCategory category = new BillCategory();
                category.setName(cursor.getString(cursor.getColumnIndex("name")));
                category.setBackgroundResId(cursor.getInt(cursor.getColumnIndex("background_res_id")));
                category.setParentId(cursor.getInt(cursor.getColumnIndex("parent_id")));
                category.setType(cursor.getInt(cursor.getColumnIndex("type")));
                category.setId(cursor.getInt(cursor.getColumnIndex("id")));
                category.setActivated(1);
                list.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public BillCategory getBillCategory(int id) {
        Cursor cursor = db.rawQuery(
                "select name, background_res_id, parent_id, activated, type from Category where id = ?",
                new String[]{String.valueOf(id)});
        BillCategory category = null;
        if (cursor.moveToFirst()) {
            category = new BillCategory();
            category.setName(cursor.getString(cursor.getColumnIndex("name")));
            category.setBackgroundResId(cursor.getInt(cursor.getColumnIndex("background_res_id")));
            category.setParentId(cursor.getInt(cursor.getColumnIndex("parent_id")));
            category.setType(cursor.getInt(cursor.getColumnIndex("type")));
            category.setActivated(cursor.getInt(cursor.getColumnIndex("activated")));
            category.setId(id);
        }
        cursor.close();
        return category;
    }

    public void saveBill(Bill bill) {
        db.execSQL("insert into Bill (price, emotion, category_id, description, location, date) values (?, ?, ?, ?, ?, ?)",
                new String[]{
                        String.valueOf(bill.getPrice()),
                        String.valueOf(bill.getEmotion()),
                        String.valueOf(bill.getCategoryId()),
                        bill.getDescription(),
                        bill.getLocation(),
                        String.valueOf(bill.getDateForMills())
                });
    }

    public void updateBill(Bill bill) {
        db.execSQL("update Bill set price=?, emotion=?, category_id=?, description=?, location=?, date=? where id=?",
                new String[]{
                        String.valueOf(bill.getPrice()),
                        String.valueOf(bill.getEmotion()),
                        String.valueOf(bill.getCategoryId()),
                        bill.getDescription(),
                        bill.getLocation(),
                        String.valueOf(bill.getDateForMills()),
                        String.valueOf(bill.getId())
                });
    }

    public void deleteBill(int id) {
        db.execSQL("delete from Bill where id=?",
                new String[]{ String.valueOf(id) });
    }

    public ArrayList<Bill> getBills(String whereClause, int limit) {
        ArrayList<Bill> list = new ArrayList<>();
        String sql = "select id, emotion, location, category_id, description, price, date from Bill " +
                (whereClause == null ? "" : whereClause) +
                "order by date desc";
        if (limit != 0) {
            sql += " limit " + limit;
        }
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Bill bill = new Bill();
                bill.setId(cursor.getInt(cursor.getColumnIndex("id")));
                bill.setEmotion(cursor.getInt(cursor.getColumnIndex("emotion")));
                bill.setLocation(cursor.getString(cursor.getColumnIndex("location")));
                bill.setCategoryId(cursor.getInt(cursor.getColumnIndex("category_id")));
                bill.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                bill.setPrice(cursor.getFloat(cursor.getColumnIndex("price")));
                bill.setDateForMills(cursor.getLong(cursor.getColumnIndex("date")));
                list.add(bill);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void updateCategory(BillCategory category) {
        db.execSQL("update Category set activated=?, background_res_id=?, count=?, parent_id=?, name=?, type=? where id=?",
                new String[]{
                        String.valueOf(category.getActivated()),
                        String.valueOf(category.getBackgroundResId()),
                        String.valueOf(category.getCount()),
                        String.valueOf(category.getParentId()),
                        category.getName(),
                        String.valueOf(category.getType()),
                        String.valueOf(category.getId())
                });
    }
}
