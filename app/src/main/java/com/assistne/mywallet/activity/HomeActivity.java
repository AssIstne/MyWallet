package com.assistne.mywallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.assistne.mywallet.R;
import com.assistne.mywallet.customview.MainItemsLayout;
import com.assistne.mywallet.customview.SimpleBillPriceLayout;
import com.assistne.mywallet.db.MyWalletDatabaseUtils;
import com.assistne.mywallet.model.Bill;
import com.assistne.mywallet.model.BillCategory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by assistne on 15/9/7.
 */
public class HomeActivity extends Activity implements View.OnClickListener{
    private static final String LOG_TAG = "test home act";

    private LinearLayout spanBillList;
    private MainItemsLayout spanBill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "on home activity onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home_layout);
        Button mainIndex = (Button)findViewById(R.id.home_navigation_btn_index);
        mainIndex.setActivated(true);
        mainIndex.setOnClickListener(this);

        RelativeLayout mainAddBill = (RelativeLayout)findViewById(R.id.home_btn_add_bill);
        mainAddBill.setOnClickListener(this);
        TextView tvBillMonth = (TextView) findViewById(R.id.home_text_bill_month);
        tvBillMonth.setText(String.format("%tm月开支", Calendar.getInstance(Locale.CHINA)));
        spanBillList = (LinearLayout)findViewById(R.id.home_span_bill_list);
        spanBill = (MainItemsLayout)findViewById(R.id.home_span_bill);

    }


    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "on home activity onResume");
        super.onResume();
        ArrayList<Bill> list = MyWalletDatabaseUtils.getInstance(this).getBills(null, 4);
        spanBillList.removeAllViews();
        for (Bill bill: list) {
            spanBillList.addView(getBillView(bill));
        }
    }

    private View getBillView(final Bill bill) {
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_bill_simple, null);
        ImageView emotion = (ImageView)view.findViewById(R.id.bill_simple_img_emotion);
        TextView tvCategory = (TextView)view.findViewById(R.id.bill_simple_text_category);
        TextView time_location = (TextView)view.findViewById(R.id.bill_simple_text_info);
        SimpleBillPriceLayout price = (SimpleBillPriceLayout)view.findViewById(R.id.bill_simple_span_price);
        emotion.setImageResource(bill.getEmotionRes());
        BillCategory category = bill.getBillCategory(this);
        tvCategory.setText(category.getName());
        time_location.setText(bill.getInfo());
        price.setPriceText(bill.getPrice());
        price.setIsIncome(category.getType() > 0);
        view.setTag(bill);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, BillDetailActivity.class);
                intent.putExtra("bill", bill);
                startActivity(intent);
            }
        });
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_btn_add_bill:
                Intent intent = new Intent(this, BillActivity.class);
                startActivity(intent);
                break;
        }
    }
}
