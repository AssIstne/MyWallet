package com.assistne.mywallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.assistne.mywallet.R;
import com.assistne.mywallet.db.MyWalletDatabaseUtils;
import com.assistne.mywallet.fragment.DeleteWarningDialogFragment;
import com.assistne.mywallet.fragment.GlobalNavigationFragment;
import com.assistne.mywallet.model.Bill;
import com.assistne.mywallet.model.BillCategory;
import com.assistne.mywallet.notification.NotificationUtils;
import com.assistne.mywallet.util.GlobalUtils;

/**
 * Created by assistne on 15/9/25.
 */
public class BillDetailActivity extends Activity implements View.OnClickListener{
    public static final String F_BILL = "bill";

    private Bill bill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);
        bill = (Bill)getIntent().getExtras().get(F_BILL);
        if (bill == null) {
            Toast.makeText(this, "bill is not existed.", Toast.LENGTH_SHORT).show();
            finish();
        }
        Log.d("detail act", "NO " + bill.getId() + " bill's path is " + bill.getImagePath());
        BillCategory category = bill.getBillCategory(this);
        GlobalNavigationFragment fragment = (GlobalNavigationFragment)getFragmentManager().findFragmentById(R.id.bill_detail_fragment_navigation);
        fragment.setTitle(GlobalNavigationFragment.NORMAL_NAV, category.getType() > 0 ? "收入" : "支出");

        findViewById(R.id.bill_detail_img_emotion).setBackgroundResource(bill.getEmotionRes());
        ((TextView)findViewById(R.id.bill_detail_text_category)).setText(category.getName());
        ((TextView)findViewById(R.id.bill_detail_text_info)).setText(bill.getInfo());
        TextView tvPrice = (TextView) findViewById(R.id.bill_detail_text_price);
        tvPrice.setText(GlobalUtils.formatPrice(bill.getPrice(), false) + "元");
        tvPrice.setTextColor(getResources().getColor(bill.isIncome() ? R.color.green : R.color.red));
        TextView tvDescription = (TextView)findViewById(R.id.bill_detail_text_description);
        if (bill.getDescription().length() == 0) {
            tvDescription.setVisibility(View.GONE);
        } else {
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(bill.getDescription());
        }

        findViewById(R.id.bill_detail_span_delete).setOnClickListener(this);
        findViewById(R.id.bill_detail_span_share).setOnClickListener(this);
        findViewById(R.id.bill_detail_span_edit).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final ImageView imgBill = (ImageView)findViewById(R.id.bill_detail_img_bill);
        if (!bill.getImagePath().equals("")) {
            imgBill.setVisibility(View.VISIBLE);
            imgBill.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int size = imgBill.getWidth();
                    imgBill.setImageBitmap(GlobalUtils.decodeSampledBitmapFromPath(bill.getImagePath(), size, size));
                }
            }, 10);
        } else {
            imgBill.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bill_detail_span_delete:
                DeleteWarningDialogFragment dialogFragment = new DeleteWarningDialogFragment();
                dialogFragment.setCallback(new DeleteWarningDialogFragment.Callback() {
                    @Override
                    public void positive() {
                        MyWalletDatabaseUtils.getInstance(BillDetailActivity.this).deleteBill(bill.getId());
                        NotificationUtils.showNotification(BillDetailActivity.this);
                        finish();
                    }

                    @Override
                    public void negative() {

                    }
                });
                dialogFragment.show(getFragmentManager(), "delete");
                break;
            case R.id.bill_detail_span_edit:
                Intent intent = new Intent(this, BillActivity.class);
                intent.putExtra("bill", bill);
                startActivityForResult(intent, BillActivity.FROM_BILL_DETAIL);
                break;
            case R.id.bill_detail_span_share:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BillActivity.FROM_BILL_DETAIL && resultCode == Activity.RESULT_OK) {
            finish();
        }
    }

}
