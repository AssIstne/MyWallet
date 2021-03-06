package com.assistne.mywallet.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.assistne.mywallet.R;
import com.assistne.mywallet.adapter.BillCategoryFragmentAdapter;
import com.assistne.mywallet.db.MyWalletDatabaseUtils;
import com.assistne.mywallet.fragment.AddActivatedCategoryFragment;
import com.assistne.mywallet.fragment.GlobalNavigationFragment;
import com.assistne.mywallet.fragment.KeyBoardFragment;
import com.assistne.mywallet.fragment.SelectPhotoFragment;
import com.assistne.mywallet.model.Bill;
import com.assistne.mywallet.model.BillCategory;
import com.assistne.mywallet.notification.NotificationUtils;
import com.assistne.mywallet.util.GlobalUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by assistne on 15/9/13.
 */
public class BillActivity extends FragmentActivity implements View.OnClickListener, View.OnTouchListener {

    private String LOG_TAG = "test act bill";

    public static final int FROM_BILL_DETAIL = 1;

    private boolean isIncome = false;
    private boolean isShowingKeyboard = false;

    private Bill currentBill;
    private FragmentManager fragmentManager;

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
    private Button btnCamera;
    private LinearLayout spanPointers;
    private ImageView imgCameraDel;
    private ImageView imgCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        findViews();
        initNavigation();

