package com.assistne.mywallet.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.assistne.mywallet.R;
import com.assistne.mywallet.adapter.KeyBoardAdapter;

/**
 * Created by assistne on 15/9/15.
 */
public class KeyBoardFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_keyboard_layout, null);
        layout.findViewById(R.id.keyboard_span_root).setOnClickListener(this);
        layout.findViewById(R.id.keyboard_btn_ensure).setOnClickListener(this);
        layout.findViewById(R.id.keyboard_span_main).setOnClickListener(this);

        GridView gridView = (GridView)layout.findViewById(R.id.keyboard_grid_numbers);
        gridView.setAdapter(new KeyBoardAdapter(getActivity()));
        gridView.setOnItemClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.keyboard_span_root:
                Log.d("test", "click layout");
                break;
            case R.id.keyboard_span_main:
                Log.d("test", "click main layout");
                break;
            case R.id.keyboard_btn_ensure:
                Log.d("test", "click ensure button");
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("test", "click " + position);
    }
}
