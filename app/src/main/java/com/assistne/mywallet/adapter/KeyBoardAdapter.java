package com.assistne.mywallet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.assistne.mywallet.R;

/**
 * Created by assistne on 15/9/15.
 */
public class KeyBoardAdapter extends BaseAdapter {

    private Context mContext;

    private final String[] NUMBERS = { "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "-1" };

    public KeyBoardAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return NUMBERS.length;
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
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_keyboard_button, null);
            ((TextView)view.findViewById(R.id.keyboard_button_text_number)).setText(NUMBERS[position]);
        } else {
            view = convertView;
        }
        return view;
    }
}
