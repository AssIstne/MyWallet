package com.assistne.mywallet.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.assistne.mywallet.R;
import com.assistne.mywallet.activity.BillActivity;

public class AddActivatedCategoryFragment extends Fragment implements View.OnClickListener{
    public static final String LOG_TAG = "fragment add act cat";

    private View spanRoot;
    private ImageView imgClose;
    private ImageView imgClear;
    private EditText etSearch;
    private ListView lvCategory;
    private ListView lvSideBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_activated_category_layout, container, false);
        findViews(view);
        return view;
    }

    public void findViews(View view) {
        spanRoot = view.findViewById(R.id.add_act_cat_span_root);
        spanRoot.setOnClickListener(this);

        imgClose = (ImageView)view.findViewById(R.id.add_act_cat_icon_close);
        imgClose.setOnClickListener(this);

        imgClear = (ImageView)view.findViewById(R.id.add_act_cat_icon_clear);
        imgClear.setOnClickListener(this);

        etSearch = (EditText)view.findViewById(R.id.add_act_cat_edit_search);

        lvCategory = (ListView)view.findViewById(R.id.add_act_cat_list_category);
        lvSideBar = (ListView)view.findViewById(R.id.add_act_cat_list_sidebar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_act_cat_icon_close:
                ((BillActivity)getActivity()).removeCategoryFragment();
                break;
            case R.id.add_act_cat_span_root:
                ((BillActivity)getActivity()).removeCategoryFragment();
                break;
            default:
                break;
        }
    }
}
