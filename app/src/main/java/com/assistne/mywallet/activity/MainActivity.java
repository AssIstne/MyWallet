package com.assistne.mywallet.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.assistne.mywallet.R;

/**
 * Created by assistne on 15/9/7.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_main);
    }
}
