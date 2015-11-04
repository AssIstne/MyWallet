package com.assistne.mywallet.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.assistne.mywallet.R;
import com.assistne.mywallet.activity.BillActivity;
import com.assistne.mywallet.db.MyWalletDatabaseUtils;
import com.assistne.mywallet.model.BillCategory;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddActivatedCategoryFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{
    public static final String LOG_TAG = "fragment add act cat";

    private static final String IS_INCOME = "isIncome";

    private boolean isIncome = false;
    private boolean isSearch = false;
    private BillActivity activity;

    private View spanRoot;
    private ImageView imgClose;
    private ImageView imgClear;
    private EditText etSearch;
    private ListView lvCategory;
    private ListView lvSideBar;
    private TextView tvTitle;

    public static AddActivatedCategoryFragment newInstance(boolean mIsIncome) {
        AddActivatedCategoryFragment fragment = new AddActivatedCategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_INCOME, mIsIncome);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_activated_category, container, false);
        activity = (BillActivity)getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            isIncome = bundle.getBoolean(IS_INCOME, false);
        }
        findViews(view);

        lvSideBar.setVisibility(isIncome ? View.GONE : View.VISIBLE);
        tvTitle.setText(isIncome ? R.string.title_category_type_in : R.string.title_category_type_out);
        ArrayList<BillCategory> data = MyWalletDatabaseUtils.getInstance(activity).getAllCategories(
                isIncome ? BillCategory.ALL_INCOME : BillCategory.ALL_SPENT);
        CategoryAdapter adapter = new CategoryAdapter(getActivity(), data);
        lvCategory.setAdapter(adapter);
        lvCategory.setOnItemClickListener(this);
        return view;
    }

    public void findViews(View view) {
        spanRoot = view.findViewById(R.id.add_act_cat_span_root);
        spanRoot.setOnClickListener(this);

        tvTitle = (TextView)view.findViewById(R.id.add_act_cat_text_title);

        imgClose = (ImageView)view.findViewById(R.id.add_act_cat_icon_close);
        imgClose.setOnClickListener(this);

        imgClear = (ImageView)view.findViewById(R.id.add_act_cat_icon_clear);
        imgClear.setOnClickListener(this);

        etSearch = (EditText)view.findViewById(R.id.add_act_cat_edit_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchOrAddCategory(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lvCategory = (ListView)view.findViewById(R.id.add_act_cat_list_category);
        lvSideBar = (ListView)view.findViewById(R.id.add_act_cat_list_sidebar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_act_cat_icon_close:
                ((BillActivity)getActivity()).removeFragment();
                break;
            case R.id.add_act_cat_span_root:
                ((BillActivity)getActivity()).removeFragment();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BillCategory category = (BillCategory)view.getTag();
        final MyWalletDatabaseUtils db = MyWalletDatabaseUtils.getInstance(activity);
        if (category != null) {
            if (category.getType() == BillCategory.NEW_CATEGORY) {
                final String name = getCatNameInQuota(category.getName());
                if (isIncome) {
                    BillCategory newCat = new BillCategory();
                    newCat.setName(name);
                    newCat.setActivated(1);
                    newCat.setType(BillCategory.CUSTOM_INCOME);
                    newCat.setParentId(
                            db.getParentCategories(BillCategory.SYSTEM_INCOME).get(0).getId());
                    db.saveCategory(newCat);
                    quitFragment();
                } else {
                    final ArrayList<BillCategory> list= db.getParentCategories(BillCategory.ALL_SPENT);
                    SelectParentCatDialog dialog = SelectParentCatDialog.newInstance(name, list);
                    dialog.setCallback(new SelectParentCatDialog.Callback() {
                        @Override
                        public void clickItem(int position) {
                            Log.d(LOG_TAG, list.get(position).getName());
                            BillCategory newCat = new BillCategory();
                            newCat.setName(name);
                            newCat.setActivated(1);
                            newCat.setType(BillCategory.CUSTOM_SPENT);
                            newCat.setParentId(list.get(position).getId());
                            newCat.setBackgroundResId(list.get(position).getBackgroundResId());
                            db.saveCategory(newCat);
                            quitFragment();
                        }
                    });
                    dialog.show(activity.getFragmentManager(), null);
                }
            } else {
                if (category.getParentId() == 0) {
                    category = (BillCategory)parent.getChildAt(position + 1).getTag();
                }
                category.setActivated(1);
                db.updateCategory(category);
                quitFragment();
            }
        }
    }

    private void quitFragment() {
        activity.setActivatedBillCategory(null);
        activity.initCategorySpan();
        activity.removeFragment();
    }

    private String getCatNameInQuota(String content) {
        if (content != null) {
            Pattern pattern = Pattern.compile("\"(.*?)\"");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                content = matcher.group();
                int end = content.length()-1;
                content = content.substring(1, end);
            }
        }
        return content;
    }


    private class CategoryAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<BillCategory> data;

        public CategoryAdapter(Context mContext, ArrayList<BillCategory> mData){
            context = mContext;
            data = mData;
        }

        public void update(ArrayList<BillCategory> mData){
            data.clear();
            data.addAll(mData);
            notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            return data.size();
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
            BillCategory category = data.get(position);
            View view;
            if (convertView == null ||
                    ((convertView.findViewById(R.id.list_item_category_text_name) == null) == isSearch)) {
                view = LayoutInflater.from(context).inflate(
                        isSearch ? R.layout.list_item_category_search : R.layout.list_item_category_simple,
                        parent, false);
            } else {
                view = convertView;
            }
            view.setTag(category);
            if (isSearch) {
                TextView tvName = (TextView)view.findViewById(R.id.list_item_category_text_name);
                TextView tvParent = (TextView)view.findViewById(R.id.list_item_category_text_parent);
                tvName.setText(category.getName());
                if (category.getType() == BillCategory.NEW_CATEGORY) {
                    tvName.setTextColor(getResources().getColor(R.color.category_light_green));
                    tvParent.setVisibility(View.GONE);
                } else {
                    tvParent.setVisibility(View.VISIBLE);
                    BillCategory parentCat = MyWalletDatabaseUtils.getInstance(getActivity()).
                            getCategory(category.getParentId());
                    if (parentCat != null) {
                        tvParent.setText(parentCat.getName());
                    }
                }
            } else {
                ImageView imageView = (ImageView)view.findViewById(R.id.list_item_category_icon_label);
                TextView textView = (TextView)view.findViewById(R.id.list_item_category_text_content);
                if (category.getParentId() == 0 && !isIncome) {
                    view.setBackgroundColor(getResources().getColor(R.color.transparent_gray));
                    imageView.setVisibility(View.VISIBLE);
                    textView.setTextColor(Color.BLACK);
                    view.setPadding(0, 10, 0, 10);

                    int colorRes;
                    switch (category.getId()) {
                        case 1:
                            colorRes = R.color.category_green;
                            break;
                        case 2:
                            colorRes = R.color.category_blue;
                            break;
                        case 3:
                            colorRes = R.color.category_orange;
                            break;
                        case 4:
                            colorRes = R.color.category_red;
                            break;
                        case 5:
                            colorRes = R.color.category_brown;
                            break;
                        case 6:
                            colorRes = R.color.category_pink;
                            break;
                        case 7:
                            colorRes = R.color.category_light_blue;
                            break;
                        default:
                            colorRes = R.color.category_light_green;
                            break;
                    }
                    imageView.setImageResource(colorRes);
                } else {
                    view.setBackgroundColor(getResources().getColor(R.color.white));
                    imageView.setVisibility(View.GONE);
                    textView.setTextColor(getResources().getColor(R.color.gray_for_text));
                    view.setPadding(0, 45, 0, 45);
                }
                textView.setText(category.getName());
            }

            return view;
        }
    }

    private void searchOrAddCategory(String input) {
        ArrayList<BillCategory> categories;
        int type = isIncome ? BillCategory.ALL_INCOME : BillCategory.ALL_SPENT;
        MyWalletDatabaseUtils db = MyWalletDatabaseUtils.getInstance(activity);
        if (input.length() == 0) {
            isSearch = false;
            categories = db.getAllCategories(type);
        }else {
            isSearch = true;
            categories = db.searchCategoriesByName(input, type);
            BillCategory category = db.getCategory(input);
            if (category == null) {
                category = new BillCategory();
                category.setName("+ 新添加分类\"" + input + "\"");
                category.setType(BillCategory.NEW_CATEGORY);
                category.setParentId(0);
                categories.add(0, category);
            }
        }
        CategoryAdapter adapter = (CategoryAdapter)lvCategory.getAdapter();
        adapter.update(categories);
    }

    public static class SelectParentCatDialog extends DialogFragment {
        public static final String NAME = "name";
        public static final String DATA = "data";
        private Callback callback;

        public static SelectParentCatDialog newInstance(String name, ArrayList<BillCategory> categories) {
            Bundle bundle = new Bundle();
            bundle.putString(NAME, name);
            bundle.putParcelableArrayList(DATA, categories);
            SelectParentCatDialog dialog = new SelectParentCatDialog();
            dialog.setArguments(bundle);
            return dialog;
        }

        public void setCallback(Callback callback) {
            this.callback = callback;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            Bundle bundle = getArguments();
            String name = bundle.getString(NAME);
            ArrayList<BillCategory> categories = bundle.getParcelableArrayList(DATA);
            String[] data = null;
            if (name != null && categories != null) {
                data = new String[categories.size()];
                for (int i = 0; i < categories.size(); i++) {
                    data[i] = categories.get(i).getName();
                }
            }
            if (data == null) {
                data = new String[0];
            }
            builder.setTitle("把\""+ name +"\"归类到").setItems(data, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (callback != null) {
                        callback.clickItem(which);
                    }
                }
            });
            return builder.create();
        }

        interface Callback{
            void clickItem(int position);
        }
    }
}
