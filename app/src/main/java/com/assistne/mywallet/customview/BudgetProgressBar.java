package com.assistne.mywallet.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.assistne.mywallet.R;

/**
 * Created by assistne on 15/10/13.
 */
public class BudgetProgressBar extends FrameLayout {

    private ImageView imgUse;

    public BudgetProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.snippet_progress_bar, this);
        imgUse = (ImageView)findViewById(R.id.progress_img_use);
    }

    public void setProgress(final float donePercent) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                float mPercent = 0f;
                if (donePercent >= 0 && donePercent <= 1) {
                    mPercent = donePercent;
                }
                int parentWidth = getWidth();
                ViewGroup.LayoutParams params = imgUse.getLayoutParams();
                params.width = (int) (parentWidth * mPercent);
                imgUse.setLayoutParams(params);
            }
        }, 10);
    }
}
