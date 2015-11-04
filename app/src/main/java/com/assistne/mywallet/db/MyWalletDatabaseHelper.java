package com.assistne.mywallet.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.assistne.mywallet.R;
import com.assistne.mywallet.db.DbReaderContract.BillTable;
import com.assistne.mywallet.db.DbReaderContract.CategoryTable;


/**
 * Created by assistne on 15/9/21.
 */
public class MyWalletDatabaseHelper extends SQLiteOpenHelper {

    private static String CREATE_BILL = "create table " +
            BillTable.TABLE_NAME + " (" +
            BillTable._ID + " integer primary key autoincrement, " +
            BillTable.CN_CATEGORY_ID + " integer not null, " +
            BillTable.CN_PRICE + " real default 0, " +
            BillTable.CN_EMOTION + " integer default 0, " +
            BillTable.CN_DESCRIPTION + " text, " +
            BillTable.CN_LOCATION + " text, " +
            BillTable.CN_DATE_IN_MILLS + " integer not null, " +
            BillTable.CN_IMAGE_PATH + " text, " +
            BillTable.CN_IS_INCOME + " integer default 0)";

    private static String CREATE_CATEGORY = "create table " +
            CategoryTable.TABLE_NAME + " (" +
            CategoryTable._ID + " integer primary key autoincrement, " +
            CategoryTable.CN_NAME + " text not null, " +
            CategoryTable.CN_PARENT_ID + " integer default 0, " +
            CategoryTable.CN_TYPE + " integer not null, " +
            CategoryTable.CN_BACKGROUND_RES_ID + " integer default 0, " +
            CategoryTable.CN_COUNT + " integer default 0, " +
            CategoryTable.CN_ACTIVATED + " integer default 0, " +
            CategoryTable.CN_DELETED + " integer default 0)";

    public MyWalletDatabaseHelper(Context mContext, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(mContext, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORY);
        db.execSQL(CREATE_BILL);
        initBillCategoryData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void initBillCategoryData(SQLiteDatabase db) {
        final String comSep = ", ";
        final String insertSQL = "insert into " +
                CategoryTable.TABLE_NAME + " (" +
                CategoryTable.CN_NAME + comSep + CategoryTable.CN_PARENT_ID + comSep +
                CategoryTable.CN_TYPE + comSep + CategoryTable.CN_BACKGROUND_RES_ID + comSep +
                CategoryTable.CN_ACTIVATED + ") values(?, ?, ?, ?, ?)";
        db.execSQL(insertSQL,
                new String[] {"餐饮", "0", "-1", String.valueOf(R.drawable.selector_round_btn_green), "0"});
        db.execSQL(insertSQL,
                new String[] {"交通", "0", "-1", String.valueOf(R.drawable.selector_round_btn_blue), "0"});
        db.execSQL(insertSQL,
                new String[] {"购物", "0", "-1", String.valueOf(R.drawable.selector_round_btn_orange), "0"});
        db.execSQL(insertSQL,
                new String[] {"娱乐", "0", "-1", String.valueOf(R.drawable.selector_round_btn_red), "0"});
        db.execSQL(insertSQL,
                new String[] {"居家", "0", "-1", String.valueOf(R.drawable.selector_round_btn_brown), "0"});
        db.execSQL(insertSQL,
                new String[] {"社交人情", "0", "-1", String.valueOf(R.drawable.selector_round_btn_pink), "0"});
        db.execSQL(insertSQL,
                new String[] {"医教", "0", "-1", String.valueOf(R.drawable.selector_round_btn_light_blue), "0"});

        db.execSQL(insertSQL,
                new String[] {"收入", "0", "1", String.valueOf(R.drawable.selector_round_btn_green), "1"});
        db.execSQL(insertSQL,
                new String[] {"工资", "8", "1", String.valueOf(R.drawable.selector_round_btn_green), "1"});
        db.execSQL(insertSQL,
                new String[] {"奖金", "8", "1", String.valueOf(R.drawable.selector_round_btn_green), "1"});
        db.execSQL(insertSQL,
                new String[] {"补贴", "8", "1", String.valueOf(R.drawable.selector_round_btn_green), "1"});
        db.execSQL(insertSQL,
                new String[] {"兼职", "8", "1", String.valueOf(R.drawable.selector_round_btn_green), "1"});
        db.execSQL(insertSQL,
                new String[] {"分红", "8", "1", String.valueOf(R.drawable.selector_round_btn_green), "1"});

        db.execSQL(insertSQL,
                new String[] {"餐饮", "1", "-1", String.valueOf(R.drawable.selector_round_btn_green), "1"});
        db.execSQL(insertSQL,
                new String[] {"交通", "2", "-1", String.valueOf(R.drawable.selector_round_btn_blue), "1"});
        db.execSQL(insertSQL,
                new String[] {"购物", "3", "-1", String.valueOf(R.drawable.selector_round_btn_orange), "1"});
        db.execSQL(insertSQL,
                new String[] {"娱乐", "4", "-1", String.valueOf(R.drawable.selector_round_btn_red), "1"});
        db.execSQL(insertSQL,
                new String[] {"居家", "5", "-1", String.valueOf(R.drawable.selector_round_btn_brown), "1"});
        db.execSQL(insertSQL,
                new String[] {"社交", "6", "-1", String.valueOf(R.drawable.selector_round_btn_pink), "1"});
        db.execSQL(insertSQL,
                new String[] {"医疗", "7", "-1", String.valueOf(R.drawable.selector_round_btn_light_blue), "1"});

    }
}
