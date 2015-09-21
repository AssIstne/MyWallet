package com.assistne.mywallet.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.assistne.mywallet.R;
import com.assistne.mywallet.activity.BillActivity;
import com.assistne.mywallet.model.BillCategory;

import java.util.ArrayList;

/**
 * Created by assistne on 15/9/17.
 */
public class BillCategoryFragment extends android.support.v4.app.Fragment {

    public static final String LOG_TAG = "test category fragment";
    public static final String DATA = "data";

    private View activatedBillCategory;
    private GridView gridView;
    private BillActivity activity;

    public static BillCategoryFragment newInstance(ArrayList<BillCategory> data) {
        BillCategoryFragment fragment = new BillCategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(DATA, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "on create view ");
        activity = (BillActivity)getActivity();
        View root = inflater.inflate(R.layout.fragment_bill_category_layout, container, false);
        Bundle bundle = getArguments();
        ArrayList<BillCategory> data = bundle.getParcelableArrayList(DATA);
        gridView = (GridView)root.findViewById(R.id.bill_category_grid_main);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setAdapter(new GridViewAdapter(getActivity(), data));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activatedBillCategory = activity.getActivatedBillCategory();
                if (activatedBillCategory != null) {
                    activatedBillCategory.setActivated(false);
                }
                activatedBillCategory = view;
                activatedBillCategory.setActivated(true);
                activity.setActivatedBillCategory(activatedBillCategory);
            }
        });
        return root;
    }

    class GridViewAdapter extends BaseAdapter {
        private ArrayList<BillCategory> data;
        private Context context;

        public GridViewAdapter(Context mContext, ArrayList<BillCategory> mData) {
            data = mData;
            context = mContext;
        }

        @Override
        public int getCount() {
            return data.size() > 10 ? 10 : data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(R.layout.grid_item_bill_category_button, null);
                BillCategory billCategory = data.get(position);
                view.findViewById(R.id.grid_item_bill_category_img_bg).setBackgroundResource(billCategory.getBackgroundResId());
                ((TextView)view.findViewById(R.id.grid_item_bill_category_text_name)).setText(billCategory.getName());
            } else {
                view = convertView;
            }
            if (position == 0 && activity.getActivatedBillCategory() == null) {
                view.setActivated(true);
                activity.setActivatedBillCategory(view);
            }
            return view;
        }
    }
}