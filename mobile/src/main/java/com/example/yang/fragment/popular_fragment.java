package com.example.yang.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.yang.myapplication.R;

/**
 * Created by yang on 2018/3/31.
 */

public class popular_fragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_lifemainxml_popular, container, false);
        ListView pop = (ListView) view.findViewById(R.id.life_popular);
        return view;
    }
}
