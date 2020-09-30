package com.example.yang.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.example.yang.item.EmotionKeyboard;

public class ChatFuntionViewPager extends FragmentPagerAdapter {

    private funtionfirstFragment funtionfirstfragment = null;

    public ChatFuntionViewPager(Context context,FragmentManager fm) {
        super(fm);
        funtionfirstfragment = new funtionfirstFragment(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        switch (position) {
            case EmotionKeyboard.FPAGE_ONE:
                fragment = funtionfirstfragment;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 1;
    }
}
