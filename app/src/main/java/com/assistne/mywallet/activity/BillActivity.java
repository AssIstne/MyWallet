package com.assistne.mywallet.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.assistne.mywallet.R;
import com.assistne.mywallet.fragment.GlobalNavigationFragment;
import com.assistne.mywallet.fragment.KeyBoardFragment;

/**
 * Created by assistne on 15/9/13.
 */
public class BillActivity extends Activity implements View.OnClickListener {

    private Button mTitleKey;
    private View mActivatedEmotion;
    private KeyBoardFragment keyBoardFragment;
    private boolean isIncome = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bill_layout);
        initNavigation();

        mActivatedEmotion = findViewById(R.id.add_bill_emotion_normal);
        mActivatedEmotion.setOnClickListener(this);
        mActivatedEmotion.setActivated(true);

        findViewById(R.id.add_bill_emotion_normal).setOnClickListener(this);
        findViewById(R.id.add_bill_emotion_good).setOnClickListener(this);
        findViewById(R.id.add_bill_emotion_bad).setOnClickListener(this);
        findViewById(R.id.add_bill_share).setOnClickListener(this);
        findViewById(R.id.add_bill_ensure).setOnClickListener(this);
        findViewById(R.id.add_bill_price).setOnClickListener(this);
    }

    private void initNavigation() {
        GlobalNavigationFragment fragment = (GlobalNavigationFragment)getFragmentManager()
                .findFragmentById(R.id.bill_fragment_navigation);
        fragment.setTitle(GlobalNavigationFragment.BILL_NAV, null);
        mTitleKey = fragment.getBtnBillTitle();
        mTitleKey.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_bill_emotion_normal:
            case R.id.add_bill_emotion_bad:
            case R.id.add_bill_emotion_good:
                mActivatedEmotion.setActivated(false);
                mActivatedEmotion = v;
                mActivatedEmotion.setActivated(true);
                break;
            case R.id.global_navigation_btn_title:
                changeTitleKey();
                break;
            case R.id.add_bill_price:
                if (keyBoardFragment == null) {
                    keyBoardFragment = new KeyBoardFragment();
                }
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.add_bill_layout, keyBoardFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                findViewById(R.id.add_bill_layout).setClickable(true);
        }
    }


    public void changeTitleKey() {
        isIncome = !isIncome;
        mTitleKey.setText(isIncome ? R.string.bill_income : R.string.bill_spend);
    }


}
