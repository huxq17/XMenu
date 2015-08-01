package com.andview.example.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.andview.example.base.BaseActivity;
import com.andview.example.base.Constants;
import com.andview.example.fragment.ContentFragment;
import com.andview.example.fragment.MenuFragment;
import com.andview.example.widget.xmenu.XMenu;
import com.andview.xmenu.R;


public class MainActivity extends BaseActivity implements MenuFragment.OnFragmentInteractionListener{
    private XMenu xMenu;
    private MenuFragment mMenuFragment;
    private ContentFragment mContentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xMenu = new XMenu(this);
        setContentView(xMenu);
        configMenu();
        configContent();
    }

    private void configMenu() {
        mMenuFragment = new MenuFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, mMenuFragment).commit();
        xMenu.setMenu(R.layout.menu_container);
        int width = Math.min(Constants.sWidth, Constants.sHeight);
        xMenu.setMenuWidth(3 * width / 4);
    }

    private void configContent() {
        mContentFragment = new ContentFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mContentFragment).commit();
        xMenu.setContent(R.layout.activity_main);
    }
    public void onFragmentInteraction(String id){

    }
    public void toggle() {
        xMenu.toggle();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                toggle();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
