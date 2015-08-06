package com.andview.example.base;

import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/7/31.
 */
public class BaseFragment extends Fragment {
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
            toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        else
            toast.setText(msg);
        toast.show();
    }

    public void toast(int id) {
        if (null == toast)
            toast = Toast.makeText(getActivity(), id, Toast.LENGTH_SHORT);
        else
            toast.setText(id);
        toast.show();
    }

}
