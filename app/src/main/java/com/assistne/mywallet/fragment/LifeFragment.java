package com.assistne.mywallet.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.assistne.mywallet.R;
import com.assistne.mywallet.activity.BillActivity;
import com.assistne.mywallet.customview.BudgetProgressBar;
import com.assistne.mywallet.customview.PriceView;
import com.assistne.mywallet.db.MyWalletDatabaseUtils;
import com.assistne.mywallet.util.BillUtils;
import com.assistne.mywallet.util.BitmapCut;
import com.assistne.mywallet.util.GlobalUtils;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by assistne on 15/10/12.
 */
public class LifeFragment extends Fragment implements OnClickListener {
    public static final String LOG_TAG = "test life fragment";

    private View root;
    private BudgetProgressBar progressBar;
    private PriceView spanUsed;
    private PriceView spanUnused;
    private TextView tvMonth;
    private TextView tvDay;
    private RelativeLayout spanDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_life, container, false);
        findViews();
        ImageView imgHead = (ImageView)root.findViewById(R.id.life_img_head);
        Bitmap img = BitmapCut.readBitmapById(getActivity(), R.drawable.main_header_bg);
        imgHead.setImageBitmap(BitmapCut.imageCropWithRect(img));

        return root;
    }

    public void findViews() {
        progressBar = (BudgetProgressBar)root.findViewById(R.id.life_span_progress_bar);

        spanUsed = (PriceView)root.findViewById(R.id.life_span_price_used);
        spanUsed.setPriceColor(Color.WHITE);

        spanUnused = (PriceView)root.findViewById(R.id.life_span_price_unused);
        spanUnused.setPriceColor(Color.WHITE);
        spanUnused.setOnClickListener(this);

        root.findViewById(R.id.life_btn_fast).setOnClickListener(this);
        root.findViewById(R.id.life_btn_camera).setOnClickListener(this);

        tvMonth = (TextView)root.findViewById(R.id.life_text_date_month);
        tvDay = (TextView)root.findViewById(R.id.life_text_date_day);

        spanDate = (RelativeLayout)root.findViewById(R.id.life_span_date);
    }

    public void refresh() {
        float budget = GlobalUtils.getBudget(getActivity());
        final Calendar calendar = Calendar.getInstance(Locale.CHINA);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        long to = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        long from = calendar.getTimeInMillis();
        float used = BillUtils.getSumPrices(MyWalletDatabaseUtils.getInstance(getActivity()).getBillsByTime(from, to), false);
        if (budget == 0) {
            progressBar.setProgress(1f);
        } else {
            progressBar.setProgress(used/budget);
        }
        spanUsed.setPriceText(used);
        spanUnused.setPriceText(budget - used);
        tvMonth.setText((month + 1) + "月");
        tvDay.setText(String.valueOf(day));
        spanDate.postDelayed(new Runnable() {
            @Override
            public void run() {
                float dayOfMonth = (float) day / (float) calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                spanDate.setTranslationX((root.getMeasuredWidth() - spanDate.getMeasuredWidth()) * dayOfMonth);
            }
        }, 10);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.life_btn_fast:
                Intent intent = new Intent(getActivity(), BillActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.life_btn_camera:
                Toast.makeText(getActivity(), "add bill with photo.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.life_span_price_unused:
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.add(R.id.record_span_root, KeyBoardFragment.newInstance(
                        false, false, String.valueOf(GlobalUtils.getBudget(getActivity())), new KeyBoardFragment.KeyboardCallBack() {
                            @Override
                            public void changePriceType() {

                            }

                            @Override
                            public void setPrice(String price) {
                                GlobalUtils.saveBudget(getActivity(), Float.valueOf(price));
                            }

                            @Override
                            public void removeKeyboard() {
                                getActivity().getFragmentManager().popBackStack();
                                refresh();
                            }
                        }
                ));
                transaction.addToBackStack(null);
                transaction.commit();
            default:
                break;
        }
    }
}
