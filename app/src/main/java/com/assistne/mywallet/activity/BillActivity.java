package com.assistne.mywallet.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.assistne.mywallet.R;
import com.assistne.mywallet.adapter.BillCategoryFragmentAdapter;
import com.assistne.mywallet.db.MyWalletDatabaseUtils;
import com.assistne.mywallet.fragment.GlobalNavigationFragment;
import com.assistne.mywallet.fragment.KeyBoardFragment;
import com.assistne.mywallet.model.Bill;
import com.assistne.mywallet.model.BillCategory;
import com.assistne.mywallet.util.GlobalUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by assistne on 15/9/13.
 */
public class BillActivity extends FragmentActivity implements View.OnClickListener {

    private String LOG_TAG = "test act bill";

    private boolean isIncome = false;
    private boolean isShowingKeyboard = false;

    private Button btnTitleKey;
    private Button btnPrice;
    private View activatedEmotion;
    private ImageView activatedPointer;
    private View activatedBillCategory;
    private ViewPager viewPager;
    private TextView tvCategory;
    private Button btnDate;
    private TextView tvLocation;
    private EditText etDescription;
    private View spanSaveBill;
    private ScrollView spanMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bill_layout);
        initNavigation();

        activatedEmotion = findViewById(R.id.bill_btn_emotion_normal);
        activatedEmotion.setOnClickListener(this);
        activatedEmotion.setActivated(true);

        spanMain = (ScrollView)findViewById(R.id.bill_span_main);
        findViewById(R.id.bill_btn_emotion_normal).setOnClickListener(this);
        findViewById(R.id.bill_btn_emotion_good).setOnClickListener(this);
        findViewById(R.id.bill_btn_emotion_bad).setOnClickListener(this);
        findViewById(R.id.bill_span_share).setOnClickListener(this);
        findViewById(R.id.bill_span_ensure).setOnClickListener(this);
        tvCategory = (TextView)findViewById(R.id.bill_text_category);
        btnPrice = (Button)findViewById(R.id.bill_btn_price);
        btnPrice.setOnClickListener(this);
        btnDate = (Button)findViewById(R.id.bill_btn_date);
        btnDate.setText(GlobalUtils.getCurrentDate());
        btnDate.setTag(Calendar.getInstance(Locale.CHINA).getTimeInMillis());
        btnDate.setOnClickListener(this);
        tvLocation = (TextView)findViewById(R.id.bill_text_location);
        tvLocation.setText(GlobalUtils.getLocation(this));
        spanSaveBill = findViewById(R.id.bill_span_ensure);
        spanSaveBill.setOnClickListener(this);
        etDescription = (EditText)findViewById(R.id.bill_edit_description);
        etDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(LOG_TAG, "focus change " + hasFocus);
                spanMain.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spanMain.fullScroll(View.FOCUS_DOWN);
                    }
                }, 100);
            }
        });
        viewPager = (ViewPager)findViewById(R.id.bill_pager_category);
        BillCategoryFragmentAdapter adapter = new BillCategoryFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
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
        ArrayList<BillCategory> data = MyWalletDatabaseUtils.getInstance(this)
                .getActivatedBillCategory(isIncome ? BillCategory.ALL_INCOME : BillCategory.ALL_SPENT);
        for (BillCategory billCategory : data) {
            Log.d(LOG_TAG, billCategory.getName());
        }
        BillCategoryFragmentAdapter adapter = (BillCategoryFragmentAdapter)viewPager.getAdapter();
        adapter.setCategoryList(data);
        adapter.notifyDataSetChanged();
        Log.d(LOG_TAG, "count of viewpager " + adapter.getCount());
        final LinearLayout pointers = (LinearLayout)findViewById(R.id.bill_span_pointers);
        if (adapter.getCount() > 1) {
            Log.d(LOG_TAG, "add pointer");
            pointers.removeAllViews();
            pointers.setVisibility(View.VISIBLE);
            for (int i = 0; i < adapter.getCount(); i++) {
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
                activatedEmotion.setActivated(false);
                activatedEmotion = v;
                activatedEmotion.setActivated(true);
                break;
            case R.id.global_navigation_btn_title:
                changeTitleKey();
                break;
            case R.id.bill_btn_price:
                Log.d(LOG_TAG, "click btn price");
                showKeyboard();
                break;
            case R.id.bill_span_ensure:
                saveBill();
                finish();
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
        activatedBillCategory = null;
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
        updateTextCategory();
    }

    public View getActivatedBillCategory() {
        return activatedBillCategory;
    }

    public void updateTextCategory() {
        tvCategory.setText(((BillCategory)activatedBillCategory.getTag()).getName());
    }

    public void saveBill() {
        int emotion = Integer.valueOf(String.valueOf(activatedEmotion.getTag()));
        String location = tvLocation.getText().toString();
        int categoryId = ((BillCategory)activatedBillCategory.getTag()).getId();
        String description = etDescription.getText().toString();
        float price = Float.valueOf(btnPrice.getText().toString());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(String.valueOf(btnDate.getTag())));
        Date date = calendar.getTime();
        Bill bill = new Bill();
        bill.setPrice(price);
        bill.setEmotion(emotion);
        bill.setCategoryId(categoryId);
        bill.setDescription(description);
        bill.setLocation(location);
        bill.setDate(date);
        MyWalletDatabaseUtils.getInstance(this).saveBill(bill);
    }
}
