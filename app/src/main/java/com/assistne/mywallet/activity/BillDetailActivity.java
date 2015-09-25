package com.assistne.mywallet.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.assistne.mywallet.R;
import com.assistne.mywallet.db.MyWalletDatabaseUtils;
import com.assistne.mywallet.fragment.GlobalNavigationFragment;
import com.assistne.mywallet.model.Bill;
import com.assistne.mywallet.model.BillCategory;
import com.assistne.mywallet.util.GlobalUtils;

/**
 * Created by assistne on 15/9/25.
 */
public class BillDetailActivity extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail_layout);
        Bill bill = (Bill)getIntent().getExtras().get("bill");
        BillCategory category = MyWalletDatabaseUtils.getInstance(this).getBillCategory(bill.getCategoryId());
        GlobalNavigationFragment fragment = (GlobalNavigationFragment)getFragmentManager().findFragmentById(R.id.bill_detail_fragment_navigation);
        fragment.setTitle(GlobalNavigationFragment.NORMAL_NAV, category.getType() > 0 ? "收入" : "支出");

        findViewById(R.id.bill_detail_img_emotion).setBackgroundResource(bill.getEmotionRes());
        ((TextView)findViewById(R.id.bill_detail_text_category)).setText(category.getName());
        ((TextView)findViewById(R.id.bill_detail_text_info)).setText(bill.getInfo());
        TextView tvPrice = (TextView) findViewById(R.id.bill_detail_text_price);
        tvPrice.setText(GlobalUtils.formatPrice(bill.getPrice(), false) + "元");
        tvPrice.setTextColor(getResources().getColor(category.getType() > 0 ? R.color.green : R.color.red));
        ((TextView)findViewById(R.id.bill_detail_text_description)).setText(bill.getDescription());

        findViewById(R.id.bill_detail_span_delete).setOnClickListener(this);
        findViewById(R.id.bill_detail_span_share).setOnClickListener(this);
        findViewById(R.id.bill_detail_span_edit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
