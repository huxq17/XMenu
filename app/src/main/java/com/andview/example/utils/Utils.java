package com.andview.example.utils;

import android.app.Activity;
import android.graphics.Point;
import android.util.DisplayMetrics;

/**
 * Created by Administrator on 2015/7/31.
 */
public class Utils {
    public static Point getScreenSize(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        int screen_width_px = displayMetrics.widthPixels;
        int screen_height_px = displayMetrics.heightPixels;
        Point point = new Point(screen_width_px, screen_height_px);
        return point;
    }
}
