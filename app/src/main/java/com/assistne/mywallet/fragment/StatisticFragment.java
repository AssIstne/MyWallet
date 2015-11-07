package com.assistne.mywallet.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.assistne.mywallet.R;
import com.assistne.mywallet.activity.BillActivity;
import com.assistne.mywallet.activity.BillDetailActivity;
import com.assistne.mywallet.customview.PriceView;
import com.assistne.mywallet.db.MyWalletDatabaseUtils;
import com.assistne.mywallet.model.Bill;
import com.assistne.mywallet.model.BillCategory;
import com.assistne.mywallet.util.BillUtils;
import com.assistne.mywallet.util.GlobalUtils;
import com.db.chart.Tools;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.ChartSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.ChartView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by assistne on 15/10/12.
 */
public class StatisticFragment extends Fragment implements View.OnClickListener{

    public static final int TYPE_MONTH = 100;
    public static final int TYPE_WEEK = 101;
    public static final int TYPE_DAY = 102;
    public static final int REQ_CODE_ADD_BILL = 110;

    public static final String FLAG_DATE = "date";
    public static final String FLAG_TYPE = "type";

    private static final int GRID_NUM = 5;

    private int currentType = TYPE_MONTH;
    private Calendar currentDate;
    private Paint paint;
    private int activatedIndex;// 活跃的数据，颜色加深
    private int dataCount;// 数据组数
    private int maxScript;// 最大下标值(月份/周数/天)

    private View.OnClickListener billListener;

    private Button activatedButton;
    private boolean monthIsIncome;
    private MyWalletDatabaseUtils db;
    private boolean isForceFresh = false;

    private View root;
    private BarChartView spanChart;
    private TextView tvGraphTitle;
    private Button btnDay;
    private Button btnWeek;
    private Button btnMonth;
    private TextView tvIncome;
    private TextView tvSpend;
    private ViewGroup spanIncome;
    private ViewGroup spanMonthInfo;
    private ViewGroup spanDayInfo;
    private TextView tvMonthDelta;
    private TextView tvMonthUnused;
    private ImageView imgDayDeltaAverage;
    private ImageView imgDayDeltaBefore;
    private TextView tvDayDeltaAverage;
    private TextView tvDayDeltaBefore;
    private TextView tvDayTotal;
    private TextView tvDayEmotion;
    private ImageView imgDayAdd;
    private LinearLayout spanBill;
    private LinearLayout spanCategory;
    private ImageButton iBtnMonthIncome;
    private ImageButton iBtnMonthSpend;

