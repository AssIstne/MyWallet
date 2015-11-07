package com.assistne.mywallet.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.assistne.mywallet.R;
import com.assistne.mywallet.fragment.GlobalNavigationFragment;
import com.assistne.mywallet.fragment.LifeFragment;
import com.assistne.mywallet.fragment.StatisticFragment;
import com.assistne.mywallet.util.GlobalUtils;

/**
 * Created by assistne on 15/10/12.
 */
public class RecordActivity extends Activity implements View.OnClickListener{

//    key word for bundle:is current fragment life, true for life fragment, false for statistic fragment
    public static final String IS_LIFE = "frag_type";

    private Button btnLife;
    private Button btnStatistic;

    private LifeFragment lifeFragment;
    private StatisticFragment statisticFragment;

    private View spanMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        GlobalNavigationFragment nav = (GlobalNavigationFragment)
                getFragmentManager().findFragmentById(R.id.record_fragment_navigation);
        nav.setTitle(GlobalNavigationFragment.NORMAL_NAV, getString(R.string.global_record));

        btnLife = (Button)findViewById(R.id.record_btn_life);
        btnLife.setOnClickListener(this);
        btnStatistic = (Button)findViewById(R.id.record_btn_statistic);
        btnStatistic.setOnClickListener(this);

        spanMain = findViewById(R.id.record_span_main);
        Bundle bundle = getIntent().getExtras();
        Boolean isLife = true;
        if (bundle != null) {
            isLife = bundle.getBoolean(IS_LIFE, true);
        }

        initFragment(isLife);
    }

    private void initFragment(Boolean isLife) {
        btnLife.setActivated(isLife);
        btnStatistic.setActivated(!isLife);

        if (statisticFragment == null) {
            statisticFragment = StatisticFragment.newInstance();
        }
        if (lifeFragment == null) {
            lifeFragment = new LifeFragment();
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.record_span_main, statisticFragment);
        transaction.add(R.id.record_span_main, lifeFragment);
        transaction.hide(statisticFragment);
        transaction.hide(lifeFragment);
        transaction.commit();
        if (isLife) {
            showLifeFragment();
        } else {
            showStatisticFragment(-1, -1);
        }
    }

    public void showStatisticFragment(long dateInMills, int type) {
        Log.d("", "date :" + GlobalUtils.getFormatDateFromMills(dateInMills) + "  type is " + type);
        if (!btnStatistic.isActivated()) {
            btnStatistic.setActivated(true);
            btnLife.setActivated(false);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.show(statisticFragment);
        transaction.hide(lifeFragment);
        transaction.commit();
        if (dateInMills != -1)
            statisticFragment.setDateAndType(dateInMills, type);
    }

    private void showLifeFragment() {
        showStatisticFragment(-1, -1);
        if (!btnLife.isActivated()) {
            btnStatistic.setActivated(false);
            btnLife.setActivated(true);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.show(lifeFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_btn_life:
                showLifeFragment();
                break;
            case R.id.record_btn_statistic:
                showStatisticFragment(-1 ,0);
                break;
            default:
                break;
        }
    }
}
