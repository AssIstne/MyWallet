package com.assistne.mywallet.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.assistne.mywallet.R;
import com.assistne.mywallet.adapter.BillCategoryFragmentAdapter;
import com.assistne.mywallet.db.MyWalletDatabaseUtils;
import com.assistne.mywallet.fragment.GlobalNavigationFragment;
import com.assistne.mywallet.fragment.KeyBoardFragment;
import com.assistne.mywallet.model.BillCategory;

import java.util.ArrayList;

/**
 * Created by assistne on 15/9/13.
 */
public class BillActivity extends FragmentActivity implements View.OnClickListener {

    private Button btnTitleKey;
    private Button btnPrice;
    private View mActivatedEmotion;
    private boolean isIncome = false;
    private boolean isShowingKeyboard = false;

    private String LOG_TAG = "test act bill";

    private ImageView activatedPointer;
    private View activatedBillCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bill_layout);
        initNavigation();

        mActivatedEmotion = findViewById(R.id.bill_btn_emotion_normal);
        mActivatedEmotion.setOnClickListener(this);
        mActivatedEmotion.setActivated(true);

        findViewById(R.id.bill_btn_emotion_normal).setOnClickListener(this);
        findViewById(R.id.bill_btn_emotion_good).setOnClickListener(this);
        findViewById(R.id.bill_btn_emotion_bad).setOnClickListener(this);
        findViewById(R.id.bill_span_share).setOnClickListener(this);
        findViewById(R.id.bill_span_ensure).setOnClickListener(this);
        btnPrice = (Button)findViewById(R.id.bill_btn_price);
        btnPrice.setOnClickListener(this);

        initCategorySpan();
    }

    private void initNavigation() {
        GlobalNavigationFragment fragment = (GlobalNavigationFragment)getFragmentManager()
                .findFragmentById(R.id.bill_fragment_navigation);
        fragment.setTitle(GlobalNavigationFragment.BILL_NAV, null);
        btnTitleKey = fragment.getBtnBillTitle();
        btnTitleKey.setOnClickListener(this);
    }

    private void initCategorySpan() {
        final ViewPager viewPager = (ViewPager)findViewById(R.id.bill_pager_category);
        ArrayList<BillCategory> data = MyWalletDatabaseUtils.getInstance(this)
                .getActivatedBillCategory(isIncome ? BillCategory.ALL_INCOME : BillCategory.ALL_SPENT);

        BillCategoryFragmentAdapter adapter = new BillCategoryFragmentAdapter(getSupportFragmentManager(), data);
        viewPager.setAdapter(adapter);
        Log.d(LOG_TAG, "count of viewpager " + adapter.getCount());
        final LinearLayout pointers = (LinearLayout)findViewById(R.id.bill_span_pointers);
        if (adapter.getCount() > 1) {
            pointers.setVisibility(View.VISIBLE);
            for (int i = 0; i < adapter.getCount(); i++) {
                Log.d(LOG_TAG, "add pointer");
                ImageView pointer = new ImageView(this);
                pointer.setImageResource(R.drawable.selecter_bill_page_pointer);
                pointer.setTag(i);
                pointer.setPadding(5, 0, 5, 0);
                pointers.addView(pointer);
            }
            activatedPointer = (ImageView)pointers.findViewWithTag(0);
            activatedPointer.setActivated(true);
        } else {
            pointers.setVisibility(View.GONE);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(LOG_TAG, "page  " + position);
                if (activatedPointer != null) {
                    activatedPointer.setActivated(false);
                }
                activatedPointer = (ImageView) pointers.findViewWithTag(position);
                if (activatedPointer != null) {
                    activatedPointer.setActivated(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bill_btn_emotion_normal:
            case R.id.bill_btn_emotion_bad:
            case R.id.bill_btn_emotion_good:
                mActivatedEmotion.setActivated(false);
                mActivatedEmotion = v;
                mActivatedEmotion.setActivated(true);
                break;
            case R.id.global_navigation_btn_title:
                changeTitleKey();
                break;
            case R.id.bill_btn_price:
                Log.d(LOG_TAG, "click btn price");
                showKeyboard();
                break;
            default:
                break;
        }
    }


    public void changeTitleKey() {
        isIncome = !isIncome;
        btnTitleKey.setText(isIncome ? R.string.bill_income : R.string.bill_spend);
        btnPrice.setTextColor(isIncome ?
            getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        initCategorySpan();
    }

    public void showKeyboard() {
        Log.d(LOG_TAG, "show keyboard");
        if (!isShowingKeyboard) {
            KeyBoardFragment keyBoardFragment = new KeyBoardFragment();
            keyBoardFragment.setCallBack(keyboardCallBack);
            Bundle bundle = new Bundle();
            bundle.putBoolean(KeyBoardFragment.IS_INCOME, isIncome);
            bundle.putString(KeyBoardFragment.PRICE, btnPrice.getText().toString());
            keyBoardFragment.setArguments(bundle);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.bill_span_root, keyBoardFragment);
            transaction.addToBackStack(null);
            isShowingKeyboard = true;
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isShowingKeyboard = false;
    }

    KeyBoardFragment.KeyboardCallBack keyboardCallBack = new KeyBoardFragment.KeyboardCallBack() {
        @Override
        public void changePriceType() {
            changeTitleKey();
        }

        @Override
        public void setPrice(String price) {
            btnPrice.setText(price);
        }

        @Override
        public void removeKeyboard() {
            if (isShowingKeyboard) {
                getFragmentManager().popBackStack();
                isShowingKeyboard = false;
            }
        }
    };

    public void setActivatedBillCategory(View mActivatedBillCategory) {
        activatedBillCategory = mActivatedBillCategory;
    }

    public View getActivatedBillCategory() {
        return activatedBillCategory;
    }
}