    public static StatisticFragment newInstance() {
        return new StatisticFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("", "stat fragment on create");
        super.onCreate(savedInstanceState);
        handleArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_statistic, container, false);
        findViews();
        spanChart.setGrid(ChartView.GridType.HORIZONTAL, paint);
        showData();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        showData();
    }

    private void setActivatedButton(Button btn){
        if (btn == null)
            return;
        if (activatedButton != null && activatedButton.getId() != btn.getId()){
            activatedButton.setActivated(false);
        }
        activatedButton = btn;
        activatedButton.setActivated(true);
    }

    private void showData() {
        switch (currentType) {
            case TYPE_MONTH:
                showMonthData();
                break;
            case TYPE_WEEK:
                showWeekData();
                break;
            case TYPE_DAY:
                Log.d("", "show day data");
                showDayData();
                break;
            default:break;
        }
    }

    private void showDayData() {
        setActivatedButton(btnDay);
        spanIncome.setVisibility(View.GONE);
        spanDayInfo.setVisibility(View.VISIBLE);
        spanMonthInfo.setVisibility(View.GONE);

        dataCount = 7;
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日", Locale.CHINA);
        tvGraphTitle.setText(format.format(currentDate.getTime()));

        if (!isForceFresh && currentType == TYPE_DAY
                && spanChart.getData().size() !=0 && spanChart.getData().get(0).isVisible()
                && maxScript - currentDate.get(Calendar.DAY_OF_MONTH) > 0 && maxScript - currentDate.get(Calendar.DAY_OF_MONTH) < dataCount) {
            setActivatedBar(dataCount - 1 - (maxScript - currentDate.get(Calendar.DAY_OF_MONTH)));
            spanChart.notifyDataUpdate();
        }else {
            isForceFresh = false;
            currentType = TYPE_DAY;
            activatedIndex = dataCount - 1;
            maxScript = currentDate.get(Calendar.DAY_OF_MONTH);

            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            calendar.setTimeInMillis(currentDate.getTimeInMillis());

            calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
            long to = calendar.getTimeInMillis();
            long from;

            float[] data = new float[dataCount];
            String[] script = new String[dataCount];
            float maxValue = 0;
            for (int i = dataCount - 1; i >= 0; i --) {
                script[i] = calendar.get(Calendar.MONTH) + 1 + "." + calendar.get(Calendar.DAY_OF_MONTH);

                calendar.add(Calendar.DAY_OF_MONTH, -1);
                from = calendar.getTimeInMillis();

                float total = 0;
                for(Bill bill : db.getBillsByTime(from, to)) {
                    if (!bill.isIncome()) {
                        total += bill.getPrice();
                    }
                }
                data[i] = total;
                maxValue = Math.max(maxValue, total);
                to = from;
            }
            ArrayList<ChartSet> d = new ArrayList<>();

            d.add(new BarSet(script, data).setColor(getResources().getColor(R.color.red_200)));
            int top = (int)(maxValue + maxValue/10)/10*10;
            top = top == 0 ? 10 : top;
            spanChart.setAxisBorderValues(0, top, top / GRID_NUM);
            spanChart.setBarSpacing(Tools.fromDpToPx(120));
            spanChart.addData(d);
            setActivatedBar(activatedIndex);
            spanChart.show();
        }
    }

    private void showWeekData() {
        dataCount = currentDate.getActualMaximum(Calendar.WEEK_OF_MONTH);
        SimpleDateFormat format = new SimpleDateFormat("MM月第W周", Locale.CHINA);
        tvGraphTitle.setText(format.format(currentDate.getTime()));

        if (!isForceFresh && currentType == TYPE_WEEK
                && spanChart.getData().size() !=0 && spanChart.getData().get(0).isVisible()
                && maxScript - currentDate.get(Calendar.WEEK_OF_MONTH) > 0 && maxScript - currentDate.get(Calendar.WEEK_OF_MONTH) < dataCount) {
            setActivatedBar(dataCount - 1 - (maxScript - currentDate.get(Calendar.WEEK_OF_MONTH)));
            spanChart.notifyDataUpdate();
        }else {
            isForceFresh = false;
            currentType = TYPE_WEEK;
            activatedIndex = dataCount - 1;
            maxScript = dataCount;

            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            calendar.set(Calendar.WEEK_OF_MONTH, calendar.getActualMaximum(Calendar.WEEK_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
            Log.d("statistic", GlobalUtils.getFormatDateFromMills(calendar.getTimeInMillis()));
            long to;
            long from = calendar.getTimeInMillis();

            float[] data = new float[dataCount];
            float maxValue = 0;
            for (int i = dataCount - 1; i >= 0; i --) {
                to = from;
                calendar.add(Calendar.WEEK_OF_MONTH, -1);
                from = calendar.getTimeInMillis();
                float total = 0;
                Log.d("statistic", "按周");
                Log.d("statistic", GlobalUtils.getFormatDateFromMills(from) + " - " + GlobalUtils.getFormatDateFromMills(to));
                for(Bill bill : db.getBillsByTime(from, to)) {
                    if (!bill.isIncome()) {
                        total += bill.getPrice();
                    }
                }
                data[i] = total;
                maxValue = Math.max(maxValue, total);
            }
            ArrayList<ChartSet> d = new ArrayList<>();

            String[] script = new String[dataCount];
            for (int i = dataCount-1; i >= 0;i--) {
                script[i] = "第" + (i + 1) +"周";
            }
            d.add(new BarSet(script, data).setColor(getResources().getColor(R.color.red_200)));
            int top = (int)(maxValue + maxValue/10)/10*10;
            top = top == 0 ? 10 : top;
            spanChart.setAxisBorderValues(0, top, top / GRID_NUM);
            spanChart.setBarSpacing(Tools.fromDpToPx(60));
            spanChart.addData(d);
            setActivatedBar(activatedIndex);
            spanChart.show();
        }
    }

    private void showMonthData() {
        spanIncome.setVisibility(View.VISIBLE);
        spanDayInfo.setVisibility(View.GONE);
        spanMonthInfo.setVisibility(View.VISIBLE);

        setActivatedButton(btnMonth);
        dataCount = 3;
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月", Locale.CHINA);
        tvGraphTitle.setText(format.format(currentDate.getTime()));

        if (!isForceFresh && currentType == TYPE_MONTH
                && spanChart.getData().size() !=0 && spanChart.getData().get(0).isVisible()
                && maxScript - currentDate.get(Calendar.MONTH) > 0 && maxScript - currentDate.get(Calendar.MONTH) < dataCount) {
            setActivatedBar(dataCount - 1 - (maxScript - currentDate.get(Calendar.MONTH)));
            spanChart.notifyDataUpdate();
        }else {
            isForceFresh = false;
            currentType = TYPE_MONTH;
            activatedIndex = dataCount - 1;
            maxScript = currentDate.get(Calendar.MONTH);

            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            calendar.setTimeInMillis(currentDate.getTimeInMillis());

            long to = calendar.getTimeInMillis();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            long from = calendar.getTimeInMillis();

            float[] dataIn = new float[dataCount];
            float[] dataOut = new float[dataCount];
            float maxValue = 0;
            for (int i = dataCount - 1; i >= 0; i --) {
                if (i < dataCount -1) {
                    to = from;
                    calendar.add(Calendar.MONTH, -1);
                    from = calendar.getTimeInMillis();
                }
                float totalIn = 0;
                float totalOut = 0;
                for(Bill bill : db.getBillsByTime(from, to)) {
                    if (bill.isIncome()) {
                        totalIn += bill.getPrice();
                    } else {
                        totalOut += bill.getPrice();
                    }
                }
                dataIn[i] = totalIn;
                dataOut[i] = totalOut;
                maxValue = Math.max(maxValue, Math.max(totalIn, totalOut));
            }
            ArrayList<ChartSet> d = new ArrayList<>();

            String[] script = new String[dataCount];
            for (int i = dataCount-1; i >= 0;i--) {
                script[i] = (currentDate.get(Calendar.MONTH) + 2 - dataCount + i) +"月";
            }
            d.add(new BarSet(script, dataOut).setColor(getResources().getColor(R.color.red_200)));
            d.add(new BarSet(script, dataIn).setColor(getResources().getColor(R.color.green_200)));
            int top = (int)(maxValue + maxValue/10)/10*10;
            top = top == 0 ? 10 : top;
            spanChart.setAxisBorderValues(0, top, top / GRID_NUM);
            spanChart.setSetSpacing(Tools.fromDpToPx(5));
            spanChart.setBarSpacing(Tools.fromDpToPx(120));
            spanChart.addData(d);
            setActivatedBar(activatedIndex);
            spanChart.show();
        }
    }

    private void handleArguments() {
        currentType = TYPE_MONTH;
        currentDate = Calendar.getInstance(Locale.CHINA);
        if (paint == null)
            paint = new Paint();
        if (db == null)
            db = MyWalletDatabaseUtils.getInstance(getActivity());
    }

    private void findViews() {
        spanChart = (BarChartView) root.findViewById(R.id.statistic_span_chart);
        spanChart.setGrid(ChartView.GridType.HORIZONTAL, new Paint());
        tvGraphTitle = (TextView)root.findViewById(R.id.statistic_text_graph_title);

        root.findViewById(R.id.statistic_img_left_arrow).setOnClickListener(this);
        root.findViewById(R.id.statistic_img_right_arrow).setOnClickListener(this);

        btnDay = (Button)root.findViewById(R.id.statistic_btn_day);
        btnDay.setOnClickListener(this);
//        btnWeek = (Button)root.findViewById(R.id.statistic_btn_week);
//        btnWeek.setOnClickListener(this);
        btnMonth = (Button)root.findViewById(R.id.statistic_btn_month);
        btnMonth.setOnClickListener(this);

        tvIncome = (TextView)root.findViewById(R.id.statistic_text_income);
        tvSpend = (TextView)root.findViewById(R.id.statistic_text_spend);
        spanIncome = (ViewGroup)root.findViewById(R.id.statistic_span_income);

        spanDayInfo = (ViewGroup)root.findViewById(R.id.statistic_span_day_detail);
        spanMonthInfo = (ViewGroup)root.findViewById(R.id.statistic_span_month_detail);
        tvMonthDelta = (TextView)root.findViewById(R.id.statistic_text_month_delta);
        tvMonthUnused = (TextView)root.findViewById(R.id.statistic_text_month_unused);

        imgDayDeltaAverage = (ImageView)root.findViewById(R.id.statistic_img_day_delta_average);
        imgDayDeltaBefore = (ImageView)root.findViewById(R.id.statistic_img_day_delta_before);
        tvDayDeltaAverage = (TextView)root.findViewById(R.id.statistic_text_day_delta_average);
        tvDayDeltaBefore = (TextView)root.findViewById(R.id.statistic_text_day_delta_before);

        tvDayTotal = (TextView)root.findViewById(R.id.statistic_text_day_total);
        tvDayEmotion = (TextView)root.findViewById(R.id.statistic_text_day_emotion);
        imgDayAdd = (ImageView)root.findViewById(R.id.statistic_img_day_add);
        imgDayAdd.setOnClickListener(this);

        spanBill = (LinearLayout)root.findViewById(R.id.statistic_span_bill);
        spanCategory = (LinearLayout)root.findViewById(R.id.statistic_span_category);

        iBtnMonthIncome = (ImageButton)root.findViewById(R.id.statistic_btn_month_income);
        iBtnMonthIncome.setOnClickListener(this);
        iBtnMonthSpend = (ImageButton)root.findViewById(R.id.statistic_btn_month_spend);
        iBtnMonthSpend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.statistic_img_left_arrow:
                changeData(-1);
                break;
            case R.id.statistic_img_right_arrow:
                changeData(1);
                break;
            case R.id.statistic_btn_day:
                currentDate = Calendar.getInstance(Locale.CHINA);
                showDayData();
                setActivatedButton(btnDay);
                break;
//            case R.id.statistic_btn_week:
//                currentDate = Calendar.getInstance(Locale.CHINA);
//                showWeekData();
//                setActivatedButton(btnWeek);
//                break;
            case R.id.statistic_btn_month:
                currentDate = Calendar.getInstance(Locale.CHINA);
                showMonthData();
                setActivatedButton(btnMonth);
                break;
            case R.id.statistic_img_day_add:
                Intent intent = new Intent(getActivity(), BillActivity.class);
                getActivity().startActivityForResult(intent, REQ_CODE_ADD_BILL);
                break;
            case R.id.statistic_btn_month_income:
                monthIsIncome = true;
                showMonthDataDetail();
                break;
            case R.id.statistic_btn_month_spend:
                monthIsIncome = false;
                showMonthDataDetail();
                break;
            default:
                break;
        }
    }

    private void setMonthActivatedButton(boolean isIncome) {
        iBtnMonthSpend.setActivated(!isIncome);
        iBtnMonthIncome.setActivated(isIncome);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_ADD_BILL:
                showData();
                break;
            default:break;
        }
    }

    private void changeData(int delta) {
        Calendar today = Calendar.getInstance(Locale.CHINA);
        long origin = currentDate.getTimeInMillis();
        switch (currentType) {
            case TYPE_MONTH:
                currentDate.add(Calendar.MONTH, delta);
                break;
            case TYPE_DAY:
                currentDate.add(Calendar.DAY_OF_MONTH, delta);
                break;
            case TYPE_WEEK:
                currentDate.add(Calendar.WEEK_OF_MONTH, delta);
                break;
            default:break;
        }
        if (currentDate.after(today)) {
            currentDate.setTimeInMillis(origin);
            Toast.makeText(getActivity(), "未来还没到呢~~", Toast.LENGTH_SHORT).show();
        } else {
            showData();
        }
    }

    private void setActivatedBar(final int index) {
        BarSet set = (BarSet)spanChart.getData().get(0);
        if (0 <= index && index < set.size()) {
            Resources resources = getResources();
            set.getEntry(activatedIndex).setColor(resources.getColor(R.color.red_200));
            Bar bar = (Bar)set.getEntry(index);
            bar.setColor(resources.getColor(R.color.red_400));
            float out = bar.getValue();
            float budget = GlobalUtils.getBudget(getActivity());
            tvSpend.setText("￥"+GlobalUtils.formatPrice(out, true));
            if (currentType == TYPE_MONTH && spanChart.getData().size() == 2) {
                spanChart.getData().get(1).getEntry(activatedIndex).setColor(resources.getColor(R.color.green_200));
                Bar b = (Bar)spanChart.getData().get(1).getEntry(index);
                float in = b.getValue();
                b.setColor(resources.getColor(R.color.green_400));
                tvIncome.setText("￥" + GlobalUtils.formatPrice(in, true));
                tvMonthDelta.setText("￥"+GlobalUtils.formatPrice(in - out, true));
                tvMonthUnused.setText("￥"+GlobalUtils.formatPrice(budget - out, true));

                showMonthDataDetail();
            }

            if (currentType == TYPE_DAY) {
                float average = budget / currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);
                float deltaAverage = out - average;
                imgDayDeltaAverage.setImageResource(deltaAverage <= 0 ? R.drawable.review_down : R.drawable.review_up);
                tvDayDeltaAverage.setText("￥" + GlobalUtils.formatPrice(Math.abs(deltaAverage), true));

                Calendar calendar = Calendar.getInstance(Locale.CHINA);
                calendar.setTimeInMillis(currentDate.getTimeInMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                long to = calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                long from = calendar.getTimeInMillis();
                Log.d("", GlobalUtils.getFormatDateFromMills(from) + " - " + GlobalUtils.getFormatDateFromMills(to));
                ArrayList<Bill> data = db.getBillsByTime(from, to);
                int good = 0;
                int normal = 0;
                int bad = 0;
                spanBill.removeAllViews();
                for (Bill bill: data) {
                    switch (bill.getEmotion()){
                        case Bill.EMOTION_BAD:
                            bad ++;
                            break;
                        case Bill.EMOTION_NORMAL:
                            normal ++;
                            break;
                        case Bill.EMOTION_GOOD:
                            good ++;
                            break;
                        default:break;
                    }
                    spanBill.addView(getBillView(bill));
                }
                tvDayEmotion.setText("开心：" + good + "笔  " + "一般：" + normal + "笔  " + "不爽：" + bad + "笔");
                tvDayTotal.setText("当日共" + data.size() + "笔记录");

                calendar.add(Calendar.DAY_OF_MONTH, -1);
                to = from;
                from = calendar.getTimeInMillis();
                float before = BillUtils.getSumPrices(db.getBillsByTime(from, to), false);
                float deltaBefore = out - before;
                imgDayDeltaBefore.setImageResource(deltaBefore <= 0 ? R.drawable.review_down : R.drawable.review_up);
                tvDayDeltaBefore.setText("￥"+GlobalUtils.formatPrice(Math.abs(deltaBefore), true));
            }
            activatedIndex = index;
        }
    }

    private View getBillView(final Bill bill) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_bill_simple, null);
        ImageView emotion = (ImageView)view.findViewById(R.id.bill_simple_img_emotion);
        TextView tvCategory = (TextView)view.findViewById(R.id.bill_simple_text_category);
        TextView time_location = (TextView)view.findViewById(R.id.bill_simple_text_info);
        PriceView price = (PriceView)view.findViewById(R.id.bill_simple_span_price);
        TextView tvDescription = (TextView)view.findViewById(R.id.bill_simple_text_description);
        if (!bill.getDescription().equals("")) {
            view.findViewById(R.id.bill_simple_img_description).setVisibility(View.VISIBLE);
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(bill.getDescription());
        }
        emotion.setImageResource(bill.getEmotionRes());
        BillCategory category = bill.getBillCategory(getActivity());
        tvCategory.setText(category.getName());
        time_location.setText(bill.getInfo());
        price.setPriceText(bill.getPrice());
        price.setIsIncome(category.getType() > 0);
        view.setTag(bill);
        if (billListener == null) {
            billListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getTag() != null && v.getTag() instanceof Bill) {
                        Bill bill = (Bill)v.getTag();
                        Intent intent = new Intent(getActivity(), BillDetailActivity.class);
                        intent.putExtra(BillDetailActivity.F_BILL, bill);
                        getActivity().startActivityForResult(intent, REQ_CODE_ADD_BILL);
                    }
                }
            };
        }
        view.setOnClickListener(billListener);
        return view;
    }

    private void showMonthDataDetail() {
        setMonthActivatedButton(monthIsIncome);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(currentDate.getTimeInMillis());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long to = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        long from = calendar.getTimeInMillis();
        ArrayList<Bill> data = db.getBillsByTime(from, to);

        HashMap<Integer  ,ArrayList<Bill>> matrix = new HashMap<>();
        float total = 0;
        for (Bill bill : data) {
            if (bill.isIncome() == monthIsIncome) {
                total += bill.getPrice();
                BillCategory category = db.getCategory(bill.getCategoryId());
                BillCategory parentCat;
                if (monthIsIncome)
                    parentCat = category;
                else
                    parentCat = db.getCategory(category.getParentId());
                if (matrix.containsKey(parentCat.getId()))
                    matrix.get(parentCat.getId()).add(bill);
                else {
                    ArrayList<Bill> bills = new ArrayList<>();
                    bills.add(bill);
                    matrix.put(parentCat.getId(), bills);
                }
            }
        }
        spanCategory.removeAllViews();
        for(Map.Entry<Integer, ArrayList<Bill>> entry : matrix.entrySet()) {
            if (entry.getValue().size() > 0)
                spanCategory.addView(getCategoryView(entry.getKey(), total, entry.getValue()));
        }
    }

    private View getCategoryView(int parentId, float total, ArrayList<Bill> data) {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.snippet_month_review_cateory, spanCategory, false);
        TextView tvName = (TextView)root.findViewById(R.id.month_review_category_text_name);
        TextView tvCount = (TextView)root.findViewById(R.id.month_review_category_text_count);
        TextView tvPrice = (TextView)root.findViewById(R.id.month_review_category_text_total_price);
        TextView tvPercent = (TextView)root.findViewById(R.id.month_review_category_text_percent);
        final ImageView imgPercent = (ImageView)root.findViewById(R.id.month_review_category_img_percent);

        tvName.setText(db.getCategory(parentId).getName());
        tvCount.setText(data.size() + "笔");
        float t = BillUtils.getSumPrices(data, monthIsIncome);
        tvPrice.setText("￥" + GlobalUtils.formatPrice(t, true));
        tvPrice.setTextColor(getResources().getColor(monthIsIncome ? R.color.green : R.color.red));
        final float percent = t/total;
        tvPercent.setText("占本月" + (int)(percent * 100) + "%");
        int colorId;
        if (monthIsIncome)
            colorId = R.color.category_light_green;
        else {
            switch (parentId) {
                case 1:
                    colorId = R.color.category_light_green;
                    break;
                case 2:
                    colorId = R.color.category_blue;
                    break;
                case 3:
                    colorId = R.color.category_orange;
                    break;
                case 4:
                    colorId = R.color.category_red;
                    break;
                case 5:
                    colorId = R.color.category_brown;
                    break;
                case 6:
                    colorId = R.color.category_pink;
                    break;
                case 7:
                    colorId = R.color.category_light_blue;
                    break;
                default:
                    colorId = R.color.category_green;
                    break;
            }
        }
        imgPercent.setBackgroundColor(getResources().getColor(colorId));
        ViewGroup.LayoutParams params = imgPercent.getLayoutParams();
        params.width = (int) (Tools.fromDpToPx(200) * percent);
        imgPercent.setLayoutParams(params);
        return root;
    }


    public void setDateAndType(long dateInMills, int type) {
        currentDate.setTimeInMillis(dateInMills);
        if (type == TYPE_MONTH || type == TYPE_WEEK || type == TYPE_DAY)
            currentType = type;
        isForceFresh = true;
        showData();
    }
}
