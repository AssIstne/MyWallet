package com.assistne.mywallet.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.assistne.mywallet.R;

/**
 * Created by assistne on 15/9/13.
 */
public class AddBillActivity extends Activity implements View.OnClickListener {

    private Button mTitleKey;
    private View mActivedEmotion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_add_bill);

        mActivedEmotion = findViewById(R.id.add_bill_emotion_normal);
        mActivedEmotion.setOnClickListener(this);
        mActivedEmotion.setActivated(true);
        mTitleKey = (Button)findViewById(R.id.add_bill_title_key);
        mTitleKey.setOnClickListener(this);

        findViewById(R.id.add_bill_back_btn).setOnClickListener(this);
        findViewById(R.id.add_bill_emotion_normal).setOnClickListener(this);
        findViewById(R.id.add_bill_emotion_good).setOnClickListener(this);
        findViewById(R.id.add_bill_emotion_bad).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_bill_back_btn:
                finish();
                break;
            case R.id.add_bill_emotion_normal:
            case R.id.add_bill_emotion_bad:
            case R.id.add_bill_emotion_good:
                mActivedEmotion.setActivated(false);
                mActivedEmotion = v;
                mActivedEmotion.setActivated(true);
                break;
            case R.id.add_bill_title_key:
                mTitleKey.setText(
                        mTitleKey.getText().equals(getResources().getString(R.string.add_bill_key_in)) ?
                                R.string.add_bill_key_out :
                                R.string.add_bill_key_in);
                break;
        }
    }
}
