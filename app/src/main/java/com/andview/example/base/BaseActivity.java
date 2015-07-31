package com.andview.example.base;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.andview.example.utils.Utils;

/**
 * Created by Administrator on 2015/7/31.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getScreenSize();
    }

    /**
     * get screen size
     */
    public void getScreenSize() {
        Point point = Utils.getScreenSize(this);
        Constants.sHeight = point.x;
        Constants.sWidth = point.y;
    }
}
