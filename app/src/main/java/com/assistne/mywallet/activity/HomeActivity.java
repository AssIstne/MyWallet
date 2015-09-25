package com.assistne.mywallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.assistne.mywallet.R;
import com.assistne.mywallet.adapter.BillsAdapter;
import com.assistne.mywallet.customview.MainItemsLayout;
import com.assistne.mywallet.db.MyWalletDatabaseUtils;
import com.assistne.mywallet.model.Bill;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by assistne on 15/9/7.
 */
public class HomeActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener{
    private static final String LOG_TAG = "test home act";

    private BillsAdapter billsAdapter;

    private ListView listView;
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
        listView = (ListView)findViewById(R.id.home_list_bill);
        listView.setOnItemClickListener(this);
        spanBill = (MainItemsLayout)findViewById(R.id.home_span_bill);

    }


    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "on home activity onResume");
        super.onResume();
        ArrayList<Bill> list = MyWalletDatabaseUtils.getInstance(this).getBills(null, 4);
        if (billsAdapter == null) {
            billsAdapter = new BillsAdapter(this, list);
            listView.setAdapter(billsAdapter);
        } else {
            billsAdapter.setDataList(list);
            billsAdapter.notifyDataSetChanged();
        }
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bill bill = ((BillsAdapter)listView.getAdapter()).getDataList().get(position);
        Intent intent = new Intent(this, BillDetailActivity.class);
        intent.putExtra("bill", bill);
        startActivity(intent);
    }
}
