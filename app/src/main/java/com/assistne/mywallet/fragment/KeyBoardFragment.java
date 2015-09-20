package com.assistne.mywallet.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.assistne.mywallet.R;
import com.assistne.mywallet.adapter.KeyBoardAdapter;
import com.assistne.mywallet.util.GlobalUtils;

/**
 * Created by assistne on 15/9/15.
 */
public class KeyBoardFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{

    public static final String IS_INCOME = "isIncome";
    public static final String PRICE = "price";

    private int pricePrefix = 0;
    private int priceSuffix = 0;

    private boolean isEditingPrefix = true;
    private boolean isIncome = false;

    private TextView tvPrice;
    private Button btnTitleKey;
    private KeyboardCallBack mCallBack;


    private String LOG_TAG = "test fragment keyboard";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_keyboard_layout, container, false);

        layout.setClickable(true);
        layout.findViewById(R.id.keyboard_span_root).setOnClickListener(this);
        layout.findViewById(R.id.keyboard_btn_ensure).setOnClickListener(this);
        layout.findViewById(R.id.keyboard_span_main).setOnClickListener(this);

        btnTitleKey = (Button)layout.findViewById(R.id.keyboard_btn_title_key);
        btnTitleKey.setOnClickListener(this);

        tvPrice = (TextView)layout.findViewById(R.id.keyboard_text_price);
        GridView gridView = (GridView)layout.findViewById(R.id.keyboard_grid_numbers);
        gridView.setAdapter(new KeyBoardAdapter(getActivity()));
        gridView.setOnItemClickListener(this);

        initParams();
        refreshPrice();
        return layout;
    }

    private void initParams() {
        Bundle bundle = getArguments();
        isIncome = bundle.getBoolean(IS_INCOME, false);
        String price = bundle.getString(PRICE, "0.0");
        float fPrice = Float.valueOf(price);
        pricePrefix = (int)fPrice;
        int temp = (int)((fPrice - pricePrefix) * 100);
        priceSuffix = temp % 10 == 0 ? temp / 10 : temp;
        Log.d(LOG_TAG, pricePrefix + ":" + priceSuffix);
        isEditingPrefix = priceSuffix == 0;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.keyboard_span_root:
            case R.id.keyboard_btn_ensure:
                Log.d(LOG_TAG, "remove keyboard");
                if (mCallBack != null) {
                    mCallBack.removeKeyboard();
                }
                break;
            case R.id.keyboard_btn_title_key:
                isIncome = !isIncome;
                refreshPrice();
                if (mCallBack != null) {
                    mCallBack.changePriceType();
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 9:
                isEditingPrefix = false;
                break;
            case 10:
                increasePrice(0);
                break;
            case 11:
                decreasePrice();
                break;
            default:
                increasePrice(position + 1);
                break;
        }
    }

    // TODO: 15/9/17 当输入小数位第一位是0时，该算法有BUG
    private void increasePrice(int number) {
        if (isEditingPrefix && pricePrefix < 10000) {
            pricePrefix = 10 * pricePrefix + number;
        } else if (!isEditingPrefix && priceSuffix < 10){
            priceSuffix = 10 * priceSuffix + number;
        }
        refreshPrice();
    }

    private void decreasePrice() {
        if ((isEditingPrefix && pricePrefix > 0)) {
            pricePrefix /= 10;
        } else if (!isEditingPrefix && priceSuffix > 0) {
            priceSuffix /= 10;
        } else if (!isEditingPrefix && priceSuffix == 0) {
            isEditingPrefix = true;
        }
        refreshPrice();
    }

    private void refreshPrice() {
        String price = pricePrefix + "." + priceSuffix;
        price = GlobalUtils.formatPrice(Float.valueOf(price), false);
        tvPrice.setText(price);
        btnTitleKey.setText(isIncome ? R.string.bill_income : R.string.bill_spend);
        btnTitleKey.setTextColor(isIncome ?
                getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        tvPrice.setTextColor(isIncome ?
                getResources().getColor(R.color.green) : getResources().getColor(R.color.red));
        if (mCallBack != null) {
            mCallBack.setPrice(price);
        }
    }

    public interface KeyboardCallBack {
        void changePriceType();
        void setPrice(String price);
        void removeKeyboard();
    }

    public void setCallBack(KeyboardCallBack CallBack) {
        mCallBack = CallBack;
    }

}
