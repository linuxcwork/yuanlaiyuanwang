package com.example.yang.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class ApplicationFragmentPageAdapt extends FragmentPagerAdapter {

    private FrequentlyUsedFragment frequently_used;
    private ServiceMarketFragment service_market;
    private final int PAGER_COUNT = 2;

    public ApplicationFragmentPageAdapt(FragmentManager fm) {
        super(fm);
        frequently_used = new FrequentlyUsedFragment();
        service_market = new ServiceMarketFragment();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = frequently_used;
                break;
            case 1:
                fragment = service_market;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }
}
