package com.assistne.mywallet.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.assistne.mywallet.db.DbReaderContract.BillTable;
import com.assistne.mywallet.db.DbReaderContract.CategoryTable;
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
            myWalletDatabaseUtils = new MyWalletDatabaseUtils(context.getApplicationContext());
        }
        return myWalletDatabaseUtils;
    }

    private static final String[] CATEGORY_PROJECTION = {
            CategoryTable._ID,
            CategoryTable.CN_NAME,
            CategoryTable.CN_PARENT_ID,
            CategoryTable.CN_ACTIVATED,
            CategoryTable.CN_BACKGROUND_RES_ID,
            CategoryTable.CN_TYPE,
            CategoryTable.CN_DELETED,
            CategoryTable.CN_COUNT
    };

    private static final String[] BILL_PROJECTION = {
            BillTable._ID,
            BillTable.CN_CATEGORY_ID,
            BillTable.CN_DATE_IN_MILLS,
            BillTable.CN_DESCRIPTION,
            BillTable.CN_EMOTION,
            BillTable.CN_LOCATION,
            BillTable.CN_PRICE,
            BillTable.CN_IMAGE_PATH,
            BillTable.CN_IS_INCOME
    };

    private static final String EQUAL = " = ";
    private static final String AND = " and ";
    private static final String DESC = " desc";

