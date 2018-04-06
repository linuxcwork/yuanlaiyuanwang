package com.example.yang.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.example.yang.myapplication.MainActivity;

/**
 * Created by yang on 2018/3/31.
 */

public class life_Fragment_Adapt extends FragmentPagerAdapter{

    private final int PAGER_COUNT = 2;
    private installed_fragment installed=null;
    private popular_fragment popular=null;

    public life_Fragment_Adapt(FragmentManager fm) {
        super(fm);

        installed = new installed_fragment();
        popular = new popular_fragment();
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case MainActivity.PAGE_ONE:
                fragment = installed;
                break;
            case MainActivity.PAGE_TWO:
                fragment = popular;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }
}
