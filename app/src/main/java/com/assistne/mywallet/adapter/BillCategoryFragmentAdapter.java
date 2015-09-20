package com.assistne.mywallet.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.assistne.mywallet.fragment.BillCategoryFragment;
import com.assistne.mywallet.model.BillCategory;

import java.util.ArrayList;

/**
 * Created by assistne on 15/9/17.
 */
public class BillCategoryFragmentAdapter extends FragmentPagerAdapter {

    private static final int COUNT_PER_PAGE = 5;
    private ArrayList<BillCategory> categoryList;

    public BillCategoryFragmentAdapter(FragmentManager fm, ArrayList<BillCategory> list) {
        super(fm);
        categoryList = list;
    }

    @Override
    public Fragment getItem(int position) {
        ArrayList<BillCategory> subList = new ArrayList<>(categoryList.subList(
                COUNT_PER_PAGE * position,
                COUNT_PER_PAGE * (position + 1) > categoryList.size() ? categoryList.size() : COUNT_PER_PAGE * (position + 1)));
        return BillCategoryFragment.newInstance(subList);
    }

    @Override
    public int getCount() {
        return 1 + categoryList.size()/ COUNT_PER_PAGE;
    }
}
