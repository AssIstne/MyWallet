package com.assistne.mywallet.db;

import android.provider.BaseColumns;

/**
 * Created by assistne on 15/10/7.
 */
public final class DbReaderContract {

    public DbReaderContract(){}

    public static abstract class BillTable implements BaseColumns {
//        CN_ for column name
        public static final String TABLE_NAME = "Bills";
        public static final String CN_CATEGORY_ID = "category_id";
        public static final String CN_PRICE = "price";
        public static final String CN_EMOTION = "emotion";
        public static final String CN_DESCRIPTION = "description";
        public static final String CN_LOCATION = "location";
        public static final String CN_DATE_IN_MILLS = "date_in_mills";
        public static final String CN_IMAGE_PATH = "image_path";
        public static final String CN_IS_INCOME = "is_income";

    }

    public static abstract class CategoryTable implements BaseColumns {
        public static final String TABLE_NAME = "Categories";
        public static final String CN_NAME = "name";
        public static final String CN_PARENT_ID = "parent_id";
        public static final String CN_TYPE = "type";
        public static final String CN_BACKGROUND_RES_ID = "background_res_id";
        public static final String CN_COUNT = "count";
        public static final String CN_ACTIVATED = "activated";
        public static final String CN_DELETED = "deleted";
    }
}