        fragmentManager = getFragmentManager();

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
        updateTitleAndPrice();
        int emotionViewId;
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
        btnDate.setText(GlobalUtils.getFormatDateFromMills(currentBill.getDateInMills()));
        btnDate.setTag(currentBill.getDateInMills());
        etDescription.setText(currentBill.getDescription());
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
        Log.d(LOG_TAG, "current bill's path " + currentBill.getImagePath());
        if (!currentBill.getImagePath().equals("")) {
            btnCamera.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPhotoImage(currentBill.getImagePath());
                }
            }, 10);
        }
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
        etDescription.setOnTouchListener(this);

        tvCategory = (TextView)findViewById(R.id.bill_text_category);

        btnPrice = (Button)findViewById(R.id.bill_btn_price);
        btnPrice.setOnClickListener(this);

        btnCamera = (Button)findViewById(R.id.bill_btn_camera);
        btnCamera.setOnClickListener(this);

        viewPager = (ViewPager)findViewById(R.id.bill_pager_category);

        spanMain = (ScrollView)findViewById(R.id.bill_span_main);

        spanPointers = (LinearLayout)findViewById(R.id.bill_span_pointers);

        findViewById(R.id.bill_span_share).setOnClickListener(this);

        findViewById(R.id.bill_span_ensure).setOnClickListener(this);

        imgCameraDel = (ImageView)findViewById(R.id.bill_img_camera_delete);
        imgCameraDel.setOnClickListener(this);

        imgCamera = (ImageView)findViewById(R.id.bill_img_camera);
        imgCamera.setOnClickListener(this);
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
    public void initCategorySpan() {
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
                showKeyboard();
                break;
            case R.id.bill_span_ensure:
                saveBill();
                setResult(Activity.RESULT_OK);
                NotificationUtils.showNotification(this);
                finish();
                break;
            case R.id.bill_btn_camera:
                showPhotoSelection();
                break;
            case R.id.bill_img_camera:
                showPhotoSelection();
                break;
            case R.id.bill_img_camera_delete:
                imgCameraDel.setVisibility(View.GONE);
                imgCamera.setVisibility(View.GONE);
                btnCamera.setVisibility(View.VISIBLE);
                imgCamera.setTag(null);
                break;
            case R.id.bill_btn_date:
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
                break;
            default:
                break;
        }
    }

    private void showPhotoSelection() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_up, R.animator.slide_down,
                R.animator.slide_up, R.animator.slide_down);
        transaction.add(R.id.bill_span_root, new SelectPhotoFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void changeTitleKey() {
        isIncome = !isIncome;
        updateTitleAndPrice();
        activatedBillCategory = null;
        currentBill.setCategoryId(BillCategory.NO_CATEGORY);
        initCategorySpan();
    }

    private void updateTitleAndPrice() {
        btnTitleKey.setText(isIncome ? R.string.global_income : R.string.global_spend);
        btnPrice.setTextColor(isIncome ?
                getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
    }


    public void showKeyboard() {
        if (!isShowingKeyboard) {
            KeyBoardFragment keyBoardFragment = KeyBoardFragment.newInstance(
                    true, isIncome, btnPrice.getText().toString(), keyboardCallBack);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.bill_span_root, keyBoardFragment);
            transaction.addToBackStack(null);
            isShowingKeyboard = true;
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        isShowingKeyboard = false;
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
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
                fragmentManager.popBackStack();
                isShowingKeyboard = false;
            }
        }
    };

    public void setActivatedBillCategory(View mActivatedBillCategory) {
        if (mActivatedBillCategory == null) {
            if (activatedBillCategory != null) {
                activatedBillCategory.setActivated(false);
            }
            activatedBillCategory = null;
            return;
        }
        if (activatedBillCategory != null &&
                (((BillCategory)activatedBillCategory.getTag()).getId() ==
                        ((BillCategory)mActivatedBillCategory.getTag()).getId())) {
//            申请激活的类目已经激活则不处理
            return;
        } else if (activatedBillCategory != null) {
            activatedBillCategory.setActivated(false);
        }
        activatedBillCategory = mActivatedBillCategory;
        activatedBillCategory.setActivated(true);
        updateTextCategory();
    }

    public View getActivatedBillCategory() {
        return activatedBillCategory;
    }

    public void updateTextCategory() {
        if (tvCategory != null && activatedBillCategory != null && activatedBillCategory.getTag() != null) {
            tvCategory.setText(((BillCategory) activatedBillCategory.getTag()).getName());
        }
    }

    public void saveBill() {
        int emotion = Integer.valueOf(String.valueOf(activatedEmotion.getTag()));
        String location = tvLocation.getText().toString();
        int categoryId = ((BillCategory)activatedBillCategory.getTag()).getId();
        String description = etDescription.getText().toString();
        float price = Float.valueOf(btnPrice.getText().toString());
        long date = (long)btnDate.getTag();
        currentBill.setPrice(price);
        currentBill.setEmotion(emotion);
        currentBill.setCategoryId(categoryId);
        currentBill.setDescription(description);
        currentBill.setLocation(location);
        currentBill.setDateInMills(date);
        currentBill.setIsIncome(isIncome);
        if (imgCamera.getVisibility() == View.VISIBLE && imgCamera.getTag() != null) {
            currentBill.setImagePath((String)imgCamera.getTag());
        } else {
            currentBill.setImagePath("");
        }
        if (currentBill.getId() == 0) {
            MyWalletDatabaseUtils.getInstance(this).saveBill(currentBill);
        } else {
            MyWalletDatabaseUtils.getInstance(this).updateBill(currentBill);
        }

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


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        spanMain.postDelayed(new Runnable() {
            @Override
            public void run() {
                spanMain.fullScroll(View.FOCUS_DOWN);
            }
        }, 100);
        return false;
    }

    public void showCategoryType() {
        AddActivatedCategoryFragment categoryFragment = AddActivatedCategoryFragment.newInstance(isIncome);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.bill_span_root, categoryFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void removeFragment() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }

    public void showPhotoImage(String path) {
        View spanCamera = findViewById(R.id.bill_span_camera);
        Log.d(LOG_TAG, spanCamera.getHeight() + ":"+spanCamera.getWidth());
        imgCamera.setImageBitmap(
                GlobalUtils.decodeSampledBitmapFromPath(path, spanCamera.getWidth(), spanCamera.getHeight()));
        imgCamera.setTag(path);
        imgCamera.setVisibility(View.VISIBLE);
        imgCameraDel.setVisibility(View.VISIBLE);
        btnCamera.setVisibility(View.GONE);
    }

    protected long getBtnDateTag() {
        return (long)btnDate.getTag();
    }

    protected void setBtnDateTag(long dateInMills) {
        btnDate.setTag(dateInMills);
        btnDate.setText(GlobalUtils.getFormatDateFromMills(dateInMills));
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance(Locale.CHINA);
            c.setTimeInMillis(((BillActivity)getActivity()).getBtnDateTag());
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            final Calendar c = Calendar.getInstance(Locale.CHINA);
            c.set(year, month, day);
            ((BillActivity)getActivity()).setBtnDateTag(c.getTimeInMillis());
        }
    }
}
