package com.assistne.mywallet.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
        String sql = "select id, name, background_res_id, parent_id from Category " +
                "where activated = 1 and " +
                typeCondition +
                "order by count desc";
        Cursor cursor = db.rawQuery(sql, new String[] {String.valueOf(type)});
        if (cursor.moveToFirst()) {
            do {
                BillCategory category = new BillCategory(
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("background_res_id")),
                        cursor.getInt(cursor.getColumnIndex("parent_id")),
                        type
                );
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
                "select name, background_res_id, parent_id, type from Category where id = ?",
                new String[]{String.valueOf(id)});
        BillCategory category = null;
        if (cursor.moveToFirst()) {
            category = new BillCategory(
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getInt(cursor.getColumnIndex("background_res_id")),
                    cursor.getInt(cursor.getColumnIndex("parent_id")),
                    cursor.getInt(cursor.getColumnIndex("type"))
            );
            category.setId(id);
        }
        cursor.close();
        return category;
    }
}
