package com.assistne.mywallet.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.assistne.mywallet.R;
import com.assistne.mywallet.activity.BillActivity;
import com.assistne.mywallet.db.MyWalletDatabaseUtils;
import com.assistne.mywallet.model.Bill;
import com.assistne.mywallet.model.BillCategory;

import java.util.ArrayList;

/**
 * Created by assistne on 15/9/17.
 */
public class BillCategoryFragment extends android.support.v4.app.Fragment {

    public static final String LOG_TAG = "test category fragment";
    public static final String DATA = "data";
    public static final String POSITION = "position";

    private int position;

    private GridView gridView;
    private BillActivity activity;
    private ArrayList<BillCategory> data;

    public static BillCategoryFragment newInstance(ArrayList<BillCategory> data, int position) {
        BillCategoryFragment fragment = new BillCategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(DATA, data);
        bundle.putInt(POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (BillActivity)getActivity();
        View root = inflater.inflate(R.layout.fragment_bill_category_layout, container, false);
        Bundle bundle = getArguments();
        data = bundle.getParcelableArrayList(DATA);
        position = bundle.getInt(POSITION);
        gridView = (GridView)root.findViewById(R.id.bill_category_grid_main);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setAdapter(new GridViewAdapter(getActivity(), data));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BillCategory category = (BillCategory) view.getTag();
                if (category.getType() == BillCategory.NEW_CATEGORY) {
                    activity.showCategoryType();
                } else {
                    view.setTag(data.get(position));
                    activity.setActivatedBillCategory(view);
                }
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                RemoveCategoryDialogFragment dialogFragment = new RemoveCategoryDialogFragment();
                RemoveCategoryDialogFragment.Callback callback = new RemoveCategoryDialogFragment.Callback() {
                    @Override
                    public void remove() {
                        BillCategory category = (BillCategory) view.getTag();
                        category.setActivated(0);
                        MyWalletDatabaseUtils.getInstance(activity).updateCategory(category);
                        activity.setActivatedBillCategory(null);
                        activity.initCategorySpan();
                    }

                    @Override
                    public void edit() {

                    }
                };
                dialogFragment.setCallback(callback);
                dialogFragment.show(activity.getFragmentManager(), null);
                return false;
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
            BillCategory billCategory = data.get(position);
            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(R.layout.grid_item_bill_category_button, null);
                view.findViewById(R.id.grid_item_bill_category_img_bg).setBackgroundResource(billCategory.getBackgroundResId());
                ((TextView)view.findViewById(R.id.grid_item_bill_category_text_name)).setText(billCategory.getName());
            } else {
                view = convertView;
            }
            if (view.getTag() == null) {
                view.setTag(data.get(position));
            }
            Bill bill = activity.getCurrentBill();
            if (bill.getCategoryId() == BillCategory.NO_CATEGORY) {
//                新增bill
                if (position == 0 && activity.getActivatedBillCategory() == null) {
//                    初始化默认激活第一个类目
                    activity.setActivatedBillCategory(view);
                }

            } else {
//                修改bill
                if (billCategory.getId() == bill.getCategoryId()) {
                    activity.setActivatedBillCategory(view);
                }
            }

            return view;
        }

    }

    public void update(ArrayList<BillCategory> newData){
        data.clear();
        data.addAll(newData);
        ((GridViewAdapter)gridView.getAdapter()).notifyDataSetChanged();
    }

    public int getPosition() {
        return position;
    }
}
