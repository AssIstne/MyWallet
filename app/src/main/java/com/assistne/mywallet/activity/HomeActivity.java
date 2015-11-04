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
import android.widget.Toast;

import com.assistne.mywallet.R;
import com.assistne.mywallet.customview.MainItemsLayout;
import com.assistne.mywallet.customview.PriceView;
import com.assistne.mywallet.db.MyWalletDatabaseUtils;
import com.assistne.mywallet.model.Bill;
import com.assistne.mywallet.model.BillCategory;
import com.assistne.mywallet.notification.NotificationUtils;
import com.assistne.mywallet.util.BillUtils;
import com.assistne.mywallet.util.GlobalUtils;

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
        setContentView(R.layout.activity_home);
        Button mainIndex = (Button)findViewById(R.id.home_navigation_btn_index);
        mainIndex.setActivated(true);
        mainIndex.setOnClickListener(this);

        RelativeLayout mainAddBill = (RelativeLayout)findViewById(R.id.home_btn_add_bill);
        mainAddBill.setOnClickListener(this);
        TextView tvBillMonth = (TextView) findViewById(R.id.home_text_bill_month);
        Calendar today = Calendar.getInstance(Locale.CHINA);
        tvBillMonth.setText(String.format("%tm月开支", today));
        spanBillList = (LinearLayout)findViewById(R.id.home_span_bill_list);
        spanBill = (MainItemsLayout)findViewById(R.id.home_span_bill);
        ((TextView) spanBill.findViewById(R.id.home_text_budget_unused)).setText("剩余预算￥" + BillUtils.getBudgetUnused(this, today));
        findViewById(R.id.home_btn_life).setOnClickListener(this);
        findViewById(R.id.home_btn_statistic).setOnClickListener(this);
        findViewById(R.id.home_navigation_btn_action).setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "on home activity onResume");
        super.onResume();
        ArrayList<Bill> list = MyWalletDatabaseUtils.getInstance(this).getLastFourBills();
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
        PriceView price = (PriceView)view.findViewById(R.id.bill_simple_span_price);
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
                intent.putExtra(BillDetailActivity.F_BILL, bill);
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
            case R.id.home_btn_life:
                goRecordActivity(true);
                break;
            case R.id.home_btn_statistic:
                goRecordActivity(false);
                break;
            case R.id.home_navigation_btn_action:
                GlobalUtils.setIsNotify(this, !GlobalUtils.isNotify(this));
                String text = GlobalUtils.isNotify(this) ? "打开" : "关闭";
                Toast.makeText(this, text + "通知栏快捷记账~", Toast.LENGTH_SHORT).show();
                NotificationUtils.showNotification(this);
                break;
            default:
                break;
        }
    }

    private void goRecordActivity(boolean isLife) {
        Intent intent = new Intent(this, RecordActivity.class);
        intent.putExtra(RecordActivity.IS_LIFE, isLife);
        startActivity(intent);
    }
}
