package com.assistne.mywallet.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.assistne.mywallet.R;

/**
 * Created by assistne on 15/9/21.
 */
public class MyWalletDatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    private static String CREATE_BILL = "create table Bill (" +
            "id integer primary key autoincrement, " +
            "category_id integer not null, " +
            "price real default 0, " +
            "emotion integer default 0, " +
            "description text, " +
            "location text, " +
            "date integer not null)";

    private static String CREATE_CATEGORY = "create table Category (" +
            "id integer primary key autoincrement, " +
            "name text not null, " +
            "parent_id integer default 0, " +
            "type integer not null, " +
            "background_res_id integer default 0, " +
            "count integer default 0," +
            "activated integer default 1)";

    public MyWalletDatabaseHelper(Context mContext, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(mContext, name, factory, version);
        context = mContext;
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
        db.execSQL("insert into Category (name, parent_id, type, background_res_id, activated) values(?, ?, ?, ?, ?)",
                new String[] {"餐饮", "0", "-1", String.valueOf(R.drawable.selector_round_btn_green), "0"});
        db.execSQL("insert into Category (name, parent_id, type, background_res_id, activated) values(?, ?, ?, ?, ?)",
                new String[] {"交通", "0", "-1", String.valueOf(R.drawable.selector_round_btn_blue), "0"});
        db.execSQL("insert into Category (name, parent_id, type, background_res_id, activated) values(?, ?, ?, ?, ?)",
                new String[] {"购物", "0", "-1", String.valueOf(R.drawable.selector_round_btn_orange), "0"});
        db.execSQL("insert into Category (name, parent_id, type, background_res_id, activated) values(?, ?, ?, ?, ?)",
                new String[] {"娱乐", "0", "-1", String.valueOf(R.drawable.selector_round_btn_red), "0"});
        db.execSQL("insert into Category (name, parent_id, type, background_res_id, activated) values(?, ?, ?, ?, ?)",
                new String[] {"居家", "0", "-1", String.valueOf(R.drawable.selector_round_btn_brown), "0"});
        db.execSQL("insert into Category (name, parent_id, type, background_res_id, activated) values(?, ?, ?, ?, ?)",
                new String[] {"社交人情", "0", "-1", String.valueOf(R.drawable.selector_round_btn_pink), "0"});
        db.execSQL("insert into Category (name, parent_id, type, background_res_id, activated) values(?, ?, ?, ?, ?)",
                new String[] {"医教", "0", "-1", String.valueOf(R.drawable.selector_round_btn_light_blue), "0"});

        db.execSQL("insert into Category (name, parent_id, type, background_res_id) values(?, ?, ?, ?)",
                new String[] {"工资", "0", "1", String.valueOf(R.drawable.selector_round_btn_green)});
        db.execSQL("insert into Category (name, parent_id, type, background_res_id) values(?, ?, ?, ?)",
                new String[] {"奖金", "0", "1", String.valueOf(R.drawable.selector_round_btn_green)});
        db.execSQL("insert into Category (name, parent_id, type, background_res_id) values(?, ?, ?, ?)",
                new String[] {"补贴", "0", "1", String.valueOf(R.drawable.selector_round_btn_green)});
        db.execSQL("insert into Category (name, parent_id, type, background_res_id) values(?, ?, ?, ?)",
                new String[] {"兼职", "0", "1", String.valueOf(R.drawable.selector_round_btn_green)});
        db.execSQL("insert into Category (name, parent_id, type, background_res_id) values(?, ?, ?, ?)",
                new String[] {"分红", "0", "1", String.valueOf(R.drawable.selector_round_btn_green)});

        db.execSQL("insert into Category (name, parent_id, type, background_res_id) values(?, ?, ?, ?)",
                new String[] {"餐饮", "1", "-1", String.valueOf(R.drawable.selector_round_btn_green)});
        db.execSQL("insert into Category (name, parent_id, type, background_res_id) values(?, ?, ?, ?)",
                new String[] {"交通", "2", "-1", String.valueOf(R.drawable.selector_round_btn_blue)});
        db.execSQL("insert into Category (name, parent_id, type, background_res_id) values(?, ?, ?, ?)",
                new String[] {"购物", "3", "-1", String.valueOf(R.drawable.selector_round_btn_orange)});
        db.execSQL("insert into Category (name, parent_id, type, background_res_id) values(?, ?, ?, ?)",
                new String[] {"娱乐", "4", "-1", String.valueOf(R.drawable.selector_round_btn_red)});
        db.execSQL("insert into Category (name, parent_id, type, background_res_id) values(?, ?, ?, ?)",
                new String[] {"居家", "5", "-1", String.valueOf(R.drawable.selector_round_btn_brown)});
        db.execSQL("insert into Category (name, parent_id, type, background_res_id) values(?, ?, ?, ?)",
                new String[] {"社交", "6", "-1", String.valueOf(R.drawable.selector_round_btn_pink)});
        db.execSQL("insert into Category (name, parent_id, type, background_res_id) values(?, ?, ?, ?)",
                new String[] {"医疗", "7", "-1", String.valueOf(R.drawable.selector_round_btn_light_blue)});

    }
}
