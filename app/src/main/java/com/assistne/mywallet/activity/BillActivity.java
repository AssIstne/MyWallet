package com.assistne.mywallet.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
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

/**
 * Created by assistne on 15/9/13.
 */
public class BillActivity extends FragmentActivity implements View.OnClickListener {

    private String LOG_TAG = "test act bill";

    private boolean isIncome = false;
    private boolean isShowingKeyboard = false;

    private Bill currentBill;

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
    private Button btnEmotionGood;
    private Button btnEmotionNormal;
    private Button btnEmotionBad;
    private LinearLayout spanPointers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_layout);
        findViews();
        initNavigation();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentBill = bundle.getParcelable("bill");
        }
        if (currentBill == null) {
            currentBill = new Bill();
            currentBill.setLocation(GlobalUtils.getLocation(this));
        }

        if (currentBill.getCategoryId() != BillCategory.NO_CATEGORY) {
            isIncome = currentBill.getBillCategory(this).getType() > 0;
        }
        if (isIncome) {
            changeTitleKey();
        }
        Log.d(LOG_TAG, "emotion " + currentBill.getEmotion());
        int emotionViewId = 0;
        switch (currentBill.getEmotion()) {
            case Bill.EMOTION_BAD:
                emotionViewId = R.id.bill_btn_emotion_bad;
                break;
            case Bill.EMOTION_GOOD:
                emotionViewId = R.id.bill_btn_emotion_good;
                break;
            default:
                emotionViewId = R.id.bill_btn_emotion_normal;
                break;
        }
//        设置活跃的表情图标
        setActivatedEmotion(findViewById(emotionViewId));

        btnPrice.setText(GlobalUtils.formatPrice(currentBill.getPrice(), false));
        btnDate.setText(GlobalUtils.getFormatDateFromMills(currentBill.getDateForMills()));
        btnDate.setTag(currentBill.getDateForMills());

        tvLocation.setText(currentBill.getLocation());

        BillCategoryFragmentAdapter adapter = new BillCategoryFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (activatedPointer != null) {
                    activatedPointer.setActivated(false);
                }
                activatedPointer = (ImageView) spanPointers.findViewWithTag(position);
                if (activatedPointer != null) {
                    activatedPointer.setActivated(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initCategorySpan();
    }

//    获取view，绑定事件
    private void findViews() {
        btnEmotionNormal = (Button)findViewById(R.id.bill_btn_emotion_normal);
        btnEmotionNormal.setOnClickListener(this);

        btnEmotionGood = (Button)findViewById(R.id.bill_btn_emotion_good);
        btnEmotionGood.setOnClickListener(this);

        btnEmotionBad = (Button)findViewById(R.id.bill_btn_emotion_bad);
        btnEmotionBad.setOnClickListener(this);

        btnDate = (Button)findViewById(R.id.bill_btn_date);
        btnDate.setOnClickListener(this);

        tvLocation = (TextView)findViewById(R.id.bill_text_location);

        spanSaveBill = findViewById(R.id.bill_span_ensure);
        spanSaveBill.setOnClickListener(this);

        etDescription = (EditText)findViewById(R.id.bill_edit_description);
        etDescription.setOnClickListener(this);

        tvCategory = (TextView)findViewById(R.id.bill_text_category);

        btnPrice = (Button)findViewById(R.id.bill_btn_price);
        btnPrice.setOnClickListener(this);

        viewPager = (ViewPager)findViewById(R.id.bill_pager_category);

        spanMain = (ScrollView)findViewById(R.id.bill_span_main);

        spanPointers = (LinearLayout)findViewById(R.id.bill_span_pointers);

        findViewById(R.id.bill_span_share).setOnClickListener(this);

        findViewById(R.id.bill_span_ensure).setOnClickListener(this);

    }

//    初始化标题栏
    private void initNavigation() {
        GlobalNavigationFragment fragment = (GlobalNavigationFragment)getFragmentManager()
                .findFragmentById(R.id.bill_fragment_navigation);
        fragment.setTitle(GlobalNavigationFragment.BILL_NAV, null);
        btnTitleKey = fragment.getBtnBillTitle();
        btnTitleKey.setOnClickListener(this);
    }

//    初始化账单类目区域
    private void initCategorySpan() {
        ArrayList<BillCategory> data = MyWalletDatabaseUtils.getInstance(this)
                .getActivatedBillCategory(isIncome ? BillCategory.ALL_INCOME : BillCategory.ALL_SPENT);
        BillCategoryFragmentAdapter adapter = (BillCategoryFragmentAdapter)viewPager.getAdapter();
        adapter.setCategoryList(data);
        adapter.notifyDataSetChanged();
        if (adapter.getCount() > 1) {
            spanPointers.removeAllViews();
            spanPointers.setVisibility(View.VISIBLE);
            for (int i = 0; i < adapter.getCount(); i++) {
                ImageView pointer = new ImageView(this);
                pointer.setImageResource(R.drawable.selecter_bill_page_pointer);
                pointer.setTag(i);
                pointer.setPadding(5, 0, 5, 0);
                spanPointers.addView(pointer);
            }
            activatedPointer = (ImageView)spanPointers.findViewWithTag(0);
            activatedPointer.setActivated(true);
        } else {
            spanPointers.setVisibility(View.GONE);
        }

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
            case R.id.bill_edit_description:
                spanMain.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spanMain.fullScroll(View.FOCUS_DOWN);
                    }
                }, 100);
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
        currentBill.setCategoryId(BillCategory.NO_CATEGORY);
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
        if (activatedBillCategory != null &&
                (((BillCategory)activatedBillCategory.getTag()).getId() ==
                        ((BillCategory)mActivatedBillCategory.getTag()).getId())) {
//            申请激活的类目已经激活则不处理
            return;
        } else if (activatedBillCategory != null) {
            Log.d(LOG_TAG, "set false");
            activatedBillCategory.setActivated(false);
        }
        activatedBillCategory = mActivatedBillCategory;
        Log.d(LOG_TAG, "set true");
        activatedBillCategory.setActivated(true);
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
        long date = (long)btnDate.getTag();
        Bill bill = new Bill();
        bill.setPrice(price);
        bill.setEmotion(emotion);
        bill.setCategoryId(categoryId);
        bill.setDescription(description);
        bill.setLocation(location);
        bill.setDateForMills(date);
        MyWalletDatabaseUtils.getInstance(this).saveBill(bill);
    }


    private void setActivatedEmotion(View emotionView) {
        if (activatedEmotion != null) {
            activatedEmotion.setActivated(false);
        }
        activatedEmotion = emotionView;
        activatedEmotion.setActivated(true);
    }

    public Bill getCurrentBill() {
        return currentBill;
    }
}
