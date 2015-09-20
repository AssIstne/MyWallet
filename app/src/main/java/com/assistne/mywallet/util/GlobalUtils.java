package com.assistne.mywallet.util;

import java.text.DecimalFormat;

/**
 * Created by assistne on 15/9/17.
 */
public class GlobalUtils {

/* 将float转换成价格字符串
 *  price: float类型
 *  isSplit: 是否用‘,’分割,如 1234.00 -> 1,234.00
 *  */
    public static String formatPrice(float price, boolean isSplit) {
        DecimalFormat format = new DecimalFormat(isSplit ? "##,##0.00" : "####0.00");
        return format.format(price);
    }
}
