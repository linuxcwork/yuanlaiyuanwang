package com.example.yang.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.example.yang.myapplication.MainActivity;

/**
 * Created by yang on 2018/3/25.
 */

public class MenuFragmentpageAdapt extends FragmentPagerAdapter{

    private messageoff_fragment messageoff=null;
    private final int PAGER_COUNT = 4;
    private linkman_fragment linkman=null;
    private lifemain_fragment lifemain=null;
    private ownmain_fragment ownmain=null;

    public MenuFragmentpageAdapt(FragmentManager gm){
        super(gm);

        messageoff = new messageoff_fragment();
        linkman = new linkman_fragment();
        lifemain = new lifemain_fragment();
        ownmain = new ownmain_fragment();

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
                fragment = messageoff;
                break;
            case MainActivity.PAGE_TWO:
                fragment = linkman;
                break;
            case MainActivity.PAGE_THREE:
                fragment = lifemain;
                break;
            case MainActivity.PAGE_FOUR:
                fragment = ownmain;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }
}
