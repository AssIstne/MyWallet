package com.assistne.mywallet.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.assistne.mywallet.R;
import com.assistne.mywallet.customview.SimpleBillPriceLayout;
import com.assistne.mywallet.model.Bill;

import java.util.ArrayList;

/**
 * Created by assistne on 15/9/11.
 */
public class BillsAdapter extends BaseAdapter {

    private ArrayList<Bill> mdataList;
    private LayoutInflater mInflater;

    public BillsAdapter(Context context, ArrayList<Bill> dataList) {
        this.mInflater = LayoutInflater.from(context);
        this.mdataList = dataList;
    }
    @Override
    public int getCount() {
        return mdataList.size() <= 4 ? mdataList.size() : 4;
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
        ViewHolder holder = null;
        if (convertView == null) {

            holder=new ViewHolder();

            convertView = mInflater.inflate(R.layout.layout_simple_bill, null);
            holder.emotion = (ImageView)convertView.findViewById(R.id.simple_bill_emotion);
            holder.category = (TextView)convertView.findViewById(R.id.simple_bill_cat);
            holder.time_location = (TextView)convertView.findViewById(R.id.simple_bill_time_and_location);
            holder.price = (SimpleBillPriceLayout)convertView.findViewById(R.id.simple_bill_price);
            convertView.setTag(holder);

        }else {

            holder = (ViewHolder)convertView.getTag();
        }

        Bill bill = mdataList.get(position);
        holder.emotion.setBackgroundResource(bill.getEmotion());
        holder.category.setText(bill.getCategory());
        holder.time_location.setText(DateFormat.format("MM.dd hh:mm", bill.getDate()) + "  " + bill.getLocation());
        holder.price.setPriceText(bill.getPrice());
        return convertView;
    }

    class ViewHolder{
        ImageView emotion;
        TextView category;
        TextView time_location;
        SimpleBillPriceLayout price;
    }
}
