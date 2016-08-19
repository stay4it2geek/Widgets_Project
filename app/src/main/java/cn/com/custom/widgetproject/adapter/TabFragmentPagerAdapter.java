package cn.com.custom.widgetproject.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * tabFragment适配器
 * Created by custom on 2016/6/14.
 */

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {
    //碎片的集合
    private ArrayList<Fragment> mFragmentList;

    public TabFragmentPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    /**
     * 实例化碎片
     * @param mFragmentList
     */
    public void setList(ArrayList<Fragment> mFragmentList) {
        this.mFragmentList = mFragmentList;

    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {

        return mFragmentList.size();
    }


}