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
import com.assistne.mywallet.db.MyWalletDatabaseUtils;
import com.assistne.mywallet.model.Bill;
import com.assistne.mywallet.model.BillCategory;

import java.util.ArrayList;

/**
 * Created by assistne on 15/9/11.
 */
public class BillsAdapter extends BaseAdapter {

    private ArrayList<Bill> dataList;
    private LayoutInflater inflater;
    private Context context;

    public BillsAdapter(Context mContext, ArrayList<Bill> mDataList) {
        inflater = LayoutInflater.from(mContext);
        context = mContext;
        dataList = mDataList;
    }
    @Override
    public int getCount() {
        return dataList.size() <= 4 ? dataList.size() : 4;
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
        ViewHolder holder;
        if (convertView == null) {

            holder=new ViewHolder();

            convertView = inflater.inflate(R.layout.list_item_bill_simple, null);
            holder.emotion = (ImageView)convertView.findViewById(R.id.bill_simple_img_emotion);
            holder.category = (TextView)convertView.findViewById(R.id.bill_simple_text_category);
            holder.time_location = (TextView)convertView.findViewById(R.id.bill_simple_text_info);
            holder.price = (SimpleBillPriceLayout)convertView.findViewById(R.id.bill_simple_span_price);
            convertView.setTag(holder);

        }else {

            holder = (ViewHolder)convertView.getTag();
        }
        Bill bill = dataList.get(position);
        BillCategory category = MyWalletDatabaseUtils.getInstance(context).getBillCategory(bill.getCategoryId());
        holder.emotion.setBackgroundResource(bill.getEmotionRes());
        holder.category.setText(category.getName());
        holder.time_location.setText(DateFormat.format("MM.dd hh:mm", bill.getDate()) + "  " + bill.getLocation());
        holder.price.setPriceText(bill.getPrice());
        holder.price.setIsIncome(category.getType() > 0);
        return convertView;
    }

    class ViewHolder{
        ImageView emotion;
        TextView category;
        TextView time_location;
        SimpleBillPriceLayout price;
    }

    public void setDataList(ArrayList<Bill> mDataList) {
        dataList.clear();
        dataList.addAll(mDataList);
    }

    public ArrayList<Bill> getDataList() {
        return dataList;
    }
}
