package com.assistne.mywallet.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.assistne.mywallet.R;
import com.assistne.mywallet.fragment.BillCategoryFragment;
import com.assistne.mywallet.model.BillCategory;

import java.util.ArrayList;

/**
 * Created by assistne on 15/9/17.
 */
public class BillCategoryFragmentAdapter extends FragmentPagerAdapter {

    public static final String LOG_TAG = "test pager adapter";
    private static final int COUNT_PER_PAGE = 10;
    private ArrayList<BillCategory> categoryList = new ArrayList<>();

    public BillCategoryFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(LOG_TAG, "get item " + position);
        return BillCategoryFragment.newInstance(getItemData(position), position);
    }

    private ArrayList<BillCategory> getItemData(int position) {
        if (COUNT_PER_PAGE * position > categoryList.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(categoryList.subList(
                COUNT_PER_PAGE * position,
                COUNT_PER_PAGE * (position + 1) > categoryList.size() ? categoryList.size() : COUNT_PER_PAGE * (position + 1)));
    }

    @Override
    public int getCount() {
        return 1 + categoryList.size()/ COUNT_PER_PAGE;
    }

    @Override
    public int getItemPosition(Object object) {
        Log.d(LOG_TAG, "get item position "+ super.getItemPosition(object));
        BillCategoryFragment fragment = (BillCategoryFragment)object;
        ArrayList<BillCategory> newData = getItemData(fragment.getPosition());
        fragment.update(newData);
        return POSITION_NONE;
    }

    public void setCategoryList(ArrayList<BillCategory> mCategoryList) {
        categoryList.clear();
        categoryList.addAll(mCategoryList);
        categoryList.add(new BillCategory("+", R.drawable.round_btn_gray, 0 ,0));
    }
}
