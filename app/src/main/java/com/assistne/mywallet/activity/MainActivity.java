package com.assistne.mywallet.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.assistne.mywallet.R;
import com.assistne.mywallet.adapter.BillsAdapter;
import com.assistne.mywallet.model.Bill;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by assistne on 15/9/7.
 */
public class MainActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_main);

        Button mainIndex = (Button)findViewById(R.id.main_index);
        mainIndex.setActivated(true);
        mainIndex.setOnClickListener(this);

        RelativeLayout mainAddBill = (RelativeLayout)findViewById(R.id.main_add_bill_bg);
        mainAddBill.setOnClickListener(this);

        ListView listView = (ListView)findViewById(R.id.main_bill_list);

        ArrayList<Bill> list = new ArrayList<>();
        Bill bill1 = new Bill();
        bill1.setEmotion(R.drawable.main_good1);
        bill1.setCategoryId(1);
        bill1.setDate(new Date());
        bill1.setLocation("香洲区海滨南路");
        bill1.setPrice((float) 20.42);
        list.add(bill1);
        Bill bill2 = new Bill();
        bill2.setEmotion(R.drawable.main_good1);
        bill2.setCategoryId(1);
        bill2.setDate(new Date());
        bill2.setLocation("香洲区海滨南路");
        bill2.setPrice((float) 30.42);
        list.add(bill2);
        Bill bill3 = new Bill();
        bill3.setEmotion(R.drawable.main_good1);
        bill3.setCategoryId(1);
        bill3.setDate(new Date());
        bill3.setLocation("香洲区海滨南路");
        bill3.setPrice((float) 41.42);
        list.add(bill3);
        Bill bill4 = new Bill();
        bill4.setEmotion(R.drawable.main_good1);
        bill4.setCategoryId(1);
        bill4.setDate(new Date());
        bill4.setLocation("香洲区海滨南路");
        bill4.setPrice((float) 104.42);
        list.add(bill4);
        BillsAdapter adapter = new BillsAdapter(this, list);
        listView.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_add_bill_bg:
                Log.d("test", "click add bill button");
                ImageView imageView = (ImageView)findViewById(R.id.main_add_bill_btn);
                RelativeLayout mainAddBill = (RelativeLayout)findViewById(R.id.main_add_bill_bg);
                Log.d("test", "background" + mainAddBill.getDrawableState().toString());
                Log.d("test", "button" + imageView.getDrawableState().toString());
                break;
        }
    }
}
