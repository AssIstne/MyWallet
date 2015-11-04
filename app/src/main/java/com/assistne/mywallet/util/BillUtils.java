package com.assistne.mywallet.util;

import android.content.Context;

import com.assistne.mywallet.db.MyWalletDatabaseUtils;
import com.assistne.mywallet.model.Bill;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by assistne on 15/10/30.
 */
public class BillUtils {

    public static float getSumPrices(ArrayList<Bill> data, boolean isIncome) {
        float sum = 0f;
        if (data != null) {
            for (Bill bill : data) {
                if (bill.isIncome() == isIncome)
                    sum += bill.getPrice();
            }
        }
        return sum;
    }

    public static float getBudgetUnused(Context context, Calendar month) {
        if (context != null && month != null) {
            float budget = GlobalUtils.getBudget(context);
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            calendar.setTimeInMillis(month.getTimeInMillis());
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            long to = calendar.getTimeInMillis();
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            long from = calendar.getTimeInMillis();
            return budget - getSumPrices(MyWalletDatabaseUtils.getInstance(context).getBillsByTime(from, to), false);
        }
        return 0;
    }

    public static float getDaySpend(Context context, Calendar day) {
        if (context != null && day != null) {
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            calendar.setTimeInMillis(day.getTimeInMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            long to = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            long from = calendar.getTimeInMillis();
            return getSumPrices(MyWalletDatabaseUtils.getInstance(context).getBillsByTime(from, to), false);
        }
        return 0;
    }
}