/*
  * 获取新增账单页面展示的账单类型
  * type:
  * BillCategory.All_INCOME-所有收入类型，包括系统自带的和用户添加的
  * BillCategory.ALL_SPENT-所有支出类型，包括系统自带的和用户添加的
  * */
    public ArrayList<BillCategory> getActivatedBillCategory(int type) {
        String typeSelection = getCatTypeSelection(type);
        Cursor cursor = db.query(CategoryTable.TABLE_NAME, CATEGORY_PROJECTION,
                CategoryTable.CN_ACTIVATED + EQUAL + "1" +
                        AND + CategoryTable.CN_DELETED + EQUAL + "0" +
                        AND + typeSelection, null, null, null, CategoryTable.CN_COUNT + DESC);
        return getCategoryListFromCursor(cursor);
    }

    public BillCategory getCategory(int id) {
        Cursor cursor = db.query(CategoryTable.TABLE_NAME, CATEGORY_PROJECTION,
                CategoryTable._ID + EQUAL + "?", new String[]{String.valueOf(id)}, null, null, null);
        ArrayList<BillCategory> list = getCategoryListFromCursor(cursor);
        return list.isEmpty()? null : list.get(0);
    }

    public BillCategory getCategory(String name) {
        Cursor cursor = db.query(CategoryTable.TABLE_NAME, CATEGORY_PROJECTION,
                CategoryTable.CN_NAME + EQUAL + "?", new String[]{name}, null, null, null);
        ArrayList<BillCategory> list = getCategoryListFromCursor(cursor);
        return list.isEmpty()? null : list.get(0);
    }

    public void saveBill(Bill bill) {
        if (bill != null) {
            Log.d("data base", "add bill, image path is " + bill.getImagePath());
            db.insert(BillTable.TABLE_NAME, null, convertBill(bill));
        }
    }

    public void updateBill(Bill bill) {
        if (bill != null && bill.getId() != 0) {
            db.update(BillTable.TABLE_NAME, convertBill(bill), BillTable._ID + " = ?",
                    new String[]{String.valueOf(bill.getId())});
        }
    }

    public void deleteBill(int id) {
        if (id != 0) {
            db.delete(BillTable.TABLE_NAME, BillTable._ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
    }

    public ArrayList<Bill> getLastFourBills() {
        Cursor cursor = db.query(BillTable.TABLE_NAME, BILL_PROJECTION, null, null, null, null,
                BillTable.CN_DATE_IN_MILLS + DESC, "4");
        return getBillListFromCursor(cursor);
    }

    public void updateCategory(BillCategory category) {
        if (category != null && category.getId() != 0) {
            db.update(CategoryTable.TABLE_NAME, convertCategory(category), CategoryTable._ID + " = ?",
                    new String[]{String.valueOf(category.getId())});
        }
    }

    public void saveCategory(BillCategory category) {
        if (category != null) {
            db.insert(CategoryTable.TABLE_NAME, null, convertCategory(category));
        }
    }

    public void deleteCategory(BillCategory category) {
        if (category != null && category.getId() != 0) {
            ContentValues values = new ContentValues();
            values.put(CategoryTable.CN_DELETED, 1);
            db.update(CategoryTable.TABLE_NAME, values, CategoryTable._ID + " = ?",
                    new String[]{String.valueOf(category.getId())});
        }
    }

    public ArrayList<BillCategory> getParentCategories(int type) {
        String typeSelection = getCatTypeSelection(type);
        Cursor cursor = db.query(CategoryTable.TABLE_NAME, CATEGORY_PROJECTION,
                CategoryTable.CN_DELETED + EQUAL + "0" +
                        AND + CategoryTable.CN_PARENT_ID + EQUAL + "0" +
                        AND + typeSelection, null, null, null, CategoryTable._ID);
        return getCategoryListFromCursor(cursor);
    }

    public ArrayList<BillCategory> getAllCategories(int type) {
        ArrayList<BillCategory> res = new ArrayList<>();
        ArrayList<BillCategory> temp = getParentCategories(type);
        Cursor cursor;
        for (BillCategory cat : temp) {
            res.add(cat);
            cursor = db.query(CategoryTable.TABLE_NAME, CATEGORY_PROJECTION,
                    CategoryTable.CN_PARENT_ID + EQUAL + "?" +
                            AND + CategoryTable.CN_DELETED + EQUAL + "0",
                    new String[]{String.valueOf(cat.getId())}, null, null, CategoryTable._ID);

            res.addAll(getCategoryListFromCursor(cursor));
        }

        return res;
    }

    public ArrayList<BillCategory> searchCategoriesByName(String likeClause, int type) {
        if (likeClause != null) {
            Cursor cursor = db.query(CategoryTable.TABLE_NAME, CATEGORY_PROJECTION,
                    "deleted = 0 and parent_id > 0" + AND + getCatTypeSelection(type) + AND + CategoryTable.CN_NAME + " like ?",
                    new String[]{'%' + likeClause + '%'}, null, null, CategoryTable._ID);
            return getCategoryListFromCursor(cursor);
        }
        return new ArrayList<>();
    }

//    根据类型不同选择不同的where语句
    private String getCatTypeSelection(int type) {
        switch (type) {
            case BillCategory.ALL_INCOME:
                return  CategoryTable.CN_TYPE + " > 0";
            case BillCategory.ALL_SPENT:
                return  CategoryTable.CN_TYPE + " < 0";
            default:
                return CategoryTable.CN_TYPE + EQUAL + type;
        }
    }

    private ArrayList<BillCategory> getCategoryListFromCursor(Cursor cursor) {
        ArrayList<BillCategory> categories = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                BillCategory category = new BillCategory();
                category.setName(cursor.getString(cursor.getColumnIndex(CategoryTable.CN_NAME)));
                category.setBackgroundResId(cursor.getInt(cursor.getColumnIndex(CategoryTable.CN_BACKGROUND_RES_ID)));
                category.setParentId(cursor.getInt(cursor.getColumnIndex(CategoryTable.CN_PARENT_ID)));
                category.setType(cursor.getInt(cursor.getColumnIndex(CategoryTable.CN_TYPE)));
                category.setId(cursor.getInt(cursor.getColumnIndex(CategoryTable._ID)));
                category.setActivated(cursor.getInt(cursor.getColumnIndex(CategoryTable.CN_ACTIVATED)));
                category.setCount(cursor.getInt(cursor.getColumnIndex(CategoryTable.CN_COUNT)));
                categories.add(category);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return categories;
    }

    private ArrayList<Bill> getBillListFromCursor(Cursor cursor) {
        ArrayList<Bill> bills = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Bill bill = new Bill();
                bill.setId(cursor.getInt(cursor.getColumnIndex(BillTable._ID)));
                bill.setEmotion(cursor.getInt(cursor.getColumnIndex(BillTable.CN_EMOTION)));
                bill.setLocation(cursor.getString(cursor.getColumnIndex(BillTable.CN_LOCATION)));
                bill.setCategoryId(cursor.getInt(cursor.getColumnIndex(BillTable.CN_CATEGORY_ID)));
                bill.setDescription(cursor.getString(cursor.getColumnIndex(BillTable.CN_DESCRIPTION)));
                bill.setPrice(cursor.getFloat(cursor.getColumnIndex(BillTable.CN_PRICE)));
                bill.setDateInMills(cursor.getLong(cursor.getColumnIndex(BillTable.CN_DATE_IN_MILLS)));
                bill.setImagePath(cursor.getString(cursor.getColumnIndex(BillTable.CN_IMAGE_PATH)));
                bill.setIsIncome(cursor.getInt(cursor.getColumnIndex(BillTable.CN_IS_INCOME)) == 1);
                bills.add(bill);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return bills;
    }

    private ContentValues convertBill(Bill bill) {
        ContentValues values = new ContentValues();
        values.put(BillTable.CN_EMOTION, bill.getEmotion());
        values.put(BillTable.CN_CATEGORY_ID, bill.getCategoryId());
        values.put(BillTable.CN_DATE_IN_MILLS, bill.getDateInMills());
        values.put(BillTable.CN_DESCRIPTION, bill.getDescription());
        values.put(BillTable.CN_LOCATION, bill.getLocation());
        values.put(BillTable.CN_PRICE, bill.getPrice());
        values.put(BillTable.CN_IMAGE_PATH, bill.getImagePath());
        values.put(BillTable.CN_IS_INCOME, bill.isIncome());
        return values;
    }

    private ContentValues convertCategory(BillCategory category) {
        ContentValues values = new ContentValues();
        values.put(CategoryTable.CN_ACTIVATED, category.getActivated());
        values.put(CategoryTable.CN_BACKGROUND_RES_ID, category.getBackgroundResId());
        values.put(CategoryTable.CN_COUNT, category.getCount());
        values.put(CategoryTable.CN_NAME, category.getName());
        values.put(CategoryTable.CN_PARENT_ID, category.getParentId());
        values.put(CategoryTable.CN_TYPE, category.getType());
        return values;
    }

    public ArrayList<Bill> getBillsByTime(long from, long to) {
        Cursor cursor = db.query(BillTable.TABLE_NAME, BILL_PROJECTION,
                BillTable.CN_DATE_IN_MILLS + ">=?" + AND + BillTable.CN_DATE_IN_MILLS + "<=?",
                new String[]{String.valueOf(from), String.valueOf(to)}, null, null, BillTable._ID + DESC);
        return getBillListFromCursor(cursor);
    }

}
