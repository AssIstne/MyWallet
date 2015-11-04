package com.assistne.mywallet.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.assistne.mywallet.R;

/**
 * Created by assistne on 15/9/11.
 */
public class PriceView extends RelativeLayout {

    private TextView mPrePrice;
    private TextView mPostPrice;
    private String moneySymbol;

    public PriceView(Context context) {
        this(context, null);
    }

    public PriceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PriceView);
        moneySymbol = array.getString(R.styleable.PriceView_money_symbol);
        String price = array.getString(R.styleable.PriceView_price_text);
        int color = array.getColor(R.styleable.PriceView_price_color, getResources().getColor(R.color.red));
        float preSize = array.getDimension(R.styleable.PriceView_pre_size, 0);
        float postSize = array.getDimension(R.styleable.PriceView_post_size, 0);
        if (moneySymbol == null) {
            moneySymbol = context.getString(R.string.symbol_rmb);
        }
        if (price == null) {
            price = "0";
        }
        float fPrice = Float.valueOf(price);
        array.recycle();
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.snippet_price_script, this);

        mPrePrice = (TextView)findViewById(R.id.price_script_text_prefix);
        mPostPrice = (TextView)findViewById(R.id.price_script_text_suffix);
        if (preSize != 0 && postSize != 0) {
            mPrePrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, preSize);
            mPostPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, postSize);
        }
        setPriceText(fPrice);
        setPriceColor(color);

    }

    public void setPriceText(float price) {
        mPrePrice.setText(moneySymbol + (int)price + ".");
        mPostPrice.setText(String.valueOf((int) (price * 100 - (int) price * 100)));

    }

    public void setIsIncome(boolean isIncome) {
        int resColorId = isIncome ? R.color.green : R.color.red;
        setPriceColor(getResources().getColor(resColorId));
    }

    public void setPriceColor(int color) {
        mPostPrice.setTextColor(color);
        mPrePrice.setTextColor(color);
    }

}
