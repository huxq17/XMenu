package com.andview.example.base;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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
    private Toast toast;

    /**
     * 格式化字符串
     *
     * @param format
     * @param args
     */
    public String format(String format, String args) {
        return String.format(format, args);
    }

    public void toast(String msg) {
        if (null == msg) {
            return;
        }
        if (null == toast)
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        else
            toast.setText(msg);
        toast.show();
    }

    public void toast(int id) {
        if (null == toast)
            toast = Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT);
        else
            toast.setText(id);
        toast.show();
    }

}
