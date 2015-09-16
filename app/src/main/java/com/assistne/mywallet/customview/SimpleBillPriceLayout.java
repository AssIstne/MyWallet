package com.assistne.mywallet.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.assistne.mywallet.R;

/**
 * Created by assistne on 15/9/11.
 */
public class SimpleBillPriceLayout extends RelativeLayout {

    private TextView mPrePrice;
    private TextView mPostPrice;
    private String moneySymbol;

    public SimpleBillPriceLayout(Context context) {
        this(context, null);
    }

    public SimpleBillPriceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SimpleBillPriceLayout);
        moneySymbol = array.getText(R.styleable.SimpleBillPriceLayout_money_symbol).toString();
        String price = array.getText(R.styleable.SimpleBillPriceLayout_price_text).toString();
        float fPrice = Float.valueOf(price);
        array.recycle();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.snippet_price_script_layout, this);
        mPrePrice = (TextView)findViewById(R.id.price_script_text_prefix);
        mPostPrice = (TextView)findViewById(R.id.price_script_text_suffix);

        setPriceText(fPrice);

    }

    public void setPriceText(float price) {
        Log.d("test", ""+price);
        mPrePrice.setText(moneySymbol + (int)price + ".");
        mPostPrice.setText(String.valueOf((int)(price*100-(int)price*100)));

    }
}
