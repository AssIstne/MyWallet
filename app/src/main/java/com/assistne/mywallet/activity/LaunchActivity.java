package com.assistne.mywallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.assistne.mywallet.R;
import com.assistne.mywallet.db.MyWalletDatabaseHelper;
import com.assistne.mywallet.util.Constants;

/**
 * Created by assistne on 15/9/7.
 */
public class LaunchActivity extends Activity {

//    控制延迟时间
    private final int delayTime = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_launch_layout);
        new MyWalletDatabaseHelper(this, Constants.DATABASE, null, 1).getWritableDatabase();
//        等待一段时间后启动MainActivity
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(LaunchActivity.this, HomeActivity.class);
                startActivity(intent);
//                结束LaunchActivity避免回到该Activity
                finish();
            }
        }, delayTime);
    }

//    重写返回键点击事件，阻止中途退出LaunchActivity
    @Override
    public void onBackPressed() {
    }
}
