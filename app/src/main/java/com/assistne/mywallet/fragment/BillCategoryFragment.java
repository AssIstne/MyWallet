package com.assistne.mywallet.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
        View root = inflater.inflate(R.layout.fragment_bill_category, container, false);
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
                final BillCategory category = (BillCategory) view.getTag();
                final MyWalletDatabaseUtils db = MyWalletDatabaseUtils.getInstance(activity);
                RemoveCategoryDialogFragment dialogFragment = RemoveCategoryDialogFragment.newInstance(category.getType());
                RemoveCategoryDialogFragment.Callback callback = new RemoveCategoryDialogFragment.Callback() {
                    @Override
                    public void remove() {
                        category.setActivated(0);
                        db.updateCategory(category);
                        activity.setActivatedBillCategory(null);
                        activity.initCategorySpan();
                    }

                    @Override
                    public void edit() {
                        EditCategoryDialog.newInstance(category).show(activity.getFragmentManager(), null);
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

    public static class EditCategoryDialog extends DialogFragment {

        public static final String CATEGORY = "cat";
        private BillCategory mCategory;

        public static EditCategoryDialog newInstance(BillCategory category) {
            if (category == null) {
                return null;
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable(CATEGORY, category);
            EditCategoryDialog dialog = new EditCategoryDialog();
            dialog.setArguments(bundle);
            return dialog;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            Bundle bundle = getArguments();
            if (bundle != null) {
                mCategory = bundle.getParcelable(CATEGORY);
            }
            final EditText editText = new EditText(getActivity());
            editText.setText(mCategory.getName());
            builder.setTitle(R.string.dialog_item_modify_category)
                    .setView(editText)
                    .setPositiveButton(R.string.global_ensure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!editText.getText().toString().equals(mCategory.getName())) {
                                mCategory.setName(editText.getText().toString());
                                MyWalletDatabaseUtils.getInstance(getActivity()).updateCategory(mCategory);
                                BillActivity activity = ((BillActivity) getActivity());
                                activity.setActivatedBillCategory(null);
                                activity.initCategorySpan();
                            }
                        }
                    })
                    .setNegativeButton(R.string.global_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            return builder.create();
        }
    }

}
