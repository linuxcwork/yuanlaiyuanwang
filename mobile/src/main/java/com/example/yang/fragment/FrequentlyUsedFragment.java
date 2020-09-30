package com.example.yang.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.yang.Activity.ThirdPartServiceAppraise;
import com.example.yang.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2018/3/31.
 */

public class FrequentlyUsedFragment extends Fragment {

    private List<Map<String,Object>> mData;
    private GridView frequentused;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_lifemainxml_installed, container, false);
        frequentused = view.findViewById(R.id.life_installed);
        mData = getdata();
        frequentused.setAdapter(new life_installed_adapt(mData,getActivity()));
        frequentused.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(mData.get(position).get("link").toString()));
                startActivity(intent);
            }
        });
        frequentused.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, menu.NONE, "移至顶部");
                menu.add(0, 1, menu.NONE, "删除服务");
                menu.add(0, 2, menu.NONE, "服务评价");
            }
        });
        return view;
    }


    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = (int)frequentused.getAdapter().getItemId(menuInfo.position);
        Map<String, Object> map= mData.get(pos);
        switch (item.getItemId()){
            case 0:
                mData.remove(pos);
                mData.add(0,map);
                break;
            case 1:
                mData.remove(pos);
                break;
            case 2:
                Intent appraise = new Intent(getContext(), ThirdPartServiceAppraise.class);
                startActivity(appraise);
                break;
        }
        frequentused.setAdapter(new life_installed_adapt(mData,getActivity()));
        return super.onContextItemSelected(item);
    }

    private List<Map<String,Object>> getdata(){
        List<Map<String,Object>> list = new ArrayList();
        Map<String,Object> map = new HashMap<>();
        for(int i = 0;i<30;i++){
            map.put("icon",R.drawable.d_haha);
            map.put("link","https://h5.m.jd.com/rn/3LmeNuQDfT1nN8qZS4jZoZQMFeV7/index.html?ad_od=share&utm_source=androidapp&utm_medium=appshare&utm_campaign=t_335139774&utm_term=CopyURL");
            map.put("text","华为");
            list.add(map);
        }
        return list;
    }
}
