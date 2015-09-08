package com.assistne.mywallet.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.assistne.mywallet.R;

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_add_bill_bg:
                Log.d("test", "click add bill button");
                ImageView imageView = (ImageView)findViewById(R.id.main_add_bill_btn);
                RelativeLayout mainAddBill = (RelativeLayout)findViewById(R.id.main_add_bill_bg);
                Log.d("test", "background"+ mainAddBill.getDrawableState().toString());
                Log.d("test", "button"+imageView.getDrawableState().toString());
                break;
        }
    }
}
