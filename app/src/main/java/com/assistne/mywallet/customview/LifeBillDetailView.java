package com.assistne.mywallet.customview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.assistne.mywallet.R;
import com.assistne.mywallet.activity.BillDetailActivity;
import com.assistne.mywallet.model.Bill;
import com.assistne.mywallet.model.BillCategory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by assistne on 15/11/7.
 */
public class LifeBillDetailView extends FrameLayout {

    private LinearLayout root;
    private OnClickListener billListener;
    private static final String[] DAY_OF_WEEK = {"日", "一", "二", "三", "四", "五", "六"};
    public LifeBillDetailView(Context context,  ArrayList<Bill> data) {
        super(context);
        ViewGroup parent = (ViewGroup)LayoutInflater.from(context).inflate(R.layout.snippet_life_bill_detail, this);
        root = (LinearLayout)parent.findViewById(R.id.snippet_life_bill_span_root);

        float spend = 0;
        for (Bill bill : data) {
            if (!bill.isIncome())
                spend += bill.getPrice();
            root.addView(getBillView(bill, context));
        }
        PriceView tvSpend = (PriceView)root.findViewById(R.id.snippet_life_bill_text_spend);
        tvSpend.setPriceColor(Color.BLACK);
        tvSpend.setPriceText(spend);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(data.get(0).getDateInMills());
        ((TextView) root.findViewById(R.id.snippet_life_bill_text_date)).setText(
                (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日");
        ((TextView)root.findViewById(R.id.snippet_life_bill_text_week)).setText(
                "星期" + DAY_OF_WEEK[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
    }

    public void setTitleOnClickListener(OnClickListener listener) {
        if (listener != null)
            root.findViewById(R.id.snippet_life_bill_span_title).setOnClickListener(listener);
    }
    private View getBillView(final Bill bill, final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_bill_simple, null);
        ImageView emotion = (ImageView)view.findViewById(R.id.bill_simple_img_emotion);
        TextView tvCategory = (TextView)view.findViewById(R.id.bill_simple_text_category);
        TextView time_location = (TextView)view.findViewById(R.id.bill_simple_text_info);
        PriceView price = (PriceView)view.findViewById(R.id.bill_simple_span_price);
        TextView tvDescription = (TextView)view.findViewById(R.id.bill_simple_text_description);
        if (!bill.getDescription().equals("")) {
            view.findViewById(R.id.bill_simple_img_description).setVisibility(View.VISIBLE);
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(bill.getDescription());
        }
        emotion.setImageResource(bill.getEmotionRes());
        BillCategory category = bill.getBillCategory(context);
        tvCategory.setText(category.getName());
        time_location.setText(bill.getInfo());
        price.setPriceText(bill.getPrice());
        price.setIsIncome(category.getType() > 0);
        view.setTag(bill);
        if (billListener == null) {
            billListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getTag() != null && v.getTag() instanceof Bill) {
                        Bill bill = (Bill)v.getTag();
                        Intent intent = new Intent(context, BillDetailActivity.class);
                        intent.putExtra(BillDetailActivity.F_BILL, bill);
                        ((Activity)context).startActivityForResult(intent, 0);
                    }
                }
            };
        }
        view.setOnClickListener(billListener);
        return view;
    }
}
