package com.assistne.mywallet.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.assistne.mywallet.R;

/**
 * Created by assistne on 15/9/16.
 */
public class GlobalNavigationFragment extends Fragment implements View.OnClickListener{

    public static final int NORMAL_NAV = 0;
    public static final int BILL_NAV = 1;

    private Button btnBack;
    private Button btnBillTitle;
    private TextView tvNormalTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_global_navigation_layout, null);
        btnBack = (Button)view.findViewById(R.id.global_navigation_btn_back);
        btnBack.setOnClickListener(this);
        btnBillTitle = (Button)view.findViewById(R.id.global_navigation_btn_title);
        tvNormalTitle = (TextView)view.findViewById(R.id.global_navigation_text_title);
        return view;
    }

    public void setTitle(int type, String title) {
        if (type == NORMAL_NAV) {
            btnBillTitle.setVisibility(View.GONE);
            tvNormalTitle.setText(title);
            tvNormalTitle.setVisibility(View.VISIBLE);
        } else if (type == BILL_NAV){
            tvNormalTitle.setVisibility(View.GONE);
            btnBillTitle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.global_navigation_btn_back:
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    public Button getBtnBillTitle() {
        return btnBillTitle;
    }
}
