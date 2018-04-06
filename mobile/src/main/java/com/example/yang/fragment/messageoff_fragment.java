package com.example.yang.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;



import com.example.yang.myapplication.R;

import com.example.yang.myapplication.signallistview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2018/3/18.
 */

public class messageoff_fragment extends Fragment {

    private ListView listView;

    public  messageoff_fragment(){

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       // return super.onCreateView(inflater, container, savedInstanceState);
        View view= inflater.inflate(R.layout.messageoffriend_fragment, container, false);
        listView = (ListView)view.findViewById(R.id.list_signal);
        List<Map<String, Object>> list=getData();
        listView.setAdapter(new signallistview(getActivity(), list));

        return view;
    }

    private List<Map<String, Object>> getData(){
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("image", R.drawable.ic_launcher_background);
            map.put("title", "这是一个标题"+i);
            map.put("info", "这是一个详细信息" + i);
            list.add(map);
        }

        return list;
    }


}
